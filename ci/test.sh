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
JDK_VERSION=`$JAVA -version 2>&1|head -n 1|awk -F\" '{split($2, v, ".");printf("%s%s", v[1], v[2])}'`
if [[ -z "${INSTALLDIR}" ]]; then
  INSTALLDIR=/opt/RedDatabase
fi
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

echo "Get crypto pro keys from minio storage"

mcli alias set myminio ${MINIO_SERVER} ${MINIO_USER} ${MINIO_PASSWORD}

ARCHIVE_PREFIX=cryptopro-keys

mcli cp myminio/ci-keys/$ARCHIVE_PREFIX.tar.gz .ci
tar xf .ci/cryptopro-keys.tar.gz -C .ci
CERT_SERIAL=$(mcli cat myminio/ci-keys/cert_serial)

KEYS_DIR=/var/opt/cprocsp/keys
mkdir -p $KEYS_DIR/root
chmod 700 $KEYS_DIR/root
cp $CI_PROJECT_DIR/.ci/TestKeys.000/ $KEYS_DIR/root -rfv
cp $CI_PROJECT_DIR/.ci/testuser.cer /tmp
chmod 700 $KEYS_DIR/root/TestKeys.000 -R
/opt/cprocsp/bin/$CPROCSP_ARCH/certmgr -inst -file "$CI_PROJECT_DIR/.ci/testuser.cer" -cont '\\.\HDIMAGE\TestKeys' -silent
/opt/cprocsp/bin/$CPROCSP_ARCH/csptest -keyset -enum_cont -fqcn -verifyc
/opt/cprocsp/bin/$CPROCSP_ARCH/csptest -passwd -cont '\\.\HDIMAGE\TestKeys' -change 12345678

echo Will use build $RDB_VERSION for testing

echo "Downloading RedDatabase $RDB_BUILD_ID"
if [[ "$RDB_MAJOR_VERSION" == "3" ]]; then
  RDB_URL=http://builds.red-soft.biz/release_hub/rdb30/${RDB_VERSION}/download/red-database:linux-${ARCH}-enterprise:${RDB_VERSION}:bin
fi

if [[ "$RDB_MAJOR_VERSION" == "FB3.0.7" ]]; then
  FB_URL=https://github.com/FirebirdSQL/firebird/releases/download/R3_0_7/Firebird-3.0.7.33374-0.amd64.tar.gz
  (curl -LJO "$FB_URL") || die "Unable to download Firebird 3.0.7"
else
  (curl -s "$RDB_URL" -o /tmp/installer.bin && chmod +x /tmp/installer.bin) || die "Unable to download RedDatabase"
fi

echo "Installing RedDatabase"
if [[ "$RDB_MAJOR_VERSION" == "2" ]]; then
  /tmp/installer.bin --DBAPasswd masterkey --mode unattended --architecture $ARCHITECTURE || die "Unable to install RedDatabase"
elif [[ "$RDB_MAJOR_VERSION" == "FB3.0.7" ]]; then
  tar xf Firebird-3.0.7.33374-0.amd64.tar.gz
  cd Firebird-3.0.7.33374-0.amd64
  ./install.sh -silent
  cd ..
else
  /tmp/installer.bin --mode unattended --sysdba_password masterkey --debuglevel 4 || die "Unable to install RedDatabase"
fi
rm -f /tmp/installer.bin
chmod 777 $TEST_DIR

if [[ "$RDB_MAJOR_VERSION" == "3" ]]; then
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

  sed -i 's/#MaxParallelWorkers = 1/MaxParallelWorkers = 8/g' "${INSTALLDIR}"/firebird.conf

  "${INSTALLDIR}"/bin/isql -user SYSDBA -password masterkey "${INSTALLDIR}"/security3.fdb -i "${SOURCES}"/ci/user3.sql
elif [[ "$RDB_MAJOR_VERSION" == "FB3.0.7" ]]; then
  sed -i 's/#AuthServer = Srp/AuthServer = Srp, Srp256, Legacy_Auth/g' "${INSTALLDIR}"/firebird.conf
  sed -i 's/#AuthClient = Srp, Srp256, Legacy_Auth\s*#Non Windows clients/AuthClient = Srp, Srp256, Legacy_Auth/g' "${INSTALLDIR}"/firebird.conf
  sed -i 's/#UserManager = Srp/UserManager = Srp, Legacy_UserManager/g' "${INSTALLDIR}"/firebird.conf
  sed -i 's/#WireCrypt = Enabled (for client) \/ Required (for server)/WireCrypt = Disabled/g' "${INSTALLDIR}"/firebird.conf

fi

echo "Start RDB..."

if [[ "$RDB_MAJOR_VERSION" == "FB3.0.7" ]]; then
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

if [[ "$RDB_MAJOR_VERSION" == "FB3.0.7" ]]; then
  "$INSTALLDIR/bin/gsec" -modify SYSDBA -password masterkey -user SYSDBA
fi

echo rdb_server | kinit rdb_server/localhost
klist

mvn $MAVEN_CLI_OPTS -f "${CI_PROJECT_DIR}"/pom.xml test -Pdeploy-internal -Pcryptoapi -Pcryptoapi-security -DreportNamePrefix=$REPORT_PREFIX -DreleaseHubBuildVersion=$VERSION  -DfailIfNoTests=false -Dtest.db.dir=$TEST_DIR -Dtest.java8.jvm=$TEST_JAVA8_JVM -Dtest=$TEST_LIST

kdestroy
