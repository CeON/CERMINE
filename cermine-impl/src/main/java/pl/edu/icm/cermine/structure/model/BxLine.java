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
 * Models a single line of text containing words.
 */
public class BxLine extends BxObject<BxLine, BxZone> implements Serializable, Printable {

    private static final long serialVersionUID = 917352034911588106L;

    /** list of line's words */
    private final List<BxWord> words = new ArrayList<BxWord>();

    @Override
    public BxLine setBounds(BxBounds bounds) {
    	super.setBounds(bounds);
    	return this;
    }

    public List<BxWord> getWords() {
        return words;
    }

    public BxLine setWords(Collection<BxWord> words) {
        if (words != null) {
            this.words.clear();
            this.words.addAll(words);
        }
        return this;
    }

    public BxLine addWord(BxWord word) {
        if (word != null) {
            this.words.add(word);
        }
        return this;
    }

    @Override
    public String toText() {

        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (BxWord w : words) {
            if (!first) {
                sb.append(" ");
            }
            first = false;
            sb.append(w.toText());
        }
        return sb.toString();
    }
}
