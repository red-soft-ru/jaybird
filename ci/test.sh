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
INSTALLDIR=/opt/RedDatabase
SOURCES=$(readlink -f $(dirname $0)/..)
OS=linux

TEST_DIR=/tmp/jaybird_test
TMPFS=/tmpfs
export FIREBIRD="$INSTALLDIR"
export LD_LIBRARY_PATH="$INSTALLDIR/lib"
export LD_PRELOAD="$INSTALLDIR/lib/libjsig.so"
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

echo "Do not generate CryptoPRO certificates in test stage"

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

sed -i '/\[Parameters\]/a warning_time_gen_2001=ll:9223372036854775807\nwarning_time_sign_2001=ll:9223372036854775807\n' /etc/opt/cprocsp/config64.ini

echo Will use build $RDB_VERSION for testing

echo "Downloading RedDatabase $RDB_BUILD_ID"
if [[ "$RDB_MAJOR_VERSION" == "3" ]]; then
  RDB_URL=http://builds.red-soft.biz/release_hub/rdb30/${RDB_VERSION}/download/red-database:linux-${ARCH}-enterprise:${RDB_VERSION}:bin
elif [[ "$RDB_MAJOR_VERSION" == "5" ]]; then
  RDB_URL=http://builds.red-soft.biz/release_hub/rdb50/${RDB_VERSION}/download/red-database:linux-${ARCH}-enterprise:${RDB_VERSION}:bin
elif [[ "$RDB_MAJOR_VERSION" == "6" ]]; then
  RDB_URL=http://builds.red-soft.biz/release_hub/rdb60/${RDB_VERSION}/download/red-database:linux-${ARCH}-enterprise:${RDB_VERSION}:bin
fi

(curl -s "$RDB_URL" -o /tmp/installer.bin && chmod +x /tmp/installer.bin) || die "Unable to download RedDatabase"

echo "Installing RedDatabase"
/tmp/installer.bin --mode unattended --sysdba_password masterkey --debuglevel 4 || die "Unable to install RedDatabase"

rm -f /tmp/installer.bin
chmod 777 $TEST_DIR

if [[ "$RDB_MAJOR_VERSION" == "6" ]]; then
  sed -i 's/#AuthServer = Srp256/AuthServer = Srp256, Srp224, Srp384, Srp512, Srp, Legacy_Auth, Gss, GostPassword, Certificate, Policy/g' "${INSTALLDIR}"/rdbserver.conf
  sed -i 's/#AuthClient = Srp256, Srp, Legacy_Auth, GostPassword, Certificate, Gss\s*#Non Windows clients/AuthClient = Srp256, Srp224, Srp384, Srp512, Srp, Legacy_Auth, GostPassword, Certificate, Gss/g' "${INSTALLDIR}"/rdbserver.conf
  sed -i 's/#UserManager = Srp/UserManager = Srp, Legacy_UserManager, GostPassword_Manager /g' "${INSTALLDIR}"/rdbserver.conf
  sed -i 's/#WireCrypt = Enabled (for client) \/ Required (for server)/WireCrypt = Enabled/g' "${INSTALLDIR}"/rdbserver.conf
  sed -i 's/#WireCryptPlugin = ChaCha64, ChaCha, Arc4/WireCryptPlugin = ChaCha64, ChaCha, Arc4, Wire_WinCrypt/g' "${INSTALLDIR}"/rdbserver.conf
  sed -i 's/#GssServerKeyfile/GssServerKeyfile/g' "${INSTALLDIR}"/rdbserver.conf
  sed -i 's/#GssServiceName/GssServiceName/g' "${INSTALLDIR}"/rdbserver.conf
  sed -i 's/#GssHostName =/GssHostName = localhost/g' "${INSTALLDIR}"/rdbserver.conf
  sed -i 's/#TraceAuthentication = 0/TraceAuthentication = 1/g' "${INSTALLDIR}"/rdbserver.conf
  sed -i 's/#ProviderName = 75/ProviderName = 80/g' "${INSTALLDIR}"/rdbserver.conf
  sed -i 's/#VerifyCertificateChain = 1/VerifyCertificateChain = 0/g' "${INSTALLDIR}"/rdbserver.conf
  sed -i 's/#CertUsernameDN = CN/CertUsernameDN = E/g' "${INSTALLDIR}"/rdbserver.conf
  sed -i 's/#ServerCertificate =/ServerCertificate = Test Test Test,CRYPTO-PRO Test Center 2,'"$CERT_SERIAL"'/g' "${INSTALLDIR}"/rdbserver.conf
  sed -i 's/#ServerPrivatePin =/ServerPrivatePin = 12345678/g' "${INSTALLDIR}"/rdbserver.conf
  sed -i 's/#TrustedCertificate =/TrustedCertificate = Test Test Test,CRYPTO-PRO Test Center 2,'"$CERT_SERIAL"'/g' "${INSTALLDIR}"/rdbserver.conf
  sed -i 's/#TrustedUser =/TrustedUser = trusted_user/g' "${INSTALLDIR}"/rdbserver.conf
  sed -i 's/#TraceAuthentication = 0/TraceAuthentication = 1/g' "${INSTALLDIR}"/rdbserver.conf
  sed -i 's/#MaxParallelWorkers = 1/MaxParallelWorkers = 8/g' "${INSTALLDIR}"/rdbserver.conf

 "${INSTALLDIR}"/bin/isql -user SYSDBA -password masterkey "${INSTALLDIR}"/security6.fdb -i "${SOURCES}"/ci/user4.sql

