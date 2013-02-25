#!/usr/bin/env python

import os
import sys
import Queue
import threading
import hashlib 
import tarfile
from multiprocessing import Pool

THREADS = 8
FILES_IN_A_TAR = 1000

class ListThread(threading.Thread):
    def __init__(self, path_q, file_q, name):
        super(ListThread, self).__init__(name=name)
        self._path_q = path_q
        self._file_q = file_q
    
    def run(self):
        while True:
            path = self._path_q.get(block=True)
            if path is None:
                break
            if os.path.isdir(path):
                files = os.listdir(path)
                for filename in files:
                    filepath = path + '/' + filename
                    if os.path.isfile(filepath):
                        self._file_q.put(filepath)
                    else:
                        self._path_q.put(filepath)
class PackingThread(threading.Thread):
    def __init__(self, file_q, output_path, name):
        super(PackingThread, self).__init__(name=name)
        self._file_q = file_q
        self._pack_set = set()
        self._output_path = output_path

    def run(self):
        while not self._file_q.empty():
            try:
                file_path = self._file_q.get(block=False)
                filenames = os.listdir(file_path)
                for filename in filenames:
                    self._pack_set.add(filename)
                if len(self._pack_set) > FILES_IN_A_TAR:
                    self.compress()
                    self._pack_set = set()
            except Queue.Empty:
                break
        if len(self._pack_set) > 0:
            self.compress()

    def compress(self):
        md5calc = hashlib.md5()
        md5calc.update(''.join(self._pack_set))
        targz_name = md5calc.hexdigest()[:10] + '.tar'
        targz = tarfile.open(self._output_path+'/'+targz_name, mode="w")
        for filepath in self._pack_set:
            targz.add(filepath)
        print(self.name + " " + targz_name)
        targz.close()

if __name__ == "__main__":
    if len(sys.argv) != 3:
        print("Usage: " + sys.argv[0] + " input_path output_path")
        exit(1)

    path = sys.argv[1]
    output_path = sys.argv[2]

    if not os.path.exists(output_path):
        os.makedirs(output_path)

    pool = Pool(processes=15)
    files_path = [item for sublist in files_path for item in sublist]

    file_q = Queue.Queue()

    for filepath in files_path:
        file_q.put(path + '/' + filepath)

    pool = []
    for i in xrange(0, THREADS):
        pool.append(PackingThread(name=i, file_q=file_q, output_path=output_path))
    for thread in pool:
        thread.start()
    for thread in pool:
        thread.join()
    

