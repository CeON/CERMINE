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


import xml.dom.minidom as minidom
import document
import config
import codecs


ZONE_LABELS = dict((zone.name, zone) for zone in config.ZONE_LABELS)


class DOMImporter(object):
    """
    Imports TrueViz documents from DOM tree
    """

    def _getChildren(self, node, nodeName):
        return (childNode for childNode in node.childNodes if childNode.nodeName == nodeName)

    def _getChild(self, node, nodeName):
        for childNode in self._getChildren(node, nodeName):
            return childNode
        return None

    def _loadProperty(self, node, name):
        propNode = self._getChild(node, name)
        return propNode.getAttribute('Value') if propNode else None

    def _loadClassification(self, node):
        clsNode = self._getChild(node, 'Classification')
        label = self._loadProperty(clsNode, 'Category') if clsNode else ''
        return ZONE_LABELS.get(label.lower(), config.DEFAULT_ZONE_LABEL)

    def _loadBounds(self, node, nodeName):
        boundsNode = self._getChild(node, nodeName)
        if boundsNode:
            builder = document.BoundsBuilder()
            for vertexNode in self._getChildren(boundsNode, 'Vertex'):
                builder.expandByVertex(float(vertexNode.getAttribute("x")),
                                       float(vertexNode.getAttribute("y")))
            return builder.getBounds()
        else:
            return document.Bounds(0.0, 0.0, 0.0, 0.0)

    def _loadChar(self, charNode):
        return document.DocumentChar(self._loadBounds(charNode, 'CharacterCorners'),
                            self._loadProperty(charNode, 'GT_Text'))

    def _loadWord(self, wordNode):
        word = document.DocumentWord()
        for charNode in self._getChildren(wordNode, 'Character'):
            word.addSubitem(self._loadChar(charNode))
        return word

    def _loadLine(self, lineNode):
        line = document.DocumentLine()
        for wordNode in self._getChildren(lineNode, 'Word'):
            line.addSubitem(self._loadWord(wordNode))
        return line

    def _loadZone(self, zoneNode):
        zone = document.DocumentZone(self._loadClassification(zoneNode))
        for lineNode in self._getChildren(zoneNode, 'Line'):
            zone.addSubitem(self._loadLine(lineNode))
        return zone

    def _loadPage(self, pageNode):
        page = document.DocumentPage()
        for zoneNode in self._getChildren(pageNode, 'Zone'):
            page.addSubitem(self._loadZone(zoneNode))
        return page

    def _loadPages(self, docNode):
        return [self._loadPage(pageNode) for pageNode in self._getChildren(docNode, 'Page')]

    def loadPages(self, doc):
        return self._loadPages(doc.documentElement)


