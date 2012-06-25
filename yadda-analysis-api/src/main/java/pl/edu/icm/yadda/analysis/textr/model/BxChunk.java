package pl.edu.icm.yadda.analysis.textr.model;

import java.io.Serializable;

/**
 * Immutable representation of a chunk of glyphs.
 * 
 * @author Lukasz Bolikowski (bolo@icm.edu.pl)
 * 
 */
public final class BxChunk extends BxObject implements Serializable, Indexable {

    private static final long serialVersionUID = -6911268485662874663L;

    /* text stored in the chunk */
    private final String text;
    private String chunkId;
    private String nextChunkId;
    private BxChunk nextChunk;
    
    public BxChunk(BxBounds bounds, String text) {
        this.bounds = bounds;
        this.text = text;
    }

	public String getId() {
		return this.chunkId;
	}

	public String getNextId() {
		return this.nextChunkId;
	}

	public Indexable setId(String id) {
		this.chunkId = id;
		return this;
	}

	public Indexable setNextId(String nextId) {
		this.nextChunkId = nextId;
		return this;
	}

	public Indexable getNext() {
		return this.nextChunk;
	}

	public Indexable setNext(Indexable elem) {
		this.nextChunk = (BxChunk) elem;
		return this;
	}   
	
	public boolean hasNext() {
		return getNext() != null;
	}

    @Override
    public BxChunk setBounds(BxBounds bounds) {
    	super.setBounds(bounds);
    	return this;
    }
    
    public BxChunk withBounds(BxBounds bounds) {
        return new BxChunk(bounds, text);
    }

    public String getText() {
        return text;
    }

    public BxChunk withText(String text) {
        return new BxChunk(bounds, text);
    }

    public String toText() {
        return text;
    }
}
