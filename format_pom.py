#!/usr/bin/env python

import xmlformatter.formatter
import os
import sys

if len(sys.argv) != 2:
	print("usage: %s %s" % (sys.argv[0], "path/to/pom.xml"))
	quit(1)
pom = sys.argv[1]
formatter = xmlformatter.formatter.Formatter(indent="4")
temp = open('pom.xml.tmp', 'w')
temp.write(formatter.format_file(pom))
temp.close()
os.remove(pom)
os.rename('pom.xml.tmp', pom)

