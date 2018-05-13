import os.path
from ckanclient import CkanClient

cacheFolder=os.path.abspath(os.path.join(os.path.dirname(os.path.realpath(__file__)), "../../data/pdeu"))
ckanBaseUrl='http://publicdata.eu/'
ckanApiUrl='http://publicdata.eu/api'
ckanClient = CkanClient(base_location=ckanApiUrl)
