#!/usr/bin/env python

from multiprocessing import Pool
from functools import partial
import os, re
import tarfile


PMC = "ftp.ncbi.nlm.nih.gov"

TARGZ_RE = re.compile(r".*\.tar\.gz")
PDF_RE = re.compile(r".*\.pdf")
NLM_RE = re.compile(r".*\.nxml")

def corename(filename):
    return '.'.join(filename.split('.')[:-1])
    
def targz_corename(filename):
    return '.'.join(filename.split('.')[:-2])

def treat_targz_item(targz, targz_path, item):
	if PDF_RE.match(item.name) or NLM_RE.match(item.name):
		targz.extract(item.name, targz_path)

        #os.rename("%s/%s" % (targz_path, item.name), "%s/%s" % (targz_path, os.path.basename(item.name)))
	#os.rmdir("%s/%s" % (targz_path, os.path.dirname(item.name)))

def treat_targz(prefix, targz_name):
	targz = tarfile.open("%s/%s" % (prefix, targz_name), "r:gz")
	map(partial(treat_targz_item, targz, prefix), targz)

	#remove the .tar.gz after unpacking
	os.remove("%s/%s" % (prefix, targz_name))
	dirname = targz_corename(targz_name)
	nlm = None
	filenames = os.listdir("%s/%s" % (prefix, dirname))
	for filename in filenames:
            if NLM_RE.match(filename):
                nlm = filename
                break
        if nlm != None:
            core = corename(nlm)
            pdfname = "%s.pdf" % core
            ###
            for filename in filenames:
                if filename != nlm and filename != pdfname:
                    os.remove("%s/%s/%s" % (prefix, dirname, filename))
            ###
            new_dirname = re.sub(r'[^a-zA-Z0-9_-]', r'_', dirname)
            print("%s/%s" % (prefix, dirname), "%s/%s" % (prefix, new_dirname))
            os.rename("%s/%s" % (prefix, dirname), "%s/%s" % (prefix, new_dirname))
        else:
            os.rmdir("%s/%s" % (prefix, dirname))

def clean(prefix):
	prefix = "%s/pub/pmc/%s" % (PMC, prefix)
	if(os.path.exists(prefix)):
		try:
			os.remove("%s/.listing" % prefix)
		except:
			pass
		files = os.listdir(prefix)
		targzs = (f for f in files if TARGZ_RE.match(f))
		map(partial(treat_targz, prefix), targzs)

if __name__ == "__main__":
    prefixes = ["%02x/%02x" % (x, y) for x in xrange(0, 256) for y in xrange(0, 256)]
    pool = Pool(processes=15)
    pool.map(clean, prefixes)
    pool.close()
    pool.join()
