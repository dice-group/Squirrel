import requests
import os
import time
import codecs
import random
from ckanaggregatorpy.interfaces.loadsaveinterface import LoadSaveInterface
import ckanaggregatorpy.assets.formats

class PackageCacheInterface(LoadSaveInterface):
    packageListFile = None
    packagesFolder = None
    rdfPackagesFolder = None
    ckanClient = None
    ckanBaseUrl = None

    def __init__(self):
        self.packageListFile = os.path.join(self.cacheFolder, "packageList.dump")
        self.packagesFolder = os.path.join(self.cacheFolder, "packages")
        self.rdfPackagesFolder = os.path.join(self.cacheFolder, "rdfPackages")
        self.rdfPackagesFile = os.path.join(self.cacheFolder, "rdfPackages.dump")
        self.rdfPackagesRdfResourcesOnlyFile = os.path.join(self.cacheFolder, "rdfPackagesRdfResourcesOnly.dump")
        self.csvPackagesFile = os.path.join(self.cacheFolder, "csvPackages.dump")
        self.csvRandomSelection15 = os.path.join(self.cacheFolder, "csvRandomSelection15.dump")

    def getRdfPackagesRdfResourcesOnly(self):
        if(not os.path.isfile(self.rdfPackagesRdfResourcesOnlyFile)):
            self.updateRdfPackagesRdfResourcesOnly()
            return self.loadFile(self.rdfPackagesRdfResourcesOnlyFile)
        else:
            return self.loadFile(self.rdfPackagesRdfResourcesOnlyFile)

    def getPackagesStream(self):
        for f in os.listdir(self.packagesFolder):
            packageFile = os.path.join(self.packagesFolder, f)
            yield self.loadFile(packageFile)

    def updateRdfPackagesRdfResourcesOnly(self):
        rdfPackages = self.getRdfPackages()
        rdfFormats = ckanaggregatorpy.assets.formats.RDF
        for rdfPackage in rdfPackages:
            for num, resource in enumerate(rdfPackage['resources']):
                if(not resource['format'] in rdfFormats):
                    rdfPackage['resources'][num] = None
                else:
                    rdfPackage['resources'][num]['format'] = self.normalizeFormat(resource['format'])
        self.saveFile(self.rdfPackagesRdfResourcesOnlyFile, rdfPackages)

    def getRdfPackages(self):
        if(not os.path.isfile(self.rdfPackagesFile)):
            self.updateRdfPackages()
            return self.loadFile(self.rdfPackagesFile)
        else:
            return self.loadFile(self.rdfPackagesFile)

    def updateRdfPackages(self):
        rdfFormats = ckanaggregatorpy.assets.formats.RDF
        self.updateXxxPackages(rdfFormats, self.rdfPackagesFile)

    def getCsvPackagesRandom15(self):
        if(not os.path.isfile(self.csvRandomSelection15)):
            self.updateCsvPackagesRandom15()
            return self.loadFile(self.csvRandomSelection15)
        else:
            return self.loadFile(self.csvRandomSelection15)

    def updateCsvPackagesRandom15(self):
        """
            This function saves selection to the file (for the gold standard creation)
        """
        csvPackagesRandom15 = self.getCsvPackagesRandom(15)
        self.saveFile(self.csvRandomSelection15, csvPackagesRandom15)

    def getCsvPackagesRandom(self, number):
        csvpackages = self.getCsvPackages()
        return self.getPackagesRandom(csvpackages, number)

    def getPackagesRandom(self, packages, number):
        """
            Returns a "number" of random packages from the array :)
        """
        return random.sample(packages, number)

    def getCsvPackages(self):
        if(not os.path.isfile(self.csvPackagesFile)):
            self.updateCsvPackages()
            return self.loadFile(self.csvPackagesFile)
        else:
            return self.loadFile(self.csvPackagesFile)

    def updateCsvPackages(self):
        csvFormats = ckanaggregatorpy.assets.formats.CSV
        self.updateXxxPackages(csvFormats, self.csvPackagesFile)

    def updateXxxPackages(self, filterArray, location):
        """
            Filter the packages according to 'format' param
            e.g. filterArray = ['csv', 'csV', 'CSv']
        """
        packageList = self.getPackageList()
        numberOfPackages = len(packageList)
        packages = []
        for num, packageId in enumerate(packageList):
            print("Querying package %d out of %d" % (num + 1, numberOfPackages))
            packageFile = os.path.join(self.packagesFolder, packageId)
            if(not os.path.exists(packageFile)):
                print("Package %s does not exist in the cache! Try updatePackages()" % packageId)
                continue
            else:
                package = self.loadFile(packageFile)
                for resource in package['resources']:
                    if(resource['format'] in filterArray):
                        print("Adding package %s to filtered cache" % (packageId))
                        packages.append(package)
                        break
        self.saveFile(location, packages)

    def updatePackages(self):
        packageList = self.getPackageList()
        numberOfPackages = len(packageList)
        for num, packageId in enumerate(packageList):
            print("Fetching package %d out of %d" % (num + 1, numberOfPackages))
            packageFile = os.path.join(self.packagesFolder, packageId)
            if(os.path.exists(packageFile)):
                print("Package %s already exists in the cache" % packageId)
                continue
            try:
                package = self.ckanClient.package_entity_get(packageId)
                self.saveFile(packageFile, package)
            except BaseException as e:
                print("Could not fetch %s because of %s" % (packageId, str(e)))

    def updateRdfCache(self):
        """ 
            Get the RDF representations of packages from CKAN
            CKAN needs CKAN+DCAT extension installed
        """
        packageList = self.getPackageList()
        numberOfPackages = len(packageList)
        for num, packageId in enumerate(packageList):
            print("Fetching package %d out of %d" % (num + 1, numberOfPackages))
            packageFile = os.path.join(self.rdfPackagesFolder, packageId)
            if(os.path.exists(packageFile)):
                print("Package %s already exists in the cache" % packageId)
                continue
            try:
                packageUrl = self.ckanBaseUrl + "dataset/" + packageId + ".rdf"
                package = requests.get(packageUrl)
                self.saveFileRaw(packageFile, package.content)
            except BaseException as e:
                print("Could not fetch %s because of %s" % (packageId, str(e)))

    def getPackageList(self):
        if(self.isPackageListOutdated()):
            self.updatePackageList()
            return self.loadFile(self.packageListFile)
        else:
            return self.loadFile(self.packageListFile)


    def updatePackageList(self):
        packageList = self.ckanClient.package_list()
        self.saveFile(self.packageListFile, packageList)

    def isPackageListOutdated(self):
        #Does not exist
        if(not os.path.isfile(self.packageListFile)):
            print("%s does not exists!" % self.packageListFile)
            return True

        #Is older than 1 week (all functions are in seconds)
        packageListAge = time.time() - os.path.getmtime(self.packageListFile)
        week = 604800 #seconds
        month = 2419200 #seconds
        if(packageListAge > month):
            print("%s is older than a month!" % self.packageListFile)
            return True

        #File empty (corrupted?)
        if(os.stat(self.packageListFile).st_size == 0):
            print("%s is empty!" % self.packageListFile)
            return True

        return False

    def normalizeFormat(self, format):
        if(format in ckanaggregatorpy.assets.formats.RDF_N3):
            return 'n3'
        elif(format in ckanaggregatorpy.assets.formats.RDF_TTL):
            return 'ttl'
        elif(format in ckanaggregatorpy.assets.formats.RDF_NT):
            return 'nt'
        elif(format in ckanaggregatorpy.assets.formats.RDF_XML):
            return 'rdf'
        elif(format in ckanaggregatorpy.assets.formats.RDF_SPARQL):
            return 'sparql'
        else:
            return 'rdf'
