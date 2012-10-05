package pl.edu.icm.coansys.metaextr.structure.model;

import java.io.Serializable;

/**
 * Immutable representation of a chunk of glyphs.
 * 
 * @author Lukasz Bolikowski (bolo@icm.edu.pl)
 * 
 */
public final class BxChunk extends BxObject<BxChunk, BxWord> implements Serializable {

    private static final long serialVersionUID = -6911268485662874663L;

    /* text stored in the chunk */
    private final String text;

    public BxChunk(BxBounds bounds, String text) {
        this.bounds = bounds;
        this.text = text;
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
