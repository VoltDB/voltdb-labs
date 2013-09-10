#!/usr/bin/env bash

. ./env.sh

# remove build artifacts
function clean() {
    rm -rf obj log stocks
}

# compile the source code for procedures and the client
function srccompile() {
    mkdir -p obj
    javac -classpath $CLASSPATH -d obj \
        src/*.java
    # stop if compilation fails
    if [ $? != 0 ]; then exit; fi
}

# run the client that drives the example
function client() {
    srccompile
    # run client
    java -classpath obj:$CLASSPATH:obj -Dlog4j.configuration=file://$LOG4J \
	client.AdTrackingBenchmark \
        --displayinterval=5 \
        --warmup=5 \
        --duration=120 \
        --servers=$SERVERS \
        --ratelimit=20000 \
        --autotune=true \
        --latencytarget=1 \
        --sites=1000 \
        --pagespersite=10 \
        --advertisers=1000 \
        --campaignsperadvertiser=10 \
        --creativespercampaign=10
}

function help() {
    echo "Usage: ./run.sh {clean|client|help|srccompile}"
}

# Run the target passed as the first arg on the command line
# If no first arg, run server
if [ $# -gt 1 ]; then help; exit; fi
if [ $# = 1 ]; then $1; else client; fi
