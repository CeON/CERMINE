package pl.edu.icm.cermine.service;

import java.util.ArrayList;
import java.util.List;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Aleksander Nowinski <a.nowinski@icm.edu.pl>
 */
public class ArticleMeta {

    static org.slf4j.Logger log = LoggerFactory.getLogger(ArticleMeta.class);
    private String title;
    private String journalTitle;
    private String doi;
    private String abstractText;
    private List<String> authors;
    private List<String> keywords;
    private String fpage;
    private String lpage;
    private String volume;
    private String issue;

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

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public String getAbstractText() {
        return abstractText;
    }

    public void setAbstractText(String abstractText) {
        this.abstractText = abstractText;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public String getAuthorsString() {
        StringBuffer resb = new StringBuffer();
        for (String a : authors) {
            resb.append(a).append(", ");
        }
        String res = resb.toString();
        if (!res.isEmpty()) {
            res = res.substring(0, resb.length() - 2);
        }
        return res;
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

    private static String extractXPathValue(Document nlm, String xpath) throws JDOMException {
        XPath xPath = XPath.newInstance(xpath);
        String res = xPath.valueOf(nlm);
        if (res != null) {
            res = res.trim();
        }
        return res;
    }

    public static ArticleMeta extractNLM(Document nlm) {
        log.debug("Starting extraction from document...");
        try {
            ArticleMeta res = new ArticleMeta();
            res.setJournalTitle(extractXPathValue(nlm, "/article/front//journal-title"));
            log.debug("Got journal title: " + res.getJournalTitle());
            res.setTitle(extractXPathValue(nlm, "/article/front//article-title"));
            log.debug("Got title from xpath: " + res.getTitle());
            res.setDoi(extractXPathValue(nlm, "/article/front//article-id[@pub-id-type='doi']"));
            log.debug("Got doi from xpath: " + res.getDoi());

            res.setAbstractText(extractXPathValue(nlm, "/article/front//abstract"));
            log.debug("Got abstract: " + res.getAbstractText());
            //front and last page:
            res.setFpage(extractXPathValue(nlm, "/article/front/article-meta/fpage"));
            res.setLpage(extractXPathValue(nlm, "/article/front/article-meta/lpage"));
            res.setVolume(extractXPathValue(nlm, "/article/front/article-meta/volume"));

            res.setIssue(extractXPathValue(nlm, "/article/front/article-meta/issue"));
            //authors:
            //@TODO: fixme - proper text extraction.
            XPath xPath = XPath.newInstance("//contrib[@contrib-type='author']/name");
            List<Element> selectNodes = xPath.selectNodes(nlm);
            List<String> authors = new ArrayList<String>();
            for (Element e : selectNodes) {
                String name = e.getValue().trim().replaceAll("\\s+", " ");
                log.debug("Got author name: " + name);
                authors.add(name);
            }
            res.setAuthors(authors);

            xPath = XPath.newInstance("//kwd");
            List<String> kwds = new ArrayList<String>();
            for (Object e : xPath.selectNodes(nlm)) {
                kwds.add(((Element) e).getTextTrim());
            }
            res.setKeywords(kwds);
            return res;
        } catch (JDOMException ex) {
            log.error("Unexpected exception while working with xpath", ex);
            throw new RuntimeException(ex);
        }
    }
}
