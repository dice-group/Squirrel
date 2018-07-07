FROM ckan/ckan

RUN apt-get update -y \
    && pip install -e 'git+https://github.com/ckan/ckanext-harvest.git#egg=ckan-harvest' -y \
    && pip install -e 'git+https://github.com/ckan/ckanext-dcat.git#egg=ckan-dcat'
ENTRYPOINT bin/bash
