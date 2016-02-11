#!/bin/sh

ParseArg()
{
ArgStr=$1
ArgName=$2

AwkProgram="(\$1 == \"$ArgName\") {print \$2}"			

echo $ArgStr | awk -F= "$AwkProgram"
}

for arg in "$@" ; do
    TempArg=`ParseArg $arg "--java"`
    if test "$TempArg" != ""; then
	JAVA_HOME=$TempArg; export JAVA_HOME 
    else
	TempArg=`ParseArg $arg "--ant"`
	if test "$TempArg" != ""; then
	    echo $TempArg
	    ANT_HOME=$TempArg; export ANT_HOME 
	else
	    ANT_OPTS="$ANT_OPTS $arg"
	fi
    fi    
done						  

# Checking JAVA_HOME and ANT_HOME environment variables
if test "$JAVA_HOME" = ""; then
    echo "Error: JAVA_HOME environment variable must be set"
    exit 1
fi
	
if test "$ANT_HOME" = ""; then
    ANT=`which ant 2>/dev/null`
    if test "$ANT" = ""; then
	echo "Error: ANT_HOME environment variable must be set"
	exit 1
    else
	echo "Ant binary was found in $ANT"
    fi
else
    if [ -e $ANT_HOME/bin/ant ]; then
		ANT=$ANT_HOME/bin/ant
    elif [ -e $ANT_HOME/ant ]; then
		ANT=$ANT_HOME/ant
    elif [ -e `which ant 2>/dev/null` ]; then
        ANT=`which ant 2>/dev/null`
    else
		echo "Error: ant binary was not found in ANT_HOME"
		exit 1
    fi
fi

CLASSPATH=
#:${JAVA_HOME}/lib/tools.jar

TARGET_CLASSPATH=`echo ../../lib/*.jar | tr ' ' ':'`

TARGET_CLASSPATH=${TARGET_CLASSPATH}:${JAVA_HOME}/lib/tools.jar

ANT_HOME=.
ANT=$ANT_HOME/bin/ant

export ANT ANT_HOME ANT_OPTS

exec $ANT $ANT_OPTIONS "$@"
