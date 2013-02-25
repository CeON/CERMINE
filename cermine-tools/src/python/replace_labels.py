#!/usr/bin/env python
import os
import sys

path = sys.argv[1]
pairs = [#("abbreviations", "glossary"),
#('conflict', 'conflict_statement'),
('body', 'body_content'),]
#('body_content_content', 'body_content'),
#('header', 'heading'),
#('HEADER', 'heading'),
#('access', 'access_data')]


for root, dirs, files in os.walk(path):
	for filename in files:
		path = os.path.join(root, filename)
		print path
		with open(path,'r') as f:
		    newlines = []
		    for line in f.readlines():
			for pair in pairs:
		            line = line.replace(pair[0], pair[1])
		        newlines.append(line)
		with open(path, 'w') as f:
		    for line in newlines:
			f.write(line)


