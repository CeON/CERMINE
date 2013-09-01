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

package pl.edu.icm.cermine.metadata.extraction.enhancers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jdom.Element;
import org.jdom.filter.Filter;

/**
 *
 * @author krusek
 * @author Dominika Tkaczyk
 */
public class Enhancers {
    
    private static final int MAX_JUNK_AFF_LENGTH = 5;
    
    //getters
    
    //author names
    public static List<String> getAuthorNames(Element metadata) {
        List<String> authors = new ArrayList<String>();
        String[] path = {TAG_FRONT, TAG_ARTICLE_META, TAG_CONTRIB_GROUP, TAG_CONTRIB};
        
        for (Element element : getElements(metadata, path)) {
            if (TAG_AUTHOR.equals(element.getAttributeValue(TAG_CONTRIB_TYPE))) {
                authors.add(element.getChildText(TAG_STRING_NAME));
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
        String[] path = {TAG_FRONT, TAG_ARTICLE_META, TAG_ARTICLE_ID};
        addValue(metadata, path, TAG_PUB_ID_TYPE, type, id);
    }
    
    //author
    public static void addAuthor(Element metadata, String author, List<String> refs) {
        addContributor(metadata, cleanOther(cleanLigatures(author)), TAG_AUTHOR, refs);
    }
    
    //editor
    static void addEditor(Element metadata, String editor) {
        addContributor(metadata, cleanLigatures(editor), TAG_EDITOR, null);
    }
    
    //email
    static void addEmail(Element metadata, String email) {
        String[] path = {TAG_FRONT, TAG_ARTICLE_META, TAG_CONTRIB_GROUP, TAG_CONTRIB};
        
        Element author = null;
        boolean one = true;
        
        for (Element element : getElements(metadata, path)) {
            Element name = element.getChild(TAG_STRING_NAME);
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
            Element em = new Element(TAG_EMAIL);
            em.setText(cleanLigatures(email));
            author.addContent(em);
        }
    }
    
    //keywords
    public static void addKeyword(Element metadata, String keyword) {
        String[] path = {TAG_FRONT, TAG_ARTICLE_META, TAG_KWD_GROUP, TAG_KWD};
        addValue(metadata, path, cleanOther(cleanLigatures(keyword)));
    }
    
    //abstract
    public static void setAbstract(Element metadata, String description) {
        String[] path = {TAG_FRONT, TAG_ARTICLE_META, TAG_ABSTRACT, TAG_P};
        setValue(metadata, path, clean(description));
    }
    
    //accepted date
    public static void setAcceptedDate(Element metadata, String day, String month, String year) {
        setHistoryDate(metadata, TAG_ACCEPTED, day, month, year);
    }
    
    //affiliation
    static void setAffiliation(Element metadata, String id, String affiliation) {
        String[] path = {TAG_FRONT, TAG_ARTICLE_META, TAG_CONTRIB_GROUP, TAG_CONTRIB};
        
        for (Element element : getElements(metadata, path)) {
            List children = element.getChildren(TAG_AFF);
            if ((id == null || id.isEmpty()) && TAG_AUTHOR.equals(element.getAttributeValue(TAG_CONTRIB_TYPE))) {
                Element aff = new Element(TAG_AFF);
                aff.setText(cleanLigatures(affiliation));
                element.addContent(aff);
            }
            for (Object child : children) {
                if (child instanceof Element && id.equals(((Element)child).getText())) {
                    ((Element)child).setText(cleanLigatures(affiliation));
                }
            }
        }
    }
    
    //issue
    public static void setIssue(Element metadata, String issue) {
        String[] path = {TAG_FRONT, TAG_ARTICLE_META, TAG_ISSUE};
        setValue(metadata, path, issue);
    }
    
    //journal
    public static void setJournal(Element metadata, String journal) {
        String[] path = {TAG_FRONT, TAG_JOURNAL_META, TAG_JOURNAL_TITLE_GROUP, TAG_JOURNAL_TITLE};
        setValue(metadata, path, cleanLigatures(journal));
    }
    
    //journal issn
    public static void setJournalIssn(Element metadata, String issn) {
        String[] path = {TAG_FRONT, TAG_JOURNAL_META, TAG_ISSN};
        setValue(metadata, path, TAG_PUB_TYPE, TAG_PPUB, issn);
    }
    
    //pages
    public static void setPages(Element metadata, String firstPage, String lastPage) {
        String[] fpath = {TAG_FRONT, TAG_ARTICLE_META, TAG_FPAGE};
        setValue(metadata, fpath, firstPage);
        String[] lpath = {TAG_FRONT, TAG_ARTICLE_META, TAG_LPAGE};
        setValue(metadata, lpath, lastPage);
    }
    
    //published date
    public static void setPublishedDate(Element metadata, String day, String month, String year) {
        String[] dpath = {TAG_FRONT, TAG_ARTICLE_META, TAG_PUB_DATE, TAG_DAY};
        setValue(metadata, dpath, day);
        String[] mpath = {TAG_FRONT, TAG_ARTICLE_META, TAG_PUB_DATE, TAG_MONTH};
        setValue(metadata, mpath, month);
        String[] ypath = {TAG_FRONT, TAG_ARTICLE_META, TAG_PUB_DATE, TAG_YEAR};
        setValue(metadata, ypath, year);
    }
    
    //publisher
    public static void setPublisher(Element metadata, String publisher) {
        String[] path = {TAG_FRONT, TAG_JOURNAL_META, TAG_PUBLISHER, TAG_PUBLISHER_NAME};
        setValue(metadata, path, cleanLigatures(publisher));
    }
    
    //received date
    public static void setReceivedDate(Element metadata, String day, String month, String year) {
        setHistoryDate(metadata, TAG_RECEIVED, day, month, year);
    }
    
    //revised date
    public static void setRevisedDate(Element metadata, String day, String month, String year) {
        setHistoryDate(metadata, TAG_REVISED, day, month, year);
    }
    
    //article title
    public static void setTitle(Element metadata, String title) {
        String[] path = {TAG_FRONT, TAG_ARTICLE_META, TAG_TITLE_GROUP, TAG_ARTICLE_TITLE};
        Enhancers.setValue(metadata, path, cleanOther(cleanLigatures(title)));
    }
    
    //volume
    public static void setVolume(Element metadata, String volume) {
        String[] path = {TAG_FRONT, TAG_ARTICLE_META, TAG_VOLUME};
        setValue(metadata, path, volume);
    }
    
    //year
    public static void setYear(Element metadata, String year) {
        String[] path = {TAG_FRONT, TAG_ARTICLE_META, TAG_PUB_DATE, TAG_YEAR};
        setValue(metadata, path, year);
    }
    
    public static void cleanAffiliations(Element metadata) {
        String[] path = {TAG_FRONT, TAG_ARTICLE_META, TAG_CONTRIB_GROUP, TAG_CONTRIB};
        
        for (Element element : getElements(metadata, path)) {
            List children = element.getChildren(TAG_AFF);
            List<Element> toRemove = new ArrayList<Element>();
            for (Object child : children) {
                if (child instanceof Element && ((Element)child).getText().length() < MAX_JUNK_AFF_LENGTH) {
                    toRemove.add((Element)child);
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
        String[] path = {TAG_FRONT, TAG_ARTICLE_META, TAG_CONTRIB_GROUP};
        Element contributors = getElement(element, path);
        
        Element contributor = new Element(TAG_CONTRIB);
        contributor.setAttribute(TAG_CONTRIB_TYPE, type);
        Element sName = new Element(TAG_STRING_NAME);
        sName.setText(name);
        contributor.addContent(sName);

        if (refs != null) {
            for (String ref : refs) {
                Element aff = new Element(TAG_AFF);
                aff.setText(ref);
                contributor.addContent(aff);
            }
        }
        contributors.addContent(contributor);
    }
    
    private static void setHistoryDate(Element element, final String type, String day, String month, String year) {
        String[] path = {TAG_FRONT, TAG_ARTICLE_META, TAG_HISTORY};
        Element history = getElement(element, path);
        history.removeContent(new Filter() {

            @Override
            public boolean matches(Object object) {
                return object instanceof Element 
                        && ((Element) object).getName().equals(TAG_DATE)
                        && type.equals(((Element) object).getAttributeValue(TAG_DATE_TYPE));
            }
        });
        
        Element date = new Element(TAG_DATE);
        date.setAttribute("date-type", type);
        history.addContent(date);
        
        String[] dpath = {TAG_DAY};
        setValue(date, dpath, day);
        String[] mpath = {TAG_MONTH};
        setValue(date, mpath, month);
        String[] ypath = {TAG_YEAR};
        setValue(date, ypath, year);
    }

    private static String cleanOther(String str) {
        return str.replaceAll("[’‘]", "'")
                  .replaceAll("[–]", "-");
    }
    
    private static String cleanLigatures(String str) {
        return str.replaceAll("\uFB00", "ff")
                  .replaceAll("\uFB01", "fi")
                  .replaceAll("\uFB02", "fl")
                  .replaceAll("\uFB03", "ffi")
                  .replaceAll("\uFB04", "ffl")
                  .replaceAll("\uFB05", "ft")
                  .replaceAll("\uFB06", "st")
                  .replaceAll("\u00E6", "ae");
    }
    
    private static String cleanHyphenation(String str) {
        str = str.replace("$", "\\$");
        
        String hyphenList = "\u002D\u00AD\u2010\u2011\u2012\u2013\u2014\u2015\u207B\u208B\u2212-";
        Pattern p = Pattern.compile("([^" + hyphenList + "]*\\S+)[" + hyphenList + "]\n", Pattern.DOTALL);
        Matcher m = p.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, m.group(1));
        }
        m.appendTail(sb);
        return sb.toString().replaceAll("\n", " ").replace("\\$", "$");
    }
    
    private static String clean(String str) {
        return cleanHyphenation(cleanLigatures(str));
    }
    
    
    private static final String TAG_ABSTRACT            = "abstract";
    private static final String TAG_ACCEPTED            = "accepted";
    private static final String TAG_AFF                 = "aff";
    private static final String TAG_ARTICLE_ID          = "article-id";
    private static final String TAG_ARTICLE_META        = "article-meta";
    private static final String TAG_ARTICLE_TITLE       = "article-title";
    private static final String TAG_AUTHOR              = "author";
    private static final String TAG_CONTRIB             = "contrib";
    private static final String TAG_CONTRIB_GROUP       = "contrib-group";
    private static final String TAG_CONTRIB_TYPE        = "contrib-type";
    private static final String TAG_DATE                = "date";
    private static final String TAG_DATE_TYPE           = "date-type";
    private static final String TAG_DAY                 = "day";
    private static final String TAG_EDITOR              = "editor";
    private static final String TAG_EMAIL               = "email";
    private static final String TAG_FPAGE               = "fpage";
    private static final String TAG_FRONT               = "front";
    private static final String TAG_HISTORY             = "history";
    private static final String TAG_ISSN                = "issn";
    private static final String TAG_ISSUE               = "issue";
    private static final String TAG_JOURNAL_META        = "journal-meta";
    private static final String TAG_JOURNAL_TITLE       = "journal-title";
    private static final String TAG_JOURNAL_TITLE_GROUP = "journal-title-group";
    private static final String TAG_KWD                 = "kwd";
    private static final String TAG_KWD_GROUP           = "kwd-group";
    private static final String TAG_LPAGE               = "lpage";
    private static final String TAG_MONTH               = "month";
    private static final String TAG_P                   = "p";
    private static final String TAG_PPUB                = "ppub";
    private static final String TAG_PUB_DATE            = "pub-date";
    private static final String TAG_PUB_ID_TYPE         = "pub-id-type";
    private static final String TAG_PUB_TYPE            = "pub-type";
    private static final String TAG_PUBLISHER           = "publisher";
    private static final String TAG_PUBLISHER_NAME      = "publisher-name";
    private static final String TAG_RECEIVED            = "received";
    private static final String TAG_REVISED             = "revised";
    private static final String TAG_STRING_NAME         = "string-name";
    private static final String TAG_TITLE_GROUP         = "title-group";
    private static final String TAG_VOLUME              = "volume";
    private static final String TAG_YEAR                = "year";
       
}
