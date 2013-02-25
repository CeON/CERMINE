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


import wx, re, os, pwd, sys
from wx.lib.pubsub import Publisher as pub
import config, gui, readpdf, network
import fasttrueviz as trueviz
import document
import profile
from constants import *

def toDocumentCoordinates(point, scale):
    return (point[0] / scale - config.XML_PAGE_DELTA_X, point[1] / scale)

def boxToBounds(box, scale):
    return document.Bounds(box[0] / scale - config.XML_PAGE_DELTA_X,
                           box[1] / scale,
                           (box[0] + box[2]) / scale - config.XML_PAGE_DELTA_X,
                           (box[1] + box[3]) / scale)

def cornersToBox(corners):
    p1, p2 = corners
    return (min(p1[0], p2[0]), min(p1[1], p2[1]),
            abs(p1[0] - p2[0]), abs(p1[1] - p2[1]))

def cornersToBounds(corners, scale):
    return boxToBounds(cornersToBox(corners), scale)

class ItemView(object):

    def __init__(self, model, scale):
        self.model = model
        self._scale = scale

    @property
    def box(self):
        return (self._scale * (self.model.bounds.x1 + config.XML_PAGE_DELTA_X),
                self._scale * self.model.bounds.y1,
                self._scale * self.model.bounds.getWidth(),
                self._scale * self.model.bounds.getHeight())

    @property
    def layer(self):
        return self.model.itemType

    def __iter__(self):
        for subitem in self.model:
            yield ItemView(subitem, self._scale)


