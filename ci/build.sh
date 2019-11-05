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
mvn clean compile package -Dmaven.test.skip=true

mkdir -p ${CI_PROJECT_DIR}/dist/jdk18/bin \
		 ${CI_PROJECT_DIR}/dist/jdk18/sources \
		 ${CI_PROJECT_DIR}/dist/jdk18/javadoc \
		 ${CI_PROJECT_DIR}/dist/jdk17/bin \
		 ${CI_PROJECT_DIR}/dist/jdk17/sources \
		 ${CI_PROJECT_DIR}/dist/jdk17/javadoc

cp modules/jaybird-jdk18/target/jaybird-*javadoc* ${CI_PROJECT_DIR}/dist/jdk18/javadoc
cp modules/jaybird-jdk18/target/jaybird* ${CI_PROJECT_DIR}/dist/jdk18/bin
cp modules/jaybird-jdk18/target/jaybird-*sources* ${CI_PROJECT_DIR}/dist/jdk18/sources
cp modules/jaybird-full-jdk18/target/jaybird-*javadoc* ${CI_PROJECT_DIR}/dist/jdk18/javadoc
cp modules/jaybird-full-jdk18/target/jaybird* ${CI_PROJECT_DIR}/dist/jdk18/bin
cp modules/jaybird-full-jdk18/target/jaybird-*sources* ${CI_PROJECT_DIR}/dist/jdk18/sources
cp modules/cryptoapi-security-jdk18/target/jaybird* ${CI_PROJECT_DIR}/dist/jdk18/bin
cp modules/cryptoapi-security-jdk18/target/jaybird-*sources* ${CI_PROJECT_DIR}/dist/jdk18/sources
cp modules/cryptoapi-jdk18/target/jaybird* ${CI_PROJECT_DIR}/dist/jdk18/bin
cp modules/cryptoapi-jdk18/target/jaybird-*sources* ${CI_PROJECT_DIR}/dist/jdk18/sources
cp modules/jaybird-test-jdk18/target/jaybird* ${CI_PROJECT_DIR}/dist/jdk18/bin
cp modules/jaybird-test-jdk18/target/jaybird-*sources* ${CI_PROJECT_DIR}/dist/jdk18/sources

cp modules/jaybird-jdk17/target/jaybird-*javadoc* ${CI_PROJECT_DIR}/dist/jdk17/javadoc
cp modules/jaybird-jdk17/target/jaybird* ${CI_PROJECT_DIR}/dist/jdk17/bin
cp modules/jaybird-jdk17/target/jaybird-*sources* ${CI_PROJECT_DIR}/dist/jdk17/sources
cp modules/jaybird-full-jdk17/target/jaybird-*javadoc* ${CI_PROJECT_DIR}/dist/jdk17/javadoc
cp modules/jaybird-full-jdk17/target/jaybird* ${CI_PROJECT_DIR}/dist/jdk17/bin
cp modules/jaybird-full-jdk17/target/jaybird-*sources* ${CI_PROJECT_DIR}/dist/jdk17/sources
cp modules/cryptoapi-security-jdk17/target/jaybird* ${CI_PROJECT_DIR}/dist/jdk17/bin
cp modules/cryptoapi-security-jdk17/target/jaybird-*sources* ${CI_PROJECT_DIR}/dist/jdk17/sources
cp modules/cryptoapi-jdk17/target/jaybird* ${CI_PROJECT_DIR}/dist/jdk17/bin
cp modules/cryptoapi-jdk17/target/jaybird-*sources* ${CI_PROJECT_DIR}/dist/jdk17/sources
cp modules/jaybird-test-jdk17/target/jaybird* ${CI_PROJECT_DIR}/dist/jdk17/bin
cp modules/jaybird-test-jdk17/target/jaybird-*sources* ${CI_PROJECT_DIR}/dist/jdk17/sources
