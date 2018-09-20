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
REPORTS_DIR="${CI_PROJECT_DIR}/results/jdk${JDK_VERSION}"
INSTALLDIR=/opt/RedDatabase
SOURCES=$(readlink -f $(dirname $0)/..)
OS=linux
RDB_VERSION=4.0.0.1013
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

KEYS_DIR=/var/opt/cprocsp/keys
mkdir -p $KEYS_DIR/root
chmod 700 $KEYS_DIR/root
cp fbt-repository/files/cert/RaUser-d.000/ $KEYS_DIR/root -rfv
chmod 700 $KEYS_DIR/root/RaUser-d.000

/opt/cprocsp/bin/$CPROCSP_ARCH/certmgr -inst -cont '\\.\HDIMAGE\c6bb7811-a370-4de7-91fb-536a1b8b4017'

cp fbt-repository/files/cert/Смирнов.cer ./testuser.cer

echo Will use build $RDB_VERSION for testing

echo "Downloading RedDatabase $RDB_BUILD_ID"
(curl -s "$RDB_URL" -o /tmp/installer.bin && chmod +x /tmp/installer.bin) || die "Unable to download RedDatabase"

echo "Installing RedDatabase"
/tmp/installer.bin --mode unattended --sysdba_password masterkey --debuglevel 4 || die "Unable to install RedDatabase"
rm -f /tmp/installer.bin
chmod 777 $TEST_DIR

sed -i 's/#AuthServer = Srp256/AuthServer = Srp, Srp256, Legacy_Auth, Gss /g' "${INSTALLDIR}"/firebird.conf
sed -i 's/#AuthClient = Srp256, Srp, Legacy_Auth, Gss\s*#Non Windows clients/AuthClient = Srp256, Srp, Legacy_Auth, Gss/g' "${INSTALLDIR}"/firebird.conf
sed -i 's/#UserManager = Srp/UserManager = Srp, Legacy_UserManager /g' "${INSTALLDIR}"/firebird.conf
sed -i 's/#WireCrypt = Enabled (for client) \/ Required (for server)/WireCrypt = Disabled/g' "${INSTALLDIR}"/firebird.conf

sed -i 's/#KrbServerKeyfile/KrbServerKeyfile/g' "${INSTALLDIR}"/firebird.conf
sed -i 's/#GssServiceName/GssServiceName/g' "${INSTALLDIR}"/firebird.conf
sed -i 's/#GssHostName =/GssHostName = localhost/g' "${INSTALLDIR}"/firebird.conf

sed -i 's/#CertVerifyChain = 1/CertVerifyChain = 0/g' "${INSTALLDIR}"/firebird.conf
sed -i 's/#CertUsernameDN = CN/CertUsernameDN = E/g' "${INSTALLDIR}"/firebird.conf

"${INSTALLDIR}"/bin/isql -user SYSDBA -password masterkey "${INSTALLDIR}"/security4.fdb -i "${SOURCES}"/ci/user.sql
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

echo rdb_server | kinit rdb_server/localhost
klist

"${SRCDIR}"/bin/ant -Dtest.report.dir=$REPORTS_DIR -Dtest.db.dir=$TEST_DIR -Djdk=${JDK_VERSION} -Dversion=$JAYBIRD_VERSION -Dbindir=${BINDIR} -Dsrcdir=${SRCDIR} -f "${SOURCES}"/ci/test.xml

kdestroy
