#!/usr/bin/env python

from multiprocessing import Pool
from subprocess import call
from functools import partial
import ftplib as ftp
import os

PMC = "ftp.ncbi.nlm.nih.gov"

def setup_connection():
	conn = ftp.FTP(PMC, "anonymous", "anonymous")
	conn.cwd("pub/pmc")
	return conn

def get_remote_files(conn, prefix):
	fl = conn.nlst(prefix)
	return fl

def get_local_files(prefix):
	paths = [] 
	try:
		files = os.listdir(prefix)
		for file in files:
			paths.append("%s/%s" % (prefix, file))
		return paths
	except OSError:
		return []
			
if __name__ == "__main__":

	conn = setup_connection()
	prefixes = ["%02x/%02x" % (x,y) for x in xrange(0,256) for y in xrange(0,256)]

	pool = Pool(processes=15)
	locals = pool.map(get_local_files, prefixes) 
	locals = [item for sublist in locals for item in sublist]
	locals = set(locals)

	pool.close()
	pool.join()

	remotes = set()
	output = open("harvest2.output", "w")
	for prefix in prefixes:
		try:
			remotes_partial = get_remote_files(conn, prefix)
		except:
			conn = setup_connection()
			remotes_partial = get_remote_files(conn, prefix)
		remotes_partial = list(set(remotes_partial)-locals)
		if len(remotes_partial) > 0:
			print(remotes_partial)
		for r in remotes:
			output.write(r+'\n')
			remotes.add(r)
		
	output.close()
