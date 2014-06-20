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

package pl.edu.icm.cermine.metadata.model;

import pl.edu.icm.cermine.metadata.tools.MetadataTools;

/**
 *
 * @author Dominika Tkaczyk
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
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public static final String DATE_ACCEPTED = "accepted";
    
    public static final String DATE_RECEIVED = "received";
    
    public static final String DATE_REVISED = "revised";
    
    public static final String DATE_PUBLISHED = "published";

    void clean() {
        day = MetadataTools.clean(day);
        month = MetadataTools.clean(month);
        year = MetadataTools.clean(year);
    }
    
}
