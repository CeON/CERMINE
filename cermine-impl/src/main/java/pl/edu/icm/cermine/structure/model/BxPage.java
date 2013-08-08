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
 * Models a single page of a document. A page is either segmented (divided into zones)
 * or not segmented (containing a list of chunks that haven't been grouped into zones yet).
 */
public final class BxPage extends BxObject<BxPage, BxDocument> implements Serializable, Printable {

    private static final long serialVersionUID = 8981043716257046347L;
    
    /** list of page's zones (if the page is segmented) */
    private final List<BxZone> zones = new ArrayList<BxZone>();

    /** list of page's text chunks (if the page is not segmented) */
    private final List<BxChunk> chunks = new ArrayList<BxChunk>();

    public List<BxZone> getZones() {
        return zones;
    }

    public Printable setZones(Collection<BxZone> zones) {
        if (zones != null) {
            this.zones.clear();
            this.zones.addAll(zones);
        }
        return this;
    }

    public Printable addZone(BxZone zone) {
        if (zone != null) {
            this.zones.add(zone);
        }
        return this;
    }

    public List<BxChunk> getChunks() {
        return chunks;
    }

    public BxPage setChunks(Collection<BxChunk> chunks) {
        if (chunks != null) {
            this.chunks.clear();
            this.chunks.addAll(chunks);
        }
        return this;
    }

    public BxPage addChunk(BxChunk chunk) {
        if (chunk != null) {
            this.chunks.add(chunk);
        }
        return this;
    }

    @Override
	public String toText() {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (BxZone w : zones) {
            if (!first) {
                sb.append("\n");
            }
            first = false;
            sb.append(w.toText());
        }
        return sb.toString();
    }
}
