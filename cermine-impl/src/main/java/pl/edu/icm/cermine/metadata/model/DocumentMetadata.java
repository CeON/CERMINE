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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import pl.edu.icm.cermine.content.cleaning.ContentCleaner;

/**
 *
 * @author Dominika Tkaczyk
 */
public class DocumentMetadata {

    private String title;
    
    private Map<String, String> ids = new HashMap<String, String>();
    
    private List<DocumentAuthor> authors = new ArrayList<DocumentAuthor>();
    
    private List<DocumentAuthor> editors = new ArrayList<DocumentAuthor>();

    private String abstrakt;
    
    private List<String> keywords = new ArrayList<String>();
    
    private String journal;
    
    private String journalISSN;
    
    private String volume;
    
    private String issue;
    
    private String firstPage;
    
    private String lastPage;
    
    private String publisher;
    
    private Map<String, DocumentDate> dates = new HashMap<String, DocumentDate>();

    private List<DocumentAffiliation> affiliations = new ArrayList<DocumentAffiliation>();
    
    
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
            aff = new DocumentAffiliation(String.valueOf(affiliations.size()), affiliation);
            affiliations.add(aff);
        }
        for (DocumentAuthor author : authors) {
            author.addAffiliation(aff);
        }
    }
    
    public void setAffiliationByIndex(String index, String affiliation) {
        DocumentAffiliation aff = getAffiliationByName(affiliation);
        if (aff == null) {
            aff = new DocumentAffiliation(String.valueOf(affiliations.size()), affiliation);
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
        DocumentAuthor author = new DocumentAuthor(authorName);
        author.setAffiliationRefs(affiliationIndexes);
        authors.add(author);
    }

    public List<DocumentAuthor> getAuthors() {
        return authors;
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
        for (DocumentAuthor author : authors) {
            author.clean();
        }
        for (DocumentAuthor editor : editors) {
            editor.clean();
        }
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
        for (DocumentDate date : dates.values()) {
           date.clean();
        }
        
    }

    public static final String ID_HINDAWI = "hindawi-id";
    public static final String ID_DOI = "doi";
    public static final String ID_URN = "urn";
    
}
