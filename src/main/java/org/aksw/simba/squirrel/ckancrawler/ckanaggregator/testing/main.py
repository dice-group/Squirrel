#!/usr/bin/env python
import os

def dump(str):
    cmd = "sudo ckanapi dump datasets --all -O datasetcanada.jsonl.gz -z -p 1 -r " + str
    print("starting terminal with " + cmd)
    try:
        os.system(cmd)
        print("success")
    except Exception, error:
        print("An exception was thrown!")
        print str(error)
    else:
        print("successful dump")
    finally:
        print("exiting ckancrawler")

    return

#TODO: CLEANUP CODE FOR PRINTING SPECIFIC STUFF



    #os.system("sudo ckanapi dump datasets --all -O datasetcanada.jsonl.gz -z -p 2 -r https://demo.ckan.org/")


