package pl.edu.icm.coansys.metaextr.structure.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Models a single zone of a page. A zone contains either lines of text
 * or a list of chunks, that haven't been grouped into lines yet.
 */
public final class BxZone extends BxObject<BxZone, BxPage> implements Serializable, Printable {

    private static final long serialVersionUID = -7331944901471939127L;

    /** zone's label */
    private BxZoneLabel label;
    /** list of zone's lines (if the zone is segmented) */
    private final List<BxLine> lines = new ArrayList<BxLine>();

    /** list of zone's text chunks (if the zone is not segmented) */
    private final List<BxChunk> chunks = new ArrayList<BxChunk>();
    
    @Override
    public Boolean isSorted() {
    	if(!isSorted)
    		return false;
    	for(BxLine line: lines)
    		if(!line.isSorted())
    			return false;
    	return true;
    }

    @Override
    public void setSorted(Boolean isSorted) {
    	this.isSorted = isSorted;
    	for(BxLine line: lines)
    		line.setSorted(isSorted);
    }

	public BxZoneLabel getLabel() {
        return label;
    }

    public BxZone setLabel(BxZoneLabel label) {
        this.label = label;
        return this;
    }

    public List<BxLine> getLines() {
        return lines;
    }
    
    public BxZone setLines(Collection<BxLine> lines) {
        if (lines == null) {
            throw new NullPointerException();
        }
        this.lines.clear();
        this.lines.addAll(lines);
        return this;
    }

    public BxZone addLine(BxLine line) {
        if (line == null) {
            throw new NullPointerException();
        }
        this.lines.add(line);
        return this;
    }

    public List<BxChunk> getChunks() {
        return chunks;
    }

    public BxZone setChunks(Collection<BxChunk> chunks) {
        if (chunks == null) {
            throw new NullPointerException();
        }
        this.chunks.clear();
        this.chunks.addAll(chunks);
        return this;
    }

    public BxZone addChunk(BxChunk chunk) {
        if (chunk == null) {
            throw new NullPointerException();
        }
        this.chunks.add(chunk);
        return this;
    }

    @Override
    public String toText() {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (BxLine w : lines) {
            if (!first) {
                sb.append("\n");
            }
            first = false;
            sb.append(w.toText());
        }
        return sb.toString();
    }
}
