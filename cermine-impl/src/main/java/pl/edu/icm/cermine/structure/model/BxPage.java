/**
 * This file is part of CERMINE project.
 * Copyright (c) 2011-2016 ICM-UW
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

import java.util.*;
import pl.edu.icm.cermine.tools.CountMap;

/**
 * Models a single page of a document. A page is either segmented (divided into zones)
 * or not segmented (containing a list of chunks that haven't been grouped into zones yet).
 * 
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public final class BxPage extends BxObject<BxZone, BxPage, BxDocument> {

    private static final long serialVersionUID = 8981043716257046347L;
    
    /** list of page's zones (if the page is segmented) */
    private final List<BxZone> zones = new ArrayList<BxZone>();

    /** list of page's text chunks (if the page is not segmented) */
    private final List<BxChunk> chunks = new ArrayList<BxChunk>();


    public BxPage setZones(Collection<BxZone> zones) {
        resetText();
        if (zones != null) {
            this.zones.clear();
            for (BxZone zone : zones) {
                addZone(zone);
            }
        }
        return this;
    }

    public BxPage addZone(BxZone zone) {
        resetText();
        if (zone != null) {
            this.zones.add(zone);
            zone.setParent(this);
        }
        return this;
    }

    public Iterator<BxChunk> getChunks() {
        return chunks.listIterator();
    }

    public BxPage setChunks(Collection<BxChunk> chunks) {
        resetText();
        if (chunks != null) {
            this.chunks.clear();
            this.chunks.addAll(chunks);
        }
        return this;
    }

    public BxPage addChunk(BxChunk chunk) {
        resetText();
        if (chunk != null) {
            this.chunks.add(chunk);
        }
        return this;
    }

    @Override
    public String getMostPopularFontName() {
        CountMap<String> map = new CountMap<String>();
        for (BxZone zone : zones) {
            for (BxLine line : zone) {
                for (BxWord word : line) {
                    for (BxChunk chunk : word) {
                        if (chunk.getFontName() != null) {
                            map.add(chunk.getFontName());
                        }
                    }
                }
            }
        }
        return map.getMaxCountObject();
    }
    
    @Override
    public Set<String> getFontNames() {
        Set<String> names = new HashSet<String>();
        for (BxZone zone : zones) {
            names.addAll(zone.getFontNames());
        }
        return names;
    }
    
    @Override
	public String toText() {
        if (getText() == null) {
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            for (BxZone w : zones) {
                if (!first) {
                    sb.append("\n");
                }
                first = false;
                sb.append(w.toText());
            }
            setText(sb.toString());
        }
        return getText();
    }

    @Override
    public Iterator<BxZone> iterator() {
        return zones.listIterator();
    }

    @Override
    public int childrenCount() {
        return zones.size();
    }

    @Override
    public BxZone getChild(int index) {
        if (index < 0 || index >= zones.size()) {
            throw new IndexOutOfBoundsException();
        }
        return zones.get(index);
    }
    
}
