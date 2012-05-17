package pl.edu.icm.yadda.analysis.textr.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Models a single line of text containing words.
 */
public class BxLine implements Serializable {

    private static final long serialVersionUID = 917352034911588106L;

    /** line's bounding box */
    private BxBounds bounds;

    /** list of line's words */
    private final List<BxWord> words = new ArrayList<BxWord>();

    public BxBounds getBounds() {
        return bounds;
    }

    public BxLine setBounds(BxBounds bounds) {
        this.bounds = bounds;
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
