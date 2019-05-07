#!/bin/bash

function die
{
	echo "$1"
	exit 1
}

function check_variable
{
	eval val='$'$1
	[ "$val" != "" ] || die "$1 not defined"
}

check_variable CI_PROJECT_DIR
check_variable JAVA_HOME

export JAVA_HOME
JAVA="${JAVA_HOME}/bin/java"
JDK_VERSION=`$JAVA -version 2>&1|head -n 1|awk -F\" '{split($2, v, ".");printf("%s%s", v[1], v[2])}'`
SOURCES=$(readlink -f $(dirname $0)/..)

cd $SOURCES
./build.sh jars

mkdir -p ${CI_PROJECT_DIR}/dist/jdk${JDK_VERSION}/bin \
		 ${CI_PROJECT_DIR}/dist/jdk${JDK_VERSION}/sources \
		 ${CI_PROJECT_DIR}/dist/jdk${JDK_VERSION}/javadoc \
		 ${CI_PROJECT_DIR}/dist/jdk${JDK_VERSION}/test

cp output/lib/jaybird-*javadoc* ${CI_PROJECT_DIR}/dist/jdk${JDK_VERSION}/javadoc
cp output/lib/jaybird-*sources* ${CI_PROJECT_DIR}/dist/jdk${JDK_VERSION}/sources
cp output/lib/jaybird-*test* ${CI_PROJECT_DIR}/dist/jdk${JDK_VERSION}/test
cp output/lib/* ${CI_PROJECT_DIR}/dist/jdk${JDK_VERSION}/bin