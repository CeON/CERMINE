#!/usr/bin/env python
# encoding: utf-8

###
# Copyright 2011 University of Warsaw, Krzysztof Rusek
# 
# This file is part of SegmEdit.
#
# SegmEdit is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# SegmEdit is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with SegmEdit.  If not, see <http://www.gnu.org/licenses/>.


from __future__ import with_statement

import config
import os
import json

class Profile(object):

    def __init__(self):
        self.directory = config.VAR_DIRECTORY

        if not os.path.isdir(self.directory):
            os.mkdir(self.directory)

        if self.exists('data.json'):
            with self.open('data.json') as reader:
                self._data = json.load(reader)
        else:
            self._data = dict()

    def __getitem__(self, key):
        return self._data[key]

    def __contains__(self, key):
        return key in self._data

    def __setitem__(self, key, value):
        self._data[key] = value

    def __delitem__(self, key):
        del self._data[key]

    def get(self, key, default=None):
        return self._data.get(key, default)

    def save(self):
        with self.open('data.json', 'w') as writer:
            json.dump(self._data, writer)

    def exists(self, path):
        return os.path.exists(self.directory + os.path.sep + path)

    def open(self, path, mode='r'):
        return open(self.directory + os.path.sep + path, mode)

_current = None

def current():
    global _current
    if _current is None:
        _current = Profile()
    return _current

if __name__ == "__main__":
    print "This is a module, don't run it as a program"
