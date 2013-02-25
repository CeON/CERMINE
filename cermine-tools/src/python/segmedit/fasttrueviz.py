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


from StringIO import StringIO
import document
import config
from xml.sax import make_parser
from xml.sax.handler import ContentHandler, EntityResolver
from xml.sax.xmlreader import InputSource, AttributesImpl
from xml.sax.saxutils import XMLGenerator

ZONE_LABELS = dict((zone.name, zone) for zone in config.ZONE_LABELS)


class EmptyEntityResolver(EntityResolver):

    def resolveEntity(self, publicId, systemId):
        input = InputSource()
        input.setByteStream(StringIO())
        return input


def handleElement(cls):
    def closure(function):
        function.handlerClass = cls
        return function
    return closure


class ElementHandler(ContentHandler):

    def __init__(self, parser, parent, name, attrs):
        self._parser = parser
        self._parent = parent
        self.name = name
        self._parser.setContentHandler(self)
        self._handle = None
        self.value = None
        self.enter(attrs)

    def enter(self, attrs):
        pass

    def startElement(self, name, attrs):
        if hasattr(self, name):
            func = getattr(self, name)
            if hasattr(func, 'handlerClass'):
                cls = func.handlerClass
                self._handle = func
            else:
                cls = ElementHandler
                self._handle = self._baseHandle
        else:
            cls = ElementHandler
            self._handle = self._baseHandle
        cls(self._parser, self, name, attrs)

    def _baseHandle(self, value):
        pass

    def endElement(self, name):
        if name == self.name:
            self._parser.setContentHandler(self._parent)
            self._parent._handle(self.leave())
        else:
            raise NotImplementedError()

    def leave(self):
        return self.value


class PropertyElementHandler(ElementHandler):

    def enter(self, attrs):
        self.value = attrs['Value']


class VertexElementHandler(ElementHandler):

    def enter(self, attrs):
        self.value = float(attrs['x']), float(attrs['y'])


class CornersElementHandler(ElementHandler):

    def enter(self, attrs):
        self.value = document.BoundsBuilder()

    @handleElement(VertexElementHandler)
    def Vertex(self, point):
        self.value.expandByVertex(*point)

    def leave(self):
        return self.value.getBounds()


class CharacterElementHandler(ElementHandler):

    def enter(self, attrs):
        self.value = document.DocumentChar(document.Bounds(0.0, 0.0, 0.0, 0.0), '')

    @handleElement(CornersElementHandler)
    def CharacterCorners(self, bounds):
        self.value.bounds = bounds

    @handleElement(PropertyElementHandler)
    def GT_Text(self, text):
        self.value.text = text


class WordElementHandler(ElementHandler):

    def enter(self, attrs):
        self.value = document.DocumentWord()

    @handleElement(CharacterElementHandler)
    def Character(self, char):
        self.value.addSubitem(char)


class LineElementHandler(ElementHandler):

    def enter(self, attrs):
        self.value = document.DocumentLine()

    @handleElement(WordElementHandler)
    def Word(self, word):
        self.value.addSubitem(word)


class ClassificationElementHandler(ElementHandler):

    def enter(self, attrs):
        self.value = config.DEFAULT_ZONE_LABEL

    @handleElement(PropertyElementHandler)
    def Category(self, label):
        self.value = ZONE_LABELS.get(label.lower(), config.DEFAULT_ZONE_LABEL)


class ZoneElementHandler(ElementHandler):

    def enter(self, attrs):
        self.value = document.DocumentZone(config.DEFAULT_ZONE_LABEL)

    @handleElement(LineElementHandler)
    def Line(self, line):
        self.value.addSubitem(line)

    @handleElement(ClassificationElementHandler)
    def Classification(self, label):
        self.value.label = label


class PageElementHandler(ElementHandler):

    def enter(self, attrs):
        self.value = document.DocumentPage()

    @handleElement(ZoneElementHandler)
    def Zone(self, zone):
        self.value.addSubitem(zone)


class DocumentElementHandler(ElementHandler):

    def enter(self, attrs):
        self.value = []

    @handleElement(PageElementHandler)
    def Page(self, page):
        self.value.append(page)


class DocumentHandler(ContentHandler):

    def __init__(self, parser, cls):
        self._parser = parser
        self._cls = cls
        self.value = None

    def startElement(self, name, attrs):
        self._cls(self._parser, self, name, attrs)

    def endElement(self, name):
        pass

    def _handle(self, value):
        self.value = value


