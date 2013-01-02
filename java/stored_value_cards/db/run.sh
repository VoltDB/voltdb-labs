#!/usr/bin/env bash

APPNAME="sv_cards"

if [ -z $VOLTDB_HOME ]; then
    export VOLTDB_HOME=`cd ~/voltdb-* && pwd`
    echo "VOLTDB_HOME was not set... using $VOLTDB_HOME"
fi
if [ ! -d $VOLTDB_HOME ]; then
    echo "VOLTDB_HOME was set to $VOLTDB_HOME, but that directory does not exist..."
    export VOLTDB_HOME=`cd ~/voltdb-* && pwd`
    echo "using $VOLTDB_HOME"
fi

CLASSPATH="`ls -1 $VOLTDB_HOME/voltdb/voltdb-*.jar`:`ls -1 $VOLTDB_HOME/lib/*.jar | tr '\n' ':'`"
if [ -d lib ]; then
  CLASSPATH="$CLASSPATH:`ls -1 lib/*.jar | tr '\n' ':'`"
fi
VOLTDB="$VOLTDB_HOME/bin/voltdb"
VOLTCOMPILER="$VOLTDB_HOME/bin/voltcompiler"
LOG4J="$VOLTDB_HOME/voltdb/log4j.xml"
LICENSE="$VOLTDB_HOME/voltdb/license.xml"
LEADER="localhost"

# remove build artifacts
function clean() {
    rm -rf obj debugoutput $APPNAME.jar voltdbroot log plannerlog.txt
}

# compile the source code for procedures and the client
function srccompile() {
    mkdir -p obj
    javac -classpath $CLASSPATH -d obj \
        src/*.java
    # stop if compilation fails
    if [ $? != 0 ]; then exit; fi
}

# build an application catalog
function catalog() {
    srccompile
    $VOLTCOMPILER obj project.xml $APPNAME.jar
    # stop if compilation fails
    if [ $? != 0 ]; then exit; fi
}

# run the voltdb server locally
function server() {
    # if a catalog doesn't exist, build one
    if [ ! -f $APPNAME.jar ]; then catalog; fi
    # run the server
    $VOLTDB create catalog $APPNAME.jar deployment deployment.xml \
        license $LICENSE leader $LEADER
}

function help() {
    echo "Usage: ./run.sh {clean|catalog|server|help}"
}

# Run the target passed as the first arg on the command line
# If no first arg, run server
if [ $# -gt 1 ]; then help; exit; fi
if [ $# = 1 ]; then $1; else server; fi
