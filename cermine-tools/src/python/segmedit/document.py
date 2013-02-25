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


from wx.lib.pubsub import Publisher
import config
import constants
from utility import partition, find
from command import CommandManager, EmptyCommandManager


class IllegalSubitem(Exception):
    pass


class Transformer(object):

    def loadPages(self, reader):
        raise NotImplementedError('Abstract method')

    def dumpPages(self, pages, writer):
        raise NotImplementedError('Abstract method')


class Storage(object):

    class Data(object):
        pass

    def __init__(self, transformer):
        self.transformer = transformer

    def openReader(self, data):
        raise NotImplementedError('Abstract method')

    def closeReader(self, reader, success):
        reader.close()

    def loadPages(self, data):
        reader = self.openReader(data)
        try:
            pages = self.transformer.loadPages(reader)
        except:
            self.closeReader(reader, False)
            raise
        else:
            return pages

    def load(self, *args, **kwargs):
        return Document(self, self.Data(*args, **kwargs))

    def openWriter(self, data, *args, **kwargs):
        raise NotImplementedError('Abstract method')

    def closeWriter(self, writer, success):
        writer.close()

    def savePages(self, pages, data, *args, **kwargs):
        writer = self.openWriter(data, *args, **kwargs)
        try:
            pages = self.transformer.dumpPages(pages, writer)
        except:
            self.closeWriter(writer, False)
            raise
        else:
            self.closeWriter(writer, True)


class FileStorage(Storage):

    class Data(Storage.Data):
        def __init__(self, path):
            self.path = path

    def openReader(self, data):
        return open(data.path, 'r')

    def openWriter(self, data, path=None):
        if path is None:
            path = data.path
        else:
            data.path = path
        return open(path, 'w')


class Bounds(object):
    """
    Document item bounding box
    """

    INFINITY = 1e900

    def __init__(self, x1=INFINITY, y1=INFINITY,
            x2= -INFINITY, y2= -INFINITY, scale=1):
        self.x1 = x1 * scale
        self.y1 = y1 * scale
        self.x2 = x2 * scale
        self.y2 = y2 * scale

    def intersects(self, other):
        return (self.x1 <= other.x2) and (self.x2 >= other.x1) and \
               (self.y1 <= other.y2) and (self.y2 >= other.y1)

    def __add__(self, other):
        return Bounds(min(self.x1, other.x1), min(self.y1, other.y1),
                      max(self.x2, other.x2), max(self.y2, other.y2))

    def containsPoint(self, point):
        return (self.x1 <= point[0] <= self.x2) and \
               (self.y1 <= point[1] <= self.y2)

    def contains(self, other):
        return (self.x1 <= other.x1 <= self.x2) and \
               (self.x1 <= other.x2 <= self.x2) and \
               (self.y1 <= other.y1 <= self.y2) and \
               (self.y1 <= other.y2 <= self.y2)

    def getWidth(self):
        return self.x2 - self.x1

    width = property(getWidth)

    def getHeight(self):
        return self.y2 - self.y1

    height = property(getHeight)

    @staticmethod
    def fromCorners(p1, p2, scale=1):
        x1, x2 = p1[0], p2[0] if p1[0] <= p2[0] else p2[0], p1[0]
        y1, y2 = p1[1], p2[1] if p1[1] <= p2[1] else p2[1], p1[1]
        return Bounds(x1, y1, x2, y2, scale=scale)

    @staticmethod
    def fromBox(x, y, width, height, scale=1):
        return Bounds(x, y, x + width, y + height, scale=scale)


class BoundsBuilder(object):

    """
    Helper class for creating bounding boxes by expanding current bounding box
    so that it contains given objects
    """

    INFINITY = 1e900

    def __init__(self):
        self.minX = BoundsBuilder.INFINITY
        self.minY = BoundsBuilder.INFINITY
        self.maxX = -BoundsBuilder.INFINITY
        self.maxY = -BoundsBuilder.INFINITY

    def expandByVertex(self, x, y):
        self.minX = min(self.minX, x)
        self.minY = min(self.minY, y)
        self.maxX = max(self.maxX, x)
        self.maxY = max(self.maxY, y)

    def expandByItem(self, item):
        self.minX = min(self.minX, item.bounds.x1)
        self.minY = min(self.minY, item.bounds.y1)
        self.maxX = max(self.maxX, item.bounds.x2)
        self.maxY = max(self.maxY, item.bounds.y2)

    def expandBySubitems(self, item):
        for subitem in item.subitems:
            self.expandByItem(subitem)

    def getBounds(self, default=Bounds()):
        if self.minX <= self.maxX and self.minY <= self.maxY:
            return Bounds(self.minX, self.minY, self.maxX, self.maxY)
        else:
            return default

    @staticmethod
    def fromItems(iterable):
        builder = BoundsBuilder()
        for item in iterable:
            builder.expandByItem(item)
        return builder.getBounds()


