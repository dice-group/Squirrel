#!/bin/bash

DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )

echo "Starting Worker with Id:$1 and connecting to frontier on $2 address. Files will be saved to sink listening on $3"
java -cp $DIR/../target/squirrel-0.1-jar-with-dependencies.jar org.aksw.simba.squirrel.cli.WorkerCli $1 $2 $3
