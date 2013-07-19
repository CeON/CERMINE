#!/usr/bin/env python

from multiprocessing import Pool
from subprocess import call
import sys

PMC = "ftp://ftp.ncbi.nlm.nih.gov/pub/pmc/"

def mirror(url):
    print(url)
    call("wget --mirror " + PMC + url, shell=True)
		

if __name__ == "__main__":
    file = sys.argv[1]
    file = open(file, "r")
    files = [f.strip() for f in file.readlines()]
    pool = Pool(processes=15)
    urls = ("%02x" % integer for integer in reversed(xrange(11, 256)))
    pool.map(mirror,  urls)
    pool.close()
    pool.join()
