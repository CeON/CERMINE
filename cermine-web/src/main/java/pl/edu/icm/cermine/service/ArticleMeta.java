package pl.edu.icm.cermine.service;

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

    String title;
    String journalTitle;

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
    
    
    private static String extractXPathValue(Document nlm, String xpath) throws JDOMException {
        XPath xPath = XPath.newInstance(xpath);
        String journalTitle = xPath.valueOf(nlm);
        return journalTitle;
    }
    
    
    public static ArticleMeta extractNLM(Document nlm) {
        log.debug("Starting extraction from document...");
        try {
            ArticleMeta res = new ArticleMeta();
            String journalTitle = extractXPathValue(nlm, "/article/front/journal-meta/journal-title-group/journal-title");
            log.debug("Got journal title: "+journalTitle);
            res.setJournalTitle(journalTitle);
            res.setTitle(extractXPathValue(nlm, "/article/front/article-meta/title-group/article-title"));
            log.debug("Got title from xpath: "+res.getTitle());
            return res;
        } catch (JDOMException ex) {
            log.error("Unexpected exception while working with xpath", ex);
            throw new RuntimeException(ex);
        }
    }
    
}
