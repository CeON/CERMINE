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


import wx, collections

class ShapeStyle(object):

    def __init__(self, fillColor, strokeColor=(0, 0, 0), strokeWidth=1, dashes=None):
        self.fillColor = fillColor or (0, 0, 0, 0)
        self.strokeColor = strokeColor
        self.strokeWidth = strokeWidth
        self.dashes = dashes
        self._brush = None
        self._pen = None

    @property
    def brush(self):
        if self._brush is None:
            self._brush = wx.Brush(wx.Colour(*self.fillColor))
        return self._brush

    @property
    def pen(self):
        if self._pen is None:
            if self.strokeWidth == 0:
                self._pen = wx.TRANSPARENT_PEN
            else:
                self._pen = wx.Pen(wx.Colour(*self.strokeColor), self.strokeWidth)
                if self.dashes:
                    self._pen.SetStyle(wx.USER_DASH)
                    self._pen.SetDashes(self.dashes)
        return self._pen


ZoneLabel = collections.namedtuple('ZoneLabel', 'name text style')


if __name__ == "__main__":
    print "This is a module, don't run it as a program"
