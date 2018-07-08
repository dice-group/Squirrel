FROM ubuntu:bionic

RUN apt-get update -y \
    && apt-get install sudo -y \
    && sudo apt-get install python-dev libpq-dev python-pip python-virtualenv git-core openjdk-8-jdk solr-jetty redis-server gedit -y \
    && sudo apt-get install postgresql -y \
    && sudo service postgresql status -y \
    && sudo service postgresql restart -y \
    && sudo mkdir -p /usr/lib/ckan/default -y \
    && sudo chown `whoami` /usr/lib/ckan/default -y \
    && virtualenv --no-site-packages /usr/lib/ckan/default -y \
    && . /usr/lib/ckan/default/bin/activate -y \
    && pip install setuptools==36.1 -y \
    && pip install -e 'git+https://github.com/ckan/ckan.git@ckan-2.8.0#egg=ckan' -y \
    && pip install -r /usr/lib/ckan/default/src/ckan/requirements.txt -y \
    && deactivate -y \
    && . /usr/lib/ckan/default/bin/activate -y \
    && sudo -u postgres createuser -S -D -R -P ckan_default -y \
    #TODO: auto create password
    && sudo -u postgres createdb -O ckan_default ckan_default -E utf-8 -y \
    && mkdir -p ~/ckan/etc -y \
    && sudo ln -s ~/ckan/etc /etc/ckan -y \
    && sudo mkdir -p /etc/ckan/default -y \
    && sudo chown -R `whoami` /etc/ckan/ -y \
    && sudo chown -R `whoami` ~/ckan/etc -y \
    && paster make-config ckan /etc/ckan/default/development.ini -y \
    #TODO:COMPLETION OF INSTALLATION STEPS BASED ON CKAN DOCS
    && pip install -e 'git+https://github.com/ckan/ckanext-harvest.git#egg=ckan-harvest' -y \
    && pip install -e 'git+https://github.com/ckan/ckanext-dcat.git#egg=ckan-dcat'
ENTRYPOINT bin/bash


su - postgres -c '/usr/lib/postgresql/10/bin/pg_ctl -D /var/lib/postgresql/10/main -l logfile stop'

/usr/lib/postgresql/10/bin/postgres -d 3 -D /var/lib/postgresql/10/main \ -c config_file=/etc/postgresql/10/main/postgresql.conf

sudo apt-get install python-dev libpq-dev python-pip python-virtualenv git-core openjdk-8-jdk solr-jetty redis-server