class DocumentItem(object):
    """
    An item of a document (page, zone, line, word or char).
    
    Some methods changing the item takes an optional manager parameter.
    It can be used to pass the command manager of the owning document
    so that the changes made to the item can be undone.
    """

    def __init__(self, bounds):
        self.bounds = bounds
        self.flagged = 0

    def flag(self, itemType, predicate):
        """
        Flags document items by setting flagged attribute.
        
        If this item type is greater than itemType, flagged attribute equals to
        the number of flagged subitems. If this item type is equal to itemType,
        flagged attribute equals to 1 if predicate(self) is true and 0
        otherwise.
        
        predicate(subitem) must impose predicate(item)
        """
        raise NotImplementedError('Abstract method')

    def flagByBounds(self, itemType, predicate):
        self.flag(itemType, lambda item: predicate(item.bounds))


class DocumentItemContainer(DocumentItem):
    """
    A document item containing subitems. Every document item class except
    for a char is a subclass of DocumentItermContainer.
    """

    def __init__(self, bounds=Bounds()):
        super(DocumentItemContainer, self).__init__(bounds)
        self.subitems = []

    def newSubitem(self, hints=()):
        raise NotImplementedError('Abstract method')

    def __len__(self):
        return len(self.subitems)

    def __iter__(self):
        for subitem in self.subitems:
            yield subitem

    def __getitem__(self, key):
        return self.subitems[key]

    def addSubitem(self, subitem, recomputeBounds=True, manager=EmptyCommandManager()):
        if subitem.itemType == self.subitemType:
            manager.setItem(self.subitems, slice(len(self.subitems), None), [subitem])
            if recomputeBounds:
                manager.setAttribute(self, 'bounds', self.bounds + subitem.bounds)
        else:
            raise IllegalSubitem

    def addSubitems(self, subitems, recomputeBounds=True):
        for subitem in subitems:
            self.addSubitem(subitem, recomputeBounds)

    def setSubitems(self, subitems, recomputeBounds=True, manager=EmptyCommandManager()):
        if isinstance(subitems, tuple):
            raise ValueError('????????/')
        manager.setAttribute(self, 'subitems', subitems)
        if recomputeBounds:
            self.recomputeBounds(manager)

    def recomputeBounds(self, manager=EmptyCommandManager()):
        manager.setAttribute(self, 'bounds', BoundsBuilder.fromItems(self))

    def flag(self, itemType, predicate):
        self.flagged = 0
        if predicate(self):
            if itemType < self.itemType:
                for subitem in self:
                    self.flagged += subitem.flag(itemType, predicate)
            elif itemType == self.itemType:
                self.flagged = 1
        return bool(self.flagged)

    def iterItems(self, itemType, predicate):
        if predicate(self):
            if itemType == self.itemType:
                yield self
            elif itemType < self.itemType:
                for subitem in self:
                    for value in subitem.iterItems(itemType, predicate):
                        yield value

    def findItem(self, itemType, predicate):
        if not predicate(self):
            return None
        elif itemType == self.itemType:
            return self
        else:
            for subitem in self:
                result = subitem.findItem(itemType, predicate)
                if result is not None:
                    return result
            return None

    def merge(self, itemType, manager):
        if itemType < self.itemType and self.flagged:
            inner, outer = partition(lambda item: item.flagged, self)
            if len(inner) == 1:
                inner[0].merge(itemType, manager)
            else:
                newSubitem = self.newSubitem(hints=inner)
                for subitem in inner:
                    newSubitem.addSubitems(subitem)
                    newSubitem.flagged += subitem.flagged
                newSubitem.merge(itemType, manager)
                outer.append(newSubitem)
                self.setSubitems(outer, False, manager)

    def _needsCutout(self, itemType):
        if self.flagged == 0:
            return False
        elif itemType == self.itemType:
            return self.flagged != len(self)
        elif self.flagged == 1:
            return find(lambda item: item.flagged, self)._needsCutout(itemType)
        else:
            return True

    def _cutout2(self, itemType, manager):
        if self.flagged:
            inner, outer = partition(lambda item: item.flagged, self)
            if itemType + 1 == self.itemType:
                cutSubitem = self.newSubitem()
                for subitem in inner:
                    subinner, subouter = partition(lambda item: item.flagged, subitem)
                    cutSubitem.addSubitems(subinner)
                    if subouter:
                        restSubitem = self.newSubitem()
                        restSubitem.addSubitems(subouter)
                        outer.append(restSubitem)
                outer.append(cutSubitem)
                self.setSubitems(outer, False, manager)
            elif itemType + 1 < self.itemType:
                if len(inner) == 1:
                    inner[0]._cutout2(itemType, manager)
                else:
                    newSubitem = self.newSubitem()
                    for subitem in inner:
                        newSubitem.addSubitems(subitem)
                        newSubitem.flagged += subitem.flagged
                    newSubitem._cutout2(itemType, manager)
                    outer.append(newSubitem)
                    self.setSubitems(outer, False, manager)

    def _cutout(self, itemType, cutItem, manager):
        if self.flagged:
            if itemType == self.itemType:
                inner, outer = partition(lambda item: item.flagged, self)
                cutItem.addSubitems(inner)
                self.setSubitems(outer, True, manager)
            elif itemType < self.itemType:
                newSubitems = []
                if cutItem is None:
                    if itemType + 1 == self.itemType or self.flagged > 1:
                        hints = [subitem for subitem in self if subitem.flagged]
                        it = cutSubitem = self.newSubitem(hints)
                        while it.itemType != itemType:
                            it.addSubitem(it.newSubitem(), False)
                            it = it[0]
                        newSubitems.append(cutSubitem)
                    else:
                        cutSubitem = None
                else:
                    cutSubitem = cutItem[0]

                for subitem in self:
                    subitem._cutout(itemType, cutSubitem, manager)
                    if subitem:
                        newSubitems.append(subitem)

                if cutItem is None and cutSubitem is not None:
                    def computeBounds(item):
                        if item.itemType != itemType:
                            for subitem in item:
                                computeBounds(subitem)
                            item.recomputeBounds()
                    computeBounds(cutSubitem)

                self.setSubitems(newSubitems, True, manager)

    def cutout(self, itemType, manager):
        if self._needsCutout(itemType):
            self._cutout(itemType, None, manager)

    def atomize(self, itemType, manager):
        if self.flagged:
            if itemType + 1 < self.itemType:
                for subitem in self:
                    subitem.atomize(itemType, manager)
            elif itemType + 1 == self.itemType:
                newSubitems = []
                for subitem in self:
                    if subitem.flagged and len(subitem) > 1:
                        for subsubitem in subitem:
                            newSubitem = self.newSubitem(hints=(subitem,))
                            newSubitem.addSubitem(subsubitem)
                            newSubitems.append(newSubitem)
                    else:
                        newSubitems.append(subitem)
                self.setSubitems(newSubitems, False, manager)


