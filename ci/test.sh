#!/bin/bash
set -x

function die()
{
	echo "$1"
	exit 1
}

function check_variable()
{
	eval val='$'$1
	[ "$val" != "" ] || die "$1 not defined"
}

function fail()
{
	exit 1
}

trap "fail" ERR INT QUIT KILL TERM

check_variable JAVA_HOME
check_variable CI_PROJECT_DIR
check_variable RDB_VERSION
check_variable VERSION

JAVA="${JAVA_HOME}/bin/java"
GDS_TYPE="${GDS_TYPE}"
JDK_VERSION=`$JAVA -version 2>&1|head -n 1|awk -F\" '{split($2, v, ".");printf("%s%s", v[1], v[2])}'`
INSTALLDIR=/opt/RedDatabase
SOURCES=$(readlink -f $(dirname $0)/..)
OS=linux

TEST_DIR=/tmp/jaybird_test
TMPFS=/tmpfs
export FIREBIRD="$INSTALLDIR"
export LD_LIBRARY_PATH="$INSTALLDIR/lib"
export JAVA_HOME
ARCH=`arch`
if [ "$ARCH" == "i686" ]; then
	ARCH="x86"
fi

if [ -d $TMPFS ]; then
    echo Found $TMPFS. Will use it for databases
    TEST_DIR="$TMPFS"
else
	mkdir -p "$TEST_DIR"
fi

