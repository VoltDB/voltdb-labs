#!/usr/bin/env bash

if [ -z $VOLTDB_HOME ]; then
    echo "looking for VoltDB under home directory"
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

function init-us() {
    srccompile
    java -classpath obj:$CLASSPATH:obj -Dlog4j.configuration=file://$LOG4J \
	client.ExampleLoader \
        --servers=$SERVERS \
        --filename=data/NYSE.csv \
        --skiplines=1
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
        client.CardBenchmark \
        --displayinterval=5 \
        --warmup=5 \
        --duration=60 \
        --servers=$SERVERS \
        --ratelimit=20000 \
        --autotune=false \
        --latencytarget=6
}

function client() {
    srccompile
    benchmark
}

function help() {
    echo "Usage: ./run.sh {help|srccompile|init-us|init-shanghai|benchmark|clean|download}"
}

# Run the target passed as the first arg on the command line
# If no first arg, run server
if [ $# -gt 1 ]; then help; exit; fi
if [ $# = 1 ]; then $1; else client; fi

