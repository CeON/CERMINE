#!/usr/bin/env python

import os
import sys
import libxml2

path = sys.argv[1]

counter = 0
for root, dirs, files in os.walk(path):
	if not dirs:
		nlm = None
		pdf = None
		xml = None
		for file in files:
			if file.endswith(".nxml"):
				nlm = file
			if file.endswith(".pdf"):
				pdf = file
			if file.endswith(".xml"):
				xml = file
		if nlm <> None and pdf <> None:
			counter += 1
			nlm = os.path.join(root, nlm)
			pdf = os.path.join(root, pdf)
			doc = libxml2.parseFile(nlm)
			context = doc.xpathNewContext()
			res = context.xpathEval("/article/front/article-meta/article-id[@pub-id-type='pmc']")
			id = res[0].getContent()
			print counter, nlm, id
			os.rename(nlm, os.path.join(root, id+".nxml"))
			os.rename(pdf, os.path.join(root, id+".pdf"))
			if xml <> None:
				xml = os.path.join(root, xml)
				os.rename(xml, os.path.join(root, id+".xml"))
			doc.freeDoc()
			context.xpathFreeContext()
