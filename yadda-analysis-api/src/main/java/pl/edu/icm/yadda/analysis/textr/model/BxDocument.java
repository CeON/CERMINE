package pl.edu.icm.yadda.analysis.textr.model;

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

    private int curPageNumber = 0;
    
    public List<BxPage> getPages() {
        return pages;
    }

    public BxDocument setPages(Collection<BxPage> pages) {
        if (pages == null) {
            throw new NullPointerException();
        }
        this.pages.clear();
   /*     for(BxPage page: pages) {
        	page.setId(this.curPageNumber++);
        	page.setNextId(this.curPageNumber);
        	this.pages.add(page);
        }
        this.pages.get(this.pages.size()-1).setId(null); */
        this.pages.addAll(pages);
        return this;
    }

    public BxDocument addPage(BxPage page) {
        if (page == null) {
            throw new NullPointerException();
        }
        page.setId(Integer.toString(this.curPageNumber++));
        this.pages.add(page);
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
    
    public List<BxPage> asPages() {
    	return this.getPages();
    }
    
    public List<BxZone> asZones() {
    	List<BxZone> ret = new ArrayList<BxZone>();
    	for(BxPage page: asPages()) {
    		ret.addAll(page.getZones());
    	}
    	return ret; 
    }
    
    public List<BxLine> asLines() {
    	List<BxLine> ret = new ArrayList<BxLine>();
    	for(BxZone zone: asZones()) {
    		ret.addAll(zone.getLines());
    	}
    	return ret;
    }
    
    public List<BxWord> asWords() {
    	List<BxWord> ret = new ArrayList<BxWord>();
    	for(BxLine line: asLines()) {
    		ret.addAll(line.getWords());
    	}
    	return ret;
    }
    
    public List<BxChunk> asChunks() {
    	List<BxChunk> ret = new ArrayList<BxChunk>();
    	for(BxWord word: asWords()) {
    		ret.addAll(word.getChunks());
    	}
    	return ret;
    }
}
