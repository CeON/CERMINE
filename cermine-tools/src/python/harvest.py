#!/usr/bin/env python

from multiprocessing import Pool
from subprocess import call
from functools import partial
import ftplib as ftp
import os
import threading, Queue


PMC = "ftp.ncbi.nlm.nih.gov"

class ConnClass():
    def setup_connection(self):
        conn = ftp.FTP(PMC, "anonymous", "anonymous")
        conn.cwd("pub/pmc")
        return conn

class ListThread(threading.Thread, ConnClass):
    def __init__(self, file_q,local_files, prefixes):
        super(ListThread, self).__init__()
        self.file_q = file_q
        self._local_files = local_files
        self.prefixes = prefixes
        self.stoprequest = threading.Event()
        self._conn = self.setup_connection()
        
    def run(self):
        while not self.stoprequest.isSet():
            for prefix in self.prefixes:
		print "PREF " + prefix
                remotes_partial = self.get_remote_files(prefix)
                remotes_partial = set(remotes_partial) - set(self._local_files)
                if len(remotes_partial):
	   	    print remotes_partial
                    map(partial(Queue.Queue.put, self.file_q), list(remotes_partial))

    def get_remote_files(self, prefix):
        fl = self._conn.nlst(prefix)
        return fl

class DownloadThread(threading.Thread):
    def __init__(self, file_q):
        super(DownloadThread, self).__init__()
        self.file_q = file_q
        self.stoprequest = threading.Event()

    def run(self):
        while not self.stoprequest.isSet():
            filename = self.file_q.get()
	    print(">> %s" % filename)
            self.download_file(filename)

    def download_file(self, filename):
	cmd = "wget --mirror 'ftp://%s/pub/pmc/%s'" % (PMC, filename)
	print(cmd)
        call(cmd, shell=True)
        
def get_local_files(prefix):
    paths = [] 
    try:
        files = os.listdir("ftp.ncbi.nlm.nih.gov/pub/pmc/" + prefix)
        for file in files:
            paths.append("%s/%s" % (prefix, file))
        return paths
    except OSError:
        return []

if __name__ == "__main__":
    file_q = Queue.Queue()
    prefixes = ["%02x/%02x" % (x,y) for x in xrange(0,256) for y in xrange(0,256)]

    pool = Pool(processes=15)
    local_files = pool.map(get_local_files, prefixes)
    local_files = [item for sublist in local_files for item in sublist]
    local_files = set(local_files)
    pool.close()
    pool.join()
###
    p_len = len(prefixes)
    assert p_len % 4 == 0
    prefixes = [prefixes[i*p_len/4:(i+1)*p_len/4] for i in xrange(0,4)]
    file_q = Queue.Queue()
    list_pool = [ListThread(file_q=file_q, local_files=local_files, prefixes=prefixes[i]) for i in xrange(0,4)]
    map(threading.Thread.start, list_pool)
    download_pool = [DownloadThread(file_q=file_q) for _ in xrange(0,8)]
    map(threading.Thread.start, download_pool)
    
