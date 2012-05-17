package pl.edu.icm.yadda.analysis.textr.model;

import java.io.Serializable;

/**
 * Immutable representation of a chunk of glyphs.
 * 
 * @author Lukasz Bolikowski (bolo@icm.edu.pl)
 * 
 */
public final class BxChunk implements Serializable {

    private static final long serialVersionUID = -6911268485662874663L;

    /* chunk's bounding box */
    private final BxBounds bounds;

    /* text stored in the chunk */
    private final String text;

    public BxChunk(BxBounds bounds, String text) {
        this.bounds = bounds;
        this.text = text;
    }

    public BxBounds getBounds() {
        return bounds;
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
