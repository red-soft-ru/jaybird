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

check_variable BINDIR
check_variable SRCDIR
check_variable JAYBIRD_VERSION
check_variable JAVA_HOME
check_variable CI_PROJECT_DIR

export JAVA_HOME
JAVA="${JAVA_HOME}/bin/java"
JDK_VERSION=`$JAVA -version 2>&1|head -n 1|awk -F\" '{split($2, v, ".");printf("%s%s", v[1], v[2])}'`
REPORTS_DIR="${CI_PROJECT_DIR}/results/jdk${JDK_VERSION}"
INSTALLDIR=/opt/RedDatabase
SOURCES=$(readlink -f $(dirname $0)/..)
OS=linux
RDB_VERSION=2.6.0.13407
ARCH=`arch`
TEST_DIR=/tmp/jaybird_test
TMPFS="/tmpfs"
if [ "$ARCH" == "i686" ]; then
	ARCH="x86"
fi

mkdir -p "${REPORTS_DIR}"

if [ -d "$TMPFS" ]; then
    echo Found $TMPFS. Will use it for databases
    TEST_DIR="$TMPFS"
else
	mkdir -p "$TEST_DIR"
fi

RDB_URL=http://builds.red-soft.biz/release_hub/rdb26/${RDB_VERSION}/download/red-database:linux-${ARCH}:${RDB_VERSION}:bin:installer
ARCHITECTURE=super

echo "Download fbt"
(git clone --depth 1 http://git.red-soft.biz/red-database/fbt-repository.git) || die "Unable to checkout tests"

CPROCSP_ARCH=amd64
if [ "$ARCH" == "x86" ]; then
	CPROCSP_ARCH=ia32
fi

KEYS_DIR=/var/opt/cprocsp/keys
mkdir -p $KEYS_DIR/root
chmod 700 $KEYS_DIR/root
cp fbt-repository/files/cert/RaUser-d.000/ $KEYS_DIR/root -rfv
chmod 700 $KEYS_DIR/root/RaUser-d.000

/opt/cprocsp/bin/$CPROCSP_ARCH/certmgr -inst -store root -cont '\\.\HDIMAGE\c6bb7811-a370-4de7-91fb-536a1b8b4017'

/opt/cprocsp/bin/$CPROCSP_ARCH/csptest -passwd -cont '\\.\HDIMAGE\c6bb7811-a370-4de7-91fb-536a1b8b4017' -change 12345678

cp fbt-repository/files/cert/Смирнов.cer ./testuser.cer

echo Will use build $RDB_VERSION for testing

echo "Downloading RedDatabase $RDB_BUILD_ID"
(curl -s "$RDB_URL" -o /tmp/installer.bin && chmod +x /tmp/installer.bin) || die "Unable to download RedDatabase"

echo "Installing RedDatabase"
/tmp/installer.bin --DBAPasswd masterkey --mode unattended --architecture $ARCHITECTURE || die "Unable to install RedDatabase"
chmod 777 $TEST_DIR

sed -i 's/#VerifyCertChain = 1/VerifyCertChain = 0/g' "${INSTALLDIR}/firebird.conf"
sed -i 's/#CertUsernameDN = CN/CertUsernameDN = E/g' "${INSTALLDIR}/firebird.conf"
sed -i 's/#ServerCertificate =/ServerCertificate = %D0%A1%D0%BC%D0%B8%D1%80%D0%BD%D0%BE%D0%B2%20%D0%90%D1%80%D1%82%D0%B5%D0%BC%20%D0%92%D1%8F%D1%87%D0%B5%D1%81%D0%BB%D0%B0%D0%B2%D0%BE%D0%B2%D0%B8%D1%87,%D0%A4%D0%B5%D0%B4%D0%B5%D1%80%D0%B0%D0%BB%D1%8C%D0%BD%D0%B0%D1%8F%20%D1%81%D0%BB%D1%83%D0%B6%D0%B1%D0%B0%20%D1%81%D1%83%D0%B4%D0%B5%D0%B1%D0%BD%D1%8B%D1%85%20%D0%BF%D1%80%D0%B8%D1%81%D1%82%D0%B0%D0%B2%D0%BE%D0%B2,071085DA7AC40C79ABE811F872541896CB/g' "${INSTALLDIR}/firebird.conf"
sed -i 's/#ServerPrivatePin = /ServerPrivatePin = 12345678/g' "${INSTALLDIR}/firebird.conf"
sed -i 's/#TrustedCertificate =/TrustedCertificate = %D0%A1%D0%BC%D0%B8%D1%80%D0%BD%D0%BE%D0%B2%20%D0%90%D1%80%D1%82%D0%B5%D0%BC%20%D0%92%D1%8F%D1%87%D0%B5%D1%81%D0%BB%D0%B0%D0%B2%D0%BE%D0%B2%D0%B8%D1%87,%D0%A4%D0%B5%D0%B4%D0%B5%D1%80%D0%B0%D0%BB%D1%8C%D0%BD%D0%B0%D1%8F%20%D1%81%D0%BB%D1%83%D0%B6%D0%B1%D0%B0%20%D1%81%D1%83%D0%B4%D0%B5%D0%B1%D0%BD%D1%8B%D1%85%20%D0%BF%D1%80%D0%B8%D1%81%D1%82%D0%B0%D0%B2%D0%BE%D0%B2,071085DA7AC40C79ABE811F872541896CB/g' "${INSTALLDIR}/firebird.conf"
sed -i 's/#TraceAuthentication = 0/TraceAuthentication = 1/g' "${INSTALLDIR}/firebird.conf"

/etc/init.d/firebird restart

"$INSTALLDIR/bin/gsec" -user SYSDBA -password masterkey -add artyom.smirnov@red-soft.ru -pw q3rgu7Ah

"${SRCDIR}"/bin/ant -Dtest.report.dir=$REPORTS_DIR -Dtest.db.dir=$TEST_DIR -Djdk=${JDK_VERSION} -Dversion=$JAYBIRD_VERSION -Dbindir=${BINDIR} -Dsrcdir=${SRCDIR} -f "${SOURCES}"/ci/test.xml
