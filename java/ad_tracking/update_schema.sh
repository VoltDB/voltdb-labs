#!/usr/bin/env bash

CATALOG_NAME="AD_TRACKING"

# This script assumes voltdb/bin is in your path
VOLTDB_HOME=$(dirname $(dirname "$(which voltdb)"))

# entire script runs within this directory:
cd db

# compile java stored procedures
if find src -name "*.java" &> /dev/null; then
    SRC=`find src -name "*.java"`
    CLASSPATH=`ls -1 $VOLTDB_HOME/voltdb/voltdb-*.jar`
    if [ ! -f $CLASSPATH ]; then
        echo "voltdb-*.jar file not found for CLASSPATH, edit this script to provide the correct path"
        exit
    fi
    mkdir -p obj
    javac -classpath $CLASSPATH -d obj $SRC
    # stop if compilation fails
    if [ $? != 0 ]; then exit; fi
fi
# compile VoltDB catalog
if [ -f ddl.sql ]; then
    voltdb compile --classpath obj -o ${CATALOG_NAME}.jar ddl.sql
    # stop if compilation fails
    if [ $? != 0 ]; then exit; fi
fi

# start database in background
voltadmin update ${CATALOG_NAME}.jar deployment.xml

echo "VoltDB is running!"
echo
echo "to stop use the command:"
echo "  voltadmin shutdown"
