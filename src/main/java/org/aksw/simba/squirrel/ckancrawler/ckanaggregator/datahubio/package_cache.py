import os.path

import ckanaggregatorpy.datahubio as datahubio
import ckanaggregatorpy.interfaces

class PackageCache(ckanaggregatorpy.interfaces.PackageCacheInterface):
    cacheFolder = datahubio.cacheFolder
    ckanClient = datahubio.ckanClient
    prefix = "datahubio"
    ckanApiUrl = datahubio.ckanApiUrl
    ckanBaseUrl = datahubio.ckanBaseUrl

    def __init__(self):
        super(self.__class__, self).__init__()
