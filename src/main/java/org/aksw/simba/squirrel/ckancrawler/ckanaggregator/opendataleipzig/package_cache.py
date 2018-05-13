import os.path

import ckanaggregatorpy.interfaces
import ckanaggregatorpy.opendataleipzig as opendataleipzig

class PackageCache(ckanaggregatorpy.interfaces.PackageCacheInterface):
    cacheFolder = opendataleipzig.cacheFolder
    ckanClient = opendataleipzig.ckanClient
    prefix = "opendataleipzig"
    ckanApiUrl = opendataleipzig.ckanApiUrl
    ckanBaseUrl = opendataleipzig.ckanBaseUrl

    def __init__(self):
        super(self.__class__, self).__init__()
