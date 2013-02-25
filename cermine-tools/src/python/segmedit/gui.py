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


import wx, os, copy, sys
from wx.lib.stattext import GenStaticText
from wx.lib.intctrl import IntCtrl
import eventsmanager, config
from constants import *
import thread
import controls

class ScrollPaintContext(object):

    def __init__(self, window):
        self.window = window
        self.x, self.y = self.window.CalcUnscrolledPosition(0, 0)
        self.width, self.height = self.window.GetSizeTuple()
        self.dc = wx.PaintDC(window)
        self.gc = wx.GraphicsContext.Create(self.dc)
        self.gc.Translate(-self.x, -self.y)
        self.region = self.window.GetUpdateRegion()
        self.region.Offset(self.x, self.y)
        self._needsOffset = 1

    def setBrush(self, brush):
        self.gc.SetBrush(brush)

    def setPen(self, pen):
        self.gc.SetPen(pen)
        self._needsOffset = pen.GetWidth() & 1

    def drawBitmap(self, bmp, x=0, y=0, width=None, height=None, scale=1):
        if width is None:
            width = bmp.GetWidth()
        if height is None:
            height = bmp.GetHeight()
        self.gc.DrawBitmap(bmp, x, y, width * scale, height * scale)

    def setStyle(self, style):
        self.setBrush(style.brush)
        self.setPen(style.pen)

    def clear(self, brush):
        self.setBrush(brush)
        self.setPen(wx.TRANSPARENT_PEN)
        self.gc.DrawRectangle(self.x, self.y, self.width, self.height)

    def drawBox(self, box):
        if self._needsOffset:
            self.gc.DrawRectangle(round(box[0]) + 0.5, round(box[1]) + 0.5,
                                  round(box[2]), round(box[3]))
        else:
            self.gc.DrawRectangle(round(box[0]), round(box[1]),
                                  round(box[2]), round(box[3]))

    def paintBox(self, box, style):
        self.setStyle(style)
        self.drawBox(box)

    def _traverseItems(self, items, layer, function, margin=2):
        for item in items:
            box = item.box
            if self.region.ContainsRectDim(box[0] - margin, box[1] - margin,
                    box[2] + 2 * margin, box[3] + 2 * margin) != wx.OutRegion:
                if item.layer == layer:
                    function(item)
                elif item.layer > layer:
                    self._traverseItems(item, layer, function, margin)

    def drawItems(self, items, layer):
        def process(item):
            self.drawBox(item.box)
        self._traverseItems(items, layer, process)

    def paintItems(self, items, layer, style):
        if callable(style):
            def process(item):
                self.setStyle(style(item))
                self.drawBox(item.box)
            self._traverseItems(items, layer, process)
        else:
            self.setStyle(style)
            self.drawItems(items, layer)


