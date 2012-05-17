package pl.edu.icm.yadda.analysis.textr.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Models a word containing text chunks.
 */
public class BxWord implements Serializable {

    private static final long serialVersionUID = 2704689342968933369L;

    /** word's bounding box */
    private BxBounds bounds;

    /** list of word's chunks */
    private final List<BxChunk> chunks = new ArrayList<BxChunk>();

    public BxBounds getBounds() {
        return bounds;
    }

    public BxWord setBounds(BxBounds bounds) {
        this.bounds = bounds;
        return this;
    }

    public List<BxChunk> getChunks() {
        return chunks;
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

    public String toText() {
        StringBuilder sb = new StringBuilder();
        for (BxChunk ch : chunks) {
            sb.append(ch.getText());
        }
        return sb.toString();
    }
}
