import urllib2
import json

req = urllib2.urlopen('https://developer.nrel.gov/api/alt-fuel-stations/v1/nearest.json?api_key=dcj2IihqQUe0XunKypsXv9vJBChxNg2Dud6er4Ae&location=Denver+CO')

http = req.read()
print http

httphead = req.headers
print httphead


