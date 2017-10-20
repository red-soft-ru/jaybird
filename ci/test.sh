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
	if [ "${ARCHITECTURE}" == "super" -o "${ARCHITECTURE}" == "superclassic" ]; then
		if [ "${SYSTEMCTL}" == "1" ]; then
			sudo systemctl reset-failed
			sudo systemctl $1 firebird-super || true
		else
			sudo service firebird $1 || true
		fi
	elif [ "${ARCHITECTURE}" == "classic" ]; then
		if [ "${SYSTEMCTL}" == "1" ]; then
			sudo systemctl reset-failed
			sudo systemctl $1 firebird-classic.socket || true
		else
			sudo service xinetd $1 || true
		fi
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
RDB_VERSION=2.6.0.13191
TEST_DIR=/tmp/jaybird_test
ARCH=`arch`
if [ "$ARCH" == "i686" ]; then
	ARCH="x86"
fi
RDB_URL=http://artifactory.red-soft.biz/list/red-database-rc/red-database/linux-${ARCH}/${RDB_VERSION}/linux-${ARCH}-${RDB_VERSION}-installer.bin
ARCHITECTURE=classic

uninstallrdb

echo "Download fbt"
(git clone --depth 1 git@git.red-soft.biz:red-database/fbt-repository) || die "Unable to checkout tests"

CPROCSP_ARCH=amd64
if [ "$ARCH" == "x86" ]; then
	CPROCSP_ARCH=ia32
fi

KEYS_DIR=/var/opt/cprocsp/keys
sudo mkdir -p $KEYS_DIR/jenkins
sudo chmod 700 $KEYS_DIR/jenkins
sudo cp fbt-repository/files/cert/RaUser-d.000/ $KEYS_DIR/jenkins -rfv
sudo chown jenkins:jenkins $KEYS_DIR/jenkins/RaUser-d.000 -R
sudo chmod 700 $KEYS_DIR/jenkins/RaUser-d.000
sudo -u jenkins /opt/cprocsp/bin/$CPROCSP_ARCH/certmgr -inst -cont '\\.\HDIMAGE\RaUser-de9e345e-157d-4d82-80d1-2098c0f28992'

sudo openssl x509 -in fbt-repository/files/cert/Смирнов.cer -inform der -outform pem -out ./testuser.cer

echo Will use build $RDB_VERSION for testing

echo "Downloading RedDatabase $RDB_BUILD_ID"
(wget -q "$RDB_URL" -O /tmp/installer.bin && chmod +x /tmp/installer.bin) || die "Unable to download RedDatabase"

echo "Installing RedDatabase"
sudo /tmp/installer.bin --DBAPasswd masterkey --mode unattended --architecture $ARCHITECTURE || die "Unable to install RedDatabase"
sudo rm -f /tmp/installer.bin
sudo rm -rf $TEST_DIR
mkdir -p $TEST_DIR
mkdir -p $WORKSPACE/results
sudo chmod 777 $TEST_DIR

sudo sed -i 's/#VerifyCertChain = 1/VerifyCertChain = 0/g' /opt/RedDatabase/firebird.conf
sudo sed -i 's/#CertUsernameDN = CN/CertUsernameDN = E/g' /opt/RedDatabase/firebird.conf

rdb_control restart
sleep 5

sudo /opt/RedDatabase/bin/gsec -user SYSDBA -password masterkey -add artyom.smirnov@red-soft.ru -pw q3rgu7Ah

export JAVA_HOME
ant -Dtest.report.dir=$TEST_DIR -Dtest.db.dir=$TEST_DIR -Djdk=${JDK_VERSION} -Dversion=$JAYBIRD_VERSION -Dbindir=${BINDIR} -Dsrcdir=${SRCDIR} -f test.xml
cp ${TEST_DIR}/*.xml $WORKSPACE/results

uninstallrdb
