package pl.edu.icm.yadda.analysis.textr.model;

import java.io.Serializable;

/**
 * Immutable representation of a chunk of glyphs.
 * 
 * @author Lukasz Bolikowski (bolo@icm.edu.pl)
 * 
 */
public final class BxChunk extends BxObject implements Serializable, Indexable<BxChunk>{

    private static final long serialVersionUID = -6911268485662874663L;

    /* text stored in the chunk */
    private final String text;
    private String chunkId;
    private String nextChunkId;
    private BxChunk nextChunk;
    private BxChunk prevChunk;
    
    public BxChunk(BxBounds bounds, String text) {
        this.bounds = bounds;
        this.text = text;
    }
    
    @Override
	public String getId() {
		return this.chunkId;
	}

    @Override
	public String getNextId() {
		return this.nextChunkId;
	}

    @Override
	public BxChunk setId(String id) {
		this.chunkId = id;
		return this;
	}

    @Override
	public BxChunk setNextId(String nextId) {
		this.nextChunkId = nextId;
		return this;
	}

	public BxChunk getPrev() {
		return this.prevChunk;
	}

    @Override
	public BxChunk setPrev(BxChunk elem) {
    //	assert elem instanceof BxChunk;
		this.prevChunk = (BxChunk) elem;
		return this;
	}   
	
    @Override
	public boolean hasPrev() {
		return getPrev() != null;
	}

    @Override
	public BxChunk getNext() {
		return this.nextChunk;
	}

    @Override
	public BxChunk setNext(BxChunk elem) {
    //	assert elem instanceof BxChunk;
		this.nextChunk = (BxChunk) elem;
		return this;
	}   
	
    @Override
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
