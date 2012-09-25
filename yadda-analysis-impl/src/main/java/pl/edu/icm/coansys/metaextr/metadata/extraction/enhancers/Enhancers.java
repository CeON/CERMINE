package pl.edu.icm.coansys.metaextr.metadata.extraction.enhancers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import org.jdom.Element;
import org.jdom.filter.Filter;

/**
 *
 * @author krusek
 */
public class Enhancers {
    
    //getters
    
    //author names
    public static List<String> getAuthorNames(Element metadata) {
        List<String> authors = new ArrayList<String>();
        String[] path = {"front", "article-meta", "contrib-group", "contrib"};
        
        for (Element element : getElements(metadata, path)) {
            if ("author".equals(element.getAttributeValue("contrib-type"))) {
                authors.add(element.getChildText("string-name"));
            }
        }
        
        return authors;
    }
    
    public static boolean isAffiliationIndex(String text) {
        Pattern pattern = Pattern.compile("\\d{1,2}");
        return pattern.matcher(text).matches();
    }
    
    //setters
    
    //article id
    public static void addArticleId(Element metadata, String type, String id) {
        String[] path = {"front", "article-meta", "article-id"};
        addValue(metadata, path, "pub-id-type", type, id);
    }
    
    //author
    public static void addAuthor(Element metadata, String author, List<String> refs) {
        addContributor(metadata, author, "author", refs);
    }
    
    //editor
    static void addEditor(Element metadata, String editor) {
        addContributor(metadata, editor, "editor", null);
    }
    
    //email
    static void addEmail(Element metadata, String email) {
        String[] path = {"front", "article-meta", "contrib-group", "contrib"};
        
        Element author = null;
        boolean one = true;
        
        for (Element element : getElements(metadata, path)) {
            Element name = element.getChild("string-name");
            String[] names = name.getText().split(" ");
            for (String namePart : names) {
                if (namePart.length() > 2 && email.toLowerCase().contains(namePart.toLowerCase())) {
                    if (author == null) {
                        author = element;
                        break;
                    } else {
                        one = false;
                    }
                }
            }
        }
        
        if (author != null && one) {
            Element em = new Element("email");
            em.setText(email);
            author.addContent(em);
        }
    }
    
    //keywords
    public static void addKeyword(Element metadata, String keyword) {
        String[] path = {"front", "article-meta", "kwd-group", "kwd"};
        addValue(metadata, path, keyword);
    }
    
    //abstract
    public static void setAbstract(Element metadata, String description) {
        String[] path = {"front", "article-meta", "abstract", "p"};
        setValue(metadata, path, description);
    }
    
    //accepted date
    public static void setAcceptedDate(Element metadata, String day, String month, String year) {
        setHistoryDate(metadata, "accepted", day, month, year);
    }
    
    //affiliation
    static void setAffiliation(Element metadata, String id, String affiliation) {
        String[] path = {"front", "article-meta", "contrib-group", "contrib"};
        
        for (Element element : getElements(metadata, path)) {
            List children = element.getChildren("aff");
            if (children.isEmpty() && (id == null || id.isEmpty()) && "author".equals(element.getAttributeValue("contrib-type"))) {
                Element aff = new Element("aff");
                aff.setText(affiliation);
                element.addContent(aff);
            }
            for (Object child : children) {
                if (child instanceof Element) {
                    if (id.equals(((Element)child).getText())) {
                        ((Element)child).setText(affiliation);
                    }
                }
            }
        }
    }
    
    //issue
    public static void setIssue(Element metadata, String issue) {
        String[] path = {"front", "article-meta", "issue"};
        setValue(metadata, path, issue);
    }
    
    //journal
    public static void setJournal(Element metadata, String journal) {
        String[] path = {"front", "journal-meta", "journal-title-group", "journal-title"};
        setValue(metadata, path, journal);
    }
    
    //journal issn
    public static void setJournalIssn(Element metadata, String issn) {
        String[] path = {"front", "journal-meta", "issn"};
        setValue(metadata, path, "pub-type", "ppub", issn);
    }
    
    //pages
    public static void setPages(Element metadata, String firstPage, String lastPage) {
        String[] fpath = {"front", "article-meta", "fpage"};
        setValue(metadata, fpath, firstPage);
        String[] lpath = {"front", "article-meta", "lpage"};
        setValue(metadata, lpath, lastPage);
    }
    
    //published date
    public static void setPublishedDate(Element metadata, String day, String month, String year) {
        String[] dpath = {"front", "article-meta", "pub-date", "day"};
        setValue(metadata, dpath, day);
        String[] mpath = {"front", "article-meta", "pub-date", "month"};
        setValue(metadata, mpath, month);
        String[] ypath = {"front", "article-meta", "pub-date", "year"};
        setValue(metadata, ypath, year);
    }
    
