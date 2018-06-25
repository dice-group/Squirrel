#from ckanapi import

#url = 'https://demo.ckan.org/api'
#abc = requests.request("GET",url)

#print abc


# from ckanapi import RemoteCKAN
# from ckanapi.cli import dump
#
#
# thing = ['dump', 'datasets']
# #thing = ['dump datasets']
# #arguments= ['-all',['-O', 'datasets.jsonl.gz'], '-z',['-p', 4],['-r', 'https://demo.ckan.org/']]
# arguments= ['-all', '-O', 'datasets.jsonl.gz', '-z', '-p', 1, '-r', 'https://demo.ckan.org/']
# ckan = RemoteCKAN('http://demo.ckan.org/api')
#
# # try:
# #     dump.dump_things(ckan, thing, arguments)
# # except (TypeError):
# #     pass
#
# dump.dump_things(ckan, thing, arguments)


# from ckanapi.cli import dump
# from ckanapi import RemoteCKAN
# thing = ['datasets']
# arguments= ['-all', '-O', 'datasets.jsonl.gz', '-z', '-p', 1, '-r', 'https://demo.ckan.org/']
# ckan = RemoteCKAN('http://demo.ckan.org/api')
# dump.dump_things(ckan, thing, arguments)

# from ckanapi.cli import main
#
# arguments = 'ckanapi dump datasets --all -O datase.jsonl.gz -z -l -p 1 -r https://demo.ckan.org/'
# main.main(arguments)

import os

os.system("sudo ckanapi dump datasets --all -O datasetcanada.jsonl.gz -z -p 2 -r https://demo.ckan.org/")

