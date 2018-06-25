import requests

r = requests.get("http://data.tc.gc.ca/v1.3/api/eng/vehicle-recall-database/recall?format=json")
x = r.headers
print x


print r.headers['content-type']

print r