class Generator(object):

    def __init__(self, writer):
        self._handler = XMLGenerator(writer, 'utf-8')

    def startElement(self, name, attrs={}):
        self._handler.startElement(name, AttributesImpl(attrs))

    def endElement(self, name):
        self._handler.endElement(name)
        self._handler.characters('\n')

    def putElement(self, name, attrs):
        self.startElement(name, attrs)
        self.endElement(name)

    def putProperty(self, name, value):
        self.putElement(name, {'Value': value})

    def putVertex(self, x, y):
        self.putElement('Vertex', {'x': str(x), 'y': str(y)})

    def putItemBounds(self, name, item):
        self.startElement(name)
        self.putVertex(item.bounds.x1, item.bounds.y1)
        self.putVertex(item.bounds.x2, item.bounds.y1)
        self.putVertex(item.bounds.x2, item.bounds.y2)
        self.putVertex(item.bounds.x1, item.bounds.y2)
        self.endElement(name)

    def putLanguage(self, type, script, codeset):
        self.putElement('Language', {'Type': type, 'Script': script, 'Codeset': codeset})

    def putFont(self, type, style, spacing, size):
        self.putElement('Font', {'Type': type, 'Style': style, 'Spacing': spacing, 'Size': size})

    def putChar(self, char):
        self.startElement('Character')
        self.putProperty('CharacterID', '')
        self.putItemBounds('CharacterCorners', char)
        self.putProperty('CharacterNext', '')
        self.putProperty('GT_Text', char.text)
        self.endElement('Character')

    def putWord(self, word):
        self.startElement('Word')
        self.putProperty('WordID', '')
        self.putItemBounds('WordCorners', word)
        self.putProperty('WordNext', '')
        self.putProperty('WordNumChars', '')
        for char in word:
            self.putChar(char)
        self.endElement('Word')

    def putLine(self, line):
        self.startElement('Line')
        self.putProperty('LineID', '')
        self.putItemBounds('LineCorners', line)
        self.putProperty('LineNext', '')
        self.putProperty('LineNumChars', '')
        for word in line:
            self.putWord(word)
        self.endElement('Line')

    def putClassification(self, category, type):
        self.startElement('Classification')
        self.putProperty('Category', category)
        self.putProperty('Type', type)
        self.endElement('Classification')

    def putZone(self, zone):
        self.startElement('Zone')
        self.putProperty('ZoneID', '')
        self.putItemBounds('ZoneCorners', zone)
        self.putProperty('ZoneNext', '')
        self.putElement('ZoneInsets', {'Top': '', 'Bottom': '', 'Left': '', 'Right': ''})
        self.putProperty('ZoneLines', '')
        self.putClassification(zone.label.name, '')
        for line in zone:
            self.putLine(line)
        self.endElement('Zone')

    def putPage(self, page):
        self.startElement('Page')
        self.putProperty('PageID', '')
        self.putProperty('PageType', '')
        self.putProperty('PageNumber', '')
        self.putProperty('PageColumns', '')
        self.putProperty('PageNext', '')
        self.putProperty('PageZones', '')
        for zone in page:
            self.putZone(zone)
        self.endElement('Page')

    def writeDocument(self, pages):
        self._handler.startDocument()
        self.startElement('Document')
        self.putProperty('DocID', '')
        self.putProperty('DocTitle', '')
        self.putProperty('DocPubName', '')
        self.putProperty('DocVolNum', '')
        self.putProperty('DocIssueNum', '')
        self.putProperty('DocMargins', '')
        self.putProperty('DocDate', '')
        self.putProperty('DocPages', '')

        self.startElement('DocImage')
        self.putProperty('Name', '')
        self.putProperty('Format', '')
        self.putProperty('Depth', '')
        self.putProperty('Compression', '')
        self.putProperty('Capture', '')
        self.putProperty('Quality', '')
        self.endElement('DocImage')

        self.putLanguage('', '', '')
        self.putFont('', '', '', '')
        self.putProperty('ReadingDir', '')
        self.putProperty('CharOrient', '')
        self.putClassification('', '')
        self.putProperty('GT_Text', '')
        for page in pages:
            self.putPage(page)
        self.endElement('Document')
        self._handler.endDocument()


class Transformer(document.Transformer):

    def loadPages(self, reader):
        parser = make_parser()
        handler = DocumentHandler(parser, DocumentElementHandler)
        parser.setEntityResolver(EmptyEntityResolver())
        parser.setContentHandler(handler)
        parser.parse(reader)
        return handler.value

    def dumpPages(self, pages, writer):
        generator = Generator(writer)
        generator.writeDocument(pages)

load = document.FileStorage(Transformer()).load


if __name__ == "__main__":
    print "This is a module, don't run it as a program"
