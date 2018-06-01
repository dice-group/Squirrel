import requests

url2 = 'https://www.google.com/'
requests.request("GET",url2)

url = 'https://api.nal.usda.gov/ndb/V2/reports?ndbno=01009&ndbno=45202763&ndbno=35193&type=b&format=xml&api_key=dcj2IihqQUe0XunKypsXv9vJBChxNg2Dud6er4Ae'
requests.request("GET",url)
