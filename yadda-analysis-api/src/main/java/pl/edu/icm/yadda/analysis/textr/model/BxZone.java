package pl.edu.icm.yadda.analysis.textr.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Models a single zone of a page. A zone contains either lines of text
 * or a list of chunks, that haven't been grouped into lines yet.
 */
public final class BxZone extends BxObject implements Serializable, Indexable, Printable {

    private static final long serialVersionUID = -7331944901471939127L;

    /** zone's id taken from the TrueViz format */
    private String zoneId;
    /** id of the next zone taken from the TrueViz format */
    private String nextZoneId;
    /** next zone in the linked list. Stored in a logical reading order */
    private BxZone nextZone;
    /** zone's label */
    private BxZoneLabel label;
    /** list of zone's lines (if the zone is segmented) */
    private final List<BxLine> lines = new ArrayList<BxLine>();

    /** list of zone's text chunks (if the zone is not segmented) */
    private final List<BxChunk> chunks = new ArrayList<BxChunk>();
    
    public String getId() {
		return zoneId;
	}

	public Indexable setId(String zoneId) {
		this.zoneId = zoneId;
		return this;
	}

	public String getNextId() {
		return nextZoneId;
	}

	public Indexable setNextId(String nextZoneId) {
		this.nextZoneId = nextZoneId;
		return this;
	}

	public Indexable getNext() {
		return this.nextZone;
	}
	
	public Indexable setNext(Indexable nextZone) {
		this.nextZone = (BxZone)nextZone;
		return this;
	}
	
	public boolean hasNext() {
		return getNext() != null;
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
    
    @Override
    public BxZone setBounds(BxBounds bounds) {
    	super.setBounds(bounds);
    	return this;
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
