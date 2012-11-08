#!/usr/bin/env bash

APPNAME="adgenerator"

CLASSPATH=$(ls -x ./target/*.jar | tr '[:space:]' ':')$(ls -x ./target/dependency/*.jar | tr '[:space:]' ':')

  java -classpath obj:$CLASSPATH:obj com.voltdb.demographicanalytics.AdGenerator \
  --configfile ../config/default.json