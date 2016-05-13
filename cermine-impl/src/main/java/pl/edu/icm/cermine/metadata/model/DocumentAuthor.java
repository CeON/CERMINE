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

import java.util.ArrayList;
import java.util.List;
import pl.edu.icm.cermine.content.cleaning.ContentCleaner;

/**
 *
 * @author Dominika Tkaczyk
 */
public class DocumentAuthor {

    private String name;
    
    private String email;

    private final List<String> affiliationRefs = new ArrayList<String>();
    
    private final List<DocumentAffiliation> affiliations = new ArrayList<DocumentAffiliation>();

    public DocumentAuthor(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addAffiliation(DocumentAffiliation affiliation) {
        if (!affiliations.contains(affiliation)) {
            affiliations.add(affiliation);
        }
    }

    public void setAffiliationRefs(List<String> refs) {
        affiliationRefs.addAll(refs);
    }

    public List<DocumentAffiliation> getAffiliations() {
        return affiliations;
    }

    public List<String> getAffiliationRefs() {
        return affiliationRefs;
    }

    void clean() {
        name = ContentCleaner.clean(name);
        email = ContentCleaner.clean(email);
        for (DocumentAffiliation affiliation : affiliations){
            affiliation.clean();
        }
    }
    
}
