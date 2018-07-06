#!/usr/bin/env python
import subprocess
#import json
#import gzip
from tld import get_tld



def dump(str):
    str2 = str
    res = get_tld(str2, as_object=True)
    str2= res.domain+".jsonl.gz"
    a = 0
    try:
        subprocess.check_output(["sudo","ckanapi","dump","datasets","--all","-O",str2,"-z","-p","1","-r",str])
    except:
        print("ckan dumping failed unexpectedly")
        a = 1
    finally:
        print("exiting ckancrawler")
        b = 0
    return a,b

#TODO: CLEANUP CODE FOR PRINTING SPECIFIC STUFF
#
# def importjson(file):
#     data = []
#     with gzip.open(file, 'rb') as f:
#         for line in f:
#             data.append(json.loads(line))
#
#         for d in data:
#             print(len(d))
#     print len(data)
#


    #os.system("sudo ckanapi dump datasets --all -O datasetcanada.jsonl.gz -z -p 2 -r https://demo.ckan.org/")


