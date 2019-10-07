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

check_variable BINDIR
check_variable SRCDIR
check_variable JAYBIRD_VERSION
check_variable JAVA_HOME
check_variable CI_PROJECT_DIR

JAVA="${JAVA_HOME}/bin/java"
JDK_VERSION=`$JAVA -version 2>&1|head -n 1|awk -F\" '{split($2, v, ".");printf("%s%s", v[1], v[2])}'`
REPORTS_DIR="${CI_PROJECT_DIR}/results/jdk${JDK_VERSION}_rdb4"
INSTALLDIR=/opt/RedDatabase
SOURCES=$(readlink -f $(dirname $0)/..)
OS=linux
RDB_VERSION=${RDB_VERSION}
RDB_MAJOR_VERSION="4"
if [[ "${RDB_VERSION:0:1}" -eq "3" ]]; then
  RDB_MAJOR_VERSION="3"
  REPORTS_DIR="${CI_PROJECT_DIR}/results/jdk${JDK_VERSION}_rdb3"
elif [[ "${RDB_VERSION:0:1}" -eq "2" ]]; then
  RDB_MAJOR_VERSION="2"
  REPORTS_DIR="${CI_PROJECT_DIR}/results/jdk${JDK_VERSION}_rdb2_6"
elif [[ "${RDB_VERSION:0:7}" == "FB3.0.4" ]]; then
  RDB_MAJOR_VERSION="FB3.0.4"
  REPORTS_DIR="${CI_PROJECT_DIR}/results/jdk${JDK_VERSION}_fb3"
  INSTALLDIR=/opt/firebird
fi
TEST_DIR=/tmp/jaybird_test
TMPFS=/tmpfs
export FIREBIRD="$INSTALLDIR"
export LD_LIBRARY_PATH="$INSTALLDIR/lib"
export JAVA_HOME
ARCH=`arch`
if [ "$ARCH" == "i686" ]; then
	ARCH="x86"
fi

mkdir -p "${REPORTS_DIR}"

if [ -d $TMPFS ]; then
    echo Found $TMPFS. Will use it for databases
    TEST_DIR="$TMPFS"
else
	mkdir -p "$TEST_DIR"
fi

RDB_URL=http://artifactory.red-soft.biz/list/red-database/red-database/linux-${ARCH}/${RDB_VERSION}/linux-${ARCH}-${RDB_VERSION}.bin

