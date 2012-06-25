package pl.edu.icm.yadda.analysis.textr.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Models a single line of text containing words.
 */
public class BxLine extends BxObject implements Serializable, Indexable, Printable {

    private static final long serialVersionUID = 917352034911588106L;

    /** list of line's words */
    private final List<BxWord> words = new ArrayList<BxWord>();
    private String lineId;
    private String nextLineId;
    private BxLine nextLine;

    @Override
    public BxLine setBounds(BxBounds bounds) {
    	super.setBounds(bounds);
    	return this;
    }

	public String getId() {
		return this.lineId;
	}

	public String getNextId() {
		return this.nextLineId;
	}

	public Indexable setId(String id) {
		this.lineId = id;
		return this;
	}

	public Indexable setNextId(String nextId) {
		this.nextLineId = nextId;
		return this;
	}

	public Indexable getNext() {
		return this.nextLine;
	}
	
	public boolean hasNext() {
		return getNext() != null;
	}

	public Indexable setNext(Indexable elem) {
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