elif [[ "$RDB_MAJOR_VERSION" == "5" ]]; then
  sed -i 's/#AuthServer = Srp256/AuthServer = Srp256, Srp224, Srp384, Srp512, Srp, Legacy_Auth, Gss, GostPassword, Certificate, Policy/g' "${INSTALLDIR}"/firebird.conf
  sed -i 's/#AuthClient = Srp256, Srp, Legacy_Auth, GostPassword, Certificate, Gss\s*#Non Windows clients/AuthClient = Srp256, Srp224, Srp384, Srp512, Srp, Legacy_Auth, GostPassword, Certificate, Gss/g' "${INSTALLDIR}"/firebird.conf
  sed -i 's/#UserManager = Srp/UserManager = Srp, Legacy_UserManager, GostPassword_Manager /g' "${INSTALLDIR}"/firebird.conf
  sed -i 's/#WireCrypt = Enabled (for client) \/ Required (for server)/WireCrypt = Enabled/g' "${INSTALLDIR}"/firebird.conf
  sed -i 's/#WireCryptPlugin = ChaCha64, ChaCha, Arc4/WireCryptPlugin = ChaCha64, ChaCha, Arc4, Wire_WinCrypt/g' "${INSTALLDIR}"/firebird.conf
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
  sed -i 's/#TraceAuthentication = 0/TraceAuthentication = 1/g' "${INSTALLDIR}"/firebird.conf
  sed -i 's/#MaxParallelWorkers = 1/MaxParallelWorkers = 8/g' "${INSTALLDIR}"/firebird.conf

 "${INSTALLDIR}"/bin/isql -user SYSDBA -password masterkey "${INSTALLDIR}"/security5.fdb -i "${SOURCES}"/ci/user4.sql

elif [[ "$RDB_MAJOR_VERSION" == "3" ]]; then
  sed -i 's/#AuthServer = Srp/AuthServer = Multifactor, Srp, Srp256, Srp224, Srp384, Srp512, Legacy_Auth, Gss/g' "${INSTALLDIR}"/firebird.conf
  sed -i 's/#AuthClient = Srp, Srp256, Legacy_Auth, Gss, Multifactor, ExtAuth\s*#Non Windows clients/AuthClient = Multifactor, Srp, Srp256, Srp224, Srp384, Srp512, Legacy_Auth, Gss, ExtAuth/g' "${INSTALLDIR}"/firebird.conf
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
fi

if [[ "$GDS_TYPE" == "" ]]; then
  GDS_TYPE="PURE_JAVA"
fi

if [[ "$GDS_TYPE" != "EMBEDDED" && "$GDS_TYPE" != "FBOOEMBEDDED" ]]; then

  echo "Start RDB..."

  "$INSTALLDIR"/bin/rdbguard -daemon -forever

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

fi

echo rdb_server | kinit rdb_server/localhost
klist

mvn $MAVEN_CLI_OPTS -f "${CI_PROJECT_DIR}"/pom.xml test -Pdeploy-internal -DreportNamePrefix=$REPORT_PREFIX -DreleaseHubBuildVersion=$VERSION  -DfailIfNoTests=false -Dtest.db.dir=$TEST_DIR -Dtest.java.jvm=$TEST_JAVA_JVM -Dtest.gds_type=$GDS_TYPE -Dorg.firebirdsql.nativeResourceShutdownDisabled=true
kdestroy
