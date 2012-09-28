package pl.edu.icm.coansys.metaextr.structure.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Models a single line of text containing words.
 */
public class BxLine extends BxObject<BxLine, BxZone> implements Serializable, Printable {

    private static final long serialVersionUID = 917352034911588106L;

    /** list of line's words */
    private final List<BxWord> words = new ArrayList<BxWord>();

    @Override
    public Boolean isSorted() {
    	if(!isSorted)
    		return false;
    	for(BxWord word: words)
    		if(!word.isSorted())
    			return false;
    	return true;
    }

    @Override
    public void setSorted(Boolean isSorted) {
    	this.isSorted = isSorted;
    	for(BxWord word:words)
    		word.setSorted(isSorted);
    }

    @Override
    public BxLine setBounds(BxBounds bounds) {
    	super.setBounds(bounds);
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
