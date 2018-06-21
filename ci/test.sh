#!/bin/bash
set -x

function die()
{
	echo "$1"
	uninstallrdb
	exit 1
}

function check_variable()
{
	eval val='$'$1
	[ "$val" != "" ] || die "$1 not defined"
}

function uninstallrdb()
{
	INSTALL_DIR=/opt/RedDatabase
	if [ -f "/opt/RedDatabase/uninstall" ]; then
		echo "Uninstalling RedDatabase"
		sudo pkill -9 rdb.\*
		sudo pkill -9 firebird.\q* || true
		sudo "$INSTALL_DIR/uninstall" --mode unattended || die "Unable to uninstall RedDatabase"
		sudo rm -rf "$INSTALL_DIR"	
	fi
}

function fail()
{
	uninstallrdb
	exit 1
}

function rdb_control()
{
    if [ "${SYSTEMCTL}" == "1" ]; then
        sudo systemctl reset-failed
        sudo systemctl $1 firebird || true
    else
        sudo service firebird $1 || true
    fi
}

command -v systemctl >/dev/null 2>&1 && SYSTEMCTL=1 || SYSTEMCTL=0

trap "fail" ERR INT QUIT KILL TERM

check_variable BINDIR
check_variable SRCDIR
check_variable JDK_VERSION
check_variable JAYBIRD_VERSION
check_variable JAVA_HOME
check_variable WORKSPACE

OS=linux
RDB_VERSION=3.0.0.917
TEST_DIR=/tmp/jaybird_test
ARCH=`arch`
if [ "$ARCH" == "i686" ]; then
	ARCH="x86"
fi

RDB_URL=http://artifactory.red-soft.biz/list/red-database-rc/red-database/linux-${ARCH}/${RDB_VERSION}/linux-${ARCH}-${RDB_VERSION}.bin
ARCHITECTURE=Classic

uninstallrdb

echo "Download fbt"
(git clone --depth 1 http://git.red-soft.biz/red-database/fbt-repository.git) || die "Unable to checkout tests" 

CPROCSP_ARCH=amd64
if [ "$ARCH" == "x86" ]; then
	CPROCSP_ARCH=ia32
fi

KEYS_DIR=/var/opt/cprocsp/keys
if [ "$USER" == "jenkins" ]; then
    sudo mkdir -p $KEYS_DIR/jenkins
    sudo chmod 700 $KEYS_DIR/jenkins
    sudo cp fbt-repository/files/cert/RaUser-d.000/ $KEYS_DIR/jenkins -rfv
	sudo chown jenkins:jenkins $KEYS_DIR/jenkins -R
	sudo chmod 700 $KEYS_DIR/jenkins/RaUser-d.000
else
    mkdir -p $KEYS_DIR/root
    chmod 700 $KEYS_DIR/root
    cp fbt-repository/files/cert/RaUser-d.000/ $KEYS_DIR/root -rfv
    chmod 700 $KEYS_DIR/root/RaUser-d.000
fi

if [ "$USER" == "jenkins" ]; then
	sudo chown jenkins:jenkins $KEYS_DIR/jenkins -R
	sudo -u jenkins /opt/cprocsp/bin/$CPROCSP_ARCH/certmgr -inst -cont '\\.\HDIMAGE\RaUser-de9e345e-157d-4d82-80d1-2098c0f28992'
else
	sudo /opt/cprocsp/bin/$CPROCSP_ARCH/certmgr -inst -cont '\\.\HDIMAGE\RaUser-de9e345e-157d-4d82-80d1-2098c0f28992'
fi	

sudo openssl x509 -in fbt-repository/files/cert/Смирнов.cer -inform der -outform pem -out ./testuser.cer

echo Will use build $RDB_VERSION for testing

echo "Downloading RedDatabase $RDB_BUILD_ID"
(wget -q "$RDB_URL" -O /tmp/installer.bin && chmod +x /tmp/installer.bin) || die "Unable to download RedDatabase"

echo "Installing RedDatabase"
sudo /tmp/installer.bin --mode unattended --sysdba_password masterkey --architecture $ARCHITECTURE --debuglevel 4 || die "Unable to install RedDatabase"
sudo rm -f /tmp/installer.bin
sudo rm -rf $TEST_DIR
mkdir -p $TEST_DIR
mkdir -p $WORKSPACE/results/jdk${JDK_VERSION}
sudo chmod 777 $TEST_DIR

sudo sed -i 's/#AuthServer = /AuthServer = Legacy_Auth, Srp, Gss, Multifactor /g' /opt/RedDatabase/firebird.conf
sudo sed -i 's/#AuthClient = /AuthClient = Legacy_Auth, Srp, Gss, Multifactor/g' /opt/RedDatabase/firebird.conf
sudo sed -i 's/#UserManager =/UserManager = Srp, Legacy_UserManager, Multifactor_Manager /g' /opt/RedDatabase/firebird.conf
sudo sed -i 's/#WireCrypt = Enabled (for client) \/ Required (for server)/WireCrypt = Disabled/g' /opt/RedDatabase/firebird.conf

sudo sed -i 's/#KrbServerKeyfile/KrbServerKeyfile/g' /opt/RedDatabase/firebird.conf
sudo sed -i 's/#KrbServiceName = rdb_server/KrbServiceName = rdb_server/g' /opt/RedDatabase/firebird.conf
sudo sed -i 's/#GssServiceName =/GssServiceName = localhost/g' /opt/RedDatabase/firebird.conf

sudo sed -i 's/#CertVerifyChain = 1/CertVerifyChain = 0/g' /opt/RedDatabase/firebird.conf
sudo sed -i 's/#CertUsernameDN = CN/CertUsernameDN = E/g' /opt/RedDatabase/firebird.conf

echo "Restart RDB..."
echo "Stopping RDB..."
ps aux|grep rdb||true
rdb_control stop
sleep 5
echo "Killing all RDB processes..."
sudo pkill -9 rdb.\* || true
ps aux|grep rdb||true
sleep 5
sudo /opt/RedDatabase/bin/isql -user SYSDBA -password masterkey /opt/RedDatabase/security3.fdb -i user.sql
echo "Start RDB..."
rdb_control start
ps aux|grep rdb||true

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

export JAVA_HOME
ant -Dtest.report.dir=$TEST_DIR -Dtest.db.dir=$TEST_DIR -Djdk=${JDK_VERSION} -Dversion=$JAYBIRD_VERSION -Dbindir=${BINDIR} -Dsrcdir=${SRCDIR} -f test.xml
cp ${TEST_DIR}/*.xml $WORKSPACE/results/jdk${JDK_VERSION}

kdestroy

uninstallrdb