class MainWindow(wx.Frame):

    def __init__(self, parent, title, size):
        super(MainWindow, self).__init__(parent, title=title,
            size=size)
        self._popupArg = None
        self.createWidgets()
        self.arrangeLayout()
        self.evtmgr = eventsmanager.EventsManager(self)
        self.Centre()
        self.Show()

    def createWidgets(self):
        """
        creates all items of the main window
        except any ancillary items
        """
        self.panel = wx.Panel(self)
        self.scroll = wx.ScrolledWindow(self.panel)
        self.firstPageBtn = wx.Button(self.panel, label="|<")
        self.pgbtn1 = wx.Button(self.panel, label="<")
        self.pgbtn2 = wx.Button(self.panel, label=">")
        self.lastPageBtn = wx.Button(self.panel, label=">|")
        self.pageCtrl = controls.TextCtrl(self.panel, style=wx.TE_CENTER)
        self.modeBtnMerge = wx.RadioButton(self.panel, label='Merge', style=wx.RB_GROUP)
        self.modeBtnCutOut = wx.RadioButton(self.panel, label='Cut out')
        self.modeBtnClassify = wx.RadioButton(self.panel, label='Classify')
        self.laybtn1 = wx.RadioButton(self.panel, label='Word', style=wx.RB_GROUP)
        self.laybtn2 = wx.RadioButton(self.panel, label='Line')
        self.laybtn3 = wx.RadioButton(self.panel, label='Zone')
        self.zoombtn1 = wx.Button(self.panel, label='-')
        self.zoombtn2 = wx.Button(self.panel, label='+')
        self.zoomCtrl = controls.TextCtrl(self.panel, style=wx.TE_CENTER)

        # menu
        self.menuBar = wx.MenuBar()

        self.menuFile = wx.Menu()
        self.menuItemOpenNet = self.menuFile.Append(wx.NewId(), "Open &network document\tCtrl+N")
        self.menuItemSendNet = self.menuFile.Append(wx.NewId(), "Send network document")
        self.menuFile.AppendSeparator()
        self.menuItemOpenXML = self.menuFile.Append(wx.NewId(), "Open local &XML\tCtrl+O")
        self.menuItemOpenPDF = self.menuFile.Append(wx.NewId(), "Open &PDF\tCtrl+P")
        self.menuItemSaveXML = self.menuFile.Append(wx.NewId(), "&Save XML\tCtrl+S")
        self.menuItemSaveXMLAs = self.menuFile.Append(wx.NewId(), "Save XML as...")
        self.menuFile.AppendSeparator()
        self.menuItemCloseXML = self.menuFile.Append(wx.NewId(), "Close XML")
        self.menuItemClosePDF = self.menuFile.Append(wx.NewId(), "Close PDF")
        self.menuFile.AppendSeparator()
        self.menuItemQuit = self.menuFile.Append(wx.NewId(), "&Quit\tCtrl+Q")
        self.menuBar.Append(self.menuFile, '&File')

        self.menuEdit = wx.Menu()
        self.menuItemUndo = self.menuEdit.Append(wx.NewId(), "Undo\tCtrl+Z")
        self.menuItemRedo = self.menuEdit.Append(wx.NewId(), "Redo\tCtrl+Y")
        self.menuBar.Append(self.menuEdit, '&Edit')

        self.menuMode = wx.Menu()
        self.menuItemChangeMode = self.menuMode.Append(wx.NewId(),
            "Change mode\tCtrl+M")
        self.menuMode.AppendSeparator()
        self.menuItemMerge = self.menuMode.AppendRadioItem(wx.NewId(), "Merge")
        self.menuItemCutOut = self.menuMode.AppendRadioItem(wx.NewId(), "Cut out")
        self.menuItemClassify = self.menuMode.AppendRadioItem(wx.NewId(), "Classify")
        self.menuBar.Append(self.menuMode, '&Mode')

        self.menuLayer = wx.Menu()
        self.menuItemChangeLayer = self.menuLayer.Append(wx.NewId(),
            "Change layer\tCtrl+L")
        self.menuLayer.AppendSeparator()
        self.menuItemWord = self.menuLayer.AppendRadioItem(wx.NewId(), "Word")
        self.menuItemLine = self.menuLayer.AppendRadioItem(wx.NewId(), "Line")
        self.menuItemZone = self.menuLayer.AppendRadioItem(wx.NewId(), "Zone")
        self.menuBar.Append(self.menuLayer, 'Current &Layer')

        self.menuZoom = wx.Menu()
        self.menuItemZoomOut = self.menuZoom.Append(wx.NewId(), "Zoom out\tCtrl+-")
        self.menuItemZoomIn = self.menuZoom.Append(wx.NewId(), "Zoom in\tCtrl++")
        self.menuBar.Append(self.menuZoom, '&Zoom')

        self.SetMenuBar(self.menuBar)

        # popup menus
        self.standardPopup = wx.Menu()
        self.menuItemAtomize = self.standardPopup.Append(wx.NewId(), "Atomize")
        self.menuItemSmartSplit = self.standardPopup.Append(wx.NewId(), "Turn word into line")
        #self.menuProperties = self.standardPopup.Append(wx.NewId(), "Item properties")

        self.zoneLabelsPopupRadio = wx.Menu()
        self.zoneLabelsPopupNormal = wx.Menu()
        self.menuItemsZoneLabelsRadio = {}
        self.menuItemsZoneLabelsNormal = {}
        self.menuItemsIdToZoneLabel = {}
        for z in config.ZONE_LABELS:
            newitem = self.zoneLabelsPopupRadio.AppendRadioItem(wx.NewId(), z.text)
            self.menuItemsIdToZoneLabel[newitem.GetId()] = z
            self.menuItemsZoneLabelsRadio[z.name] = newitem
            newitem = self.zoneLabelsPopupNormal.Append(wx.NewId(), z.text)
            self.menuItemsIdToZoneLabel[newitem.GetId()] = z
            self.menuItemsZoneLabelsNormal[z.name] = newitem

        # status bar
        #self.CreateStatusBar()



    def arrangeLayout(self):
        """
        sets sizes, fonts, colours etc. of the items created in
        createWidgets(),
        arranges them using sizers,
        creates ancillary items like statc texts, static boxes etc, useful 
        for the frame layout
        """

        ###
        # SIZE
        ###
        self.firstPageBtn.SetInitialSize((config.BUTTON_HEIGHT * 115 / 100,
            config.BUTTON_HEIGHT))
        self.pgbtn1.SetInitialSize((config.BUTTON_HEIGHT * 115 / 100,
            config.BUTTON_HEIGHT))
        self.pgbtn2.SetInitialSize((config.BUTTON_HEIGHT * 115 / 100,
            config.BUTTON_HEIGHT))
        self.lastPageBtn.SetInitialSize((config.BUTTON_HEIGHT * 115 / 100,
            config.BUTTON_HEIGHT))
        self.pageCtrl.SetInitialSize((config.BUTTON_HEIGHT * 240 / 100,
            config.BUTTON_HEIGHT))

        self.modeBtnMerge.SetInitialSize((config.BUTTON_HEIGHT * 300 / 100,
            config.BUTTON_HEIGHT))
        self.modeBtnCutOut.SetInitialSize((config.BUTTON_HEIGHT * 300 / 100,
            config.BUTTON_HEIGHT))
        self.modeBtnClassify.SetInitialSize((config.BUTTON_HEIGHT * 300 / 100,
            config.BUTTON_HEIGHT))

        self.laybtn1.SetInitialSize((config.BUTTON_HEIGHT * 240 / 100,
            config.BUTTON_HEIGHT))
        self.laybtn2.SetInitialSize((config.BUTTON_HEIGHT * 240 / 100,
            config.BUTTON_HEIGHT))
        self.laybtn3.SetInitialSize((config.BUTTON_HEIGHT * 240 / 100,
            config.BUTTON_HEIGHT))

        self.zoombtn1.SetInitialSize((config.BUTTON_HEIGHT * 115 / 100,
            config.BUTTON_HEIGHT))
        self.zoombtn2.SetInitialSize((config.BUTTON_HEIGHT * 115 / 100,
            config.BUTTON_HEIGHT))
        self.zoomCtrl.SetInitialSize((config.BUTTON_HEIGHT * 160 / 100,
            config.BUTTON_HEIGHT))

        ###
        # SIZERS
        ###
        panelsizer = wx.BoxSizer()
        vbox = wx.BoxSizer(wx.VERTICAL)
        flgrid = wx.FlexGridSizer(1, 9, hgap=3)
        nboflabels = len(config.ZONE_LABELS)
        # Static boxes and static box sizers
        pgsb = wx.StaticBox(self.panel, label='Page')
        pgsbsizer = wx.StaticBoxSizer(pgsb, wx.HORIZONTAL)
        modesb = wx.StaticBox(self.panel, label='Mode')
        modesbsizer = wx.StaticBoxSizer(modesb, wx.HORIZONTAL)
        layersb = wx.StaticBox(self.panel, label='Current layer')
        layersbsizer = wx.StaticBoxSizer(layersb, wx.HORIZONTAL)
        zoomsb = wx.StaticBox(self.panel, label='Zoom')
        zoomsbsizer = wx.StaticBoxSizer(zoomsb, wx.HORIZONTAL)

        # adjusting flex grid sizer
        flgrid.AddGrowableCol(1, 1)
        flgrid.AddGrowableCol(3, 1)
        flgrid.AddGrowableCol(5, 1)
        flgrid.AddGrowableCol(6, 1)
        flgrid.AddGrowableCol(7, 1)

        # adding widgets to sizers and sizers to sizers
        vbox.Add(self.scroll, proportion=1, flag=wx.EXPAND | wx.ALL, border=3)
        vbox.Add(flgrid, proportion=0, flag=wx.EXPAND)

        flgrid.Add(pgsbsizer)
        flgrid.Add((-1, -1), 1)
        flgrid.Add(modesbsizer)
        flgrid.Add((-1, -1), 1)
        flgrid.Add(layersbsizer)
        flgrid.Add((-1, -1), 1)
        flgrid.Add((-1, -1), 1)
        flgrid.Add((-1, -1), 1)
        flgrid.Add(zoomsbsizer)

        pgsbsizer.Add(self.firstPageBtn)
        pgsbsizer.Add(self.pgbtn1)
        pgsbsizer.Add(self.pageCtrl)
        pgsbsizer.Add(self.pgbtn2)
        pgsbsizer.Add(self.lastPageBtn)

        #modesbsizer.Add(self.modeBtnSelect)
        modesbsizer.Add(self.modeBtnMerge)
        modesbsizer.Add(self.modeBtnCutOut)
        modesbsizer.Add(self.modeBtnClassify)

        layersbsizer.Add(self.laybtn1)
        layersbsizer.Add(self.laybtn2)
        layersbsizer.Add(self.laybtn3)

        zoomsbsizer.Add(self.zoombtn1)
        zoomsbsizer.Add(self.zoomCtrl)
        zoomsbsizer.Add(self.zoombtn2)

        # connecting sizers to main panel
        self.panel.SetSizer(vbox)
        panelsizer.Add(self.panel, 1, flag=wx.EXPAND)
        self.SetSizer(panelsizer)

        ###
        # FONTS
        ###
        btnfont = wx.SystemSettings_GetFont(wx.SYS_SYSTEM_FONT)
        btnfont.SetPointSize(config.BUTTON_FONT_SIZE)
        labelfont = wx.SystemSettings_GetFont(wx.SYS_SYSTEM_FONT)
        labelfont.SetPointSize(config.LABEL_FONT_SIZE)
        btnlittlefont = wx.SystemSettings_GetFont(wx.SYS_SYSTEM_FONT)
        btnlittlefont.SetPointSize(config.LITTLE_BUTTON_FONT_SIZE)

        # setting fonts
        self.firstPageBtn.SetFont(btnfont)
        self.pgbtn1.SetFont(btnfont)
        self.pgbtn2.SetFont(btnfont)
        self.lastPageBtn.SetFont(btnfont)
        map(lambda b: b.SetFont(btnfont),
            #[self.modeBtnSelect, self.modeBtnMerge, self.modeBtnCutOut])
            [self.modeBtnMerge, self.modeBtnCutOut, self.modeBtnClassify])
        self.laybtn1.SetFont(btnfont)
        self.laybtn2.SetFont(btnfont)
        self.laybtn3.SetFont(btnfont)

        # fonts for ancillary items
        pgsb.SetFont(labelfont)
        modesb.SetFont(labelfont)
        layersb.SetFont(labelfont)
        zoomsb.SetFont(labelfont)

        ###
        # COLOURS
        ###
        self.scroll.SetBackgroundStyle(wx.BG_STYLE_CUSTOM)


    def setPage(self, index, total):
        self.firstPageBtn.Enable(index > 0)
        self.pgbtn1.Enable(index > 0)
        self.pgbtn2.Enable(index + 1 < total)
        self.lastPageBtn.Enable(index + 1 < total)
        self.pageCtrl.Enable(total > 0)
        if total:
            self.pageCtrl.Text = "%d/%d" % (index + 1, total)
        else:
            self.pageCtrl.Text = ""


    def setZoom(self, zoom):
        self.zoomCtrl.Text = str(zoom)


    def setScrollViewport(self, width, height, oldwidth=0, oldheight=0):
        """
        Sets scroll viewport dimensions and refreshes it.
        """

        step = config.SCROLL_STEP
        xsteps = width / step
        if width % step != 0:
            xsteps = xsteps + 1
        ysteps = height / step
        if height % step != 0:
            ysteps = ysteps + 1

            (width, height, oldwidth, oldheight)
        if oldwidth and oldheight:

            xscrollsize, yscrollsize = self.scroll.GetSize()
            xpos, ypos = self.scroll.GetViewStart()

            ppux, ppuy = self.scroll.GetScrollPixelsPerUnit()
            xcenter = xpos * ppux + (xscrollsize / 2)
            ycenter = ypos * ppuy + (yscrollsize / 2)

            newxcenter = xcenter * width / oldwidth
            newycenter = ycenter * height / oldheight

            newxstart = newxcenter - (xscrollsize / 2)
            newystart = newycenter - (yscrollsize / 2)

            newxpos = max(0, newxstart / step)
            newypos = max(0, newystart / step)

        else:
            newxpos, newypos = 0, 0

        self.scroll.SetScrollbars(step, step, xsteps, ysteps, newxpos, newypos)
        self.refreshScroll()


    def refreshScroll(self, box=None, margin=0):
        if box is None:
            self.scroll.Refresh()
        else:
            dx, dy = self.scroll.CalcUnscrolledPosition(0, 0)
            self.scroll.RefreshRect((box[0] - dx - margin, box[1] - dy - margin,
                                     box[2] + 2 * margin, box[3] + 2 * margin))


    def createScrollPaintContext(self):
        return ScrollPaintContext(self.scroll)


    def selectFile(self, message="Select file to open", wildcard='*', save=False, directory=""):
        if save:
            style = wx.FD_SAVE
        else:
            style = wx.FD_OPEN
        filedialog = wx.FileDialog(self, style=style, message=message,
            wildcard=wildcard, defaultDir=directory)
        if filedialog.ShowModal() == wx.ID_OK:
            filename = filedialog.GetFilename()
            dirname = filedialog.GetDirectory()
            #path = dirname + os.path.sep + filename
            filedialog.Destroy()
            return (dirname, filename)
        else:
            filedialog.Destroy()
            return None

    def selectNetworkDocument(self, currentDocuments, availableDocuments):
        """
        Opens a dialog window with a choice of network documents available to open
        """
        self.currentDocuments = currentDocuments
        self.availableDocuments = availableDocuments

        self.netopenDialog = wx.Dialog(self, size=(800, 520), style=wx.DEFAULT_FRAME_STYLE)
        dialogPanel = wx.Panel(self.netopenDialog)
        mainSizer = wx.BoxSizer(wx.HORIZONTAL)
        dialogPanel.SetSizer(mainSizer)

        recentlyOpenSB = wx.StaticBox(dialogPanel, label='Recently opened documents')
        recentlyOpenSBSizer = wx.StaticBoxSizer(recentlyOpenSB, wx.VERTICAL)
        mainSizer.Add(recentlyOpenSBSizer, proportion=2, flag=wx.EXPAND)

        self.netopenRecentlyList = wx.ListCtrl(dialogPanel, style=wx.LC_REPORT | wx.LC_SINGLE_SEL)
        self.netopenRecentlyList.Show(True)
        self.netopenRecentlyList.InsertColumn(0, 'Document name')
        self.netopenRecentlyList.InsertColumn(1, 'Status')
        self.netopenRecentlyList.SetColumnWidth(0, 200)
        self.netopenRecentlyList.SetColumnWidth(1, 80)
        recentlyOpenSBSizer.Add(self.netopenRecentlyList, proportion=1, flag=wx.EXPAND)

        newDocSB = wx.StaticBox(dialogPanel, label='New documents')
        newDocSBSizer = wx.StaticBoxSizer(newDocSB, wx.VERTICAL)
        mainSizer.Add(newDocSBSizer, proportion=2, flag=wx.EXPAND)

        self.netopenNewList = wx.ListCtrl(dialogPanel, style=wx.LC_REPORT | wx.LC_SINGLE_SEL)
        self.netopenNewList.Show(True)
        self.netopenNewList.InsertColumn(0, 'Document title')
        self.netopenNewList.InsertColumn(1, 'Status')
        self.netopenNewList.SetColumnWidth(0, 200)
        self.netopenNewList.SetColumnWidth(1, 80)
        newDocSBSizer.Add(self.netopenNewList, proportion=1, flag=wx.EXPAND)

        rightSizer = wx.BoxSizer(wx.VERTICAL)
        mainSizer.Add(rightSizer, proportion=2, flag=wx.EXPAND)

        self.netopenTitleSB = wx.StaticBox(dialogPanel, label="Title")
        titleSBSizer = wx.StaticBoxSizer(self.netopenTitleSB)
        rightSizer.Add(titleSBSizer, proportion=1, flag=wx.EXPAND | wx.TOP, border=30)
        self.netopenTitleText = wx.StaticText(dialogPanel)
        titleSBSizer.Add(self.netopenTitleText, proportion=1, flag=wx.EXPAND)

        self.netopenCommentSB = wx.StaticBox(dialogPanel, label="Comment")
        commentSBSizer = wx.StaticBoxSizer(self.netopenCommentSB)
        rightSizer.Add(commentSBSizer, proportion=1, flag=wx.EXPAND | wx.BOTTOM, border=30)
        self.netopenCommentText = wx.StaticText(dialogPanel)
        commentSBSizer.Add(self.netopenCommentText, proportion=1, flag=wx.EXPAND)

        self.netopenOpenBtn = wx.Button(dialogPanel, label='OK', size=(130, 35))
        self.netopenCancelBtn = wx.Button(dialogPanel, label='Cancel', size=(130, 35))
        self.netopenRandomBtn = wx.Button(dialogPanel, label='Random document', size=(130, 35))
        rightSizer.Add(self.netopenOpenBtn, flag=wx.ALIGN_RIGHT | wx.RIGHT | wx.TOP, border=20)
        rightSizer.Add(self.netopenCancelBtn, flag=wx.ALIGN_RIGHT | wx.RIGHT, border=20)
        rightSizer.Add(self.netopenRandomBtn, flag=wx.ALIGN_RIGHT | wx.TOP | wx.BOTTOM | wx.RIGHT, border=20)

        for i in range(len(self.currentDocuments)):
            idx = self.netopenRecentlyList.InsertStringItem(i, self.currentDocuments[i].text)
            self.netopenRecentlyList.SetStringItem(idx, 1, self.currentDocuments[i].status)
            self.netopenRecentlyList.SetItemData(idx, i)

        for i in range(len(self.availableDocuments)):
            idx = self.netopenNewList.InsertStringItem(i, self.availableDocuments[i].text)
            self.netopenNewList.SetStringItem(idx, 1, self.availableDocuments[i].status)
            self.netopenNewList.SetItemData(idx, i)

        self.evtmgr.registerNetopenEvents()
        if self.netopenDialog.ShowModal() == wx.ID_OK:
            itemSelected = self.netopenNewList.GetFirstSelected()
            if itemSelected != -1:
                recordidx = self.netopenNewList.GetItemData(itemSelected)
                selectedRecord = self.availableDocuments[recordidx]
            else:
                itemSelected = self.netopenRecentlyList.GetFirstSelected()
                if itemSelected != -1:
                    recordidx = self.netopenRecentlyList.GetItemData(itemSelected)
                    selectedRecord = self.currentDocuments[recordidx]
                else:
                    selectedRecord = None
        else:
            selectedRecord = None

        self.netopenDialog.Destroy()

        if selectedRecord:
            return selectedRecord.id
        else:
            return None


    def sendNetworkDocument(self, comment=''):
        self.netsendDialog = wx.Dialog(self, size=(400, 400), style=wx.DEFAULT_FRAME_STYLE)
        dialogPanel = wx.Panel(self.netsendDialog)
        mainSizer = wx.BoxSizer(wx.VERTICAL)
        dialogPanel.SetSizer(mainSizer)

        mainSizer.Add(wx.StaticText(dialogPanel, label='Comment:'))
        commentField = wx.TextCtrl(dialogPanel, style=wx.TE_MULTILINE, value=comment)
        mainSizer.Add(commentField, proportion=1, flag=wx.EXPAND)
        mainSizer.Add(wx.StaticText(dialogPanel, label='Document status:'), flag=wx.TOP, border=20)
        statusesList = ['locked', 'unlocked', 'error', 'complete']
        statusChoice = wx.Choice(dialogPanel, choices=statusesList)
        mainSizer.Add(statusChoice, flag=wx.LEFT, border=15)

        buttonsSizer = wx.BoxSizer(wx.HORIZONTAL)
        mainSizer.Add(buttonsSizer, flag=wx.EXPAND)

        self.netsendOKButton = wx.Button(dialogPanel, label='OK')
        self.netsendCancelButton = wx.Button(dialogPanel, label='Cancel')
        buttonsSizer.Add(self.netsendOKButton, flag=wx.ALL | wx.ALIGN_RIGHT, border=20)
        buttonsSizer.Add(self.netsendCancelButton, flag=wx.ALL | wx.ALIGN_RIGHT, border=20)

        self.evtmgr.registerNetsendEvents()
        if self.netsendDialog.ShowModal() == wx.ID_OK:
            self.netsendDialog.Destroy()
            return statusesList[statusChoice.GetCurrentSelection()], commentField.GetValue()
        else:
            self.netsendDialog.Destroy()
            return None


    def enableMenuItems(self, sendNet='NoChange', openPDF='NoChange', closeXML='NoChange',
            closePDF='NoChange', save='NoChange', saveAs='NoChange',
            undo='NoChange', redo='NoChange'):
        if sendNet != 'NoChange':
            self.menuItemSendNet.Enable(sendNet)
        if openPDF != 'NoChange':
            self.menuItemOpenPDF.Enable(openPDF)
        if closeXML != 'NoChange':
            self.menuItemCloseXML.Enable(closeXML)
        if closePDF != 'NoChange':
            self.menuItemClosePDF.Enable(closePDF)
        if save != 'NoChange':
            self.menuItemSaveXML.Enable(save)
        if saveAs != 'NoChange':
            self.menuItemSaveXMLAs.Enable(saveAs)
        if undo != 'NoChange':
            self.menuItemUndo.Enable(undo)
        if redo != 'NoChange':
            self.menuItemRedo.Enable(redo)

    def switchMode(self, mode):
        if mode == MERGE:
            self.modeBtnMerge.SetValue(True)
            self.menuItemMerge.Check(True)
        elif mode == CUTOUT:
            self.modeBtnCutOut.SetValue(True)
            self.menuItemCutOut.Check(True)
        elif mode == CLASSIFY:
            self.modeBtnClassify.SetValue(True)
            self.menuItemClassify.Check(True)

    def switchLayer(self, layer):
        if layer == WORD:
            self.laybtn1.SetValue(True)
            self.menuItemWord.Check(True)
        elif layer == LINE:
            self.laybtn2.SetValue(True)
            self.menuItemLine.Check(True)
        elif layer == ZONE:
            self.laybtn3.SetValue(True)
            self.menuItemZone.Check(True)

    def yesNoCancelDialog(self, message):
        dlg = wx.MessageDialog(self, message, style=wx.YES_NO | wx.CANCEL | wx.YES_DEFAULT)
        answer = dlg.ShowModal()
        dlg.Destroy()
        return answer

    def errorDialog(self, errorMessage):
        if isinstance(errorMessage, unicode):
            errorMessage = errorMessage.encode('utf-8')
        dlg = wx.MessageDialog(self, errorMessage, style=wx.ICON_ERROR)
        dlg.ShowModal()
        dlg.Destroy()
        dlg.Destroy()

    def taskDialog(self, message, task):
        dlg = wx.ProgressDialog("Working...", message)
        timer = wx.Timer(dlg)
        def timerHandler(evt):
            dlg.Pulse()
        dlg.Bind(wx.EVT_TIMER, timerHandler)
        timer.Start(100)
        dlg.Pulse()
        def func():
            try:
                task()
            finally:
                wx.CallAfter(dlg.EndModal, wx.ID_CLOSE)
        thread.start_new_thread(func, ())
        dlg.ShowModal()
        timer.Stop()
        dlg.Destroy()


    def textDialog(self, message, default=''):
        dlg = wx.TextEntryDialog(self, message, defaultValue=default)
        if dlg.ShowModal() == wx.ID_OK:
            result = dlg.GetValue()
        else:
            result = None
        dlg.Destroy()
        return result

    def _popup(self, menu, arg):
        self._popupArg = arg
        self.scroll.PopupMenu(menu)
        # PopupMenu returns control when the user has dismissed the menu so
        # we no longer have to store arg
        self._popupArg = None

    def popupZoneLabelMenu(self, label=None, arg=None):
        if label is None:
            self._popup(self.zoneLabelsPopupNormal, arg)
        else:
            self.menuItemsZoneLabelsRadio[label.name].Check()
            self._popup(self.zoneLabelsPopupRadio, arg)

    def popupStandardMenu(self, layer, arg=None):
        self.menuItemSmartSplit.Enable(layer == WORD)
        self._popup(self.standardPopup, arg)

if __name__ == "__main__":
    print "This is a module, don't run it as a program"
