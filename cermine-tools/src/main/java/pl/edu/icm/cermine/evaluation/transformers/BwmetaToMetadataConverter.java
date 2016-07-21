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

package pl.edu.icm.cermine.evaluation.transformers;

import java.util.ArrayList;
import java.util.List;
import org.jdom.Attribute;
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
public class BwmetaToMetadataConverter implements ModelToModelConverter<Element, DocumentMetadata> {
   
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
            convertKeywords(source, metadata);
            convertDoi(source, metadata);
            convertYear(source, metadata);
            convertAuthors(source, metadata);
            convertAffiliations(source, metadata);

            return metadata;
        } catch (JDOMException ex) {
            throw new TransformationException(ex);
        }
    }

    private void convertTitle(Element source, DocumentMetadata metadata) throws JDOMException {
        XPath xpath = XPath.newInstance("./x:element/x:name[not(@type)]");
        xpath.addNamespace("x", source.getNamespaceURI());
        Element title = (Element)xpath.selectSingleNode(source);
        if (title != null) {
            metadata.setTitle(XMLTools.getTextContent(title));
        }
    }

    private void convertAbstract(Element source, DocumentMetadata metadata) throws JDOMException {
        XPath xpath = XPath.newInstance("./x:element/x:description[@type='abstract']");
        xpath.addNamespace("x", source.getNamespaceURI());
        Element abstrakt = (Element)xpath.selectSingleNode(source);
        if (abstrakt != null) {
            metadata.setAbstrakt(XMLTools.getTextContent(abstrakt));
        }
    }
    
    private void convertJournal(Element source, DocumentMetadata metadata) throws JDOMException {
        XPath xpath = XPath.newInstance("./x:element/x:structure/x:ancestor[@level='bwmeta1.level.hierarchy_Journal_Journal']/x:name[@type='canonical']");
        xpath.addNamespace("x", source.getNamespaceURI());
        Element journal = (Element)xpath.selectSingleNode(source);
        if (journal != null) {
            metadata.setJournal(XMLTools.getTextContent(journal));
        }
    }
    
    private void convertVolume(Element source, DocumentMetadata metadata) throws JDOMException {
        XPath xpath = XPath.newInstance("./x:element/x:structure/x:ancestor[@level='bwmeta1.level.hierarchy_Journal_Volume']/x:name[@type='canonical']");
        xpath.addNamespace("x", source.getNamespaceURI());
        Element volume = (Element)xpath.selectSingleNode(source);
        if (volume != null) {
            metadata.setVolume(XMLTools.getTextContent(volume));
        }
    }

    private void convertIssue(Element source, DocumentMetadata metadata) throws JDOMException {
        XPath xpath = XPath.newInstance("./x:element/x:structure/x:ancestor[@level='bwmeta1.level.hierarchy_Journal_Number']/x:name[@type='canonical']");
        xpath.addNamespace("x", source.getNamespaceURI());
        Element issue = (Element)xpath.selectSingleNode(source);
        if (issue != null) {
            metadata.setIssue(XMLTools.getTextContent(issue));
        }
    }

    private void convertPages(Element source, DocumentMetadata metadata) throws JDOMException {
        XPath xpath = XPath.newInstance("./x:element/x:structure/x:current[@level='bwmeta1.level.hierarchy_Journal_Article']/@position");
        xpath.addNamespace("x", source.getNamespaceURI());
        Attribute pages = (Attribute)xpath.selectSingleNode(source);
        if (pages != null) {
            String fp = pages.getValue().replaceFirst("-.*", "");
            String lp = pages.getValue().replaceFirst(".*-", "");
            metadata.setPages(fp, lp);
        }
    }
    
    private void convertKeywords(Element source, DocumentMetadata metadata) throws JDOMException {
        XPath xpath = XPath.newInstance("./x:element/x:tags[@type='keyword']/x:tag");
        xpath.addNamespace("x", source.getNamespaceURI());
        List keywords = xpath.selectNodes(source);
        for (Object keyword : keywords) {
            metadata.addKeyword(XMLTools.getTextContent((Element)keyword));
        }
    }
    
    private void convertDoi(Element source, DocumentMetadata metadata) throws JDOMException {
        XPath xpath = XPath.newInstance("./x:element/x:id[@scheme='bwmeta1.id-class.DOI']/@value");
        xpath.addNamespace("x", source.getNamespaceURI());
        Attribute doi = (Attribute)xpath.selectSingleNode(source);
        if (doi != null) {
            metadata.addId(DocumentMetadata.ID_DOI, doi.getValue());
        }
    }
    
    private void convertYear(Element source, DocumentMetadata metadata) throws JDOMException {
        XPath xpath = XPath.newInstance("./x:element/x:structure/x:ancestor[@level='bwmeta1.level.hierarchy_Journal_Year']/x:name[@type='canonical']");
        xpath.addNamespace("x", source.getNamespaceURI());
        Element year = (Element)xpath.selectSingleNode(source);
        if (year != null) {
            metadata.setDate(DocumentDate.DATE_PUBLISHED, null, null, XMLTools.getTextContent(year));
        }
    }
    
    private void convertAuthors(Element source, DocumentMetadata metadata) throws JDOMException {
        XPath xpath = XPath.newInstance("./x:element/x:contributor[@role='author']");
        xpath.addNamespace("x", source.getNamespaceURI());
        List authors = xpath.selectNodes(source);
        for (Object a : authors) {
            Element author = (Element) a;
            XPath nxpath = XPath.newInstance("./x:name[@type='canonical']");
            nxpath.addNamespace("x", author.getNamespaceURI());
            Element authorName = (Element)nxpath.selectSingleNode(author);
            if (authorName == null) {
                continue;
            }
            
            List<String> ais = new ArrayList<String>();
            nxpath = XPath.newInstance("./x:affiliation-ref/@ref");
            nxpath.addNamespace("x", author.getNamespaceURI());
            List affRefs = nxpath.selectNodes(author);
            for (Object affRef : affRefs) {
                ais.add(((Attribute)affRef).getValue());
            }
            
            nxpath = XPath.newInstance("./x:attribute[@key='contact-email']/x:value");
            nxpath.addNamespace("x", author.getNamespaceURI());
            Element email = (Element)nxpath.selectSingleNode(author);
            if (email == null) {
                metadata.addAuthor(XMLTools.getTextContent(authorName), ais);
            } else {
                metadata.addAuthor(XMLTools.getTextContent(authorName), ais, XMLTools.getTextContent(email));
            }
        }
    }
    
    private void convertAffiliations(Element source, DocumentMetadata metadata) throws JDOMException {
        XPath xpath = XPath.newInstance("./x:element/x:affiliation");
        xpath.addNamespace("x", source.getNamespaceURI());
        List affs = xpath.selectNodes(source);
        for (Object a : affs) {
            Element affiliation = (Element)a;
            String id = affiliation.getAttributeValue("id");
            String text = XMLTools.getTextContent(affiliation.getChild("text", source.getNamespace()));
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
