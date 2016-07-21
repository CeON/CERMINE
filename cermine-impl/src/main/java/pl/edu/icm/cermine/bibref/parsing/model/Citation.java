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

package pl.edu.icm.cermine.bibref.parsing.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a citation as a sequence of citation tokens.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class Citation {

    private String text;
    private List<CitationToken> tokens;

    public Citation(String text, List<CitationToken> tokens) {
        this.text = text;
        this.tokens = tokens;
    }

    public Citation(String text) {
        this(text, new ArrayList<CitationToken>());
    }

    public Citation() {
        this("");
    }

    public String getText() {
        return text;
    }

    public List<CitationToken> getTokens() {
        return tokens;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void addToken(CitationToken token) {
        tokens.add(token);
    }

    public void appendText(String text) {
        this.text += text;
    }

}