    //publisher
    public static void setPublisher(Element metadata, String publisher) {
        String[] path = {"front", "journal-meta", "publisher", "publisher-name"};
        setValue(metadata, path, publisher);
    }
    
    //received date
    public static void setReceivedDate(Element metadata, String day, String month, String year) {
        setHistoryDate(metadata, "received", day, month, year);
    }
    
    //revised date
    public static void setRevisedDate(Element metadata, String day, String month, String year) {
        setHistoryDate(metadata, "revised", day, month, year);
    }
    
    //article title
    public static void setTitle(Element metadata, String title) {
        String[] path = {"front", "article-meta", "title-group", "article-title"};
        Enhancers.setValue(metadata, path, title);
    }
    
    //volume
    public static void setVolume(Element metadata, String volume) {
        String[] path = {"front", "article-meta", "volume"};
        setValue(metadata, path, volume);
    }
    
    //year
    public static void setYear(Element metadata, String year) {
        String[] path = {"front", "article-meta", "pub-date", "year"};
        setValue(metadata, path, year);
    }
    
    static void cleanAffiliations(Element metadata) {
        String[] path = {"front", "article-meta", "contrib-group", "contrib"};
        
        for (Element element : getElements(metadata, path)) {
            List children = element.getChildren("aff");
            List<Element> toRemove = new ArrayList<Element>();
            for (Object child : children) {
                if (child instanceof Element) {
                    if (((Element)child).getText().length() < 5) {
                        toRemove.add((Element)child);
                    }
                }
            }
            for (Element aff : toRemove) {
                element.removeContent(aff);
            }
        }
    }
    
    
    //internal
    
    protected static Element getElement(Element element, String[] path) {
        Element el = element;
        for (String name : path) {
            if (el.getChild(name) == null) {
                el.addContent(new Element(name));
            }
            el = el.getChild(name);
        }
        return el;
    }
    
    protected static List<Element> getElements(Element element, String[] path) {
        Element el = element;
        for (int i = 0; i < path.length - 1; i++) {
            String name = path[i];
            if (el.getChild(name) == null) {
                el.addContent(new Element(name));
            }
            el = el.getChild(name);
        }
        List<Element> elements = new ArrayList<Element>();
        for (Object object : el.getChildren(path[path.length-1])) {
            if (object instanceof Element) {
                elements.add((Element) object);
            }
        }
        return elements;
    }
    
    private static void addValue(Element element, String[] path, String value) {
        Element parent = getElement(element, Arrays.copyOfRange(path, 0, path.length - 1));
        Element child = new Element(path[path.length - 1]);
        child.setText(value);
        parent.addContent(child);
    }
    
    private static void addValue(Element element, String[] path, String attrName, String attrValue, String value) {
        Element parent = getElement(element, Arrays.copyOfRange(path, 0, path.length - 1));
        Element child = new Element(path[path.length - 1]);
        child.setText(value);
        child.setAttribute(attrName, attrValue);
        parent.addContent(child);
    }
    
    private static void setValue(Element element, String[] path, String value) {
        Element child = getElement(element, path);
        child.setText(value);
    }
    
    private static void setValue(Element element, String[] path, String attrName, String attrValue, String value) {
        Element child = getElement(element, path);
        child.setText(value);
        child.setAttribute(attrName, attrValue);
    }
    
    private static void addContributor(Element element, String name, String type, List<String> refs) {
        String[] path = {"front", "article-meta", "contrib-group"};
        Element contributors = getElement(element, path);
        
        Element contributor = new Element("contrib");
        contributor.setAttribute("contrib-type", type);
        Element sName = new Element("string-name");
        sName.setText(name);
        contributor.addContent(sName);

        if (refs != null) {
            for (String ref : refs) {
                Element aff = new Element("aff");
                aff.setText(ref);
                contributor.addContent(aff);
            }
        }
        contributors.addContent(contributor);
    }
    
    private static void setHistoryDate(Element element, final String type, String day, String month, String year) {
        String[] path = {"front", "article-meta", "history"};
        Element history = getElement(element, path);
        history.removeContent(new Filter() {

            @Override
            public boolean matches(Object object) {
                return object instanceof Element 
                        && ((Element) object).getName().equals("date")
                        && type.equals(((Element) object).getAttributeValue("date-type"));
            }
        });
        
        Element date = new Element("date");
        date.setAttribute("date-type", type);
        history.addContent(date);
        
        String[] dpath = {"day"};
        setValue(date, dpath, day);
        String[] mpath = {"month"};
        setValue(date, mpath, month);
        String[] ypath = {"year"};
        setValue(date, ypath, year);
    }

}
