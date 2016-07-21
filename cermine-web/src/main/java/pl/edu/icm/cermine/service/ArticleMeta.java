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

package pl.edu.icm.cermine.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;
import org.slf4j.LoggerFactory;

/**
 * @author Aleksander Nowinski (a.nowinski@icm.edu.pl)
 */
public class ArticleMeta {

    static org.slf4j.Logger log = LoggerFactory.getLogger(ArticleMeta.class);
    private String title;
    private String journalTitle;
    private String journalISSN;
    private String publisher;
    private String publisherLoc;
    private String doi;
    private String urn;
    private String abstractText;
    private List<ContributorMeta> authors;
    private List<ContributorMeta> editors;
    private List<String> keywords;
    private String fpage;
    private String lpage;
    private String volume;
    private String issue;
    private String pubDate;
    private String receivedDate;
    private String revisedDate;
    private String acceptedDate;
    private List<ArticleMeta> references;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getJournalTitle() {
        return journalTitle;
    }

    public void setJournalTitle(String journalTitle) {
        this.journalTitle = journalTitle;
    }

    public String getJournalISSN() {
        return journalISSN;
    }

    public void setJournalISSN(String journalISSN) {
        this.journalISSN = journalISSN;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPublisherLoc() {
        return publisherLoc;
    }

    public void setPublisherLoc(String publisherLoc) {
        this.publisherLoc = publisherLoc;
    }

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public String getUrn() {
        return urn;
    }

    public void setUrn(String urn) {
        this.urn = urn;
    }

    public String getAbstractText() {
        return abstractText;
    }

    public void setAbstractText(String abstractText) {
        this.abstractText = abstractText;
    }

    public List<ContributorMeta> getAuthors() {
        return authors;
    }

    public void setAuthors(List<ContributorMeta> authors) {
        this.authors = authors;
    }

    public List<ContributorMeta> getEditors() {
        return editors;
    }

    public void setEditors(List<ContributorMeta> editors) {
        this.editors = editors;
    }
    
    public String getKeywordsString() {
        StringBuilder resb = new StringBuilder();
        for (String k : keywords) {
            resb.append(k).append("; ");
        }
        
        String res = resb.toString();
        if (!res.isEmpty()) {
            res = res.substring(0, resb.length() - 2);
        }
        return res;
    }

    public String getFpage() {
        return fpage;
    }

    public void setFpage(String fpage) {
        this.fpage = fpage;
    }

    public String getLpage() {
        return lpage;
    }

    public void setLpage(String lpage) {
        this.lpage = lpage;
    }
    
    public String getPages() {
        if (fpage != null && !fpage.isEmpty() && lpage != null && !lpage.isEmpty()) {
            return fpage + "-" + lpage;
        }
        return fpage;        
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(String receivedDate) {
        this.receivedDate = receivedDate;
    }

    public String getRevisedDate() {
        return revisedDate;
    }

    public void setRevisedDate(String revisedDate) {
        this.revisedDate = revisedDate;
    }

    public String getAcceptedDate() {
        return acceptedDate;
    }

    public void setAcceptedDate(String acceptedDate) {
        this.acceptedDate = acceptedDate;
    }

    public List<ArticleMeta> getReferences() {
        return references;
    }

    public void setReferences(List<ArticleMeta> references) {
        this.references = references;
    }
    
    private static String extractXPathValue(Document nlm, String xpath) throws JDOMException {
        XPath xPath = XPath.newInstance(xpath);
        String res = xPath.valueOf(nlm);
        if (res != null) {
            res = res.trim();
        }
        return res;
    }
    
    private static String extractDateValue(Document nlm, String xpath) throws JDOMException {
        String pubYear = extractXPathValue(nlm, xpath + "/year");
        String pubMonth = extractXPathValue(nlm, xpath + "/month");
        String pubDay = extractXPathValue(nlm, xpath + "/day");
        String pubDate = pubYear;
        if (pubYear != null && !pubYear.isEmpty() && pubMonth != null && !pubMonth.isEmpty()) {
            pubDate += "-";
            pubDate += pubMonth;
            if (pubDay != null && !pubDay.isEmpty()) {
                pubDate += "-";
                pubDate += pubDay;
            }
        }
        return pubDate;
    }

    private static Map<String, String> extractAffiliations(Document nlm, String xpath) throws JDOMException {
        XPath xPath = XPath.newInstance(xpath);
        List<Element> nodes = xPath.selectNodes(nlm);
        Map<String, String> affMap = new HashMap<String, String>();
        for (Element node : nodes) {
            String affId = node.getAttributeValue("id");
            affMap.put(affId, node.getValue().trim().replaceFirst(affId, "").trim());
        }
        return affMap;
    }
    
    private static List<ContributorMeta> extractContributorMeta(Document nlm, String xpath, Map<String, String> affiliations) throws JDOMException {
        XPath xPath = XPath.newInstance(xpath);
        List<Element> nodes = xPath.selectNodes(nlm);
        List<ContributorMeta> authors = new ArrayList<ContributorMeta>();
        for (Element node : nodes) {
            Element nameEl = node.getChild("name");
            String name;
            if (nameEl != null) {
                name = nameEl.getChildText("surname")+", "+nameEl.getChildText("given-names");
            } else {
                name = node.getChildText("string-name");
            }
            log.debug("Got author name: " + name);
            ContributorMeta author = new ContributorMeta(name);

            if (affiliations != null) {
                List xrefs = node.getChildren("xref");
                for (Object xref : xrefs) {
                    Element xrefEl = (Element) xref;
                    String xrefType = xrefEl.getAttributeValue("ref-type");
                    String xrefId = xrefEl.getAttributeValue("rid");
                    if ("aff".equals(xrefType) && xrefId != null && affiliations.get(xrefId) != null) {
                        log.debug("Got author affiliation: " + affiliations.get(xrefId));
                        author.addAffiliations(affiliations.get(xrefId));
                    }
                }
            }
           
            List<Element> emails = node.getChildren("email");
            for (Element email : emails) {
                log.debug("Got author email: " + email);
                author.addEmail(email.getText());
            }
            
            authors.add(author);
        }
        return authors;            
    }
    
    private static List<ArticleMeta> extractReferences(Document nlm, String xpath) throws JDOMException {
        XPath xPath = XPath.newInstance(xpath);
        List<Element> nodes = xPath.selectNodes(nlm);
        List<ArticleMeta> references = new ArrayList<ArticleMeta>();
        for (Element node : nodes) {
            ArticleMeta reference = new ArticleMeta();
            
            reference.setAbstractText(node.getValue());
            reference.setTitle(node.getChildText("article-title"));
            reference.setPublisher(node.getChildText("publisher-name"));
            reference.setPublisherLoc(node.getChildText("publisher-loc"));
            reference.setPubDate(node.getChildText("year"));
            reference.setFpage(node.getChildText("fpage"));
            reference.setLpage(node.getChildText("lpage"));
            reference.setJournalTitle(node.getChildText("source"));
            reference.setVolume(node.getChildText("volume"));
            reference.setIssue(node.getChildText("issue"));
            
            List<Element> authorNodes = node.getChildren("string-name");
            List<ContributorMeta> authors = new ArrayList<ContributorMeta>();
            for (Element authorNode : authorNodes) {
                ContributorMeta author = new ContributorMeta();
                author.setGivennames(authorNode.getChildText("given-names"));
                author.setSurname(authorNode.getChildText("surname"));
                authors.add(author);
            }
            reference.setAuthors(authors);
            
            references.add(reference);            
        }
        return references;
    }

    public static ArticleMeta extractNLM(Document nlm) {
        log.debug("Starting extraction from document...");
        try {
            ArticleMeta res = new ArticleMeta();
            
            res.setJournalTitle(extractXPathValue(nlm, "/article/front//journal-title"));
            log.debug("Got journal title: " + res.getJournalTitle());
            
            res.setJournalISSN(extractXPathValue(nlm, "/article/front//journal-meta/issn[@pub-type='ppub']"));
            log.debug("Got journal ISSN: " + res.getJournalISSN());
            
            res.setPublisher(extractXPathValue(nlm, "/article/front//publisher-name"));
            log.debug("Got publisher name: " + res.getPublisher());
            
            res.setTitle(extractXPathValue(nlm, "/article/front//article-title"));
            log.debug("Got title from xpath: " + res.getTitle());
            
            res.setDoi(extractXPathValue(nlm, "/article/front//article-id[@pub-id-type='doi']"));
            log.debug("Got doi from xpath: " + res.getDoi());
            
            res.setUrn(extractXPathValue(nlm, "/article/front//article-id[@pub-id-type='urn']"));
            log.debug("Got urn: " + res.getUrn());

            res.setAbstractText(extractXPathValue(nlm, "/article/front//abstract"));
            log.debug("Got abstract: " + res.getAbstractText());

            //front and last page:
            res.setFpage(extractXPathValue(nlm, "/article/front/article-meta/fpage"));
            res.setLpage(extractXPathValue(nlm, "/article/front/article-meta/lpage"));
            
            res.setVolume(extractXPathValue(nlm, "/article/front/article-meta/volume"));

            res.setIssue(extractXPathValue(nlm, "/article/front/article-meta/issue"));
            
            Map<String, String> affiliations = extractAffiliations(nlm, "//aff");
            
            res.setAuthors(extractContributorMeta(nlm, "//contrib[@contrib-type='author']", affiliations));
            res.setEditors(extractContributorMeta(nlm, "//contrib[@contrib-type='editor']", null));
            
            res.setPubDate(extractDateValue(nlm, "//pub-date"));
            res.setReceivedDate(extractDateValue(nlm, "//history/date[@date-type='received']"));
            res.setRevisedDate(extractDateValue(nlm, "//history/date[@date-type='revised']"));
            res.setAcceptedDate(extractDateValue(nlm, "//history/date[@date-type='accepted']"));

            XPath xPath = XPath.newInstance("//kwd");
            List<String> kwds = new ArrayList<String>();
            for (Object e : xPath.selectNodes(nlm)) {
                kwds.add(((Element) e).getTextTrim());
            }
            res.setKeywords(kwds);
            
            res.setReferences(extractReferences(nlm, "//ref/mixed-citation"));
            
            return res;
        } catch (JDOMException ex) {
            log.error("Unexpected exception while working with xpath", ex);
            throw new RuntimeException(ex);
        }
    }
    
    public static class ContributorMeta {
        private String name;
        private String givennames;
        private String surname;
        private List<String> affiliations = new ArrayList<String>();
        private List<String> emails = new ArrayList<String>();

        public ContributorMeta() {
        }

        public ContributorMeta(String name) {
            this.name = name;
        }

        public String getName() {
            if (name == null) {
                if (givennames == null) {
                    return surname;
                } else if (surname == null) {
                    return givennames;
                } else {
                    return surname + ", " + givennames;
                }
            }
            return name;
        }

        public String getGivennames() {
            return givennames;
        }

        public void setGivennames(String givennames) {
            this.givennames = givennames;
        }

        public String getSurname() {
            return surname;
        }

        public void setSurname(String surname) {
            this.surname = surname;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<String> getAffiliations() {
            return affiliations;
        }

        public void setAffiliations(List<String> affiliations) {
            this.affiliations = affiliations;
        }
        
        public void addAffiliations(String affiliation) {
            affiliations.add(affiliation);
        }

        public List<String> getEmails() {
            return emails;
        }

        public void setEmails(List<String> emails) {
            this.emails = emails;
        }
        
        public void addEmail(String email) {
            emails.add(email);
        }
    }
}
