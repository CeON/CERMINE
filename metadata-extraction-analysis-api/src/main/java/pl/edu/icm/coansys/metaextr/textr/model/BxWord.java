package pl.edu.icm.coansys.metaextr.textr.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Models a word containing text chunks.
 */
public class BxWord extends BxObject<BxWord, BxLine> implements Serializable,Printable {

    private static final long serialVersionUID = 2704689342968933369L;

    /** list of word's chunks */
    private final List<BxChunk> chunks = new ArrayList<BxChunk>();

    @Override
    public Boolean isSorted() {
    	return isSorted;
    }

    @Override
    public void setSorted(Boolean isSorted) {
    	this.isSorted = isSorted;
    	for(BxChunk chunk: chunks)
    		chunk.setSorted(isSorted);
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

    @Override
    public String toText() {
        StringBuilder sb = new StringBuilder();
        for (BxChunk ch : chunks) {
            sb.append(ch.getText());
        }
        return sb.toString();
    }
}
