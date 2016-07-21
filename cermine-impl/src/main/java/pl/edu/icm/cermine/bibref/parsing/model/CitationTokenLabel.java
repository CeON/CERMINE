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

import java.util.EnumMap;
import java.util.Map;

/**
 * Citation token label.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public enum CitationTokenLabel {
    
    ARTICLE_TITLE,
    ARTICLE_TITLE_FIRST,
    CONF,
    CONF_FIRST,
    CONTENT,
    CONTENT_FIRST,
    DOI,
    EDITION,
    GIVENNAME,
    GIVENNAME_FIRST,
    ISSUE,
    PAGEF,
    PAGEL,
    PUBLISHER_LOC,
    PUBLISHER_LOC_FIRST,
    PUBLISHER_NAME,
    PUBLISHER_NAME_FIRST,
    SC,
    SC_FIRST,
    SERIES,
    SOURCE,
    SOURCE_FIRST,
    SURNAME,
    SURNAME_FIRST,
    TEXT,
    TEXT_BEFORE_ARTICLE_TITLE,
    TEXT_BEFORE_CONF,
    TEXT_BEFORE_CONTENT,
    TEXT_BEFORE_EDITION,
    TEXT_BEFORE_GIVENNAME,
    TEXT_BEFORE_ISSUE,
    TEXT_BEFORE_PAGEF,
    TEXT_BEFORE_PAGEL,
    TEXT_BEFORE_PUBLISHER_LOC,
    TEXT_BEFORE_PUBLISHER_NAME,
    TEXT_BEFORE_SC,
    TEXT_BEFORE_SERIES,
    TEXT_BEFORE_SOURCE,
    TEXT_BEFORE_SURNAME,
    TEXT_BEFORE_URI,
    TEXT_BEFORE_VOLUME,
    TEXT_BEFORE_VOLUME_SERIES,
    TEXT_BEFORE_YEAR,
    URI,
    VOLUME,
    VOLUME_SERIES,
    YEAR;

    private static final Map<CitationTokenLabel, CitationTokenLabel> FIRST_LABELS =
            new EnumMap<CitationTokenLabel, CitationTokenLabel>(CitationTokenLabel.class);
    private static final Map<CitationTokenLabel, CitationTokenLabel> TEXT_BEFORE_LABELS =
            new EnumMap<CitationTokenLabel, CitationTokenLabel>(CitationTokenLabel.class);
    private static final Map<CitationTokenLabel, CitationTokenLabel> NORMALIZE_LABELS =
            new EnumMap<CitationTokenLabel, CitationTokenLabel>(CitationTokenLabel.class);

    static {
        FIRST_LABELS.put(ARTICLE_TITLE,  ARTICLE_TITLE_FIRST);
        FIRST_LABELS.put(CONF,           CONF_FIRST);
        FIRST_LABELS.put(CONTENT,        CONTENT_FIRST);
        FIRST_LABELS.put(GIVENNAME,      GIVENNAME_FIRST);
        FIRST_LABELS.put(PUBLISHER_LOC,  PUBLISHER_LOC_FIRST);
        FIRST_LABELS.put(PUBLISHER_NAME, PUBLISHER_NAME_FIRST);
        FIRST_LABELS.put(SC,             SC_FIRST);
        FIRST_LABELS.put(SOURCE,         SOURCE_FIRST);
        FIRST_LABELS.put(SURNAME,        SURNAME_FIRST);

        TEXT_BEFORE_LABELS.put(ARTICLE_TITLE,   TEXT_BEFORE_ARTICLE_TITLE);
        TEXT_BEFORE_LABELS.put(CONF,            TEXT_BEFORE_CONF);
        TEXT_BEFORE_LABELS.put(CONTENT,         TEXT_BEFORE_CONTENT);
        TEXT_BEFORE_LABELS.put(EDITION,         TEXT_BEFORE_EDITION);
        TEXT_BEFORE_LABELS.put(GIVENNAME,       TEXT_BEFORE_GIVENNAME);
        TEXT_BEFORE_LABELS.put(ISSUE,           TEXT_BEFORE_ISSUE);
        TEXT_BEFORE_LABELS.put(PAGEF,           TEXT_BEFORE_PAGEF);
        TEXT_BEFORE_LABELS.put(PAGEL,           TEXT_BEFORE_PAGEL);
        TEXT_BEFORE_LABELS.put(PUBLISHER_LOC,   TEXT_BEFORE_PUBLISHER_LOC);
        TEXT_BEFORE_LABELS.put(PUBLISHER_NAME,  TEXT_BEFORE_PUBLISHER_NAME);
        TEXT_BEFORE_LABELS.put(SC,              TEXT_BEFORE_SC);
        TEXT_BEFORE_LABELS.put(SERIES,          TEXT_BEFORE_SERIES);
        TEXT_BEFORE_LABELS.put(SOURCE,          TEXT_BEFORE_SOURCE);
        TEXT_BEFORE_LABELS.put(SURNAME,         TEXT_BEFORE_SURNAME);
        TEXT_BEFORE_LABELS.put(URI,             TEXT_BEFORE_URI);
        TEXT_BEFORE_LABELS.put(VOLUME,          TEXT_BEFORE_VOLUME);
        TEXT_BEFORE_LABELS.put(VOLUME_SERIES,   TEXT_BEFORE_VOLUME_SERIES);
        TEXT_BEFORE_LABELS.put(YEAR,            TEXT_BEFORE_YEAR);

        NORMALIZE_LABELS.put(TEXT_BEFORE_ARTICLE_TITLE,    TEXT);
        NORMALIZE_LABELS.put(TEXT_BEFORE_CONF,             TEXT);
        NORMALIZE_LABELS.put(TEXT_BEFORE_CONTENT,          TEXT);
        NORMALIZE_LABELS.put(TEXT_BEFORE_EDITION,          TEXT);
        NORMALIZE_LABELS.put(TEXT_BEFORE_GIVENNAME,        TEXT);
        NORMALIZE_LABELS.put(TEXT_BEFORE_ISSUE,            TEXT);
        NORMALIZE_LABELS.put(TEXT_BEFORE_PAGEF,            TEXT);
        NORMALIZE_LABELS.put(TEXT_BEFORE_PAGEL,            TEXT);
        NORMALIZE_LABELS.put(TEXT_BEFORE_PUBLISHER_LOC,    TEXT);
        NORMALIZE_LABELS.put(TEXT_BEFORE_PUBLISHER_NAME,   TEXT);
        NORMALIZE_LABELS.put(TEXT_BEFORE_SC,               TEXT);
        NORMALIZE_LABELS.put(TEXT_BEFORE_SERIES,           TEXT);
        NORMALIZE_LABELS.put(TEXT_BEFORE_SOURCE,           TEXT);
        NORMALIZE_LABELS.put(TEXT_BEFORE_SURNAME,          TEXT);
        NORMALIZE_LABELS.put(TEXT_BEFORE_URI,              TEXT);
        NORMALIZE_LABELS.put(TEXT_BEFORE_VOLUME,           TEXT);
        NORMALIZE_LABELS.put(TEXT_BEFORE_VOLUME_SERIES,    TEXT);
        NORMALIZE_LABELS.put(TEXT_BEFORE_YEAR,             TEXT);
        NORMALIZE_LABELS.put(ARTICLE_TITLE_FIRST,          ARTICLE_TITLE);
        NORMALIZE_LABELS.put(CONF_FIRST,                   CONF);
        NORMALIZE_LABELS.put(CONTENT_FIRST,                CONTENT);
        NORMALIZE_LABELS.put(GIVENNAME_FIRST,              GIVENNAME);
        NORMALIZE_LABELS.put(PUBLISHER_LOC_FIRST,          PUBLISHER_LOC);
        NORMALIZE_LABELS.put(PUBLISHER_NAME_FIRST,         PUBLISHER_NAME);
        NORMALIZE_LABELS.put(SC_FIRST,                     SC);
        NORMALIZE_LABELS.put(SOURCE_FIRST,                 SOURCE);
        NORMALIZE_LABELS.put(SURNAME_FIRST,                SURNAME);
    }

    private static CitationTokenLabel mapLabel(Map<CitationTokenLabel, CitationTokenLabel> map,
            CitationTokenLabel label) {
        if (!map.containsKey(label)) {
            return null;
        }
        return map.get(label);
    }


    public static CitationTokenLabel getFirstLabel(CitationTokenLabel label) {
        return mapLabel(FIRST_LABELS, label);
    }

    public static CitationTokenLabel getTextBeforeLabel(CitationTokenLabel label) {
        return mapLabel(TEXT_BEFORE_LABELS, label);
    }

    public static CitationTokenLabel getNormalizedLabel(CitationTokenLabel label) {
        return mapLabel(NORMALIZE_LABELS, label);
    }

}
