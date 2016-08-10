#!/bin/bash

DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )

echo "Starting Frontier with $1 address"
echo "Logs output to $2"
java -cp $DIR/../target/squirrel-0.1-jar-with-dependencies.jar org.aksw.simba.squirrel.cli.ZeroMQBasedFrontierCli $1 $2
