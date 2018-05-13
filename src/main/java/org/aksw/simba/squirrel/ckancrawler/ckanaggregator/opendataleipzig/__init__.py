import os.path
from ckanclient import CkanClient

cacheFolder=os.path.abspath(os.path.join(os.path.dirname(os.path.realpath(__file__)), "../../data/opendataleipzig"))
ckanApiUrl='https://opendata.leipzig.de/api'
ckanBaseUrl='https://opendata.leipzig.de/'
ckanClient = CkanClient(base_location=ckanApiUrl)