echo "Download fbt"
(git clone --depth 1 http://git.red-soft.biz/red-database/fbt-repository.git) || die "Unable to checkout tests" 

CPROCSP_ARCH=amd64
if [ "$ARCH" == "x86" ]; then
	CPROCSP_ARCH=ia32
fi

useradd firebird

KEYS_DIR=/var/opt/cprocsp/keys
mkdir -p $KEYS_DIR/root
chmod 700 $KEYS_DIR/root
cp fbt-repository/files/cert/RaUser-d.000/ $KEYS_DIR/root -rfv
chmod 700 $KEYS_DIR/root/RaUser-d.000

/opt/cprocsp/bin/$CPROCSP_ARCH/certmgr -inst -cont '\\.\HDIMAGE\c6bb7811-a370-4de7-91fb-536a1b8b4017'
/opt/cprocsp/bin/$CPROCSP_ARCH/csptest -passwd -cont '\\.\HDIMAGE\c6bb7811-a370-4de7-91fb-536a1b8b4017' -change 12345678

mkdir -p $KEYS_DIR/firebird
chmod 700 $KEYS_DIR/firebird
cp fbt-repository/files/cert/RaUser-d.000/ $KEYS_DIR/firebird -rfv
chmod 700 $KEYS_DIR/firebird/RaUser-d.000
chown firebird:firebird -R $KEYS_DIR/firebird

sudo -u firebird /opt/cprocsp/bin/$CPROCSP_ARCH/certmgr -inst -cont '\\.\HDIMAGE\c6bb7811-a370-4de7-91fb-536a1b8b4017'
sudo -u firebird /opt/cprocsp/bin/$CPROCSP_ARCH/csptest -passwd -cont '\\.\HDIMAGE\c6bb7811-a370-4de7-91fb-536a1b8b4017' -change 12345678

cp fbt-repository/files/cert/Smirnov.cer ./testuser.cer

sed -i '/\[Parameters\]/a warning_time_gen_2001=ll:9223372036854775807\nwarning_time_sign_2001=ll:9223372036854775807\n' /etc/opt/cprocsp/config64.ini

echo Will use build $RDB_VERSION for testing

echo "Downloading RedDatabase $RDB_BUILD_ID"
if [[ "$RDB_MAJOR_VERSION" == "2" ]]; then
  RDB_URL=http://builds.red-soft.biz/release_hub/rdb26/${RDB_VERSION}/download/red-database:linux-${ARCH}:${RDB_VERSION}:bin:installer
  ARCHITECTURE=super
elif [[ "$RDB_MAJOR_VERSION" == "3" ]]; then
  RDB_URL=http://builds.red-soft.biz/release_hub/rdb30/${RDB_VERSION}/download/red-database:linux-${ARCH}-enterprise:${RDB_VERSION}:bin
fi

if [[ "$RDB_MAJOR_VERSION" == "FB3.0.4" ]]; then
  FB_URL=http://github.com/FirebirdSQL/firebird/releases/download/R3_0_4/Firebird-3.0.4.33054-0.amd64.tar.gz
  (curl -LJO "$FB_URL") || die "Unable to download Firebird 3.0.4"
else
  (curl -s "$RDB_URL" -o /tmp/installer.bin && chmod +x /tmp/installer.bin) || die "Unable to download RedDatabase"
fi

echo "Installing RedDatabase"
if [[ "$RDB_MAJOR_VERSION" == "2" ]]; then
  /tmp/installer.bin --DBAPasswd masterkey --mode unattended --architecture $ARCHITECTURE || die "Unable to install RedDatabase"
elif [[ "$RDB_MAJOR_VERSION" == "FB3.0.4" ]]; then
  tar xf Firebird-3.0.4.33054-0.amd64.tar.gz
  cd Firebird-3.0.4.33054-0.amd64
  ./install.sh -silent
  cd ..
else
  /tmp/installer.bin --mode unattended --sysdba_password masterkey --debuglevel 4 || die "Unable to install RedDatabase"
fi
rm -f /tmp/installer.bin
chmod 777 $TEST_DIR

if [[ "$RDB_MAJOR_VERSION" == "4" ]]; then
  sed -i 's/#AuthServer = Srp256/AuthServer = Srp256, Srp, Legacy_Auth, Gss, GostPassword, Certificate, Policy/g' "${INSTALLDIR}"/firebird.conf
  sed -i 's/#AuthClient = Srp256, Srp, Legacy_Auth, Gss\s*#Non Windows clients/AuthClient = Srp256, Srp, Legacy_Auth, Gss, GostPassword, Certificate/g' "${INSTALLDIR}"/firebird.conf
  sed -i 's/#UserManager = Srp/UserManager = Srp, Legacy_UserManager, GostPassword_Manager /g' "${INSTALLDIR}"/firebird.conf
  sed -i 's/#WireCrypt = Enabled (for client) \/ Required (for server)/WireCrypt = Disabled/g' "${INSTALLDIR}"/firebird.conf

  sed -i 's/#KrbServerKeyfile/KrbServerKeyfile/g' "${INSTALLDIR}"/firebird.conf
  sed -i 's/#GssServiceName/GssServiceName/g' "${INSTALLDIR}"/firebird.conf
  sed -i 's/#GssHostName =/GssHostName = localhost/g' "${INSTALLDIR}"/firebird.conf
  sed -i 's/#TraceAuthentication = 0/TraceAuthentication = 1/g' "${INSTALLDIR}"/firebird.conf

  sed -i 's/#VerifyCertificateChain = 1/VerifyCertificateChain = 0/g' "${INSTALLDIR}"/firebird.conf
  sed -i 's/#CertUsernameDN = CN/CertUsernameDN = E/g' "${INSTALLDIR}"/firebird.conf
  sed -i 's/#ServerCertificate =/ServerCertificate = %D0%A1%D0%BC%D0%B8%D1%80%D0%BD%D0%BE%D0%B2%20%D0%90%D1%80%D1%82%D0%B5%D0%BC%20%D0%92%D1%8F%D1%87%D0%B5%D1%81%D0%BB%D0%B0%D0%B2%D0%BE%D0%B2%D0%B8%D1%87,%D0%A4%D0%B5%D0%B4%D0%B5%D1%80%D0%B0%D0%BB%D1%8C%D0%BD%D0%B0%D1%8F%20%D1%81%D0%BB%D1%83%D0%B6%D0%B1%D0%B0%20%D1%81%D1%83%D0%B4%D0%B5%D0%B1%D0%BD%D1%8B%D1%85%20%D0%BF%D1%80%D0%B8%D1%81%D1%82%D0%B0%D0%B2%D0%BE%D0%B2,071085DA7AC40C79ABE811F872541896CB/g' "${INSTALLDIR}"/firebird.conf
  sed -i 's/#ServerPrivatePin =/ServerPrivatePin = 12345678/g' "${INSTALLDIR}"/firebird.conf
  sed -i 's/#TrustedCertificate =/TrustedCertificate = %D0%A1%D0%BC%D0%B8%D1%80%D0%BD%D0%BE%D0%B2%20%D0%90%D1%80%D1%82%D0%B5%D0%BC%20%D0%92%D1%8F%D1%87%D0%B5%D1%81%D0%BB%D0%B0%D0%B2%D0%BE%D0%B2%D0%B8%D1%87,%D0%A4%D0%B5%D0%B4%D0%B5%D1%80%D0%B0%D0%BB%D1%8C%D0%BD%D0%B0%D1%8F%20%D1%81%D0%BB%D1%83%D0%B6%D0%B1%D0%B0%20%D1%81%D1%83%D0%B4%D0%B5%D0%B1%D0%BD%D1%8B%D1%85%20%D0%BF%D1%80%D0%B8%D1%81%D1%82%D0%B0%D0%B2%D0%BE%D0%B2,071085DA7AC40C79ABE811F872541896CB/g' "${INSTALLDIR}"/firebird.conf
  sed -i 's/#TrustedUser =/TrustedUser = trusted_user/g' "${INSTALLDIR}"/firebird.conf

"${INSTALLDIR}"/bin/isql -user SYSDBA -password masterkey "${INSTALLDIR}"/security4.fdb -i "${SOURCES}"/ci/user4.sql
elif [[ "$RDB_MAJOR_VERSION" == "3" ]]; then
  sed -i 's/#AuthServer = Srp/AuthServer = Multifactor, Srp, Srp256, Legacy_Auth, Gss/g' "${INSTALLDIR}"/firebird.conf
  sed -i 's/#AuthClient = Srp, Srp256, Legacy_Auth, Gss, Multifactor\s*#Non Windows clients/AuthClient = Multifactor, Srp, Srp256, Legacy_Auth, Gss/g' "${INSTALLDIR}"/firebird.conf
  sed -i 's/#UserManager = Srp/UserManager = Srp, Legacy_UserManager, Multifactor_Manager /g' "${INSTALLDIR}"/firebird.conf
  sed -i 's/#WireCrypt = Enabled (for client) \/ Required (for server)/WireCrypt = Disabled/g' "${INSTALLDIR}"/firebird.conf

  sed -i 's/#KrbServerKeyfile/KrbServerKeyfile/g' "${INSTALLDIR}"/firebird.conf
  sed -i 's/#GssServiceName/GssServiceName/g' "${INSTALLDIR}"/firebird.conf
  sed -i 's/#GssHostName =/GssHostName = localhost/g' "${INSTALLDIR}"/firebird.conf

  sed -i 's/#CertVerifyChain = 1/CertVerifyChain = 0/g' "${INSTALLDIR}"/firebird.conf
  sed -i 's/#CertUsernameDN = CN/CertUsernameDN = E/g' "${INSTALLDIR}"/firebird.conf
  sed -i 's/#ServerCertificate =/ServerCertificate = %D0%A1%D0%BC%D0%B8%D1%80%D0%BD%D0%BE%D0%B2%20%D0%90%D1%80%D1%82%D0%B5%D0%BC%20%D0%92%D1%8F%D1%87%D0%B5%D1%81%D0%BB%D0%B0%D0%B2%D0%BE%D0%B2%D0%B8%D1%87,%D0%A4%D0%B5%D0%B4%D0%B5%D1%80%D0%B0%D0%BB%D1%8C%D0%BD%D0%B0%D1%8F%20%D1%81%D0%BB%D1%83%D0%B6%D0%B1%D0%B0%20%D1%81%D1%83%D0%B4%D0%B5%D0%B1%D0%BD%D1%8B%D1%85%20%D0%BF%D1%80%D0%B8%D1%81%D1%82%D0%B0%D0%B2%D0%BE%D0%B2,071085DA7AC40C79ABE811F872541896CB/g' "${INSTALLDIR}"/firebird.conf
  sed -i 's/#ServerPrivatePin =/ServerPrivatePin = 12345678/g' "${INSTALLDIR}"/firebird.conf
  sed -i 's/#TrustedCertificate =/TrustedCertificate = %D0%A1%D0%BC%D0%B8%D1%80%D0%BD%D0%BE%D0%B2%20%D0%90%D1%80%D1%82%D0%B5%D0%BC%20%D0%92%D1%8F%D1%87%D0%B5%D1%81%D0%BB%D0%B0%D0%B2%D0%BE%D0%B2%D0%B8%D1%87,%D0%A4%D0%B5%D0%B4%D0%B5%D1%80%D0%B0%D0%BB%D1%8C%D0%BD%D0%B0%D1%8F%20%D1%81%D0%BB%D1%83%D0%B6%D0%B1%D0%B0%20%D1%81%D1%83%D0%B4%D0%B5%D0%B1%D0%BD%D1%8B%D1%85%20%D0%BF%D1%80%D0%B8%D1%81%D1%82%D0%B0%D0%B2%D0%BE%D0%B2,071085DA7AC40C79ABE811F872541896CB/g' "${INSTALLDIR}"/firebird.conf
  sed -i 's/#TrustedUser =/TrustedUser = trusted_user/g' "${INSTALLDIR}"/firebird.conf

  sed -i 's/#TraceAuthentication = 0/TraceAuthentication = 1/g' "${INSTALLDIR}"/firebird.conf

  sed -i 's/#GSSLibrary = libgssapi_krb5.so/GSSLibrary = \/usr\/lib64\/libgssapi_krb5.so.2/g' "${INSTALLDIR}"/firebird.conf

  "${INSTALLDIR}"/bin/isql -user SYSDBA -password masterkey "${INSTALLDIR}"/security3.fdb -i "${SOURCES}"/ci/user3.sql
elif [[ "$RDB_MAJOR_VERSION" == "FB3.0.4" ]]; then
  sed -i 's/#AuthServer = Srp/AuthServer = Srp, Srp256, Legacy_Auth/g' "${INSTALLDIR}"/firebird.conf
  sed -i 's/#AuthClient = Srp, Srp256, Legacy_Auth\s*#Non Windows clients/AuthClient = Srp, Srp256, Legacy_Auth/g' "${INSTALLDIR}"/firebird.conf
  sed -i 's/#UserManager = Srp/UserManager = Srp, Legacy_UserManager/g' "${INSTALLDIR}"/firebird.conf
  sed -i 's/#WireCrypt = Enabled (for client) \/ Required (for server)/WireCrypt = Disabled/g' "${INSTALLDIR}"/firebird.conf

else
  sed -i 's/#VerifyCertChain = 1/VerifyCertChain = 0/g' "${INSTALLDIR}/firebird.conf"
  sed -i 's/#CertUsernameDN = CN/CertUsernameDN = E/g' "${INSTALLDIR}/firebird.conf"
  sed -i 's/#ServerCertificate =/ServerCertificate = %D0%A1%D0%BC%D0%B8%D1%80%D0%BD%D0%BE%D0%B2%20%D0%90%D1%80%D1%82%D0%B5%D0%BC%20%D0%92%D1%8F%D1%87%D0%B5%D1%81%D0%BB%D0%B0%D0%B2%D0%BE%D0%B2%D0%B8%D1%87,%D0%A4%D0%B5%D0%B4%D0%B5%D1%80%D0%B0%D0%BB%D1%8C%D0%BD%D0%B0%D1%8F%20%D1%81%D0%BB%D1%83%D0%B6%D0%B1%D0%B0%20%D1%81%D1%83%D0%B4%D0%B5%D0%B1%D0%BD%D1%8B%D1%85%20%D0%BF%D1%80%D0%B8%D1%81%D1%82%D0%B0%D0%B2%D0%BE%D0%B2,071085DA7AC40C79ABE811F872541896CB/g' "${INSTALLDIR}/firebird.conf"
  sed -i 's/#PrivateKeyPin = /PrivateKeyPin = 12345678/g' "${INSTALLDIR}/firebird.conf"
  sed -i 's/#TrustedCertificate =/TrustedCertificate = %D0%A1%D0%BC%D0%B8%D1%80%D0%BD%D0%BE%D0%B2%20%D0%90%D1%80%D1%82%D0%B5%D0%BC%20%D0%92%D1%8F%D1%87%D0%B5%D1%81%D0%BB%D0%B0%D0%B2%D0%BE%D0%B2%D0%B8%D1%87,%D0%A4%D0%B5%D0%B4%D0%B5%D1%80%D0%B0%D0%BB%D1%8C%D0%BD%D0%B0%D1%8F%20%D1%81%D0%BB%D1%83%D0%B6%D0%B1%D0%B0%20%D1%81%D1%83%D0%B4%D0%B5%D0%B1%D0%BD%D1%8B%D1%85%20%D0%BF%D1%80%D0%B8%D1%81%D1%82%D0%B0%D0%B2%D0%BE%D0%B2,071085DA7AC40C79ABE811F872541896CB/g' "${INSTALLDIR}/firebird.conf"
  sed -i 's/#TraceAuthentication = 0/TraceAuthentication = 1/g' "${INSTALLDIR}/firebird.conf"

fi

echo "Start RDB..."

if [[ "$RDB_MAJOR_VERSION" == "2" ]]; then
  /etc/init.d/firebird restart

  "$INSTALLDIR/bin/gsec" -user SYSDBA -password masterkey -add artyom.smirnov@red-soft.ru -pw q3rgu7Ah
  "$INSTALLDIR/bin/gsec" -user SYSDBA -password masterkey -add trusted_user -pw trusted
elif [[ "$RDB_MAJOR_VERSION" == "FB3.0.4" ]]; then
  /etc/init.d/firebird restart
  "$INSTALLDIR/bin/gsec" -modify SYSDBA -password masterkey -user SYSDBA
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

echo rdb_server | kinit rdb_server/localhost
klist

"${SRCDIR}"/bin/ant -Dtest.report.dir=$REPORTS_DIR -Dtest.db.dir=$TEST_DIR -Djdk=${JDK_VERSION} -Dversion=$JAYBIRD_VERSION -Dbindir=${BINDIR} -Dsrcdir=${SRCDIR} -f "${SOURCES}"/ci/test.xml

kdestroy
