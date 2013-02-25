#!/usr/bin/env python

from multiprocessing import Pool
from subprocess import call
from functools import partial
import ftplib as ftp
import os
import threading, Queue
import sys
import shutil

xmls_path = sys.argv[1]
pubmed_path = sys.argv[2]



def get_local_files(prefix):
    paths = [] 
    try:
        files = os.listdir(pubmed_path + "/" + prefix)
        for file in files:
            paths.append((file, "%s/%s" % (prefix, file)))
        return paths
    except OSError:
        return []

file_q = Queue.Queue()
prefixes = ["%02x/%02x" % (x,y) for x in xrange(0,256) for y in xrange(0,256)]

pool = Pool(processes=6)
local_files = pool.map(get_local_files, prefixes)
local_files = [item for sublist in local_files for item in sublist]
local_files = dict(local_files)
pool.close()
pool.join()

xmls = os.listdir(xmls_path)
pdfs = [xml[-3]+"pdf" for xml in xmls]

for pdf in pdfs:
    location = local_files[pdf]
    shutil.copy(pumbed_path + "/" + location, xmls_path)