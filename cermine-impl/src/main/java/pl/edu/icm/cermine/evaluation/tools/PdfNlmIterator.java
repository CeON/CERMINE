/**
 * This file is part of CERMINE project.
 * Copyright (c) 2011-2013 ICM-UW
 *
 * CERMINE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CERMINE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with CERMINE. If not, see <http://www.gnu.org/licenses/>.
 */

package pl.edu.icm.cermine.evaluation.tools;

import java.io.File;
import java.io.FilenameFilter;
import java.util.*;

public class PdfNlmIterator implements Iterable<PdfNlmPair> {
	private Integer curItemIdx = -1;
	private List<PdfNlmPair> entries = null;
	private final Comparator<File> alphabeticalOrder = new Comparator<File>(){
		@Override
		public int compare(File o1, File o2) {
			return o1.getName().compareTo(o2.getName());
		}
	};

	private String coreName(String name) {
		String[] parts = name.split("\\.");
		StringBuilder ret = new StringBuilder();
		for (Integer partIdx = 0; partIdx < parts.length-1; ++partIdx) {
			ret.append(parts[partIdx]);
		}
		return ret.toString();
	}

	public PdfNlmIterator(String dirPath) {
		File dir = new File(dirPath);
		if (!dir.exists()) {
			throw new IllegalArgumentException("provided input file must exist");
		}
		if (!dir.isDirectory()) {
			throw new IllegalArgumentException("provided input file must be a directory");
		}
		if (dir.list().length == 0) {
			throw new IllegalArgumentException("provided directory can't be empty");
		}
		List<File> interestingFiles = new ArrayList<File>(Arrays.asList(dir.listFiles(new FilenameFilter(){
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".pdf") || name.endsWith(".nxml");
			}
		})));
		if(interestingFiles.size() < 2) {
			throw new IllegalArgumentException("There are too few files in the given directory!");
		}
		Collections.sort(interestingFiles, alphabeticalOrder);

		entries = new ArrayList<PdfNlmPair>();
		Integer prevIdx, curIdx;
		prevIdx = 0;
		curIdx = 1;
		while(curIdx < interestingFiles.size()) {
			if(coreName(interestingFiles.get(prevIdx).getName()).equals(coreName(interestingFiles.get(curIdx).getName()))) {
				entries.add(new PdfNlmPair(interestingFiles.get(curIdx), interestingFiles.get(prevIdx)));
				curIdx += 2; prevIdx += 2;
			} else {
				curIdx += 1; prevIdx += 1;
			}
		}
	}

	public Integer size() {
		return entries.size();
	}

	@Override
	public Iterator<PdfNlmPair> iterator() {
		return new Iterator<PdfNlmPair>() {

			@Override
			public boolean hasNext() {
				return curItemIdx + 1 < entries.size();
			}

			@Override
			public PdfNlmPair next() {
				++curItemIdx;
				PdfNlmPair actualItem = entries.get(curItemIdx);
				return actualItem;
			}

			@Override
			public void remove() {
				++curItemIdx;
			}
		};
	}

}
