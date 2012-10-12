package pl.edu.icm.cermine.structure.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Models a document containing pages.
 */
public final class BxDocument implements Serializable {

    private static final long serialVersionUID = -4826783896245709986L;

    /** list of document's pages */
    private final List<BxPage> pages = new ArrayList<BxPage>();
    
    private String filename = null;

    private int curPageNumber = 0;

    public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public List<BxPage> getPages() {
        return pages;
    }

    public BxDocument setPages(Collection<BxPage> pages) {
        if (pages != null) {
            this.pages.clear();
            this.pages.addAll(pages);
        }
        return this;
    }

    public BxDocument addPage(BxPage page) {
        if (page != null) {
            page.setId(Integer.toString(this.curPageNumber++));
            this.pages.add(page);
        }
        return this;
    }

    public String toText() {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Printable w : pages) {
            if (!first) {
                sb.append("\n");
            }
            first = false;
            sb.append(w.toText());
        }
        return sb.toString();
    }
    
    /**
     * 
     * @return a list of constituent pages. The list keeps the order used in the original file
     */
    public List<BxPage> asPages() {
    	return this.getPages();
    }
    
    /**
     * 
     * @return a list of constituent zones. The list holds the logical reading-order if 
     * a ReadingOrderResolver was run on the original document.
     */
    public List<BxZone> asZones() {
    	List<BxZone> ret = new ArrayList<BxZone>();
    	for(BxPage page: asPages()) {
    		ret.addAll(page.getZones());
    	}
    	return ret; 
    }
    
    /**
     * 
     * @return a list of constituent text lines. The list holds the logical reading-order if 
     * a ReadingOrderResolver was run on the original document.
     */
    public List<BxLine> asLines() {
    	List<BxLine> ret = new ArrayList<BxLine>();
    	for(BxZone zone: asZones()) {
    		ret.addAll(zone.getLines());
    	}
    	return ret;
    }
    
    /**
     * 
     * @return a list of constituent words. The list holds the logical reading-order if 
     * a ReadingOrderResolver was run on the original document.
     */
    public List<BxWord> asWords() {
    	List<BxWord> ret = new ArrayList<BxWord>();
    	for(BxLine line: asLines()) {
    		ret.addAll(line.getWords());
    	}
    	return ret;
    }
    
    /**
     * 
     * @return a list of constituent letters. The list holds the logical reading-order if 
     * a ReadingOrderResolver was run on the original document.
     */
    public List<BxChunk> asChunks() {
    	List<BxChunk> ret = new ArrayList<BxChunk>();
    	for(BxWord word: asWords()) {
    		ret.addAll(word.getChunks());
    	}
    	return ret;
    }
}
