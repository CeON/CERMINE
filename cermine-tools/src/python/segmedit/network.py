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


from wsgiref.handlers import format_date_time
import fasttrueviz as trueviz
import json
from collections import namedtuple
from MultipartPostHandler import MultipartPostHandler
import urllib2
import config
import profile
import os
import readpdf
from urllib import quote_plus

DocumentInfo = namedtuple('DocumentInfo', 'id text status comment username')


class IfModifiedSinceHandler(urllib2.BaseHandler):

    def __init__(self, stamp):
        self.stamp = stamp

    def http_request(self, request):
        request.add_header('If-Modified-Since', format_date_time(self.stamp))
        return request

    def http_error_304(self, req, fp, code, msg, hdrs):
        return fp


def url(suffix, *args):
    return str('http://%s/%s/%s' % (config.NETWORK_ADDRESS, profile.current()['username'], suffix % args))


def getJSON(path, *args):
    return json.load(urllib2.urlopen(url(path, *args)))


def getCurrentDocuments():
    return [DocumentInfo(**obj) for obj in getJSON('list_current')]


def getAvailableDocuments():
    return [DocumentInfo(**obj) for obj in getJSON('list_available')]


def getDocument(id):
    retvalue = getJSON('documents/%s', id)
    return retvalue and DocumentInfo(**retvalue)

def _downloadFile(url, filename):
    path = profile.current().directory + os.path.sep + 'netfiles'
    if not os.path.isdir(path):
            os.mkdir(path)
    path += os.path.sep + filename
    if os.path.exists(path):
        handler = IfModifiedSinceHandler(os.path.getmtime(path))
        netfile = urllib2.build_opener(handler).open(url)
    else:
        netfile = urllib2.urlopen(url)
    if netfile.code != 304:
        with open(path, 'wb') as locfile:
            locfile.write(netfile.read())
    return path


def getPDF(id):
    return readpdf.Pdf(_downloadFile(url('pdfs/%s', id), '%s.pdf' % id))


def getXML(id):
    return trueviz.load(_downloadFile(url('xmls/%s', id), '%s.xml' % id))


def setDocumentStatus(id, status):
    return getJSON('set_status/%s?status=%s', id, status)


def setDocumentComment(id, comment):
    if isinstance(comment, unicode):
        comment = comment.encode('utf-8')
    return getJSON('set_comment/%s?comment=%s', id, quote_plus(comment))


def sendXML(id, file):
    opener = urllib2.build_opener(MultipartPostHandler)
    return json.load(opener.open(url('send/%s', id), {'file': file}))


#class NetworkStorage(document.Storage):
#
#    class Data(document.Storage.Data):
#        def __init__(self, id):
#            self.id = id
#
#    def openReader(self, data):
#        return urllib2.urlopen('http://localhost:8080/abc/xmls/%s' % data.id)
#
#    def openWriter(self, data, status=None, comment=None):
#        self._status = status
#        self._comment = comment
#        self._id = data.id
#        return StringIO()
#
#    def closeWriter(self, writer, success):
#        if success:
#            urllib2.urlopen('http://localhost:8080/abc/send/%s' % self._id,
#                'status=%s&comment=%s' % (self._status, self._comment))
#        writer.close()
#
#load = NetworkStorage(trueviz.Transformer()).load

if __name__ == "__main__":
    print "This is a module, don't run it as a program"
