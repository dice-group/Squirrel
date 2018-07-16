FROM dicegroup/squirrel-base

RUN apt-get update && apt-get install -y netcat

ADD entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

VOLUME ["/var/squirrel/data"]

ENTRYPOINT ["/bin/bash", "/entrypoint.sh"]
