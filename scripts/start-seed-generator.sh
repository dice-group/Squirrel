#!/bin/bash

DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )

echo "Starting Seed Generator. Sending URIs to $1 address"
java -cp $DIR/../target/squirrel-0.1-jar-with-dependencies.jar org.aksw.simba.squirrel.cli.CkanSeedGeneratorCli $1 $2
