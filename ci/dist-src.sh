#!/bin/bash
set -e

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

ARCHIVE_PREFIX=jaybird-$VERSION

if [ "$TAG" != "" ]; then
	sed -e "s|version.tag.maven\" value=\".*|version.tag.maven\" value=\"-$TAG\"/>|" -i build/init.xml
fi

hash=`git stash create`
[ "$hash" = "" ] && hash=HEAD
rm -rf dist-src
mkdir -p dist-src
git archive --format=tar.gz --prefix=$ARCHIVE_PREFIX/ $hash -o dist-src/$ARCHIVE_PREFIX.tar.gz
git archive --format=zip --prefix=$ARCHIVE_PREFIX/ $hash -o dist-src/$ARCHIVE_PREFIX.zip
