#!/usr/bin/env python

from __future__ import division 
from segmedit.fasttrueviz import load
import sys
import os
import re
import itertools
import Queue
import threading
from multiprocessing import Pool

write_q = Queue.Queue()
file_q = Queue.Queue() 

class CheckThread(threading.Thread):
    def __init__(self, write_q, file_q):
        super(CheckThread, self).__init__()
        self.stoprequest = threading.Event()
        self.write_q = write_q
        self.file_q = file_q
    
    def run(self):
        while not self.stoprequest.isSet():
            try:
                filename = self.file_q.get()
                print("check " + filename)
                unknown = 0
                known = 0
                doc = load(filename)
                for page in doc:
                    for zone in page:
                        if zone.label.name == "unknown":
                            unknown += 1
                        else:
                            known += 1
                delete = False
                if known+unknown == 0:
                    delete = True
                    factor = 0
                else:
                    factor = known / (known+unknown)
                self.write_q.put('%s %4.3f\n' % (filename, factor))
            except Queue.Empty:
                continue

    def join(self):
        self.stoprequest.set()
        super(CheckThread, self).join()


class WriteThread(threading.Thread):
    def __init__(self, dest_file, write_q):
        super(WriteThread, self).__init__()
        self.dest_file = dest_file
        self.stoprequest = threading.Event()
        self.write_q = write_q

    def run(self):
        while not self.stoprequest.isSet():
            try:
                write = self.write_q.get()
                print(write)
                self.dest_file.write(write+'\n')
            except Queue.Empty:
                continue

    def join(self):
        self.stoprequest.set()
        super(WriteThread, self).join()

if len(sys.argv) != 2:
    print("Usage: remove_empty.py /path/to/xml/directory")
    exit(1)
    
path = sys.argv[1]
all_files = []
dest_file = open("remove_empty.log", "w")
for root, _, files in os.walk(path):
    for filename in files:
        all_files.append(root + filename)
print(len(all_files))
for file in all_files:
    file_q.put(file)
writer = WriteThread(dest_file, write_q)
writer.start()
pool = [CheckThread(write_q, file_q) for _ in xrange(0,10)] 
for thread in pool:
    thread.start()
while not file_q.empty():
    continue
for thread in pool:
    thread.join()

while not write_q.empty():
    continue
writer.join()