class DocumentChar(DocumentItem):

    itemType = constants.CHAR

    def __init__ (self, bounds, text):
        super(DocumentChar, self).__init__(bounds)
        self.text = text

    def __nonzero__(self):
        return True

    def findItem(self, itemType, predicate):
        if itemType == self.itemType and predicate(self):
            return self
        else:
            return None

    def flag(self, itemType, predicate):
        self.flagged = int(predicate(self)) if itemType == self.itemType else 0
        return bool(self.flagged)


class DocumentWord(DocumentItemContainer):

    itemType = constants.WORD
    subitemType = constants.CHAR

    def __init__ (self):
        super(DocumentWord, self).__init__()

    # We don't want anyone to create new chars so this method stays abstract
    # def newSubitem(self, hints=()):


    def smartSplitted(self, line):
        if self.flagged:
            chars = sorted(self, key=lambda item: item.bounds.x1)
            word = line.newSubitem()
            threshold = 1
            right = chars[0].bounds.x1
            for char in chars:
                if char.bounds.x1 - right > threshold:
                    line.addSubitem(word)
                    word = line.newSubitem()
                word.addSubitem(char)
                right = char.bounds.x2
            line.addSubitem(word)


class DocumentLine(DocumentItemContainer):

    itemType = constants.LINE
    subitemType = constants.WORD

    def __init__ (self):
        super(DocumentLine, self).__init__()

    def newSubitem(self, hints=()):
        return DocumentWord()

    def smartSplitWords(self, zone, manager):
        if self.flagged:
            line = zone.newSubitem()
            subitems = []
            for word in self:
                word.smartSplitted(line)
                if line:
                    zone.addSubitem(line, manager=manager)
                    line = zone.newSubitem()
                else:
                    subitems.append(word)
            self.setSubitems(subitems, manager=manager)
        if self:
            zone.addSubitem(self, manager=manager)


