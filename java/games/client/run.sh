#!/usr/bin/env bash

APPNAME="stocks"

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
echo "using CLASSPATH $CLASSPATH"
VOLTDB="$VOLTDB_HOME/bin/voltdb"
VOLTCOMPILER="$VOLTDB_HOME/bin/voltcompiler"
LOG4J="$VOLTDB_HOME/voltdb/log4j.xml"
LICENSE="$VOLTDB_HOME/voltdb/license.xml"
SERVERS="localhost:21212" # could be comma-separated list of host:port

# remove build artifacts
function clean() {
    rm -rf obj log
}

# compile the source code for procedures and the client
function srccompile() {
    mkdir -p obj
    javac -classpath $CLASSPATH -d obj \
        src/*.java
    # stop if compilation fails
    if [ $? != 0 ]; then exit; fi
}

# EDIT BELOW
#   run once with rate-limit=300000 or a higher number on a cluster of more than three machines
#   Observe the actual throughput.  This is the max for the database, but the client is trying 
#   to send more and getting back-pressure.
#   
#   Set the rate-limit to 90-95% of the observed max throughput.  This should provide excellent
#   throughput with good latency.
#
function benchmark() {
    java -classpath obj:$CLASSPATH -Dlog4j.configuration=file://$LOG4J \
        client.GameBenchmark \
        --displayinterval=5 \
        --warmup=5 \
        --duration=300 \
        --servers=$SERVERS \
        --ratelimit=51000 \
        --autotune=true \
        --latencytarget=6
}

function client() {
    srccompile
    benchmark
}

function help() {
    echo "Usage: ./run.sh {help|srccompile|client|clean}"
}

# Run the target passed as the first arg on the command line
# If no first arg, run server
if [ $# -gt 1 ]; then help; exit; fi
if [ $# = 1 ]; then $1; else client; fi

