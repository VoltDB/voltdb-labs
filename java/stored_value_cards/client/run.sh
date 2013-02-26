#!/usr/bin/env bash

. ./env.sh

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
    echo "Usage: ./run.sh {clean|srccompile|client|help}"
}

# Run the target passed as the first arg on the command line
# If no first arg, run server
if [ $# -gt 1 ]; then help; exit; fi
if [ $# = 1 ]; then $1; else client; fi
