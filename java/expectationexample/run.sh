#!/usr/bin/env bash

APPNAME="expectationsample"
SOURCEPATH="com/voltdb/expectation"
CLASSPATH="`ls -x $VOLT_HOME/voltdb/voltdb-*.jar | tr '[:space:]' ':'``ls -x $VOLT_HOME/lib/*.jar | tr '[:space:]' ':'`"
VOLTDB="$VOLT_HOME/bin/voltdb"
VOLTCOMPILER="$VOLT_HOME/bin/voltcompiler"
LICENSE="$VOLT_HOME/voltdb/license.xml"
LEADER="localhost"

# remove build artifacts
function clean() {
    rm -rf obj debugoutput $APPNAME.jar voltdbroot plannerlog.txt voltdbroot
}

# compile the source code for procedures and the client
function srccompile() {
    mkdir -p obj
    javac -classpath $CLASSPATH -d obj \
        src/$SOURCEPATH/*.java \
        src/$SOURCEPATH/configuration/*.java \
        src/$SOURCEPATH/procs/*.java

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

# run the client that drives the example
function client() {
    async-benchmark
}

# Asynchronous benchmark sample
# Use this target for argument help
function async-benchmark-help() {
    srccompile
    java -classpath obj:$CLASSPATH:obj com.voltdb.expectation.ExpectationSample --help
}

function async-benchmark() {
    srccompile
    java -classpath obj:$CLASSPATH:obj com.voltdb.expectation.ExpectationSample \
        --servers=localhost
}

function help() {
    echo "Usage: ./run.sh {clean|catalog|server|client}"
}

# Run the target passed as the first arg on the command line
# If no first arg, run server
if [ $# -gt 1 ]; then help; exit; fi
if [ $# = 1 ]; then $1; else server; fi
