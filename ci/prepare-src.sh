#!/bin/bash
set -e

SOURCES=$(readlink -f $(dirname $0)/..)

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

check_variable VERSION

RESULT_DIR=`pwd`/dist-src

rm -rf $RESULT_DIR
mkdir -p $RESULT_DIR

RDB_VERSION=4.0.0.998
ARCH=`arch`
if [ "$ARCH" == "i686" ]; then
	ARCH="x86"
fi
RDB_URL=http://artifactory.red-soft.biz/list/red-database/red-database/linux-${ARCH}/${RDB_VERSION}/linux-${ARCH}-${RDB_VERSION}.tar.gz

(wget -q "$RDB_URL" -O /tmp/rdb_4_0.tar.gz && tar -xzf /tmp/rdb_4_0.tar.gz) || die "Unable to download RedDatabase"

echo Preparing $VERSION sources
cd $SOURCES

cp /tmp/rdb_4_0/include/firebird/FbInterface.java $SOURCES/src/jna-client/org/firebirdsql/jna/fbclient/

echo Archiving sources
ARCHIVE_PREFIX=jaybird-$VERSION
git archive --prefix=$ARCHIVE_PREFIX/ -o dist-src/$ARCHIVE_PREFIX.tar.gz HEAD
git archive --prefix=$ARCHIVE_PREFIX/ -o dist-src/$ARCHIVE_PREFIX.zip HEAD
