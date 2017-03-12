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
package pl.edu.icm.cermine.bibref.model;

/**
 * Bibliographic reference types.
 * 
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public enum BibEntryType {

    ARTICLE         ("article"),
    BOOK            ("book"),
    BOOKLET         ("booklet"),
    INBOOK          ("inbook"),
    INCOLLECTION    ("incollection"),
    INPROCEEDINGS   ("inproceedings"),
    MANUAL          ("manual"),
    MASTERSTHESIS   ("mastersthesis"),
    MISC            ("misc"),
    PHDTHESIS       ("phdthesis"),
    PROCEEDINGS     ("proceedings"),
    TECHREPORT      ("techreport"),
    UNPUBLISHED     ("unpublished");
    
    private final String type;

    private BibEntryType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
    
    public static BibEntryType ofType(String type) {
        for (BibEntryType t : BibEntryType.values()) {
            if (t.getType().equals(type)) {
                return t;
            }
        }
        return null;
    }
    
}
