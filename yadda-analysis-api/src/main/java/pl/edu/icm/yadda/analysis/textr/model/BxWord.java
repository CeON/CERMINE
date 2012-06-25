package pl.edu.icm.yadda.analysis.textr.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Models a word containing text chunks.
 */
public class BxWord extends BxObject implements Serializable, Indexable, Printable {

    private static final long serialVersionUID = 2704689342968933369L;

    /** list of word's chunks */
    private final List<BxChunk> chunks = new ArrayList<BxChunk>();
    private String wordId;
    private String nextWordId;
    private BxWord nextWord;

    @Override
    public BxWord setBounds(BxBounds bounds) {
    	super.setBounds(bounds);
    	return this;
    }

	public List<BxChunk> getChunks() {
		return chunks;
	}

	public String getId() {
		return this.wordId;
	}

	public String getNextId() {
		return this.nextWordId;
	}

	public Indexable setId(String id) {
		this.wordId = id;
		return this;
	}

	public Indexable setNextId(String nextId) {
		this.nextWordId = nextId;
		return this;
	}

	public Indexable getNext() {
		return this.nextWord;
	}

	public Indexable setNext(Indexable elem) {
		this.nextWord = (BxWord) elem;
		return this;
	}
	
	public boolean hasNext() {
		return getNext() != null;
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
