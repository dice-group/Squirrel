import os
import requests
import re

import ckanaggregatorpy.datagov.package_cache
import ckanaggregatorpy.datahubio.package_cache
import ckanaggregatorpy.pdeu.package_cache
import ckanaggregatorpy.opencanada.package_cache
import ckanaggregatorpy.assets.formats

from ckanaggregatorpy.interfaces.loadsaveinterface import LoadSaveInterface

class CkanQuery(LoadSaveInterface):
    formatsFile = os.path.abspath(os.path.join(os.path.dirname(os.path.realpath(__file__)), "../../data/formats.set"))
    csvPropertyMatchingFile= os.path.abspath(os.path.join(os.path.dirname(os.path.realpath(__file__)), "../../data/propertymatchingdata.pickle"))
    propertyMatchingDataFolder = os.path.abspath(os.path.join(os.path.dirname(os.path.realpath(__file__)), "../../data/csvresources/propertymatchingdata"))
    ckanCaches = [
            ckanaggregatorpy.datagov.package_cache.PackageCache(),
            ckanaggregatorpy.datahubio.package_cache.PackageCache(),
            ckanaggregatorpy.pdeu.package_cache.PackageCache(),
            ckanaggregatorpy.opencanada.package_cache.PackageCache()
            ]
    def __init__(self):
        pass

    def getRdfPackagesRdfResourcesOnly(self):
        results = []
        for ckanCache in self.ckanCaches:
            rdfpackages = ckanCache.getRdfPackagesRdfResourcesOnly()
            prefix = ckanCache.prefix
            ckanApiUrl = ckanCache.ckanApiUrl
            #Prefixes are mapped to name in ckan_catalog table in LODStats_WWW
            results.append({'rdfpackages': rdfpackages, 'prefix': prefix, 'ckanApiUrl': ckanApiUrl})
        return results

    def getCsvPackages(self):
        results = []
        for ckanCache in self.ckanCaches:
            csvpackages = ckanCache.getCsvPackages()
            prefix = ckanCache.prefix
            ckanApiUrl = ckanCache.ckanApiUrl
            #Prefixes are mapped to name in ckan_catalog table in LODStats_WWW
            results.append({'csvpackages': csvpackages, 'prefix': prefix, 'ckanApiUrl': ckanApiUrl})
        return results

    def getRdfPackagesRdfResourcesOnlyNormalized(self):
        results = []
        for ckanCache in self.ckanCaches:
            rdfpackages = ckanCache.getRdfPackagesRdfResourcesOnly()
            prefix = ckanCache.prefix
            ckanApiUrl = ckanCache.ckanApiUrl
            #Prefixes are mapped to name in ckan_catalog table in LODStats_WWW
            results.append({'rdfpackages': rdfpackages, 'prefix': prefix, 'ckanApiUrl': ckanApiUrl})
        return results

    def dumpRdfPackagesRdfResourcesOnly(self, path='/tmp/ckan_catalogs.pickled'):
        rdfPackages = ckanQuery.getRdfPackagesRdfResourcesOnly()
        file = open(path, 'wb')
        pickle.dump(rdfPackages, file, protocol=pickle.HIGHEST_PROTOCOL)
        file.close()

    def encode(self, string):
        if(string is None):
            return ""
        elif(type(string) is int):
            return string
        elif(string is ""):
            return "N/A"
        else:
            return string.encode('utf-8')

    def getFormats(self):
        if(not os.path.isfile(self.formatsFile)):
            self.updateFormats()
            return self.loadFile(self.formatsFile)
        else:
            return self.loadFile(self.formatsFile)

    def updateFormats(self):
        formats = set()
        for ckanCache in self.ckanCaches:
            packages = ckanCache.getPackagesStream()
            for package in packages:
                for resource in package['resources']:
                    formats.add(resource['format'])
        self.saveFile(self.formatsFile, formats)

    def getLicensesCount(self):
        for ckanCache in self.ckanCaches:
            licenses = {}
            packages = ckanCache.getPackagesStream() # generator
            prefix = ckanCache.prefix
            ckanApiUrl = ckanCache.ckanApiUrl
            for package in packages:
                isopen = package.get('isopen', '')
                license = package.get('license', '')
                license_id = package.get('license_id', '')
                license_title = package.get('license_title', '')
                license_url = package.get('license_url', '')
                if(licenses.get(license_id, 0) != 0):
                    count = licenses.get(license_id)['count'] + 1
                else:
                    count = 1
                licenses[license_id] = {
                    'license': license,
                    'license_id': license_id,
                    'license_title': license_title,
                    'license_url': license_url,
                    'count': count
                }
            f = open('license'+prefix+'.csv', 'wb+')
            for license in licenses:
                csvStringLicense = ", ".join([str(self.encode(license)),
                                              str(self.encode(licenses[license]['count'])),
                                              str(self.encode(licenses[license]['license_title'])),
                                              str(self.encode(licenses[license]['license_url'])),
                                              str(self.encode(licenses[license]['license']))])
                f.write(csvStringLicense + "\n")
            f.close()

    def getCsvRandomSelection15(self):
        """
            This function is used for selecting a set of 15 random csv packages
            from each available data catalogue and saving it in the cache
        """
        results = []
        for ckanCache in self.ckanCaches:
            csvPackagesRandom15 = ckanCache.getCsvPackagesRandom15()
            #Create top property pointing to the first CSV file from the package
            for i, package in enumerate(csvPackagesRandom15):
                package = self.outlineTopCsv(package)
            prefix = ckanCache.prefix
            ckanApiUrl = ckanCache.ckanApiUrl
            results.append({'csvpackagesrandom15': csvPackagesRandom15, 'prefix': prefix, 'ckanApiUrl': ckanApiUrl})
        return results

    def outlineTopCsv(self, package):
        for j,resource in enumerate(package['resources']):
            if(resource['format'] in ckanaggregatorpy.assets.formats.CSV):
                package['topCsvLink'] = resource['url']
                package['topCsvIndex'] = j
                break
        return package

    def getCsvPackageListForPropertyMatching(self):
        if(not os.path.isfile(self.csvPropertyMatchingFile)):
            self.updateCsvPackageListForPropertyMatching()
            return self.loadFile(self.csvPropertyMatchingFile)
        else:
            return self.loadFile(self.csvPropertyMatchingFile)

    def updateCsvPackageListForPropertyMatching(self):
        """
            Iterate through the csv selection from getCsvRandomSelection15
            Filter out invalid csv resources (404, zipped, etc.)
            save proper 15 resources to a file
        """
        csvEndpoints = self.getCsvRandomSelection15()
        csvEndpointsFiltered = []
        for csvEndpoint in csvEndpoints:
            prefix = csvEndpoint['prefix']
            ckanApiUrl = csvEndpoint['ckanApiUrl']
            csvPackages = csvEndpoint['csvpackagesrandom15']
            ckanCache = eval("ckanaggregatorpy.%s.package_cache.PackageCache()"%prefix)
            #topCsvLink = csvPackage['topCsvLink']
            #topCsvIndex = csvPackage['topCsvIndex']

            for i, csvPackage in enumerate(csvPackages):
                r = requests.Request()
                r.status_code = 400
                while(not r.status_code == requests.codes.ok):
                    #only http protocol
                    while(not csvPackage['topCsvLink'].startswith("http")):
                        csvPackage = self.getRandomUniqueCsv(ckanCache, csvPackages)
                    #should have .csv extension
                    csvExtensionRegex = re.compile("\.csv", re.IGNORECASE)
                    while(not csvExtensionRegex.search(csvPackage['topCsvLink'])):
                        csvPackage = self.getRandomUniqueCsv(ckanCache, csvPackages)
                    try:
                        r = requests.get(csvPackage['topCsvLink'], timeout=1)
                        if(not r.status_code == requests.codes.ok):
                            csvPackage = self.getRandomUniqueCsv(ckanCache, csvPackages)
                    except requests.exceptions.ConnectionError as connectionError:
                        print(str(connectionError))
                        csvPackage = self.getRandomUniqueCsv(ckanCache, csvPackages)
                    except requests.exceptions.ReadTimeout as readTimeout:
                        print(str(readTimeout))
                        csvPackage = self.getRandomUniqueCsv(ckanCache, csvPackages)
                csvPackages[i] = csvPackage
            csvEndpointsFiltered.append({'csvpackagesrandom15': csvPackages, 'prefix': prefix, 'ckanApiUrl': ckanApiUrl})

        #Save filtered csv files to the file!
        self.saveFile(self.csvPropertyMatchingFile, csvEndpointsFiltered)
        print "Done!"

    def getRandomUniqueCsv(self, ckanCache, packages):
        package = None
        while(package == None):
            package = self.getOneRandomUniqueCsv(ckanCache, packages)
        return package

    def getOneRandomUniqueCsv(self, ckanCache, packages):
        newPackage = ckanCache.getCsvPackagesRandom(1)[0]
        unique = True
        for package in packages:
            if(package['id'] == newPackage['id']):
                unique = False

        if(unique):
            return self.outlineTopCsv(newPackage)
        else:
            return None

if __name__ == "__main__":
    ckanQuery = CkanQuery()
    #rdfPackages = ckanQuery.getRdfPackagesRdfResourcesOnly()
    #ckanQuery.dumpRdfPackagesRdfResourcesOnly()
    #ckanQuery.getLicensesCount()
    #csvPackages = ckanQuery.getCsvPackages()
    #csvPackagesRandom15 = ckanQuery.getCsvRandomSelection15()
    csvs = ckanQuery.getCsvPackageListForPropertyMatching()
    import ipdb; ipdb.set_trace()
