package pl.edu.icm.cermine.pubmed.importer;

import java.util.Formatter;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.hadoop.hbase.util.FSUtils.DirFilter;

public class PubmedCollectionIterator implements Iterable<PubmedEntry>{
	
	private Integer curItem = 0;
	private List<PubmedEntry> entries = null;
	
	public PubmedCollectionIterator(String pubmedPath) {
		File pubmedDir = new File(pubmedPath);
		if(!pubmedDir.exists()) {
			throw new IllegalArgumentException("provided input file must exist");
		}
		if(!pubmedDir.isDirectory()) {
			throw new IllegalArgumentException("provided input file must be a directory");
		}
		if(pubmedDir.list().length == 0) {
			throw new IllegalArgumentException("provided directory can't be empty");
		}

		entries = new ArrayList<PubmedEntry>();
		for(Integer idx1=0 ; idx1 < 256; ++idx1) {
			for(Integer idx2=0 ; idx2 < 256 ; ++idx2) {
				StringBuilder sb = new StringBuilder();
				Formatter formatter = new Formatter(sb);
				formatter.format("%s/%02x/%02x", pubmedPath, idx1, idx2);
				String dirPath = sb.toString();
				File dir = new File(dirPath);
				if(dir.exists()) {
					for(File journalDir: dir.listFiles()) {
						File[] journalFiles = journalDir.listFiles();
						if(!journalDir.isDirectory()) {
							continue;
						}
						if(journalFiles.length > 2) {
							continue;
						}
						PubmedEntry entry = new PubmedEntry();
						for(File journalFile: journalFiles) {
							if(journalFile.getName().matches(".*\\.pdf")) {
								entry.setPdf(journalFile);
							}
							if(journalFile.getName().matches(".*\\.nxml")) {
								entry.setNlm(journalFile);
							}
							entry.setKey(journalDir.getName());
						}
						entries.add(entry);
					}
				}
			}
		}
	}
	
	@Override
	public Iterator<PubmedEntry> iterator() {
		return new Iterator<PubmedEntry>() {
			@Override
			public boolean hasNext() {
				return curItem + 1 < entries.size();
			}
			
			@Override
			public PubmedEntry next() {
				PubmedEntry actualItem = entries.get(curItem);
				++curItem;
				return actualItem;
			}
			
			@Override
			public void remove() {
				++curItem;
			}
		};
	}
	
	public static void main(String[] args) {
		PubmedCollectionIterator iter = new PubmedCollectionIterator("/home/pawel/icm/ftp.ncbi.nlm.nih.gov/pub/pmc");
		for(PubmedEntry entry: iter) {
			try {
				System.out.println(entry.getNlm().getName() + " ");
			} catch(NullPointerException e) {}
			try {
				System.out.println(entry.getPdf().getName() + " ");
			} catch(NullPointerException e) {}
			try {
				//System.out.print(entry.getKey() + " ");
			} catch(NullPointerException e) {}
			//System.out.println("");
		}
	}
}
