package pl.edu.icm.cermine.pubmed.pig;

import java.io.File;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Iterator;
import java.util.List;

public class PubmedCollectionIterator implements Iterable<PubmedEntry> {

    private Integer curItemIdx = -1;
    private List<PubmedEntry> entries = null;

    public PubmedCollectionIterator(String pubmedPath) {
        File pubmedDir = new File(pubmedPath);
        if (!pubmedDir.exists()) {
            throw new IllegalArgumentException("provided input file must exist");
        }
        if (!pubmedDir.isDirectory()) {
            throw new IllegalArgumentException("provided input file must be a directory");
        }
        if (pubmedDir.list().length == 0) {
            throw new IllegalArgumentException("provided directory can't be empty");
        }

        entries = new ArrayList<PubmedEntry>();
        for (Integer idx1 = 0; idx1 < 256; ++idx1) {
            for (Integer idx2 = 0; idx2 < 256; ++idx2) {
                StringBuilder sb = new StringBuilder();
                Formatter formatter = new Formatter(sb);
                formatter.format("%s/%02x/%02x", pubmedPath, idx1, idx2);
                String dirPath = sb.toString();
                File dir = new File(dirPath);
                if (dir.exists()) {
                    for (File journalDir : dir.listFiles()) {
                        File[] journalFiles = journalDir.listFiles();
                        if (!journalDir.isDirectory()) {
                            continue;
                        }
                        if (journalFiles.length > 2) {
                            continue;
                        }
                        PubmedEntry entry = new PubmedEntry();
                        File pdfFile = null;
                        File nlmFile = null;
                        for (File journalFile : journalFiles) {
                            if (journalFile.getName().matches(".*\\.pdf")) {
                                pdfFile = journalFile;
                            } else if (journalFile.getName().matches(".*\\.nxml")) {
                                nlmFile = journalFile;
                            }
                        }
                        if (nlmFile == null && pdfFile == null) {
                            continue;
                        }
                        entry.setNlm(nlmFile);
                        entry.setPdf(pdfFile);
                        entry.setKey(journalDir.getName());
                        entries.add(entry);
                    }
                }
            }
        }
    }

    public Integer size() {
    	return entries.size();
    }
    
    @Override
    public Iterator<PubmedEntry> iterator() {
        return new Iterator<PubmedEntry>() {

            @Override
            public boolean hasNext() {
                return curItemIdx + 1 < entries.size();
            }

            @Override
            public PubmedEntry next() {
                ++curItemIdx;
                PubmedEntry actualItem = entries.get(curItemIdx);
                return actualItem;
            }

            @Override
            public void remove() {
                ++curItemIdx;
            }
        };
    }

    public static void main(String[] args) {
        PubmedCollectionIterator iter = new PubmedCollectionIterator("/home/pawel/icm/ftp.ncbi.nlm.nih.gov/pub/pmc");
        for (PubmedEntry entry : iter) {
            try {
                System.out.println(entry.getNlm().getName() + " ");
            } catch (NullPointerException e) {
                System.out.println("____");
            }
            try {
                System.out.println(entry.getPdf().getName() + " ");
            } catch (NullPointerException e) {
                System.out.println("____");
            }
            try {
                //System.out.print(entry.getKey() + " ");
            } catch (NullPointerException e) {
            }
            //System.out.println("");
        }
    }
}
