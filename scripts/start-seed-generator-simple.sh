#!/bin/bash

DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )

echo "Starting Simple Seed Generator. Sending URIs to $1 address"
java -cp $DIR/../target/squirrel-0.1-jar-with-dependencies.jar org.aksw.simba.squirrel.cli.SimpleSeedGeneratorCli $1 $2
