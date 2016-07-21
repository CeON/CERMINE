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

package pl.edu.icm.cermine.metadata.model;

import pl.edu.icm.cermine.content.cleaning.ContentCleaner;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class DocumentDate {

    private String year;
    
    private String month;
    
    private String day;

    public DocumentDate(String year) {
        this(year, null, null);
    }
    
    public DocumentDate(String year, String month, String day) {
        this.year = year;
        this.month = month;
        this.day = day;
        clean();
    }

    public String getDay() {
        return day;
    }

   public String getMonth() {
        return month;
    }

    public String getYear() {
        return year;
    }

    public static final String DATE_ACCEPTED = "accepted";
    
    public static final String DATE_RECEIVED = "received";
    
    public static final String DATE_REVISED = "revised";
    
    public static final String DATE_PUBLISHED = "published";

    private void clean() {
        day = ContentCleaner.clean(day);
        month = ContentCleaner.clean(month);
        year = ContentCleaner.clean(year);
    }
    
}
