#!/usr/bin/env python

from __future__ import division 
from __future__ import print_function
from segmedit.fasttrueviz import load
import sys
import os.path
import os
import re
import itertools
import Queue
import threading
from multiprocessing import Pool
from rprint import print

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
            except Queue.Empty:
                continue
	    except SAXParseException:
		factor = 0
            self.write_q.put('%s %4.3f\n' % (os.path.basename(filename), factor))

    def join(self):
        self.stoprequest.set()
        super(CheckThread, self).join()


if len(sys.argv) != 3:
    print("Usage: remove_empty.py /path/to/xml/directory")
    exit(1)
    
dir_path = sys.argv[2]
all_files = []

for root, _, files in os.walk(dir_path):
    for filename in files:
        all_files.append(root + filename)
print("number of all files: %d" % len(all_files))

for file in all_files:
    file_q.put(file)

check_threads_pool = [CheckThread(write_q, file_q) for _ in xrange(0,10)] 
for thread in pool:
    thread.start()
    
while not file_q.empty():
    continue

[thread.join() for thread in check_threads_pool]

writer.join()
log_file.close()
