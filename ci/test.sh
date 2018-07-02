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
	sudo -u jenkins /opt/cprocsp/bin/$CPROCSP_ARCH/certmgr -inst -cont '\\.\HDIMAGE\c6bb7811-a370-4de7-91fb-536a1b8b4017'
else
	sudo /opt/cprocsp/bin/$CPROCSP_ARCH/certmgr -inst -cont '\\.\HDIMAGE\c6bb7811-a370-4de7-91fb-536a1b8b4017'
fi

/opt/cprocsp/bin/amd64/csptest -passwd -cont '\\.\HDIMAGE\c6bb7811-a370-4de7-91fb-536a1b8b4017' -change 12345678

sudo cp fbt-repository/files/cert/Смирнов.cer ./testuser.cer

echo Will use build $RDB_VERSION for testing

echo "Downloading RedDatabase $RDB_BUILD_ID"
(wget -q "$RDB_URL" -O /tmp/installer.bin && chmod +x /tmp/installer.bin) || die "Unable to download RedDatabase"

echo "Installing RedDatabase"
sudo /tmp/installer.bin --DBAPasswd masterkey --mode unattended --architecture $ARCHITECTURE || die "Unable to install RedDatabase"
sudo rm -f /tmp/installer.bin
sudo rm -rf $TEST_DIR
mkdir -p $TEST_DIR
mkdir -p $WORKSPACE/results/jdk${JDK_VERSION}
sudo chmod 777 $TEST_DIR

sudo sed -i 's/#VerifyCertChain = 1/VerifyCertChain = 0/g' /opt/RedDatabase/firebird.conf
sudo sed -i 's/#CertUsernameDN = CN/CertUsernameDN = E/g' /opt/RedDatabase/firebird.conf
sudo sed -i 's/#ServerCertificate =/ServerCertificate = %D0%A1%D0%BC%D0%B8%D1%80%D0%BD%D0%BE%D0%B2%20%D0%90%D1%80%D1%82%D0%B5%D0%BC%20%D0%92%D1%8F%D1%87%D0%B5%D1%81%D0%BB%D0%B0%D0%B2%D0%BE%D0%B2%D0%B8%D1%87,%D0%A4%D0%B5%D0%B4%D0%B5%D1%80%D0%B0%D0%BB%D1%8C%D0%BD%D0%B0%D1%8F%20%D1%81%D0%BB%D1%83%D0%B6%D0%B1%D0%B0%20%D1%81%D1%83%D0%B4%D0%B5%D0%B1%D0%BD%D1%8B%D1%85%20%D0%BF%D1%80%D0%B8%D1%81%D1%82%D0%B0%D0%B2%D0%BE%D0%B2,071085DA7AC40C79ABE811F872541896CB/g' /opt/RedDatabase/firebird.conf
sudo sed -i 's/#ServerPrivatePin = /ServerPrivatePin = 12345678/g' /opt/RedDatabase/firebird.conf

rdb_control restart
sleep 5

sudo /opt/RedDatabase/bin/gsec -user SYSDBA -password masterkey -add artyom.smirnov@red-soft.ru -pw q3rgu7Ah

export JAVA_HOME
ant -Dtest.report.dir=$TEST_DIR -Dtest.db.dir=$TEST_DIR -Djdk=${JDK_VERSION} -Dversion=$JAYBIRD_VERSION -Dbindir=${BINDIR} -Dsrcdir=${SRCDIR} -f test.xml
cp ${TEST_DIR}/*.xml $WORKSPACE/results/jdk${JDK_VERSION}

uninstallrdb