class Application(wx.App):

    def __init__(self):
        super(Application, self).__init__()

        self.frame = gui.MainWindow(None, config.WINDOW_TITLE,
            config.MAIN_WINDOW_SIZE)

        self.pageWidth = 0
        self.pageHeight = 0
        self.pageBitmap = None

        self.xml = None
        self.pdf = None
        self.networkDocumentId = None

        self.selectionCorners = None

        self.pageCount = 0
        self.pageIndex = 0
        self.frame.setPage(0, 0)

        self.zoom = config.DEFAULT_ZOOM
        self.frame.setZoom(self.zoom)
        self.mode = MERGE
        self.frame.switchMode(self.mode)
        self.layer = WORD
        self.frame.switchLayer(self.layer)

        self.frame.enableMenuItems(sendNet=False, openPDF=False, closeXML=False,
            closePDF=False, save=False, saveAs=False, undo=False, redo=False)

        pub.subscribe(self.pageChangeMsg, "FIRSTPAGE")
        pub.subscribe(self.pageChangeMsg, "PGUP")
        pub.subscribe(self.pageChangeMsg, "CHANGE_PAGE")
        pub.subscribe(self.pageChangeMsg, "PGDOWN")
        pub.subscribe(self.pageChangeMsg, "LASTPAGE")
        pub.subscribe(self.zoomChangeMsg, "ZOOMIN")
        pub.subscribe(self.zoomChangeMsg, "ZOOMOUT")
        pub.subscribe(self.zoomChangeMsg, "CHANGE_ZOOM")
        pub.subscribe(self.setModeMsg, "MODE_MERGE")
        pub.subscribe(self.setModeMsg, "MODE_CUTOUT")
        pub.subscribe(self.setModeMsg, "MODE_CLASSIFY")
        pub.subscribe(self.setLayerMsg, "LAYER1")
        pub.subscribe(self.setLayerMsg, "LAYER2")
        pub.subscribe(self.setLayerMsg, "LAYER3")
        pub.subscribe(self.changeModeMsg, "CHANGE_MODE")
        pub.subscribe(self.changeLayerMsg, "CHANGE_LAYER")
        pub.subscribe(self.paintScroll, "PAINT")
        pub.subscribe(self.openNet, "OPENNET")
        pub.subscribe(self.sendNet, "SENDNET")
        pub.subscribe(self.openXML, "OPENXML")
        pub.subscribe(self.openPDF, "OPENPDF")
        pub.subscribe(self.saveXML, "SAVEXML")
        pub.subscribe(self.saveXML, "SAVEXMLAS")
        pub.subscribe(self.closeXML, "CLOSEXML")
        pub.subscribe(self.closePDF, "CLOSEPDF")
        pub.subscribe(self.undoRedoMsg, "UNDO")
        pub.subscribe(self.undoRedoMsg, "REDO")
        pub.subscribe(self.closeApp, "CLOSEAPP")
        pub.subscribe(self.popupMenuMsg, "POPUPMENU")
        pub.subscribe(self.atomizeMsg, "ATOMIZE")
        pub.subscribe(self.smartSplitMsg, "SMART_SPLIT")
        pub.subscribe(self.classifyMsg, "CLASSIFY")
        pub.subscribe(self.documentChangeMsg, "DOCUMENT_CHANGE")
        pub.subscribe(self.scrollLeftDownMsg, "SCROLL_LEFT_DOWN")
        pub.subscribe(self.scrollMoveMsg, "SCROLL_MOVE")
        pub.subscribe(self.scrollLeaveMsg, "SCROLL_LEAVE")
        pub.subscribe(self.scrollLeftUpMsg, "SCROLL_LEFT_UP")

        self.MainLoop()



    def openXML(self, message=None, openedDocument=None):
        # TODO: trochę tu posprzątać. Osobno wczytywanie pliku z dysku, osobno inicjowanie 
        # już wczytanego dokumentu.
        if openedDocument: # network document
            if self.xml:
                if not self.closeXML():
                    return
            self.xml = openedDocument
            self.frame.enableMenuItems(sendNet=True)
            self.pdf = None
            path = None

        else:
            prof = profile.current()
            retvalue = self.frame.selectFile(message='Select an XML (Trueviz) file',
                wildcard='XML files (*.xml)|*.xml;*.XML|All files (*)|*',
                directory=prof.get("lastXmlDirectory", ""))

            if retvalue:
                if self.xml:
                    if not self.closeXML():
                        return

                prof["lastXmlDirectory"] = retvalue[0]
                prof.save()
                path = os.path.sep.join(retvalue)

                self.frame.SetTitle(config.WINDOW_TITLE + " - " + retvalue[1])

                self.pdf = None

                try:
                    self.xml = self.runTask('Opening XML document...', trueviz.load, (path,))
                    if not self.xml:
                        self.xml = None
                        raise Exception('Document is empty')
                        return
                except Exception as e:
                    sys.excepthook(*sys.exc_info())
                    self.frame.errorDialog("Error while reading file %s:\n%s" % \
                        (path, e))
                    return
                self.frame.enableMenuItems(saveAs=True)
            else:
                return

        if self.xml:
            self.pageCount = len(self.xml)
            self.setPage(0)

            self.frame.enableMenuItems(openPDF=True, closeXML=True)

            if  path:
                # and open the PDF file
                filebasere = re.match(r'(.*\.)[xX][mM][lL]', retvalue[1])
                if filebasere:
                    filebase = filebasere.groups()[0]
                    pathbase1 = os.path.sep.join((retvalue[0], filebase))
                    pathbase2 = os.path.sep.join((prof.get("lastPdfDirectory", ""), filebase))
                    paths = (pathbase1 + 'pdf', pathbase1 + 'PDF',
                        pathbase2 + 'pdf', pathbase2 + 'PDF')
                    self.openPDF(paths=paths)
                else:
                    self.openPDF()


    def openNet(self, message):
        prof = profile.current()
        if 'username' not in prof:
            username = self.frame.textDialog('Enter username:',
                default=pwd.getpwuid(os.getuid())[0])
            if username is None:
                return
            if not re.match(r'[a-zA-Z0-9_]+', username):
                self.frame.errorDialog('Invalid username')
                return
            prof['username'] = username
            prof.save()
        try:
            currentDocuments = network.getCurrentDocuments()
            availableDocuments = network.getAvailableDocuments()
        except Exception as e:
            self.frame.errorDialog("Cannot get list of network documents: %s" % (e,))
            return

        choosenDocument = self.frame.selectNetworkDocument(currentDocuments, availableDocuments)

        if choosenDocument:
            if self.xml:
                if not self.closeXML():
                    return
            try:
                setStatusResult = network.setDocumentStatus(choosenDocument, 'locked')
            except Exception as e:
                self.frame.errorDialog("Cannot set document status: %s" % (e,))
                return

            if setStatusResult:
                try:
                    self.openXML(openedDocument=self.runTask('Opening XML document...', network.getXML, (choosenDocument,)))
                except Exception as e:
                    self.frame.errorDialog("Cannot get network document: %s" % (e,))
                    return
                try:
                    self.openPDF(openedDocument=self.runTask('Opening PDF document...', network.getPDF, (choosenDocument,)))
                except Exception as e:
                    self.frame.errorDialog("Cannot get a PDF file from the server: %s" \
                        % (e,))
                self.networkDocumentId = choosenDocument
                self.frame.SetTitle(config.WINDOW_TITLE + " - network document: " + choosenDocument)
            else:
                self.frame.errorDialog("File is locked by other user")


    def sendNet(self, message=None):
        retvalue = self.frame.sendNetworkDocument()
        if retvalue:
            self.runTask('Saving XML document...', self.xml.save)
            xmlFile = open(self.xml.storageData.path, "r")
            try:
                success = self.runTask('Sending XML document...',
                    network.sendXML, (self.networkDocumentId, xmlFile),)
            except Exception as e:
                self.frame.errorDialog("Cannot send the XML file: %s" % (e,))
            else:
                if success:
                    try:
                        network.setDocumentComment(self.networkDocumentId, retvalue[1])
                    except Exception as e:
                        self.frame.errorDialog("Cannot set comment: %s" % (e,))
                    try:
                        network.setDocumentStatus(self.networkDocumentId, retvalue[0])
                    except Exception as e:
                        self.frame.errorDialog("Cannot set status: %s" % (e,))
                    else:
                        if retvalue[0] != 'locked':
                            self.closeXML()
                else:
                    self.frame.errorDialog("Sending document failed")
            finally:
                xmlFile.close()

    def _sendToServer(self, status, comment):
        self.xml.save()

    def openPDF(self, message=None, paths=None, openedDocument=None):
        path = None
        if self.xml:
            if openedDocument:
                self.pdf = openedDocument
                self.frame.enableMenuItems(closePDF=True)
                self.preparePageBitmap()
            else:
                if paths:
                    for p in paths:
                        if os.path.exists(p):
                            path = p
                            break
                if not path:
                    prof = profile.current()

                    retvalue = self.frame.selectFile(message='Select a PDF file',
                        wildcard='PDF files (*.pdf)|*.pdf;*.PDF|All files (*)|*',
                        directory=prof.get("lastPdfDirectory", ""))
                    if retvalue:
                        prof["lastPdfDirectory"] = retvalue[0]
                        prof.save()
                        path = os.path.sep.join(retvalue)

                if path:
                    try:
                        self.pdf = self.runTask('Opening PDF document...', readpdf.Pdf, (path,))
                    except Exception as e:
                        self.frame.errorDialog("Error while reading file %s:\n%s" \
                            % (path, e))
                    else:
                        self.frame.enableMenuItems(closePDF=True)
                        self.preparePageBitmap()
        else:
            self.frame.errorDialog("Open first an XML file")

    def saveXML(self, message=None):
        if self.xml:
            if not message or message.topic[0] == "SAVEXML":
                self.runTask('Saving XML document...', self.xml.save)
                self.frame.enableMenuItems(save=False)
            else: # save as...
                retvalue = self.frame.selectFile(message='Save as...', save=True,
                    wildcard='XML files (*.xml)|*.xml;*.XML|All files (*)|*')
                if retvalue:
                    path = os.path.sep.join(retvalue)
                    self.runTask('Saving XML document...', self.xml.save, (path),)
                    self.frame.enableMenuItems(save=False)
        else:
            self.frame.errorDialog("Nothing to save")

    def askSaveXML(self):
        """
        Asks the user (if necessary) whether to save the XML file and, if
        needed, saves the file. Returns true if operation succeeded
        (the user has not pressed Cancel button).
        """
        if self.xml.isModified():
            if self.networkDocumentId:
                response = self.frame.yesNoCancelDialog("Do you want to send the document to the server?")
            else:
                response = self.frame.yesNoCancelDialog("Do you want to save the XML file?")
            if response == wx.ID_YES:
                if self.networkDocumentId:
                    self.sendNet()
                else:
                    self.xml.save()
                return True
            elif response == wx.ID_CANCEL:
                return False
            else:
                return True
        else:
            return True

    def closeXML(self, message=None):
        if not self.askSaveXML():
            return False
        self.closePDF()
        self.xml = None
        self.networkDocumentId = None
        self.pageCount = 0
        self.pageIndex = 0
        self.frame.enableMenuItems(sendNet=False, openPDF=False, closeXML=False, save=False,
            saveAs=False, undo=False, redo=False)
        self.frame.setPage(0, 0)
        self.setZoom(config.DEFAULT_ZOOM, changePage=True)
        self.frame.SetTitle(config.WINDOW_TITLE)
        return True


    def closePDF(self, message=None):
        self.pdf = None
        self.preparePageBitmap()
        self.frame.enableMenuItems(closePDF=False)


    def setZoom(self, zoom, changePage=False):
        oldWidth = self.pageWidth * self.zoom // 100
        oldHeight = self.pageHeight * self.zoom // 100
        newWidth = self.pageWidth * zoom // 100
        newHeight = self.pageHeight * zoom // 100
        self.zoom = zoom

        self.frame.setZoom(zoom)
        if changePage:
            self.frame.setScrollViewport(newWidth, newHeight)
        else:
            self.frame.setScrollViewport(newWidth, newHeight, oldWidth, oldHeight)



    def zoomChangeMsg(self, message):
        min_zoom = 5
        if message.topic[0] == "CHANGE_ZOOM":
            zoom = max(min_zoom, min(config.MAX_ZOOM, message.data))
            if zoom != self.zoom:
                self.setZoom(zoom)
            else:
                self.frame.setZoom(self.zoom)
        else:
            """
            after a message "ZOOMIN" or "ZOOMOUT" in the Publisher
            """
            msgtext = message.topic[0]
            newzoom = self.zoom
            if msgtext == "ZOOMIN":
                if self.zoom < config.MAX_ZOOM:
                    newzoom = self.zoom * 100 / 85
                    if newzoom > config.MAX_ZOOM:
                        newzoom = config.MAX_ZOOM
                    if newzoom == self.zoom:
                        newzoom = newzoom + 1
                    if self.zoom < 100 and newzoom > 100:
                        newzoom = 100
            else:
                if self.zoom > 5:
                    newzoom = self.zoom * 85 / 100
                    if newzoom < 5:
                        newzoom = 5
                    if self.zoom > 100 and newzoom < 100:
                        newzoom = 100
            if newzoom != self.zoom:
                self.setZoom(newzoom)


    def preparePageBitmap(self):
        """
        Prepares a page bitmap based on pageIndex and the opened pdf file
        (if any)
        """
        if self.pdf and self.pageIndex < len(self.pdf):
            stream = self.pdf.getPageStream(self.pageIndex)
            image = wx.ImageFromStream(stream)
            bitmap = wx.BitmapFromImage(image)
            self.pageBitmap = bitmap
            self.pageWidth = bitmap.GetWidth()
            self.pageHeight = bitmap.GetHeight()
        else:
            self.pageWidth = config.CONVERT_DPI * 2100 / 254
            self.pageHeight = config.CONVERT_DPI * 2970 / 254
            self.pageBitmap = None
        self.frame.setScrollViewport(self.pageWidth * self.zoom // 100,
            self.pageHeight * self.zoom // 100)


    def setPage(self, pageIndex):
        self.pageIndex = pageIndex
        self.frame.setPage(self.pageIndex, self.pageCount)
        self.preparePageBitmap()


    def pageChangeMsg(self, message):
        if message.topic[0] == "CHANGE_PAGE":
            pageIndex = message.data
        elif message.topic[0] == "FIRSTPAGE":
            pageIndex = 0
        elif message.topic[0] == "LASTPAGE":
            pageIndex = self.pageCount - 1
        elif message.topic[0] == "PGDOWN":
            pageIndex = self.pageIndex - 1
        else:
            pageIndex = self.pageIndex + 1
        if 0 <= pageIndex < self.pageCount:
            self.setPage(pageIndex)
        elif message.topic[0] == "CHANGE_PAGE":
            # Restore previous value
            self.frame.setPage(self.pageIndex, self.pageCount)


    def setModeMsg(self, message):
        topic = message.topic[0]
        if topic == "MODE_MERGE":
            self.mode = MERGE
        elif topic == "MODE_CUTOUT":
            self.mode = CUTOUT
        else: # topic == "MODE_CLASSIFY"
            self.mode = CLASSIFY
        self.frame.switchMode(self.mode)
        if self.mode == CLASSIFY:
            self.layer = ZONE
            self.frame.switchLayer(self.layer)
        self.frame.refreshScroll()


    def scrollLeftDownMsg(self, message):
        if self.xml:
            self.selectionCorners = [message.data, message.data]


    def scrollMoveMsg(self, message):
        if self.selectionCorners:
            self.frame.refreshScroll(cornersToBox(self.selectionCorners), margin=2)
            self.selectionCorners[1] = message.data
            self.frame.refreshScroll(cornersToBox(self.selectionCorners), margin=2)

    def scrollLeaveMsg(self, message):
        pass

    def xmlScale(self):
        return config.XML_UNIT_RATIO * self.zoom / 100


    def scrollLeftUpMsg(self, message):
        if self.selectionCorners:
            self.frame.refreshScroll(cornersToBox(self.selectionCorners), margin=2)
            bounds = cornersToBounds(self.selectionCorners, self.xmlScale())
            self.selectionCorners = None
            if self.mode == CUTOUT:
                self.xml.cutout(self.pageIndex, self.layer, bounds)
            elif self.mode == MERGE:
                self.xml.merge(self.pageIndex, self.layer, bounds)
            elif self.mode == CLASSIFY:
                if bounds.width and bounds.height:
                    self.frame.popupZoneLabelMenu(arg=bounds)


    def undoRedoMsg(self, message):
        if self.xml:
            if message.topic[0] == "UNDO":
                self.xml.undo()
            else:
                self.xml.redo()


    def documentChangeMsg(self, message):
        if not self.networkDocumentId:
            self.frame.enableMenuItems(save=self.xml.isModified())
        self.frame.enableMenuItems(undo=self.xml.canUndo(), redo=self.xml.canRedo())
        self.frame.refreshScroll()


    def setLayerMsg(self, message):
        topic = message.topic[0]
        if topic == "LAYER1":
            self.layer = WORD
        elif topic == "LAYER2":
            self.layer = LINE
        else:
            self.layer = ZONE
        # wysłać do gui informację o zmianie warstwy
        self.frame.switchLayer(self.layer)
        if self.layer != ZONE and self.mode == CLASSIFY:
            self.mode = MERGE
            self.frame.switchMode(self.mode)
        self.frame.refreshScroll()

    def changeModeMsg(self, message):
        if self.mode == MERGE:
            self.mode = CUTOUT
        elif self.mode == CUTOUT:
            self.mode = CLASSIFY
        elif self.mode == CLASSIFY:
            self.mode = MERGE
        self.frame.switchMode(self.mode)
        if self.mode == CLASSIFY:
            self.layer = ZONE
            self.frame.switchLayer(self.layer)
        self.frame.refreshScroll()

    def changeLayerMsg(self, message):
        if self.layer == WORD:
            self.layer = LINE
        elif self.layer == LINE:
            self.layer = ZONE
        elif self.layer == ZONE:
            self.layer = WORD
        self.frame.switchLayer(self.layer)
        if self.layer != ZONE and self.mode == CLASSIFY:
            self.mode = MERGE
            self.frame.switchMode(self.mode)
        self.frame.refreshScroll()

    def paintScroll(self, message):
        ctx = message.data
        if self.xml:
            ctx.clear(wx.LIGHT_GREY_BRUSH)
            if self.pageBitmap:
                ctx.drawBitmap(self.pageBitmap, scale=self.zoom / 100.0)
            else:
                ctx.setBrush(wx.WHITE_BRUSH)
                ctx.setPen(wx.TRANSPARENT_PEN)
                ctx.drawBox((0, 0, self.pageWidth * self.zoom / 100,
                    self.pageHeight * self.zoom / 100))
            page = ItemView(self.xml.pages[self.pageIndex],
                            self.xmlScale())
            if self.mode == CLASSIFY:
                def getStyle(item):
                    return item.model.label.style
                ctx.paintItems(page, ZONE, getStyle)
            else:
                ctx.paintItems(page, self.layer + 1, config.HIGHER_LAYER_STYLE)
                ctx.paintItems(page, self.layer, config.CURRENT_LAYER_STYLE)
                ctx.paintItems(page, self.layer - 1, config.LOWER_LAYER_STYLE)
            if self.selectionCorners:
                ctx.paintBox(cornersToBox(self.selectionCorners),
                             config.SELECTION_STYLE)
        else:
            ctx.clear(wx.GREY_BRUSH)

    def closeApp(self, message=None):
        if self.xml:
            if not self.closeXML():
                return
        self.frame.Destroy()

    def popupMenuMsg(self, message):
        if self.xml:
            clickedPosition = message.data
            clickedItem = self.xml.findItemByPoint(self.pageIndex, self.layer,
                toDocumentCoordinates(clickedPosition, self.xmlScale()))
            if clickedItem:
                if self.mode == CLASSIFY:
                    self.frame.popupZoneLabelMenu(clickedItem.label, clickedItem)
                else:
                    self.frame.popupStandardMenu(self.layer, arg=clickedPosition)

    def atomizeMsg(self, message):
        pos = message.data
        if self.xml:
            self.xml.atomize(self.pageIndex, self.layer, cornersToBounds([pos, pos], self.xmlScale()))

    def smartSplitMsg(self, message):
        pos = message.data
        if self.xml:
            self.xml.smartSplitWords(self.pageIndex, cornersToBounds([pos, pos], self.xmlScale()))

    def classifyMsg(self, message):
        label, arg = message.data
        if isinstance(arg, document.Bounds):
            self.xml.changeLabels(self.pageIndex, arg, label)
        elif isinstance(arg, document.DocumentZone):
            self.xml.changeLabel(arg, label)
        else:
            raise ValueError()


    def runTask(self, message, task, args=()):
        # Python 2.x lacks nonlocal keyword
        results = [None, None]
        def closure():
            try:
                results[0] = task(*args)
            except Exception as e:
                sys.excepthook(*sys.exc_info())
                results[1] = e
        self.frame.taskDialog(message, closure)
        if results[1] is not None:
            raise results[1]
        else:
            return results[0]


if __name__ == "__main__":
    app = Application()
