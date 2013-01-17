#!/usr/bin/env bash

APPNAME="flickrreader-1.0"

# find voltdb binaries in either installation or distribution directory.
if [ -n "$(which voltdb 2> /dev/null)" ]; then
    VOLTDB_BIN=$(dirname "$(which voltdb)")
else
    VOLTDB_BIN="$VOLT_HOME/bin"
fi

echo $VOLTDB_BIN

# installation layout has all libraries in $VOLTDB_ROOT/lib/voltdb
if [ -d "$VOLTDB_BIN/../lib/voltdb" ]; then
    VOLTDB_BASE=$(dirname "$VOLTDB_BIN")
    VOLTDB_LIB="$VOLTDB_BASE/lib/voltdb"
    VOLTDB_VOLTDB="$VOLTDB_LIB"
# distribution layout has libraries in separate lib and voltdb directories
else
    VOLTDB_LIB="$VOLTDB_BIN/../lib"
    VOLTDB_VOLTDB="$VOLTDB_BIN/../voltdb"
fi

CLASSPATH=$(ls -x "$VOLTDB_VOLTDB"/voltdb-*.jar | tr '[:space:]' ':')$(ls -x "$VOLTDB_LIB"/*.jar | egrep -v 'voltdb[a-z0-9.-]+\.jar' | tr '[:space:]' ':')$(ls -x ./target/*.jar | tr '[:space:]' ':')$(ls -x ./target/dependency/*.jar | tr '[:space:]' ':')
VOLTDB="$VOLTDB_BIN/voltdb"
VOLTCOMPILER="$VOLTDB_BIN/voltcompiler"
LOG4J="$VOLTDB_VOLTDB/log4j.xml"
LICENSE="$VOLTDB_VOLTDB/license.xml"
HOST="localhost"
 echo $CLASSPATH


# build an application catalog
function catalog() {
    rm ./$APPNAME-catalog.jar
    $VOLTCOMPILER ./target/classes ./project.xml ./$APPNAME-catalog.jar
    # stop if compilation fails
    if [ $? != 0 ]; then exit; fi
}

# run the voltdb server locally
function server() {
    
    catalog
    # run the server
    $VOLTDB create catalog ./$APPNAME-catalog.jar deployment ./deployment.xml \
        license $LICENSE host $HOST
}

function help() {
    echo "Usage: ./run.sh {catalog|server}"
}

# Run the target passed as the first arg on the command line
# If no first arg, run server
if [ $# -gt 1 ]; then help; exit; fi
if [ $# = 1 ]; then $1; else server; fi
