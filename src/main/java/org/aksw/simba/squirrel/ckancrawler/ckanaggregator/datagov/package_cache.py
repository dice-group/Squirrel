import os.path
import time
import requests
from bs4 import BeautifulSoup
import re

try:
    import cPickle as pickle
except:
    import pickle

import ckanaggregatorpy.datagov as datagov
import ckanaggregatorpy.interfaces

class PackageCache(ckanaggregatorpy.interfaces.PackageCacheInterface):
    cacheFolder = datagov.cacheFolder
    pagesFolder = os.path.join(datagov.cacheFolder, "pages")
    datagovPageUrl = "http://catalog.data.gov/dataset?page="
    datagovNumberOfPages = 9772 # this number has to be updated manually
    ckanClient = datagov.ckanClient
    prefix = "datagov"
    ckanApiUrl = datagov.ckanApiUrl
    ckanBaseUrl = datagov.ckanBaseUrl

    def __init__(self):
        super(self.__class__, self).__init__()

    def updatePackageList(self):
        """
            ckanclient.package_list() does not work for data.gov portal
            we have to fetch all the pages from the web portal
            and then scrape package ids from them
        """
        self.updateDataGovPages()
        self.extractDataGovDatasetIds()

    def extractDataGovDatasetIds(self):
        """
            Extract Dataset Ids from the scraped pages
            Should be run after updateDataGovPages
        """
        datasetIds = []
        for i in range(1, self.datagovNumberOfPages + 1):
            pageFile = os.path.join(self.pagesFolder, "page" + str(i))
            pageUrl = self.datagovPageUrl+str(i)
            if(not os.path.isfile(pageFile)):
                print("Page %s does not exist! Did you run updateDataGovPages?" % pageUrl)
                break
            f = open(pageFile, "rU")
            page = f.read()
            f.close()
            soup = BeautifulSoup(page)
            for dataset in soup.find_all(href=re.compile("dataset/")):
                datasetIds.append(dataset["href"].split("/")[-1])

        self.saveFile(self.packageListFile, datasetIds)

    def updateDataGovPages(self):
        """
            Saves all the pages from the data.gov portal to data/datagov/pages folder
        """
        for i in range(1, self.datagovNumberOfPages + 1):
            print("Getting page %s out of %s" % (i, self.datagovNumberOfPages))
            pageFile = os.path.join(self.pagesFolder, "page" + str(i))
            pageUrl = self.datagovPageUrl+str(i)
            if(os.path.isfile(pageFile)):
                print("Page %s is already fetched, skipping." % pageUrl)
                continue
            r = requests.get(pageUrl)
            assert r.status_code == requests.status_codes.codes.OK
            f = open(pageFile, "w")
            f.write(r.content)
            f.close()
            time.sleep(0.5)
