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
 * Models a word containing text chunks.
 */
public class BxWord extends BxObject<BxWord, BxLine> implements Serializable,Printable {

    private static final long serialVersionUID = 2704689342968933369L;

    /** list of word's chunks */
    private final List<BxChunk> chunks = new ArrayList<BxChunk>();

	public List<BxChunk> getChunks() {
		return chunks;
	}

    public BxWord setChunks(Collection<BxChunk> chunks) {
        if (chunks != null) {
            this.chunks.clear();
            for (BxChunk chunk : chunks) {
                addChunk(chunk);
            }
        }
        return this;
    }

    public BxWord addChunk(BxChunk chunk) {
        if (chunks != null) {
            this.chunks.add(chunk);
            chunk.setParent(this);
        }
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
