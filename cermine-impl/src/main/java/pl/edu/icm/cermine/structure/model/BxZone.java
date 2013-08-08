/**
 * This file is part of CERMINE project.
 * Copyright (c) 2011-2013 ICM-UW
 *
 * CERMINE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CERMINE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with CERMINE. If not, see <http://www.gnu.org/licenses/>.
 */

package pl.edu.icm.cermine.structure.model;

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
        if (lines != null) {
            this.lines.clear();
            this.lines.addAll(lines);
        }
        return this;
    }

    public BxZone addLine(BxLine line) {
        if (line != null) {
            this.lines.add(line);
        }
        return this;
    }

    public List<BxChunk> getChunks() {
        return chunks;
    }

    public BxZone setChunks(Collection<BxChunk> chunks) {
        if (chunks != null) {
            this.chunks.clear();
            this.chunks.addAll(chunks);
        }
        return this;
    }

    public BxZone addChunk(BxChunk chunk) {
        if (chunk != null) {
            this.chunks.add(chunk);
        }
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
