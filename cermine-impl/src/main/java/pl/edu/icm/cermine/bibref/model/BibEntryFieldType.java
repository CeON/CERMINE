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
 * Bibliographic reference field types.
 * 
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public enum BibEntryFieldType {

    ABSTRACT        ("abstract"),
    ADDRESS         ("address"),
    AFFILIATION     ("affiliation"),
    ANNOTE          ("annote"),
    AUTHOR          ("author"),
    BOOKTITLE       ("booktitle"),
    CHAPTER         ("chapter"),
    CONTENTS        ("contents"),
    COPYRIGHT       ("copyright"),
    CROSSREF        ("crossref"),
    DOI             ("doi"),
    EDITION         ("edition"),
    EDITOR          ("editor"),
    HOWPUBLISHED    ("howpublished"),
    INSTITUTION     ("institution"),
    ISBN            ("isbn"),
    ISSN            ("issn"),
    JOURNAL         ("journal"),
    KEY             ("key"),
    KEYWORDS        ("keywords"),
    LANGUAGE        ("language"),
    LCCN            ("lccn"),
    LOCATION        ("location"),
    MONTH           ("month"),
    MRNUMBER        ("mrnumber"),
    NOTE            ("note"),
    NUMBER          ("number"),
    ORGANIZATION    ("organization"),
    PAGES           ("pages"),
    PMID            ("pmid"),
    PUBLISHER       ("publisher"),
    SCHOOL          ("school"),
    SERIES          ("series"),
    TITLE           ("title"),
    TYPE            ("type"),
    URL             ("url"),
    VOLUME          ("volume"),
    YEAR            ("year");

    private final String type;

    BibEntryFieldType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static BibEntryFieldType ofType(String type) {
        for (BibEntryFieldType t : BibEntryFieldType.values()) {
            if (t.getType().equals(type)) {
                return t;
            }
        }
        return null;
    }
    
}