class DocumentZone(DocumentItemContainer):

    itemType = constants.ZONE
    subitemType = constants.LINE

    def __init__ (self, label=config.DEFAULT_ZONE_LABEL):
        super(DocumentZone, self).__init__()
        self.label = label

    def newSubitem(self, hints=()):
        return DocumentLine()

    def setLabel(self, label, manager=EmptyCommandManager()):
        manager.setAttribute(self, 'label', label)

    def smartSplitWords(self, manager):
        if self.flagged:
            oldLines = list(self)
            self.setSubitems([], manager=manager)
            for line in oldLines:
                line.smartSplitWords(self, manager)


class DocumentPage(DocumentItemContainer):

    itemType = constants.PAGE
    subitemType = constants.ZONE

    def __init__ (self):
        super(DocumentPage, self).__init__()

    def newSubitem(self, hints=()):
        label = config.DEFAULT_ZONE_LABEL
        for subitem in hints:
            if subitem.label != config.DEFAULT_ZONE_LABEL:
                if label == config.DEFAULT_ZONE_LABEL:
                    label = subitem.label
                elif label != subitem.label:
                    label = config.DEFAULT_ZONE_LABEL
                    break
        return DocumentZone(label)

    def smartSplitWords(self, manager):
        if self.flagged:
            for subitem in self:
                subitem.smartSplitWords(manager)


class Document(object):
    def __init__(self, storage, storageData):
        self.storage = storage
        self.storageData = storageData
        self.pages = storage.loadPages(storageData)
        self._manager = CommandManager(config.UNDO_HISTORY_SIZE)

    def __len__(self):
        return len(self.pages)

    def __getitem__(self, key):
        return self.pages[key]

    def findItemByPoint(self, page, itemType, point):
        if isinstance(page, int):
            page = self[page]
        return page.findItem(itemType,
            lambda item: item.bounds.containsPoint(point))

    def merge(self, page, itemType, bounds):
        if isinstance(page, int):
            page = self[page]
        page.flagByBounds(itemType, bounds.intersects)
        page.merge(itemType, self._manager)
        if self._manager.commit():
            Publisher().sendMessage('DOCUMENT_CHANGE')

    def cutout(self, page, itemType, bounds):
        if isinstance(page, int):
            page = self[page]
        page.flagByBounds(itemType - 1, bounds.intersects)
        page.cutout(itemType, self._manager)
        if self._manager.commit():
            Publisher().sendMessage('DOCUMENT_CHANGE')

    def cutout2(self, pagenb, itemType, bounds):
        self.pages[pagenb].flagByBounds(itemType - 1, bounds.intersects)
        self.pages[pagenb]._cutout2(itemType, self._manager)
        if self._manager.commit():
            Publisher().sendMessage('DOCUMENT_CHANGE')

    def atomize(self, page, itemType, bounds):
        if isinstance(page, int):
            page = self[page]
        page.flagByBounds(itemType, bounds.intersects)
        page.atomize(itemType, self._manager)
        if self._manager.commit():
            Publisher().sendMessage('DOCUMENT_CHANGE')

    def smartSplitWords(self, page, bounds):
        if isinstance(page, int):
            page = self[page]
        page.flagByBounds(constants.WORD, bounds.intersects)
        page.smartSplitWords(self._manager)
        if self._manager.commit():
            Publisher().sendMessage('DOCUMENT_CHANGE')

    def changeLabel(self, zone, label):
        zone.setLabel(label, self._manager)
        if self._manager.commit():
            Publisher().sendMessage('DOCUMENT_CHANGE')

    def changeLabels(self, page, bounds, label):
        if isinstance(page, int):
            page = self[page]
        for zone in page.iterItems(constants.ZONE, lambda item: bounds.intersects(item.bounds)):
            zone.setLabel(label, self._manager)
        if self._manager.commit():
            Publisher().sendMessage('DOCUMENT_CHANGE')

    def canUndo(self):
        return self._manager.canUndo()

    def undo(self):
        if self._manager.undo():
            Publisher().sendMessage('DOCUMENT_CHANGE')

    def canRedo(self):
        return self._manager.canRedo()

    def redo(self):
        if self._manager.redo():
            Publisher().sendMessage('DOCUMENT_CHANGE')

    def isModified(self):
        """
        Returns true if the document has been modified since last save and
        false otherwise.
        """
        return not self._manager.isClean()

    def save(self, *args, **kwargs):
        self.storage.savePages(self.pages, self.storageData, *args, **kwargs)
        self._manager.clean()


if __name__ == "__main__":
    print "This is a module, don't run it as a program"
