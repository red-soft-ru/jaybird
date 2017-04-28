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

trap "fail" ERR INT QUIT KILL TERM

check_variable BINDIR
check_variable SRCDIR
check_variable JDK_VERSION
check_variable JAYBIRD_VERSION

OS=linux
RDB_VERSION=2.6.0.13002
ARCH=`arch`
if [ "$ARCH" == "i686" ]; then
	ARCH="x86"
fi
RDB_URL=http://artifactory.red-soft.biz/list/red-database-rc/red-database/linux-${ARCH}/${RDB_VERSION}/linux-${ARCH}-${RDB_VERSION}-installer.bin

uninstallrdb

echo Will use build $RDB_VERSION for testing

echo "Downloading RedDatabase $RDB_BUILD_ID"
(wget -q "$RDB_URL" -O /tmp/installer.bin && chmod +x /tmp/installer.bin) || die "Unable to download RedDatabase"

echo "Installing RedDatabase"
sudo /tmp/installer.bin --DBAPasswd masterkey --mode unattended --architecture classic || die "Unable to install RedDatabase"
sudo rm -f /tmp/installer.bin

ant -Djdk=${JDK_VERSION} -Dversion=$JAYBIRD_VERSION -Dbindir=${BINDIR} -Dsrcdir=${SRCDIR} -f test.xml

uninstallrdb
