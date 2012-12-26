#!/usr/bin/env bash

APPNAME="ApacheLogSimulator"

CLASSPATH=$(ls -x ./target/*.jar | tr '[:space:]' ':')$(ls -x ./target/dependency/*.jar | tr '[:space:]' ':')

java -classpath obj:$CLASSPATH:obj com.voltdb.apachelogreader.ApacheLogSimulator

