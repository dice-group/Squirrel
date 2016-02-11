#!/bin/bash
LDSPIDER_TRIPLES_FILE=/var/ldspider/data/triples.nt
LDSPIDER_LOG_FILE=/var/ldspider/data/ldspider.log
LDSPIDER_ERROR_LOG_FILE=/var/ldspider/data/ldspider.error.log
TMP=/tmp
CURRENT_DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
CONTAINER_NAME=$1
docker run -it --volumes-from=$CONTAINER_NAME -v $CURRENT_DIR:$TMP ldspider cp $LDSPIDER_TRIPLES_FILE $TMP
docker run -it --volumes-from=$CONTAINER_NAME -v $CURRENT_DIR:$TMP ldspider cp $LDSPIDER_LOG_FILE $TMP
docker run -it --volumes-from=$CONTAINER_NAME -v $CURRENT_DIR:$TMP ldspider cp $LDSPIDER_ERROR_LOG_FILE $TMP
