#!/usr/bin/env python

from multiprocessing import Pool
from functools import partial
import os, re
import tarfile


PMC = "ftp.ncbi.nlm.nih.gov"

def clean(prefix):
	prefix = "%s/pub/pmc/%s" % (PMC, prefix)
	if(os.path.exists(prefix)):
		files = os.listdir(prefix)
		dirs = [f for f in files if os.path.isdir("%s/%s" % (prefix, f))]
		for dir in dirs:
			subdir_files = os.listdir("%s/%s" % (prefix, dir))
			if len(subdir_files) == 1:
				os.remove("%s/%s/%s" % (prefix, dir, subdir_files[0]))
				os.rmdir("%s/%s" % (prefix, dir))

if __name__ == "__main__":
    prefixes = ["%02x/%02x" % (x, y) for x in xrange(0, 256) for y in xrange(0, 256)]
    pool = Pool(processes=15)
    pool.map(clean, prefixes)
    pool.close()
    pool.join()
