package pl.edu.icm.yadda.analysis.textr.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Models a single page of a document. A page is either segmented (divided into zones)
 * or not segmented (containing a list of chunks that haven't been grouped into zones yet).
 */
public final class BxPage extends BxObject implements Serializable, Indexable, Printable {

    private static final long serialVersionUID = 8981043716257046347L;

    /** page number in the document */
    private String pageId;

    /** number of the next page from the sequence */
    private String nextPageId;

    /** next page in the linked list of pages. Stored in the logical reading order */
    private BxPage nextPage;
    
    /** list of page's zones (if the page is segmented) */
    private final List<BxZone> zones = new ArrayList<BxZone>();

    /** list of page's text chunks (if the page is not segmented) */
    private final List<BxChunk> chunks = new ArrayList<BxChunk>();

    @Override
    public BxPage setBounds(BxBounds bounds) {
    	super.setBounds(bounds);
    	return this;
    }
    
    public Indexable setId(String pageId) {
    	this.pageId = pageId;
    	return this;
    }
    
    public String getId() {
    	return this.pageId;
    }
    
    public Indexable setNextId(String nextPageId) {
    	this.nextPageId = nextPageId;
    	return this;
    }
    
    public String getNextId() {
    	return this.nextPageId;
    }

    public Indexable setNext(Indexable nextPage) {
    	this.nextPage = (BxPage)nextPage;
    	return this;
    }
    
    public Indexable getNext() {
    	return this.nextPage;
    }
	
	public boolean hasNext() {
		return getNext() != null;
	}

    public List<BxZone> getZones() {
        return zones;
    }

    public Printable setZones(Collection<BxZone> zones) {
        if (zones == null) {
            throw new NullPointerException();
        }
        this.zones.clear();
        this.zones.addAll(zones);
        return this;
    }

    public Printable addZone(BxZone zone) {
        if (zone == null) {
            throw new NullPointerException();
        }
        this.zones.add(zone);
        return this;
    }

    public List<BxChunk> getChunks() {
        return chunks;
    }

    public BxPage setChunks(Collection<BxChunk> chunks) {
        if (chunks == null) {
            throw new NullPointerException();
        }
        this.chunks.clear();
        this.chunks.addAll(chunks);
        return this;
    }

    public BxPage addChunk(BxChunk chunk) {
        if (chunk == null) {
            throw new NullPointerException();
        }
        this.chunks.add(chunk);
        return this;
    }

    /* (non-Javadoc)
	 * @see pl.edu.icm.yadda.analysis.textr.model.Printable#toText()
	 */
    @Override
	public String toText() {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (BxZone w : zones) {
            if (!first) {
                sb.append("\n");
            }
            first = false;
            sb.append(w.toText());
        }
        return sb.toString();
    }
}
