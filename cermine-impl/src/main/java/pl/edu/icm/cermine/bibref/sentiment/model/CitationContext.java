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

package pl.edu.icm.cermine.bibref.sentiment.model;

/**
 * A class for storing a single reference to another document in a document's text.
 *
 * @author Dominika Tkaczyk
 */
public class CitationContext {
    
    private String key;
    
    private int startRefPosition;
    
    private int endRefPosition;
    
    private String context;

    /**
     * Returns the text context surrounding the reference in a text.
     * 
     * @return the text context
     */
    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    /**
     * Returns the index of the character following the last reference character
     * in the document's text.
     * 
     * @return the index of the character
     */
    public int getEndRefPosition() {
        return endRefPosition;
    }

    public void setEndRefPosition(int endRefPosition) {
        this.endRefPosition = endRefPosition;
    }

    /**
     * Retuns the key corresponding to the input citation.
     * 
     * @return the key
     */
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Returns the index of the first reference character in the document's text.
     * 
     * @return the index of the first reference character
     */
    public int getStartRefPosition() {
        return startRefPosition;
    }

    public void setStartRefPosition(int startRefPosition) {
        this.startRefPosition = startRefPosition;
    }
    
}
