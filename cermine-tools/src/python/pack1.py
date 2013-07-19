#!/usr/bin/env python

import os
import sys
import Queue
import threading
import hashlib 
import tarfile
from multiprocessing import Pool
from functools import partial
from subprocess import call

THREADS = 8

def pack_and_send(output_path, path):
    md5 = hashlib.md5()
    md5.update(path)
    tarname = md5.hexdigest()[:10] + '.tar'
    print(tarname)
    tarpath = output_path + '/' + tarname, "w"
    tar = tarfile.open(tarpath)
    tar.add(path)
    tar.close()
    call("scp %s hadoop.vls:/mnt/tmp/pszostek_pubmed" % tarpath)
    print(tarpath)

if __name__ == "__main__":
    if len(sys.argv) != 3:
        print("Usage: " + sys.argv[0] + " input_path output_path")
        exit(1)

    path = sys.argv[1]
    prefixes = ["%02x" % x for x in xrange(0,256)]
    paths = [path + '/' + prefix for prefix in prefixes]
    output_path = sys.argv[2]
    
    if not os.path.exists(output_path):
        os.makedirs(output_path)

    pool = Pool(processes=8)
    pool.map(partial(pack_and_send, output_path=output_path), paths)
    
    

