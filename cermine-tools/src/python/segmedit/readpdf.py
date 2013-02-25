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


import config
import os, sqlite3, tempfile, datetime, hashlib, subprocess

class PageUnavailable(Exception):
    pass

class ConvertionError(Exception):
    pass

class Pdf(object):
    def __init__(self, path):
        """
        converts pdf file to jpgs,
        counts number of pages
        """
        # convert path to absolute path
        path = os.path.abspath(path)

        # init directory and index file -- if necessary
        directory = config.VAR_DIRECTORY
        idxfile = config.INDEX_FILE

        if not os.path.isdir(directory):
            os.mkdir(directory)

        sqliteconn = sqlite3.connect(idxfile)
        crs = sqliteconn.cursor()
        try:
            crs.execute("""CREATE TABLE pdfinfo (
                path text,
                md5 text,
                lastaccess datetime,
                pagescount int,
                pagesdir text
            )""")
        except sqlite3.OperationalError:
            pass

        # check if the file is already in the directory
        f = file(path, 'rb')
        m = hashlib.md5(f.read(8096))
        md5sum = m.hexdigest()

        crs.execute("""SELECT rowid, pagescount, pagesdir FROM pdfinfo
            WHERE path = ? AND md5 = ?""",
            (path, md5sum))

        row = crs.fetchone()
        if row:
            sqlid, self.pagescount, self.pagesdir = row
            crs.execute("""UPDATE pdfinfo SET lastaccess = ?
                WHERE rowid = ?""", (datetime.datetime.now(), sqlid))
        else:
            self.pagesdir = tempfile.mkdtemp(dir=config.VAR_DIRECTORY,
                prefix="pdfpages")

            #status = os.system(" ".join([config.CONVERT, config.CONVERT_OPTIONS,
            #    "\"" + path + "\"", "\"" + self.pagesdir + os.path.sep +
            #    "page_%d.jpg" + "\""]))
            pipe = subprocess.Popen(" ".join([config.CONVERT, config.CONVERT_OPTIONS,
                "\"" + path + "\"", "\"" + self.pagesdir + os.path.sep +
                "page_%d.jpg" + "\""]), shell=True, stdout=subprocess.PIPE,
                stderr=subprocess.PIPE)
            stderrContent = pipe.stderr.read()
            status = pipe.wait()

            if status != 0:
                crs.close()
                sqliteconn.close()
                raise ConvertionError(unicode(stderrContent, errors="ignore"))

            self.pagescount = len(os.listdir(self.pagesdir))

            crs.execute("""INSERT INTO pdfinfo
                    (path, md5, lastaccess, pagescount, pagesdir)
                    VALUES (?, ?, ?, ?, ?)""",
                (path, md5sum, datetime.datetime.now(),
                    self.pagescount, self.pagesdir))

        sqliteconn.commit()
        crs.close()
        sqliteconn.close()



    def getNbOfPages(self):
        return self.pagescount

    def getPage(self, pagenb):
        if pagenb < 1 or pagenb > self.pagescount:
            raise PageUnavailable
        pagefile = self.pagesdir + os.path.sep + \
            "page_%d.jpg" % (pagenb - 1,)
        currentpagejpg = open(pagefile, "rb").read()
        return currentpagejpg

    def __len__(self):
        return self.pagescount

    def getPageStream(self, index):
        # TODO: a może zwrócić None?
        if not 0 <= index < self.pagescount:
            raise PageUnavailable
        path = self.pagesdir + os.path.sep + "page_%d.jpg" % (index)
        return open(path, "rb")
