#!/bin/bash

DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )

echo "Starting Frontier with $1 address"
java -cp $DIR/../target/ldspider-1.3-jar-with-dependencies.jar org.aksw.simba.ldspider.cli.ZeroMQBasedFrontierCli $1