class DOMExporter(object):
    """
    Exports TrueViz documents to DOM tree
    """

    def _appendProperty(self, node, name, value):
        propNode = node.ownerDocument.createElement(name)
        propNode.setAttribute('Value', value)
        node.appendChild(propNode)

    def _appendVertex(self, node, x, y):
        vertexNode = node.ownerDocument.createElement('Vertex')
        vertexNode.setAttribute('x', str(x))
        vertexNode.setAttribute('y', str(y))
        node.appendChild(vertexNode)

    def _appendBounds(self, node, tagName, item):
        boundsNode = node.ownerDocument.createElement(tagName)
        self._appendVertex(boundsNode, item.bounds.x1, item.bounds.y1)
        self._appendVertex(boundsNode, item.bounds.x2, item.bounds.y1)
        self._appendVertex(boundsNode, item.bounds.x2, item.bounds.y2)
        self._appendVertex(boundsNode, item.bounds.x1, item.bounds.y2)
        node.appendChild(boundsNode)

    def _appendLanguage(self, node, type, script, codeset):
        langNode = node.ownerDocument.createElement('Language')
        langNode.setAttribute('Type', type)
        langNode.setAttribute('Script', script)
        langNode.setAttribute('Codeset', codeset)
        node.appendChild(langNode)

    def _appendFont(self, node, type, style, spacing, size):
        fontNode = node.ownerDocument.createElement('Font')
        fontNode.setAttribute('Type', type)
        fontNode.setAttribute('Style', style)
        fontNode.setAttribute('Spacing', spacing)
        fontNode.setAttribute('Size', size)
        node.appendChild(fontNode)

    def _appendChar(self, wordNode, charItem):
        charNode = wordNode.ownerDocument.createElement('Character')
        self._appendProperty(charNode, 'CharacterID', '')
        self._appendBounds(charNode, 'CharacterCorners', charItem)
        self._appendProperty(charNode, 'CharacterNext', '')
        self._appendProperty(charNode, 'GT_Text', charItem.text)
        wordNode.appendChild(charNode)

    def _appendWord(self, lineNode, wordItem):
        wordNode = lineNode.ownerDocument.createElement('Word')
        self._appendProperty(wordNode, 'WordID', '')
        self._appendBounds(wordNode, 'WordCorners', wordItem)
        self._appendProperty(wordNode, 'WordNext', '')
        self._appendProperty(wordNode, 'WordNumChars', '')
        for charItem in wordItem.subitems:
            self._appendChar(wordNode, charItem)
        lineNode.appendChild(wordNode)

    def _appendLine(self, zoneNode, lineItem):
        lineNode = zoneNode.ownerDocument.createElement('Line')
        self._appendProperty(lineNode, 'LineID', '')
        self._appendBounds(lineNode, 'LineCorners', lineItem)
        self._appendProperty(lineNode, 'LineNext', '')
        self._appendProperty(lineNode, 'LineNumChars', '')
        for wordItem in lineItem.subitems:
            self._appendWord(lineNode, wordItem)
        zoneNode.appendChild(lineNode)

    def _appendClassification(self, node, category, type):
        clsNode = node.ownerDocument.createElement('Classification')
        self._appendProperty(clsNode, 'Category', category)
        self._appendProperty(clsNode, 'Type', type)
        node.appendChild(clsNode)

    def _appendZone(self, pageNode, zoneItem):
        zoneNode = pageNode.ownerDocument.createElement('Zone')
        self._appendProperty(zoneNode, 'ZoneID', '')
        self._appendBounds(zoneNode, 'ZoneCorners', zoneItem)
        self._appendProperty(zoneNode, 'ZoneNext', '')
        insetsNode = pageNode.ownerDocument.createElement('ZoneInsets')
        insetsNode.setAttribute('Top', '')
        insetsNode.setAttribute('Bottom', '')
        insetsNode.setAttribute('Left', '')
        insetsNode.setAttribute('Right', '')
        zoneNode.appendChild(insetsNode)
        self._appendProperty(zoneNode, 'ZoneLines', '')
        self._appendClassification(zoneNode, zoneItem.label.name, '')
        for lineItem in zoneItem.subitems:
            self._appendLine(zoneNode, lineItem)
        pageNode.appendChild(zoneNode)

    def _appendPage(self, docNode, pageItem):
        pageNode = docNode.ownerDocument.createElement('Page')
        self._appendProperty(pageNode, 'PageID', '')
        self._appendProperty(pageNode, 'PageType', '')
        self._appendProperty(pageNode, 'PageNumber', '')
        self._appendProperty(pageNode, 'PageColumns', '')
        self._appendProperty(pageNode, 'PageNext', '')
        self._appendProperty(pageNode, 'PageZones', '')
        for zoneItem in pageItem.subitems:
            self._appendZone(pageNode, zoneItem)
        docNode.appendChild(pageNode)

    def _appendDocContent(self, docNode, pages):
        self._appendProperty(docNode, 'DocID', '')
        self._appendProperty(docNode, 'DocTitle', '')
        self._appendProperty(docNode, 'DocPubName', '')
        self._appendProperty(docNode, 'DocVolNum', '')
        self._appendProperty(docNode, 'DocIssueNum', '')
        self._appendProperty(docNode, 'DocMargins', '')
        self._appendProperty(docNode, 'DocDate', '')
        self._appendProperty(docNode, 'DocPages', '')
        imgNode = docNode.ownerDocument.createElement('DocImage')
        self._appendProperty(imgNode, 'Name', '')
        self._appendProperty(imgNode, 'Format', '')
        self._appendProperty(imgNode, 'Depth', '')
        self._appendProperty(imgNode, 'Compression', '')
        self._appendProperty(imgNode, 'Capture', '')
        self._appendProperty(imgNode, 'Quality', '')
        docNode.appendChild(imgNode)
        self._appendLanguage(docNode, '', '', '')
        self._appendFont(docNode, '', '', '', '')
        self._appendProperty(docNode, 'ReadingDir', '')
        self._appendProperty(docNode, 'CharOrient', '')
        self._appendClassification(docNode, '', '')
        self._appendProperty(docNode, 'GT_Text', '')
        for pageItem in pages:
            self._appendPage(docNode, pageItem)

    def dumpPages(self, pages):
        impl = minidom.getDOMImplementation()
        dt = impl.createDocumentType('Document', None, 'Trueviz.dtd')
        doc = impl.createDocument(None, 'Document', dt)
        self._appendDocContent(doc.documentElement, pages)
        return doc


class Transformer(document.Transformer):

    def __init__(self):
        self.domImporter = DOMImporter()
        self.domExporter = DOMExporter()

    def loadPages(self, reader):
        """
        Loads document pages from file
        """
        return self.domImporter.loadPages(minidom.parse(reader))

    def dumpPages(self, pages, writer):
        """
        Stores document pages in file
        """
        self.domExporter.dumpPages(pages).writexml(
            writer, encoding='utf-8', newl='\n')


load = document.FileStorage(Transformer()).load


if __name__ == "__main__":
    print "This is a module, don't run it as a program"
