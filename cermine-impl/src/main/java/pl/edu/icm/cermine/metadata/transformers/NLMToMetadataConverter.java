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

package pl.edu.icm.cermine.metadata.transformers;

import java.util.ArrayList;
import java.util.List;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.metadata.model.DocumentDate;
import pl.edu.icm.cermine.metadata.model.DocumentMetadata;
import pl.edu.icm.cermine.tools.XMLTools;
import pl.edu.icm.cermine.tools.transformers.ModelToModelConverter;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class NLMToMetadataConverter implements ModelToModelConverter<Element, DocumentMetadata> {
   
    @Override
    public DocumentMetadata convert(Element source, Object... hints) throws TransformationException {
        try {
            DocumentMetadata metadata = new DocumentMetadata();
            
            convertTitle(source, metadata);
            convertAbstract(source, metadata);
            convertJournal(source, metadata);
            convertVolume(source, metadata);
            convertIssue(source, metadata);
            convertPages(source, metadata);
            convertPublisher(source, metadata);
            convertKeywords(source, metadata);
            convertIds(source, metadata);
            convertDates(source, metadata);
            convertAuthors(source, metadata);
            convertEditors(source, metadata);
            convertAffiliations(source, metadata);
           
            return metadata;
        } catch (JDOMException ex) {
            throw new TransformationException(ex);
        }
    }

    private void convertTitle(Element source, DocumentMetadata metadata) throws JDOMException {
        XPath xpath = XPath.newInstance("./article-meta//article-title");
        Element title = (Element)xpath.selectSingleNode(source);
        if (title != null) {
            metadata.setTitle(XMLTools.getTextContent(title));
        }
    }

    private void convertAbstract(Element source, DocumentMetadata metadata) throws JDOMException {
        XPath xpath = XPath.newInstance("./article-meta//abstract");
        Element abstrakt = (Element)xpath.selectSingleNode(source);
        if (abstrakt != null) {
            metadata.setAbstrakt(XMLTools.getTextContent(abstrakt));
        }
    }
    
    private void convertJournal(Element source, DocumentMetadata metadata) throws JDOMException {
        XPath xpath = XPath.newInstance("./journal-meta//journal-title");
        Element journal = (Element)xpath.selectSingleNode(source);
        if (journal != null) {
            metadata.setJournal(XMLTools.getTextContent(journal));
        }
            
        xpath = XPath.newInstance("./journal-meta//issn[@pub-type='ppub']");
        Element journalISSN = (Element)xpath.selectSingleNode(source);
        if (journalISSN != null) {
            metadata.setJournalISSN(XMLTools.getTextContent(journalISSN));
        }
    }
    
    private void convertVolume(Element source, DocumentMetadata metadata) throws JDOMException {
        XPath xpath = XPath.newInstance("./article-meta//volume");
        Element volume = (Element)xpath.selectSingleNode(source);
        if (volume != null) {
            metadata.setVolume(XMLTools.getTextContent(volume));
        }
    }

    private void convertIssue(Element source, DocumentMetadata metadata) throws JDOMException {
        XPath xpath = XPath.newInstance("./article-meta//issue");
        Element issue = (Element)xpath.selectSingleNode(source);
        if (issue != null) {
            metadata.setIssue(XMLTools.getTextContent(issue));
        }
    }

    private void convertPages(Element source, DocumentMetadata metadata) throws JDOMException {
        XPath xpath = XPath.newInstance("./article-meta//fpage");
        Element fPage = (Element)xpath.selectSingleNode(source);
        String fp = "";
        if (fPage != null) {
            fp = XMLTools.getTextContent(fPage);
        }
        xpath = XPath.newInstance("./article-meta//lpage");
        Element lPage = (Element)xpath.selectSingleNode(source);
        String lp = "";
        if (lPage != null) {
            lp = XMLTools.getTextContent(lPage);
        }
        metadata.setPages(fp, lp);
    }    
    
    private void convertPublisher(Element source, DocumentMetadata metadata) throws JDOMException {
        XPath xpath = XPath.newInstance("./journal-meta//publisher-name");
        Element publisher = (Element)xpath.selectSingleNode(source);
        if (publisher != null) {
            metadata.setPublisher(XMLTools.getTextContent(publisher));
        }
    }
    
    private void convertKeywords(Element source, DocumentMetadata metadata) throws JDOMException {
        XPath xpath = XPath.newInstance("./article-meta//kwd-group/kwd");
        List keywords = xpath.selectNodes(source);
        for (Object keyword : keywords) {
            metadata.addKeyword(XMLTools.getTextContent((Element)keyword));
        }
    }
    
    private void convertIds(Element source, DocumentMetadata metadata) throws JDOMException {
        XPath xpath = XPath.newInstance("./article-meta//article-id");
        List ids = xpath.selectNodes(source);
        for (Object i : ids) {
            Element id = (Element) i;
            metadata.addId(id.getAttributeValue("pub-id-type"), XMLTools.getTextContent(id));
        }
    }
    
    private void convertDates(Element source, DocumentMetadata metadata) throws JDOMException {
        XPath xpath = XPath.newInstance("./article-meta//pub-date[@pub-type='ppub']");
        Element pubDate = (Element)xpath.selectSingleNode(source);
        if (pubDate == null) {
            xpath = XPath.newInstance("./article-meta//pub-date");
            pubDate = (Element)xpath.selectSingleNode(source);
        }
        convertDate(DocumentDate.DATE_PUBLISHED, pubDate, metadata);
        xpath = XPath.newInstance("./article-meta//history/date[@date-type='received']");
        convertDate(DocumentDate.DATE_RECEIVED, (Element)xpath.selectSingleNode(source), metadata);
        xpath = XPath.newInstance("./article-meta//history/date[@date-type='accepted']");
        convertDate(DocumentDate.DATE_ACCEPTED, (Element)xpath.selectSingleNode(source), metadata);
        xpath = XPath.newInstance("./article-meta//history/date[@date-type='revised']");
        convertDate(DocumentDate.DATE_REVISED, (Element)xpath.selectSingleNode(source), metadata);
    }
    
    private void convertDate(String type, Element source, DocumentMetadata metadata) {
        if (source == null) {
            return;
        }
        String day = null;
        if (source.getChild("day") != null) {
            day = XMLTools.getTextContent(source.getChild("day"));
        }
        String month = null;
        if (source.getChild("month") != null) {
            month = XMLTools.getTextContent(source.getChild("month"));
        }
        String year = null;
        if (source.getChild("year") != null) {
            year = XMLTools.getTextContent(source.getChild("year"));
        }
        metadata.setDate(type, day, month, year);
    }
    
    private void convertAuthors(Element source, DocumentMetadata metadata) throws JDOMException {
        XPath xpath = XPath.newInstance("./article-meta//contrib[@contrib-type='author']");
        List authors = xpath.selectNodes(source);
        for (Object a : authors) {
            Element author = (Element)a;
            String name = author.getChildText("string-name");
            if (name == null && author.getChild("name") != null) {
                StringBuilder sb = new StringBuilder();
                for (Object gn : author.getChild("name").getChildren("given-names")){
                    sb.append(XMLTools.getTextContent((Element)gn));
                    sb.append(" ");
                }
                sb.append(author.getChild("name").getChildText("surname"));
                name = sb.toString().trim();
            }
            if (name == null) {
                name = XMLTools.getTextContent(author.getChild("collab"));
            }
            List<String> aIs = new ArrayList<String>();
            for (Object af : author.getChildren("xref")) {
                Element aff = (Element)af;
                if ("aff".equals(aff.getAttributeValue("ref-type"))) {
                    aIs.add(aff.getAttributeValue("rid"));
                }
            }
            xpath = XPath.newInstance(".//email");
            if (xpath.selectSingleNode(author) != null) {
                String email = XMLTools.getTextContent((Element)xpath.selectSingleNode(author));
                metadata.addAuthor(name, aIs, email);
            } else {
                metadata.addAuthor(name, aIs);
            }
        }
    }
    
    private void convertEditors(Element source, DocumentMetadata metadata) throws JDOMException {
        XPath xpath = XPath.newInstance("./article-meta//contrib[@contrib-type='editor']");
        List authors = xpath.selectNodes(source);
        for (Object a : authors) {
            Element author = (Element)a;
            String name = author.getChildText("string-name");
            if (name == null) {
                XPath xpath1 = XPath.newInstance("./name/given-names");
                XPath xpath2 = XPath.newInstance("./name/surname");
                name = XMLTools.getTextContent((Element)xpath1.selectSingleNode(author)) + " "
                        + XMLTools.getTextContent((Element)xpath2.selectSingleNode(author));
            }
            metadata.addEditor(name);
        }
    }
    
    private void convertAffiliations(Element source, DocumentMetadata metadata) throws JDOMException {
        XPath xpath = XPath.newInstance("./article-meta//aff");
        List affs = xpath.selectNodes(source);
        for (Object a : affs) {
            Element affiliation = (Element)a;
            String id = affiliation.getAttributeValue("id");
            String label = affiliation.getChildText("label");
            String text = XMLTools.getTextContent(affiliation);
            if (text != null && label != null && text.startsWith(label)) {
                text = text.substring(label.length());
            }
            if (text != null) {
                text = text.replaceAll("\\s+", " ").trim();
            }
            if (id == null) {
                metadata.addAffiliationToAllAuthors(text);
            } else {
                metadata.setAffiliationByIndex(id, text);
            }
        }
    }
    
    @Override
    public List<DocumentMetadata> convertAll(List<Element> source, Object... hints) throws TransformationException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
