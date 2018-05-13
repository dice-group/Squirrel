import os.path
from ckanclient import CkanClient

cacheFolder=os.path.abspath(os.path.join(os.path.dirname(os.path.realpath(__file__)), "../../data/datahubio"))
ckanApiUrl='http://datahub.io/api'
ckanBaseUrl='http://datahub.io/'
ckanClient = CkanClient(base_location=ckanApiUrl)
