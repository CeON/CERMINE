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


import wx
from wx.lib.pubsub import Publisher as pub
import random
import re

class EventsManager(object):
    def __init__(self, frame):
        self.frame = frame
        self.registerEvents()

    def registerEvents(self):
        self.frame.scroll.Bind(wx.EVT_PAINT, self.scrollOnPaint)
        self.frame.firstPageBtn.Bind(wx.EVT_BUTTON, self.firstPageBtnOnClick)
        self.frame.pgbtn1.Bind(wx.EVT_BUTTON, self.pgbtn1OnClick)
        self.frame.pageCtrl.Bind(self.frame.pageCtrl.EVT_TEXT_CHANGED, self.pageCtrlOnChanged)
        self.frame.pgbtn2.Bind(wx.EVT_BUTTON, self.pgbtn2OnClick)
        self.frame.lastPageBtn.Bind(wx.EVT_BUTTON, self.lastPageBtnOnClick)

        self.frame.zoombtn1.Bind(wx.EVT_BUTTON, self.zoomOut)
        self.frame.zoombtn2.Bind(wx.EVT_BUTTON, self.zoomIn)
        self.frame.zoomCtrl.Bind(self.frame.zoomCtrl.EVT_TEXT_CHANGED, self.zoomCtrlOnChanged)
        self.frame.Bind(wx.EVT_MENU, self.zoomOut, id=self.frame.menuItemZoomOut.GetId())
        self.frame.Bind(wx.EVT_MENU, self.zoomIn, id=self.frame.menuItemZoomIn.GetId())

        self.frame.modeBtnMerge.Bind(wx.EVT_RADIOBUTTON, self.modeMerge)
        self.frame.modeBtnCutOut.Bind(wx.EVT_RADIOBUTTON, self.modeCutOut)
        self.frame.modeBtnClassify.Bind(wx.EVT_RADIOBUTTON, self.modeClassify)
        self.frame.Bind(wx.EVT_MENU, self.modeMerge, id=self.frame.menuItemMerge.GetId())
        self.frame.Bind(wx.EVT_MENU, self.modeCutOut, id=self.frame.menuItemCutOut.GetId())
        self.frame.Bind(wx.EVT_MENU, self.modeClassify, id=self.frame.menuItemClassify.GetId())

        self.frame.laybtn1.Bind(wx.EVT_RADIOBUTTON, self.layerWord)
        self.frame.laybtn2.Bind(wx.EVT_RADIOBUTTON, self.layerLine)
        self.frame.laybtn3.Bind(wx.EVT_RADIOBUTTON, self.layerZone)
        self.frame.Bind(wx.EVT_MENU, self.layerWord, id=self.frame.menuItemWord.GetId())
        self.frame.Bind(wx.EVT_MENU, self.layerLine, id=self.frame.menuItemLine.GetId())
        self.frame.Bind(wx.EVT_MENU, self.layerZone, id=self.frame.menuItemZone.GetId())

        self.frame.Bind(wx.EVT_MENU, self.menuChangeMode, id=self.frame.menuItemChangeMode.GetId())
        self.frame.Bind(wx.EVT_MENU, self.menuChangeLayer, id=self.frame.menuItemChangeLayer.GetId())

        self.frame.Bind(wx.EVT_MENU, self.menuOpenNet, id=self.frame.menuItemOpenNet.GetId())
        self.frame.Bind(wx.EVT_MENU, self.menuSendNet, id=self.frame.menuItemSendNet.GetId())
        self.frame.Bind(wx.EVT_MENU, self.menuOpenXML, id=self.frame.menuItemOpenXML.GetId())
        self.frame.Bind(wx.EVT_MENU, self.menuOpenPDF, id=self.frame.menuItemOpenPDF.GetId())
        self.frame.Bind(wx.EVT_MENU, self.menuSaveXML, id=self.frame.menuItemSaveXML.GetId())
        self.frame.Bind(wx.EVT_MENU, self.menuSaveXMLAs, id=self.frame.menuItemSaveXMLAs.GetId())
        self.frame.Bind(wx.EVT_MENU, self.menuCloseXML, id=self.frame.menuItemCloseXML.GetId())
        self.frame.Bind(wx.EVT_MENU, self.menuClosePDF, id=self.frame.menuItemClosePDF.GetId())
        self.frame.Bind(wx.EVT_MENU, self.closeApp, id=self.frame.menuItemQuit.GetId())

        self.frame.Bind(wx.EVT_MENU, self.menuUndo, id=self.frame.menuItemUndo.GetId())
        self.frame.Bind(wx.EVT_MENU, self.menuRedo, id=self.frame.menuItemRedo.GetId())

        self.frame.Bind(wx.EVT_MENU, self.menuAtomize, id=self.frame.menuItemAtomize.GetId())
        self.frame.Bind(wx.EVT_MENU, self.menuSmartSplit, id=self.frame.menuItemSmartSplit.GetId())

        for item in self.frame.menuItemsZoneLabelsRadio.itervalues():
            self.frame.Bind(wx.EVT_MENU, self.menuClassify, id=item.GetId())

        for item in self.frame.menuItemsZoneLabelsNormal.itervalues():
            self.frame.Bind(wx.EVT_MENU, self.menuClassify, id=item.GetId())

        self.frame.scroll.Bind(wx.EVT_LEFT_DOWN, self.scrollOnLeftDown)
        self.frame.scroll.Bind(wx.EVT_RIGHT_DOWN, self.scrollOnRightDown)
        self.frame.scroll.Bind(wx.EVT_LEFT_UP, self.scrollOnLeftUp)
        self.frame.scroll.Bind(wx.EVT_MOTION, self.scrollOnMotion)
        self.frame.scroll.Bind(wx.EVT_ENTER_WINDOW, self.scrollOnEnter)
        self.frame.scroll.Bind(wx.EVT_LEAVE_WINDOW, self.scrollOnLeave)
        self.frame.Bind(wx.EVT_CLOSE, self.closeApp)


    def registerNetopenEvents(self):
        self.frame.netopenNewList.Bind(wx.EVT_LIST_ITEM_SELECTED, self.netopenNewSelected)
        self.frame.netopenRecentlyList.Bind(wx.EVT_LIST_ITEM_SELECTED, self.netopenRecentlySelected)
        self.frame.netopenNewList.Bind(wx.EVT_LEFT_DCLICK, self.netopenOpenBtnOnClick)
        self.frame.netopenRecentlyList.Bind(wx.EVT_LEFT_DCLICK, self.netopenOpenBtnOnClick)
        self.frame.netopenOpenBtn.Bind(wx.EVT_BUTTON, self.netopenOpenBtnOnClick)
        self.frame.netopenCancelBtn.Bind(wx.EVT_BUTTON, self.netopenCancelBtnOnClick)
        self.frame.netopenRandomBtn.Bind(wx.EVT_BUTTON, self.netopenRandomBtnOnClick)

    def registerNetsendEvents(self):
        self.frame.netsendCancelButton.Bind(wx.EVT_BUTTON, self.netsendCancelBtnOnClick)
        self.frame.netsendOKButton.Bind(wx.EVT_BUTTON, self.netsendOKBtnOnClick)

    def firstPageBtnOnClick(self, evt):
        pub.sendMessage("FIRSTPAGE")

    def pgbtn1OnClick(self, evt):
        pub.sendMessage("PGDOWN")

    def pageCtrlOnChanged(self, evt):
        match = re.match('\s*([+-]?\d+)', evt.String)
        if match:
            pub.sendMessage("CHANGE_PAGE", int(match.group(1)) - 1)
        else:
            # This should eventually restore previous value
            pub.sendMessage("CHANGE_PAGE", -1)

    def pgbtn2OnClick(self, evt):
        pub.sendMessage("PGUP")

    def lastPageBtnOnClick(self, evt):
        pub.sendMessage("LASTPAGE")

    def scrollOnPaint(self, evt):
        pub.sendMessage("PAINT", self.frame.createScrollPaintContext())

    def zoomOut(self, evt):
        pub.sendMessage("ZOOMOUT")

    def zoomIn(self, evt):
        pub.sendMessage("ZOOMIN")

    def zoomCtrlOnChanged(self, evt):
        match = re.match('\s*([+-]?\d+)\s*$', evt.String)
        if match:
            pub.sendMessage("CHANGE_ZOOM", int(match.group(1)))
        else:
            # This should eventually restore previous value
            pub.sendMessage("CHANGE_ZOOM", -1)

    def modeMerge(self, evt):
        pub.sendMessage("MODE_MERGE")

    def modeCutOut(self, evt):
        pub.sendMessage("MODE_CUTOUT")

    def modeClassify(self, evt):
        pub.sendMessage("MODE_CLASSIFY")

    def layerWord(self, evt):
        pub.sendMessage("LAYER1")

    def layerLine(self, evt):
        pub.sendMessage("LAYER2")

    def layerZone(self, evt):
        pub.sendMessage("LAYER3")

    def menuChangeMode(self, evt):
        pub.sendMessage("CHANGE_MODE")

    def menuChangeLayer(self, evt):
        pub.sendMessage("CHANGE_LAYER")

    def menuOpenXML(self, evt):
        pub.sendMessage("OPENXML")

    def menuOpenNet(self, evt):
        pub.sendMessage("OPENNET")

    def menuSendNet(self, evt):
        pub.sendMessage("SENDNET")

    def menuOpenPDF(self, evt):
        pub.sendMessage("OPENPDF")

    def menuSaveXML(self, evt):
        pub.sendMessage("SAVEXML")

    def menuSaveXMLAs(self, evt):
        pub.sendMessage("SAVEXMLAS")

    def menuCloseXML(self, evt):
        pub.sendMessage("CLOSEXML")

    def menuClosePDF(self, evt):
        pub.sendMessage("CLOSEPDF")

    def closeApp(self, evt):
        pub.sendMessage("CLOSEAPP")

    def menuUndo(self, evt):
        pub.sendMessage("UNDO")

    def menuRedo(self, evt):
        pub.sendMessage("REDO")

    def menuAtomize(self, evt):
        pub.sendMessage("ATOMIZE", self.frame._popupArg)

    def menuSmartSplit(self, evt):
        pub.sendMessage("SMART_SPLIT", self.frame._popupArg)

    def netopenNewSelected(self, evt):
        recsel = self.frame.netopenRecentlyList.GetFirstSelected()
        if recsel != -1:
            self.frame.netopenRecentlyList.Select(recsel, False)
        newsel = self.frame.netopenNewList.GetFirstSelected()
        recordidx = self.frame.netopenNewList.GetItemData(newsel)
        record = self.frame.availableDocuments[recordidx]
        self.frame.netopenTitleText.SetLabel(record.text)
        self.frame.netopenTitleText.Wrap(self.frame.netopenTitleSB.GetSize().width - 15)
        self.frame.netopenCommentText.SetLabel(record.comment)
        self.frame.netopenCommentText.Wrap(self.frame.netopenCommentSB.GetSize().width - 15)

    def netopenRecentlySelected(self, evt):
        newsel = self.frame.netopenNewList.GetFirstSelected()
        if newsel != -1:
            self.frame.netopenNewList.Select(newsel, False)
        recsel = self.frame.netopenRecentlyList.GetFirstSelected()
        recordidx = self.frame.netopenRecentlyList.GetItemData(recsel)
        record = self.frame.currentDocuments[recordidx]
        self.frame.netopenTitleText.SetLabel(record.text)
        self.frame.netopenTitleText.Wrap(self.frame.netopenTitleSB.GetSize().width - 15)
        self.frame.netopenCommentText.SetLabel(record.comment)
        self.frame.netopenCommentText.Wrap(self.frame.netopenCommentSB.GetSize().width - 15)

    def netopenOpenBtnOnClick(self, evt):
        self.frame.netopenDialog.EndModal(wx.ID_OK)

    def netopenCancelBtnOnClick(self, evt):
        self.frame.netopenDialog.EndModal(wx.ID_CANCEL)

    def netopenRandomBtnOnClick(self, evt):
        nbOfNew = self.frame.netopenNewList.GetItemCount()
        if nbOfNew > 0:
            choice = random.randint(0, nbOfNew - 1)
            self.frame.netopenNewList.Select(choice, True)
            self.netopenNewSelected(None)

    def netsendOKBtnOnClick(self, evt):
        self.frame.netsendDialog.EndModal(wx.ID_OK)

    def netsendCancelBtnOnClick(self, evt):
        self.frame.netsendDialog.EndModal(wx.ID_CANCEL)

    def _scrollMousePosition(self, evt):
        return self.frame.scroll.CalcUnscrolledPosition(evt.GetPosition()).Get()

    def scrollOnLeftDown(self, evt):
        pub.sendMessage("SCROLL_LEFT_DOWN", self._scrollMousePosition(evt))

    def scrollOnLeftUp(self, evt):
        pub.sendMessage("SCROLL_LEFT_UP")

    def scrollOnRightDown(self, evt):
        pub.sendMessage("POPUPMENU", self._scrollMousePosition(evt))

    def scrollOnMotion(self, evt):
        pub.sendMessage("SCROLL_MOVE", self._scrollMousePosition(evt))

    def scrollOnEnter(self, evt):
        pub.sendMessage("SCROLL_ENTER", self._scrollMousePosition(evt))

    def scrollOnLeave(self, evt):
        pub.sendMessage("SCROLL_LEAVE")

    def menuClassify(self, evt):
        zoneLabel = self.frame.menuItemsIdToZoneLabel[evt.GetId()]
        pub.sendMessage("CLASSIFY", (zoneLabel, self.frame._popupArg))



if __name__ == "__main__":
    print "This is a module, don't run this as a program"
