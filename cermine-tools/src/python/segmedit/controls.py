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

class TextCtrl(wx.TextCtrl):
    """
    Text control with additional EVT_TEXT_CHANGED event fired *after* text
    change.
    Use SetText() to change the text of the control without firing
    EVT_TEXT_CHANGED event.
    """

    EVT_TEXT_CHANGED = wx.PyEventBinder(wx.NewEventType())

    def __init__(self, *args, **kwargs):
        wx.TextCtrl.__init__(self, *args, **kwargs)
        self.__prevText = self.GetValue()
        self.Bind(wx.EVT_KILL_FOCUS, self.__onKillFocus)
        self.Bind(wx.EVT_CHAR, self.__onChar)

    def __onKillFocus(self, event):
        self.__handleChange()
        event.Skip()

    def __onChar(self, event):
        if event.GetKeyCode() == wx.WXK_RETURN:
            self.__handleChange()
        event.Skip()

    def __handleChange(self):
        if self.GetValue() != self.__prevText:
            event = wx.CommandEvent(self.EVT_TEXT_CHANGED.typeId, self.Id)
            self.__prevText = event.String = self.GetValue()
            self.ProcessEvent(event)

    def GetText(self):
        return self.GetValue()

    def SetText(self, value):
        self.SetValue(value)
        self.__prevText = value

    Text = property(GetText, SetText)


if __name__ == "__main__":
    print "This is a module, don't run it as a program"
