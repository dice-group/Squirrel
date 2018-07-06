#!/usr/bin/env python
import subprocess
#import json
#import gzip
from tld import get_tld



def dump(str):
    str2 = str
    res = get_tld(str2, as_object=True)
    str2 = res.domain
    cmd = "sudo ckanapi dump datasets --all -O " + str2 + ".jsonl.gz -z -p 1 -r " + str
    str3 = str2+".jsonl.gz"
    print("starting terminal with " + cmd)
    a = 0
    c = 0
    print("a is ", a , "c is ", c)
    try:
        subprocess.check_output(["sudo","ckanapi","dump","datasets","--all","-O",str3,"-z","-p","4","-r",str])
        a = 0
        #os.system(cmd)
    except:
        print("ckan dumping failed unexpectedly")
        a = 1

    # except CKANAPIError:
    #     print('reduce threads')
    # except IOError(Exception):
    #     #print(ioe)
    #     c = 2
    # except BaseException:
    #     logging.exception("exception")
    #     a = 1
    #     #print str(error)
    # else:
    #     print("successful dump")
    #     a.append(2)
    finally:
        print("exiting ckancrawler")
        b = 0


    return a,b,c

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