echo "Download fbt"
(git clone --depth 1 http://git.red-soft.biz/red-database/fbt-repository.git) || die "Unable to checkout tests" 

CPROCSP_ARCH=amd64
if [ "$ARCH" == "x86" ]; then
	CPROCSP_ARCH=ia32
fi

echo "Creating request and getting certificate for testing"

echo "Copying gamma to use CPSD random number generator"

cp fbt-repository/files/cert/cpsd_gamma/db1/kis_1 /var/opt/cprocsp/dsrf/db1/kis_1
cp fbt-repository/files/cert/cpsd_gamma/db2/kis_1 /var/opt/cprocsp/dsrf/db2/kis_1

echo "Configure CPSD"

/opt/cprocsp/sbin/amd64/cpconfig -hardware rndm -del BIO_TUI
/opt/cprocsp/sbin/amd64/cpconfig -hardware rndm -configure cpsd -add string fbt-repository/files/cert/cpsd_gamma/db1/kis_1 /var/opt/cprocsp/dsrf/db1/kis_1
/opt/cprocsp/sbin/amd64/cpconfig -hardware rndm -configure cpsd -add string fbt-repository/files/cert/cpsd_gamma/db2/kis_1 /var/opt/cprocsp/dsrf/db2/kis_1

echo "Creating certificate"

echo o | /opt/cprocsp/bin/amd64/cryptcp -creatcert -provtype 80 -ex -provname 'Crypto-Pro GOST R 34.10-2012 KC1 CSP' -dn 'CN="Test Test Test", INN=532117570513, SNILS=15278361414, E=test@red-soft.ru, O="RS"' -cont '\\.\HDIMAGE\TESTA' -certusage 1.3.6.1.5.5.7.3.2,1.3.6.1.5.5.7.3.4,1.2.643.100.113.1,1.2.643.100.2.1 -pin 12345678

echo "Getting serial number of certificate"

CERT_SERIAL=`/opt/cprocsp/bin/amd64/certmgr -list | grep Serial | awk -F\0x '{print $2}'`

echo "Exporting certificate to BASE64 file"

/opt/cprocsp/bin/amd64/certmgr -export -base64 -dest /tmp/testuser.cer
sed -i '1i-----BEGIN CERTIFICATE-----' /tmp/testuser.cer
echo '-----END CERTIFICATE-----' >> /tmp/testuser.cer

sed -i '/\[Parameters\]/a warning_time_gen_2001=ll:9223372036854775807\nwarning_time_sign_2001=ll:9223372036854775807\n' /etc/opt/cprocsp/config64.ini

echo Will use build $RDB_VERSION for testing

echo "Downloading RedDatabase $RDB_BUILD_ID"
if [[ "$RDB_MAJOR_VERSION" == "2" ]]; then
  RDB_URL=http://builds.red-soft.biz/release_hub/rdb26/${RDB_VERSION}/download/red-database:linux-${ARCH}:${RDB_VERSION}:bin:installer
  ARCHITECTURE=super
elif [[ "$RDB_MAJOR_VERSION" == "3" ]]; then
  RDB_URL=http://builds.red-soft.biz/release_hub/rdb30/${RDB_VERSION}/download/red-database:linux-${ARCH}-enterprise:${RDB_VERSION}:bin
elif [[ "$RDB_MAJOR_VERSION" == "4" ]]; then
  RDB_URL=http://builds.red-soft.biz/release_hub/rdb40/${RDB_VERSION}/download/red-database:linux-${ARCH}-enterprise:${RDB_VERSION}:bin
fi

(curl -s "$RDB_URL" -o /tmp/installer.bin && chmod +x /tmp/installer.bin) || die "Unable to download RedDatabase"

echo "Installing RedDatabase"
if [[ "$RDB_MAJOR_VERSION" == "2" ]]; then
  /tmp/installer.bin --DBAPasswd masterkey --mode unattended --architecture $ARCHITECTURE || die "Unable to install RedDatabase"
else
  /tmp/installer.bin --mode unattended --sysdba_password masterkey --debuglevel 4 || die "Unable to install RedDatabase"
fi
rm -f /tmp/installer.bin
chmod 777 $TEST_DIR

if [[ "$RDB_MAJOR_VERSION" == "4" ]]; then
  sed -i 's/#AuthServer = Srp256/AuthServer = Srp256, Srp, Legacy_Auth, Gss, GostPassword, Certificate, Policy/g' "${INSTALLDIR}"/firebird.conf
  sed -i 's/#AuthClient = Srp256, Srp, Legacy_Auth, Gss\s*#Non Windows clients/AuthClient = Srp256, Srp, Legacy_Auth, Gss, GostPassword, Certificate/g' "${INSTALLDIR}"/firebird.conf
  sed -i 's/#UserManager = Srp/UserManager = Srp, Legacy_UserManager, GostPassword_Manager /g' "${INSTALLDIR}"/firebird.conf
  sed -i 's/#WireCrypt = Enabled (for client) \/ Required (for server)/WireCrypt = Enabled/g' "${INSTALLDIR}"/firebird.conf
  sed -i 's/#WireCryptPlugin = ChaCha, Arc4/WireCryptPlugin = Wire_WinCrypt/g' "${INSTALLDIR}"/firebird.conf

  sed -i 's/#GssServerKeyfile/GssServerKeyfile/g' "${INSTALLDIR}"/firebird.conf
  sed -i 's/#GssServiceName/GssServiceName/g' "${INSTALLDIR}"/firebird.conf
  sed -i 's/#GssHostName =/GssHostName = localhost/g' "${INSTALLDIR}"/firebird.conf
  sed -i 's/#TraceAuthentication = 0/TraceAuthentication = 1/g' "${INSTALLDIR}"/firebird.conf

  sed -i 's/#ProviderName = 75/ProviderName = 80/g' "${INSTALLDIR}"/firebird.conf

  sed -i 's/#VerifyCertificateChain = 1/VerifyCertificateChain = 0/g' "${INSTALLDIR}"/firebird.conf
  sed -i 's/#CertUsernameDN = CN/CertUsernameDN = E/g' "${INSTALLDIR}"/firebird.conf
  sed -i 's/#ServerCertificate =/ServerCertificate = Test Test Test,CRYPTO-PRO Test Center 2,'"$CERT_SERIAL"'/g' "${INSTALLDIR}"/firebird.conf
  sed -i 's/#ServerPrivatePin =/ServerPrivatePin = 12345678/g' "${INSTALLDIR}"/firebird.conf
  sed -i 's/#TrustedCertificate =/TrustedCertificate = Test Test Test,CRYPTO-PRO Test Center 2,'"$CERT_SERIAL"'/g' "${INSTALLDIR}"/firebird.conf
  sed -i 's/#TrustedUser =/TrustedUser = trusted_user/g' "${INSTALLDIR}"/firebird.conf

"${INSTALLDIR}"/bin/isql -user SYSDBA -password masterkey "${INSTALLDIR}"/security4.fdb -i "${SOURCES}"/ci/user4.sql
elif [[ "$RDB_MAJOR_VERSION" == "3" ]]; then
  sed -i 's/#AuthServer = Srp/AuthServer = Multifactor, Srp, Srp256, Legacy_Auth, Gss/g' "${INSTALLDIR}"/firebird.conf
  sed -i 's/#AuthClient = Srp, Srp256, Legacy_Auth, Gss, Multifactor, ExtAuth\s*#Non Windows clients/AuthClient = Multifactor, Srp, Srp256, Legacy_Auth, Gss, ExtAuth/g' "${INSTALLDIR}"/firebird.conf
  sed -i 's/#UserManager = Srp/UserManager = Srp, Legacy_UserManager, Multifactor_Manager /g' "${INSTALLDIR}"/firebird.conf
  sed -i 's/#WireCrypt = Enabled (for client) \/ Required (for server)/WireCrypt = Enabled/g' "${INSTALLDIR}"/firebird.conf
  sed -i 's/#WireCryptPlugin = Arc4/WireCryptPlugin = Wire_WinCrypt/g' "${INSTALLDIR}"/firebird.conf

  sed -i 's/#KrbServerKeyfile/KrbServerKeyfile/g' "${INSTALLDIR}"/firebird.conf
  sed -i 's/#GssServiceName/GssServiceName/g' "${INSTALLDIR}"/firebird.conf
  sed -i 's/#GssHostName =/GssHostName = localhost/g' "${INSTALLDIR}"/firebird.conf

  sed -i 's/#ProviderName = 75/ProviderName = 80/g' "${INSTALLDIR}"/firebird.conf

  sed -i 's/#CertVerifyChain = 1/CertVerifyChain = 0/g' "${INSTALLDIR}"/firebird.conf
  sed -i 's/#CertUsernameDN = CN/CertUsernameDN = E/g' "${INSTALLDIR}"/firebird.conf
  sed -i 's/#ServerCertificate =/ServerCertificate = Test Test Test,CRYPTO-PRO Test Center 2,'"$CERT_SERIAL"'/g' "${INSTALLDIR}"/firebird.conf
  sed -i 's/#ServerPrivatePin =/ServerPrivatePin = 12345678/g' "${INSTALLDIR}"/firebird.conf
  sed -i 's/#TrustedCertificate =/TrustedCertificate = Test Test Test,CRYPTO-PRO Test Center 2,'"$CERT_SERIAL"'/g' "${INSTALLDIR}"/firebird.conf
  sed -i 's/#TrustedUser =/TrustedUser = trusted_user/g' "${INSTALLDIR}"/firebird.conf

  sed -i 's/#TraceAuthentication = 0/TraceAuthentication = 1/g' "${INSTALLDIR}"/firebird.conf

  sed -i 's/#GSSLibrary = libgssapi_krb5.so/GSSLibrary = \/usr\/lib64\/libgssapi_krb5.so.2/g' "${INSTALLDIR}"/firebird.conf

  "${INSTALLDIR}"/bin/isql -user SYSDBA -password masterkey "${INSTALLDIR}"/security3.fdb -i "${SOURCES}"/ci/user3.sql
else
  sed -i 's/#VerifyCertChain = 1/VerifyCertChain = 0/g' "${INSTALLDIR}/firebird.conf"
  sed -i 's/#CertUsernameDN = CN/CertUsernameDN = E/g' "${INSTALLDIR}/firebird.conf"
  sed -i 's/#ServerCertificate =/ServerCertificate = Test Test Test,CRYPTO-PRO Test Center 2,'"$CERT_SERIAL"'/g' "${INSTALLDIR}"/firebird.conf
  sed -i 's/#PrivateKeyPin = /PrivateKeyPin = 12345678/g' "${INSTALLDIR}/firebird.conf"
  sed -i 's/#TrustedCertificate =/TrustedCertificate = Test Test Test,CRYPTO-PRO Test Center 2,'"$CERT_SERIAL"'/g' "${INSTALLDIR}"/firebird.conf
  sed -i 's/#TraceAuthentication = 0/TraceAuthentication = 1/g' "${INSTALLDIR}/firebird.conf"

  sed -i 's/#ProviderName = 75/ProviderName = 80/g' "${INSTALLDIR}"/firebird.conf

fi

# Delete symlink for fbclient to test fbclient.jar
if [[ "$GDS_TYPE" != "EMBEDDED" && "$GDS_TYPE" != "FBOOEMBEDDED" ]]; then
  rm -f /usr/lib64/libfbclient.so
fi

echo "Start RDB..."

if [[ "$RDB_MAJOR_VERSION" == "2" ]]; then
  /etc/init.d/firebird restart
else
  "$INSTALLDIR"/bin/rdbguard -daemon -forever
fi

(nc -h 2>&1|grep -q 'Zero-I/O mode') && NC="nc -z" || NC="nc --send-only"

echo "Waiting until port 3050 opened..."
try=10
while ! $NC localhost 3050 </dev/null; do
    sleep 5
    try=$((try-1))
    if [ $try = 0 ]; then
        die "Unable to connect to RDB..."
    fi
done

if [[ "$RDB_MAJOR_VERSION" == "2" ]]; then
  "$INSTALLDIR/bin/gsec" -user SYSDBA -password masterkey -add TEST@RED-SOFT.RU -pw q3rgu7Ah
  "$INSTALLDIR/bin/gsec" -user SYSDBA -password masterkey -add trusted_user -pw trusted
  "$INSTALLDIR/bin/gsec" -user SYSDBA -password masterkey -add UserWithGostPassword -pw password
fi

echo rdb_server | kinit rdb_server/localhost
klist

if [[ "$GDS_TYPE" == "" ]]; then
  GDS_TYPE="PURE_JAVA"
fi

mvn $MAVEN_CLI_OPTS -f "${CI_PROJECT_DIR}"/pom.xml test -Pdeploy-internal -DreportNamePrefix=$REPORT_PREFIX -DreleaseHubBuildVersion=$VERSION  -DfailIfNoTests=false -Dtest.db.dir=$TEST_DIR -Dtest.java7.skip=$SKIP_JAVA7_TEST -Dtest.java8.jvm=$TEST_JAVA8_JVM -Dtest.java7.jvm=$TEST_JAVA7_JVM -Dtest=$TEST_LIST -Dtest.gds_type=$GDS_TYPE
kdestroy
