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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import pl.edu.icm.cermine.content.cleaning.ContentCleaner;
import pl.edu.icm.cermine.tools.timeout.TimeoutRegister;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class DocumentMetadata {

    private String title;
    
    private final Map<String, String> ids = new HashMap<String, String>();
    
    private final List<DocumentAuthor> authors = new ArrayList<DocumentAuthor>();
    
    private final List<DocumentAuthor> editors = new ArrayList<DocumentAuthor>();

    private String abstrakt;
    
    private List<String> keywords = new ArrayList<String>();
    
    private String journal;
    
    private String journalISSN;
    
    private String volume;
    
    private String issue;
    
    private String firstPage;
    
    private String lastPage;
    
    private String publisher;
    
    private final Map<String, DocumentDate> dates = new HashMap<String, DocumentDate>();

    private final List<DocumentAffiliation> affiliations = new ArrayList<DocumentAffiliation>();
    
    private final List<String> emails = new ArrayList<String>();
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
    public List<String> getIdTypes() {
        List<String> idTypes = new ArrayList<String>();
        idTypes.addAll(ids.keySet());
        return idTypes;
    }
    
    public String getId(String idType) {
        return ids.get(idType);
    }
    
    public void setDate(String type, String day, String month, String year) {
        DocumentDate date = new DocumentDate(year, month, day);
        dates.put(type, date);
    }
    
    public DocumentDate getDate(String type) {
        return dates.get(type);
    }
    
    public void addAffiliationToAllAuthors(String affiliation) {
        DocumentAffiliation aff = getAffiliationByName(affiliation);
        if (aff == null) {
            aff = new DocumentAffiliation(affiliation);
            affiliations.add(aff);
        }
        for (DocumentAuthor author : authors) {
            author.addAffiliation(aff);
        }
    }
    
    public void setAffiliationByIndex(String index, String affiliation) {
        DocumentAffiliation aff = getAffiliationByName(affiliation);
        if (aff == null) {
            aff = new DocumentAffiliation(affiliation);
            affiliations.add(aff);
        }
        for (DocumentAuthor author : authors) {
            if (author.getAffiliationRefs().contains(index)) {
                author.addAffiliation(aff);
            }
        }
    }
    
    private DocumentAffiliation getAffiliationByName(String name) {
        DocumentAffiliation ret = null;
        for (DocumentAffiliation affiliation : affiliations) {
            if (affiliation.getRawText().equals(name)){
                ret = affiliation;
            }
        }
        return ret;
    }
    
    public void addId(String type, String id) {
        ids.put(type, id);
    }
    
    public void addAuthor(String authorName, List<String> affiliationIndexes) {
        DocumentAuthor author = new DocumentAuthor(authorName, new ArrayList<String>(affiliationIndexes));
        authors.add(author);
    }
    
    public void addAuthor(String authorName, List<String> affiliationIndexes, String email) {
        if (email.isEmpty()) {
            email = null;
        }
        if (email != null && !emails.contains(email)) {
            emails.add(email);
        }
        DocumentAuthor author = new DocumentAuthor(authorName, email, new ArrayList<String>(affiliationIndexes));
        authors.add(author);
    }

    public void addAuthorWithAffiliations(String authorName, String email, List<String> affiliations) {
        if (email.isEmpty()) {
            email = null;
        }
        if (email != null && !emails.contains(email)) {
            emails.add(email);
        }
        
        DocumentAuthor author = new DocumentAuthor(authorName, email);
        authors.add(author);
        
        for (String affiliation: affiliations) {
            DocumentAffiliation aff = getAffiliationByName(affiliation);
            if (aff == null) {
                aff = new DocumentAffiliation(affiliation);
                this.affiliations.add(aff);
                author.addAffiliation(aff);
            }
        }
    }
    
    public List<DocumentAuthor> getAuthors() {
        return authors;
    }

    public List<String> getEmails() {
        return emails;
    }
    
    public void addEmail(String email) {
        emails.add(email);
    }            
    
    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public String getJournal() {
        return journal;
    }

    public void setJournal(String journal) {
        this.journal = journal;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getAbstrakt() {
        return abstrakt;
    }

    public void setAbstrakt(String abstrakt) {
        this.abstrakt = abstrakt;
    }

    public void addEditor(String editorName) {
        editors.add(new DocumentAuthor(editorName));
    }

    public List<DocumentAuthor> getEditors() {
        return editors;
    }
    
    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getJournalISSN() {
        return journalISSN;
    }

    public void setJournalISSN(String journalISSN) {
        this.journalISSN = journalISSN;
    }
    
    public void setPages(String firstPage, String lastPage) {
        this.firstPage = firstPage;
        this.lastPage = lastPage;
    }

    public String getFirstPage() {
        return firstPage;
    }

    public String getLastPage() {
        return lastPage;
    }
    
    public void addKeyword(String keyword) {
        keywords.add(keyword);
    }

    public List<String> getKeywords() {
        return keywords;
    }
    
    public List<DocumentAffiliation> getAffiliations() {
        return affiliations;
    }

    public void clean() {
        title = ContentCleaner.cleanAllAndBreaks(title);
        for (String id : ids.keySet()) {
            ids.put(id, ContentCleaner.clean(ids.get(id)));
        }
        for (DocumentAffiliation affiliation : affiliations) {
            affiliation.clean();
            TimeoutRegister.get().check();
        }
        Collections.sort(affiliations, new Comparator<DocumentAffiliation>() {
            @Override
            public int compare(DocumentAffiliation a1, DocumentAffiliation a2) {
                return a1.getRawText().compareTo(a2.getRawText());
            }
        });
        int id = 0;
        for (DocumentAffiliation affiliation : affiliations) {
            affiliation.setId(String.valueOf(id++));
        }
        for (DocumentAuthor author : authors) {
            author.clean();
            Collections.sort(author.getAffiliationRefs());
            Collections.sort(author.getEmails());
            Collections.sort(author.getAffiliations(), new Comparator<DocumentAffiliation>() {
                @Override
                public int compare(DocumentAffiliation a1, DocumentAffiliation a2) {
                    if (!a1.getId().equals(a2.getId())) {
                        return a1.getId().compareTo(a2.getId());
                    }
                    return a1.getRawText().compareTo(a2.getRawText());
                }
            });
            TimeoutRegister.get().check();
        }
        for (DocumentAuthor editor : editors) {
            editor.clean();
        }
        Collections.sort(emails);
        abstrakt = ContentCleaner.cleanAllAndBreaks(abstrakt);
        List<String> newKeywords = new ArrayList<String>(keywords.size());
        for (String keyword : keywords) {
            newKeywords.add(ContentCleaner.clean(keyword));
        }
        keywords = newKeywords;
        journal = ContentCleaner.clean(journal);
        journalISSN = ContentCleaner.clean(journalISSN);
        volume = ContentCleaner.clean(volume);
        issue = ContentCleaner.clean(issue);
        firstPage = ContentCleaner.clean(firstPage);
        lastPage = ContentCleaner.clean(lastPage);
        publisher = ContentCleaner.clean(publisher);
    }

    public static final String ID_HINDAWI = "hindawi-id";
    public static final String ID_DOI = "doi";
    public static final String ID_URN = "urn";
    
}
