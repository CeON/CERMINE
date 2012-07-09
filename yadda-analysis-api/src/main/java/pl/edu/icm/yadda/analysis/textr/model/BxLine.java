package pl.edu.icm.yadda.analysis.textr.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Models a single line of text containing words.
 */
public class BxLine extends BxObject implements Serializable, Indexable<BxLine>, Printable {

    private static final long serialVersionUID = 917352034911588106L;

    /** list of line's words */
    private final List<BxWord> words = new ArrayList<BxWord>();
    private String lineId;
    private String nextLineId;
    private BxLine nextLine;
    private BxLine prevLine;

    @Override
    public BxLine setBounds(BxBounds bounds) {
    	super.setBounds(bounds);
    	return this;
    }

    @Override
	public String getId() {
		return this.lineId;
	}

	@Override
	public String getNextId() {
		return this.nextLineId;
	}

	@Override
	public BxLine setId(String id) {
		this.lineId = id;
		return this;
	}

	@Override
	public BxLine setNextId(String nextId) {
		this.nextLineId = nextId;
		return this;
	}

	@Override
	public BxLine getPrev() {
		return this.prevLine;
	}
	
	@Override
	public boolean hasPrev() {
		return getPrev() != null;
	}

	@Override
	public BxLine setPrev(BxLine elem) {
		this.prevLine = (BxLine) elem;
		return this;
	}

	@Override
	public BxLine getNext() {
		return this.nextLine;
	}
	
	@Override
	public boolean hasNext() {
		return getNext() != null;
	}

	@Override
	public BxLine setNext(BxLine elem) {
		this.nextLine = (BxLine) elem;
		return this;
	}   

    public List<BxWord> getWords() {
        return words;
    }

    public BxLine setWords(Collection<BxWord> words) {
        if (words == null) {
            throw new NullPointerException();
        }
        this.words.clear();
        this.words.addAll(words);
        return this;
    }

    public BxLine addWord(BxWord word) {
        if (word == null) {
            throw new NullPointerException();
        }
        this.words.add(word);
        return this;
    }

    @Override
    public String toText() {

        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (BxWord w : words) {
            if (!first) {
                sb.append(" ");
            }
            first = false;
            sb.append(w.toText());
        }
        return sb.toString();
    }
}
