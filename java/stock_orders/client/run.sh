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

# download fresh market symbols, firms
function download() {
    mkdir -p data
    curl "http://www.nasdaq.com/screening/companies-by-name.aspx?exchange=NASDAQ&render=download" -o data/NASDAQ.csv
    curl "http://www.nasdaq.com/screening/companies-by-name.aspx?exchange=NYSE&render=download" -o data/NYSE.csv
    curl "http://www.nasdaq.com/screening/companies-by-name.aspx?exchange=AMEX&render=download" -o data/AMEX.csv
    #curl "http://www.nyse.com/NMDSServices/membershipCSV;jsessionid=993FBF56244A0D8B43672C79070C7822?market_name=nyse_nyseamexequities" -o data/firms.csv
}

function init-us() {
    srccompile
    java -classpath obj:$CLASSPATH:obj -Dlog4j.configuration=file://$LOG4J \
	client.StocksLoader \
        --servers=$SERVERS \
        --filename=data/NYSE.csv \
        --skiplines=1
}

function init-shanghai() {
    srccompile
    java -classpath obj:$CLASSPATH:obj -Dlog4j.configuration=file://$LOG4J \
	client.StocksLoader \
        --servers=$SERVERS \
        --filename=data/shanghai.txt \
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
        client.OrdersBenchmark \
        --displayinterval=5 \
        --warmup=5 \
        --duration=60 \
        --servers=$SERVERS \
        --ratelimit=20000 \
        --autotune=true \
        --latencytarget=1
}

function client() {
    srccompile
    init-us
    benchmark
}



function help() {
    echo "Usage: ./run.sh {clean|srccompile|client|help}"
}

# Run the target passed as the first arg on the command line
# If no first arg, run server
if [ $# -gt 1 ]; then help; exit; fi
if [ $# = 1 ]; then $1; else client; fi
