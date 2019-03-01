docker rm $(docker ps -a | grep openlink/virtuoso-opensource-7 | awk '{print $1}')
if [ -d "data/virt_db" ] 
then
    echo "Directory virt_db exists."
    cd data/virt_db 
else
    echo "Warning: Directory virt_db does not exists. Creating it"
    mkdir data/virt_db
    cd data/virt_db 
fi
docker run \
    --name my_virtdb \
    --interactive \
    --tty \
    --env DBA_PASSWORD=$1 \
    --publish 1111:1111 \
    --publish  8890:8890 \
    --volume `pwd`:/database \
    openlink/virtuoso-opensource-7:latest


