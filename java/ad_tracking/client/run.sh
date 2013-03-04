#!/usr/bin/env bash

. ./env.sh

APPNAME="load_stocks"

# remove build artifacts
function clean() {
    rm -rf obj log stocks
}

# download fresh market symbols
function download-stocks() {
    mkdir -p stocks
    curl "http://www.nasdaq.com/screening/companies-by-name.aspx?letter=0&exchange=nasdaq&render=download" -o stocks/NASDAQ.csv
    curl "http://www.nasdaq.com/screening/companies-by-name.aspx?letter=0&exchange=nyse&render=download" -o stocks/NYSE.csv
    curl "http://www.nasdaq.com/screening/companies-by-name.aspx?letter=0&exchange=amex&render=download" -o stocks/AMEX.csv
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
    # download symbols if needed
    if [ ! -f stocks/NASDAQ.csv ]; then download-stocks; fi
    # run client
    java -classpath obj:$CLASSPATH:obj -Dlog4j.configuration=file://$LOG4J \
	client.StocksLoader load.props stocks/NASDAQ.csv stocks/NYSE.csv stocks/AMEX.csv

}

function help() {
    echo "Usage: ./run.sh {clean|client|download-stocks|help|srccompile}"
}

# Run the target passed as the first arg on the command line
# If no first arg, run server
if [ $# -gt 1 ]; then help; exit; fi
if [ $# = 1 ]; then $1; else client; fi
