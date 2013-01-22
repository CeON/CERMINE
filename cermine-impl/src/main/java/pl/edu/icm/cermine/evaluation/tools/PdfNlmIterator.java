package pl.edu.icm.cermine.evaluation.tools;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Formatter;
import java.util.Iterator;
import java.util.List;

public class PdfNlmIterator implements Iterable<PdfNlmPair> {
	private Integer curItemIdx = -1;
	private List<PdfNlmPair> entries = null;
	private final Comparator<File> ALPHABETICAL_ORDER = new Comparator<File>(){
		@Override
		public int compare(File o1, File o2) {
			return o1.getName().compareTo(o2.getName());
		}
	};

	private String coreName(String name) {
		String[] parts = name.split("\\.");
		String ret = "";
		for(Integer partIdx = 0; partIdx < parts.length-1; ++partIdx) {
			ret += parts[partIdx];
		}
		return ret;
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
				if(name.endsWith(".pdf") || name.endsWith(".nxml"))
					return true;
				else
					return false;
			}
		})));
		if(interestingFiles.size() < 2) {
			throw new IllegalArgumentException("There are too few files in the given directory!");
		}
		Collections.sort(interestingFiles, ALPHABETICAL_ORDER);

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

	public static void main(String[] args) {
		PdfNlmIterator iter = new PdfNlmIterator("/home/pawel/git/10");
		Integer counter = 0;
		for (PdfNlmPair entry : iter) {
			System.out.println(++counter);
			System.out.println(entry.getNlm().getName() + " ");
			System.out.println(entry.getPdf().getName() + " ");
		}
	}
}
