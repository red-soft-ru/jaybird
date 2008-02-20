#!/bin/sh

# Checking JAVA_HOME and ANT_HOME environment variables
if test "$JAVA_HOME" = ""; then
    echo "Error: JAVA_HOME environment variable must be set"
    exit 1
fi
	
if test "$ANT_HOME" = ""; then
    echo "Error: ANT_HOME environment variable must be set"
    exit 1
fi

CLASSPATH=
#:${JAVA_HOME}/lib/tools.jar

TARGET_CLASSPATH=`echo ../../lib/*.jar | tr ' ' ':'`

TARGET_CLASSPATH=${TARGET_CLASSPATH}:${JAVA_HOME}/lib/tools.jar

ANT=$ANT_HOME/bin/ant
#xerces/xalan for test support.
JAXP_DOM_FACTORY="org.apache.xerces.jaxp.DocumentBuilderFactoryImpl"
JAXP_SAX_FACTORY="org.apache.xerces.jaxp.SAXParserFactoryImpl"

ANT_OPTS="$ANT_OPTS -Djavax.xml.parsers.DocumentBuilderFactory=$JAXP_DOM_FACTORY"
ANT_OPTS="$ANT_OPTS -Djavax.xml.parsers.SAXParserFactory=$JAXP_SAX_FACTORY"

exec $ANT $ANT_OPTIONS "$@"
