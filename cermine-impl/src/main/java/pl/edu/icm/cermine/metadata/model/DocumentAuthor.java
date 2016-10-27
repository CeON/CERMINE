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

import java.util.ArrayList;
import java.util.List;
import pl.edu.icm.cermine.content.cleaning.ContentCleaner;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class DocumentAuthor {
    
    private String name;
    
    private List<String> emails;

    private List<String> affiliationRefs;
    
    private List<DocumentAffiliation> affiliations;

    public DocumentAuthor(String name) {
        this(name, null, new ArrayList<String>());
    }

    public DocumentAuthor(String name, String email) {
        this(name, email, new ArrayList<String>());
    }

    public DocumentAuthor(String name, List<String> affiliationRefs) {
        this(name, null, affiliationRefs);
    }

    public DocumentAuthor(String name, String email, List<String> affiliationRefs) {
        this.name = name;
        this.emails = new ArrayList<String>();
        if (email != null) {
            this.emails.add(email);
        }
        this.affiliationRefs = affiliationRefs;
        this.affiliations = new ArrayList<DocumentAffiliation>();
    }

    public List<String> getEmails() {
        return emails;
    }

    public void addEmail(String email) {
        if (email != null && !email.isEmpty() && !emails.contains(email)) {
            emails.add(email);
        }
    }

    public String getName() {
        return name;
    }

    public void addAffiliation(DocumentAffiliation affiliation) {
        if (!affiliations.contains(affiliation)) {
            affiliations.add(affiliation);
        }
    }

    public List<DocumentAffiliation> getAffiliations() {
        return affiliations;
    }

    public List<String> getAffiliationRefs() {
        return affiliationRefs;
    }

    void clean() {
        name = ContentCleaner.clean(name);
        List<String> newEmails = new ArrayList<String>();
        for (String email: emails) {
            newEmails.add(ContentCleaner.clean(email));
        }
        emails = newEmails;
    }
    
}
