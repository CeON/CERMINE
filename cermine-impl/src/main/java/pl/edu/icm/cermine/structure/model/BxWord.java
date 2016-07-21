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
 * Models a word containing text chunks.
 * 
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class BxWord extends BxObject<BxChunk, BxWord, BxLine> {

    private static final long serialVersionUID = 2704689342968933369L;

    /** list of word's chunks */
    private final List<BxChunk> chunks = new ArrayList<BxChunk>();


    public BxWord setChunks(Collection<BxChunk> chunks) {
        resetText();
        if (chunks != null) {
            this.chunks.clear();
            for (BxChunk chunk : chunks) {
                addChunk(chunk);
            }
        }
        return this;
    }

    public BxWord addChunk(BxChunk chunk) {
        resetText();
        if (chunks != null) {
            this.chunks.add(chunk);
            chunk.setParent(this);
        }
        return this;
    }

    @Override
    public String getMostPopularFontName() {
        CountMap<String> map = new CountMap<String>();
        for (BxChunk chunk : chunks) {
            if (chunk.getFontName() != null) {
                map.add(chunk.getFontName());
            }
        }
        return map.getMaxCountObject();
    }
    
    @Override
    public Set<String> getFontNames() {
        Set<String> names = new HashSet<String>();
        for (BxChunk chunk : chunks) {
            if (chunk.getFontName() != null) {
                names.add(chunk.getFontName());
            }
        }
        return names;
    }
    
    @Override
    public String toText() {
        if (getText() == null) {
            StringBuilder sb = new StringBuilder();
            for (BxChunk ch : chunks) {
                sb.append(ch.getText());
            }
            setText(sb.toString());
        }
        return getText();
    }

    @Override
    public Iterator<BxChunk> iterator() {
        return chunks.listIterator();
    }

    @Override
    public int childrenCount() {
        return chunks.size();
    }

    @Override
    public BxChunk getChild(int index) {
        if (index < 0 || index >= chunks.size()) {
            throw new IndexOutOfBoundsException();
        }
        return chunks.get(index);
    }
}
