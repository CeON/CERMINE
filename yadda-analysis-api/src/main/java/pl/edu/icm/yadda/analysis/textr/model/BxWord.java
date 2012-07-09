package pl.edu.icm.yadda.analysis.textr.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Models a word containing text chunks.
 */
public class BxWord extends BxObject implements Serializable, Indexable<BxWord>, Printable {

    private static final long serialVersionUID = 2704689342968933369L;

    /** list of word's chunks */
    private final List<BxChunk> chunks = new ArrayList<BxChunk>();
    private String wordId;
    private String nextWordId;
    private BxWord nextWord;
    private BxWord prevWord;

    @Override
    public BxWord setBounds(BxBounds bounds) {
    	super.setBounds(bounds);
    	return this;
    }

	public List<BxChunk> getChunks() {
		return chunks;
	}

	@Override
	public String getId() {
		return this.wordId;
	}

	@Override
	public String getNextId() {
		return this.nextWordId;
	}

	@Override
	public BxWord setId(String id) {
		this.wordId = id;
		return this;
	}

	@Override
	public BxWord setNextId(String nextId) {
		this.nextWordId = nextId;
		return this;
	}

	@Override
	public BxWord getNext() {
		return this.nextWord;
	}

	@Override
	public BxWord setNext(BxWord elem) {
		this.nextWord = (BxWord) elem;
		return this;
	}
	
	@Override
	public boolean hasNext() {
		return getNext() != null;
	}

	@Override
	public BxWord getPrev() {
		return this.prevWord;
	}

	@Override
	public BxWord setPrev(BxWord elem) {
		this.prevWord = (BxWord) elem;
		return this;
	}
	
	@Override
	public boolean hasPrev() {
		return getPrev() != null;
	}
	
    public BxWord setChunks(Collection<BxChunk> chunks) {
        if (chunks == null) {
            throw new NullPointerException();
        }
        this.chunks.clear();
        this.chunks.addAll(chunks);
        return this;
    }

    public BxWord addChunks(BxChunk chunks) {
        if (chunks == null) {
            throw new NullPointerException();
        }
        this.chunks.add(chunks);
        return this;
    }

    @Override
    public String toText() {
        StringBuilder sb = new StringBuilder();
        for (BxChunk ch : chunks) {
            sb.append(ch.getText());
        }
        return sb.toString();
    }
}
