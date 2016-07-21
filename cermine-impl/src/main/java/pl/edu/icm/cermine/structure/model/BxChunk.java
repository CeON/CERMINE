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

import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Immutable representation of a chunk of glyphs.
 * 
 * @author Lukasz Bolikowski (bolo@icm.edu.pl)
 */
public final class BxChunk extends BxObject<Character, BxChunk, BxWord> {

    private static final long serialVersionUID = -6911268485662874663L;

    private String fontName;
    
    public BxChunk(BxBounds bounds, String text) {
        this.setBounds(bounds);
        this.setText(text);
    }
 
    public BxChunk withBounds(BxBounds bounds) {
        return new BxChunk(bounds, this.getText());
    }

    public BxChunk withText(String text) {
        return new BxChunk(getBounds(), text);
    }

    public String getFontName() {
        return fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    @Override
    public String toText() {
        return this.getText();
    }

    @Override
    public String getMostPopularFontName() {
        return fontName;
    }

    @Override
    public Set<String> getFontNames() {
        return Sets.newHashSet(fontName);
    }

    @Override
    public Iterator<Character> iterator() {
        List<Character> characters = new ArrayList<Character>();
        for (char ch: getText().toCharArray()) {
            characters.add(ch);
        }
        return characters.listIterator();
    }

    @Override
    public int childrenCount() {
        return toText().length();
    }

    @Override
    public Character getChild(int index) {
        if (index < 0 || index >= toText().length()) {
            throw new IndexOutOfBoundsException();
        }
        return toText().charAt(index);
    }
}
