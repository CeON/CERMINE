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
 * Models a single line of text containing words.
 * 
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class BxLine extends BxObject<BxWord, BxLine, BxZone> {

    private static final long serialVersionUID = 917352034911588106L;

    /** list of line's words */
    private final List<BxWord> words = new ArrayList<BxWord>();

    @Override
    public BxLine setBounds(BxBounds bounds) {
    	super.setBounds(bounds);
    	return this;
    }

    public BxLine setWords(Collection<BxWord> words) {
        resetText();
        if (words != null) {
            this.words.clear();
            for (BxWord word : words) {
                addWord(word);
            }
        }
        return this;
    }

    public BxLine addWord(BxWord word) {
        resetText();
        if (word != null) {
            this.words.add(word);
            word.setParent(this);
        }
        return this;
    }
    
    @Override
    public String getMostPopularFontName() {
        CountMap<String> map = new CountMap<String>();
        for (BxWord word : words) {
            for (BxChunk chunk : word) {
                if (chunk.getFontName() != null) {
                    map.add(chunk.getFontName());
                }
            }
        }
        return map.getMaxCountObject();
    }
    
    @Override
    public Set<String> getFontNames() {
        Set<String> names = new HashSet<String>();
        for (BxWord word : words) {
            names.addAll(word.getFontNames());
        }
        return names;
    }

    @Override
    public String toText() {
        if (getText() == null) {
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            for (BxWord w : words) {
                if (!first) {
                    sb.append(" ");
                }
                first = false;
                sb.append(w.toText());
            }
            setText(sb.toString());
        }
        return getText();
    }

    @Override
    public Iterator<BxWord> iterator() {
        return words.listIterator();
    }

    @Override
    public int childrenCount() {
        return words.size();
    }

    @Override
    public BxWord getChild(int index) {
        if (index < 0 || index >= words.size()) {
            throw new IndexOutOfBoundsException();
        }
        return words.get(index);
    }
}
