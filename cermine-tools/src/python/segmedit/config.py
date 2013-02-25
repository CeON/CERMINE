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


import os
from configtypes import ShapeStyle, ZoneLabel

MAIN_WINDOW_SIZE = (1024, 700)

WINDOW_TITLE = "SegmEdit"

# fonts sizes
BUTTON_FONT_SIZE = 10
LABEL_FONT_SIZE = 9
LITTLE_BUTTON_FONT_SIZE = 7

# buttons sizes
BUTTON_HEIGHT = 23
LITTLE_BUTTON_HEIGHT = 21

# zone labels
OLD_ZONE_LABELS = ['Title', 'Author', 'Affiliation', 'Abstract',
               'Body', 'Header', 'Footer', 'Unknown']
ZONE_LABELS = [
    ZoneLabel('gen_body', 'gen_body', ShapeStyle((180, 180, 255, 90))),
    ZoneLabel('gen_metadata', 'gen_metadata', ShapeStyle((30, 100, 30, 127))),
    ZoneLabel('gen_other', 'gen_other', ShapeStyle((245, 245, 245, 0))),
    ZoneLabel('gen_references', 'gen_references', ShapeStyle((150, 230, 230, 127))),
###
    ZoneLabel('abstract', 'Abstract', ShapeStyle((230, 230, 150, 127))),
    ZoneLabel('affiliation', 'Affiliation', ShapeStyle((230, 150, 230, 127))),
    ZoneLabel('author', 'Author', ShapeStyle((255, 180, 180, 90))),
    ZoneLabel('bib_info', 'Bibliographic info', ShapeStyle((30, 100, 30, 127))),
    ZoneLabel('body', 'Body', ShapeStyle((180, 180, 255, 90))),
    ZoneLabel('copyright', 'Copyright/License', ShapeStyle((103, 255, 52, 127))),
    ZoneLabel('correspondence', 'Correspondence', ShapeStyle((30, 30, 100, 127))),
    ZoneLabel('dates', 'Dates', ShapeStyle((100, 90, 0, 127))),
    ZoneLabel('editor', 'Editor', ShapeStyle((90, 30, 90, 127))),
    ZoneLabel('equation', 'Equation', ShapeStyle((140, 170, 0, 127))),
    ZoneLabel('equation_label', 'Equation label', ShapeStyle((90, 110, 0, 127))),
    ZoneLabel('figure', 'Figure', ShapeStyle((180, 130, 0, 127))),
    ZoneLabel('figure_caption', 'Figure caption', ShapeStyle((120, 80, 0, 127))),
    #ZoneLabel('footer', 'Footer', ShapeStyle((100, 100, 100, 127))),
    ZoneLabel('header', 'Header', ShapeStyle((180, 180, 180, 127))),
    ZoneLabel('keywords', 'Keywords', ShapeStyle((30, 90, 90, 127))),
    ZoneLabel('page_number', 'Page number', ShapeStyle((100, 100, 100, 127))),
    ZoneLabel('references', 'References', ShapeStyle((150, 230, 230, 127))),
    ZoneLabel('table', 'Table', ShapeStyle((140, 10, 170, 127))),
    ZoneLabel('table_caption', 'Table caption', ShapeStyle((80, 5, 100, 127))),
    ZoneLabel('title', 'Title', ShapeStyle((180, 255, 180, 90))),
    ZoneLabel('type', 'Type', ShapeStyle((100, 30, 30, 127))),
    ZoneLabel('unknown', 'Unknown', ShapeStyle((245, 245, 245, 0))),
]

DEFAULT_ZONE_LABEL = ZONE_LABELS[-1]

#SCROLL_STEP = 20
SCROLL_STEP = 30

# directories and files
VAR_DIRECTORY = os.environ['HOME'] + os.path.sep + "segmedit_cache"
INDEX_FILE = VAR_DIRECTORY + os.path.sep + "index.sqlite"

CONVERT_DPI = 300
CONVERT = "/usr/bin/convert"
CONVERT_OPTIONS = "-density " + str(CONVERT_DPI)

XML_PAGE_DELTA_X = 0
XML_UNIT_RATIO = CONVERT_DPI / 72.0

#XML_PAGE_HEIGHT = 3299
#XML_PAGE_DELTA_X = 0
#XML_UNIT_RATIO = CONVERT_DPI / 300.0

UNDO_HISTORY_SIZE = None #unlimited

MAX_ZOOM = 200
DEFAULT_ZOOM = 52

HIGHER_LAYER_STYLE = ShapeStyle((64, 64, 64, 32), strokeWidth=0)
CURRENT_LAYER_STYLE = ShapeStyle((0, 255, 0, 64), (255, 0, 0), 1)
LOWER_LAYER_STYLE = ShapeStyle((32, 0, 255, 32), (0, 0, 64, 32), 1)

SELECTION_STYLE = ShapeStyle(None, strokeWidth=1, dashes=[1, 2])

NETWORK_ADDRESS = 'ciemniak.icm.edu.pl:7171'

if __name__ == "__main__":
    print "This is a module, don't run this as a program"
