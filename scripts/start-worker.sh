#!/bin/bash

DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )

echo "Starting Worker with Id:$1 and connecting to frontier on $2 address. Files will be saved to sink listening on $3"
java -cp $DIR/../target/ldspider-1.3-jar-with-dependencies.jar org.aksw.simba.ldspider.cli.WorkerCli $1 $2 $3
