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
public class GrobidToMetadataConverter implements ModelToModelConverter<Element, DocumentMetadata> {
   
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
           
            return metadata;
        } catch (JDOMException ex) {
            throw new TransformationException(ex);
        }
    }

    private void convertTitle(Element source, DocumentMetadata metadata) throws JDOMException {
        XPath xpath = XPath.newInstance(".//x:titleStmt/x:title");
        xpath.addNamespace("x", source.getNamespaceURI());
        Element title = (Element)xpath.selectSingleNode(source);
        if (title != null) {
            metadata.setTitle(XMLTools.getTextContent(title));
        }
    }

    private void convertAbstract(Element source, DocumentMetadata metadata) throws JDOMException {
        XPath xpath = XPath.newInstance(".//x:abstract");
        xpath.addNamespace("x", source.getNamespaceURI());
        Element abstrakt = (Element)xpath.selectSingleNode(source);
        if (abstrakt != null) {
            metadata.setAbstrakt(XMLTools.getTextContent(abstrakt));
        }
    }
    
    private void convertJournal(Element source, DocumentMetadata metadata) throws JDOMException {
        XPath xpath = XPath.newInstance(".//x:monogr/x:title[@level='j' and @type='main']");
        xpath.addNamespace("x", source.getNamespaceURI());
        Element journal = (Element)xpath.selectSingleNode(source);
        if (journal != null) {
            metadata.setJournal(XMLTools.getTextContent(journal));
        }
    }
    
    private void convertVolume(Element source, DocumentMetadata metadata) throws JDOMException {
        XPath xpath = XPath.newInstance(".//x:monogr/x:imprint/x:biblScope[@unit='volume']");
        xpath.addNamespace("x", source.getNamespaceURI());
        Element volume = (Element)xpath.selectSingleNode(source);
        if (volume != null) {
            metadata.setVolume(XMLTools.getTextContent(volume));
        }
    }

    private void convertIssue(Element source, DocumentMetadata metadata) throws JDOMException {
        XPath xpath = XPath.newInstance(".//x:monogr/x:imprint/x:biblScope[@unit='issue']");
        xpath.addNamespace("x", source.getNamespaceURI());
        Element issue = (Element)xpath.selectSingleNode(source);
        if (issue != null) {
            metadata.setIssue(XMLTools.getTextContent(issue));
        }
    }

    private void convertPages(Element source, DocumentMetadata metadata) throws JDOMException {
        XPath xpath = XPath.newInstance(".//x:monogr/x:imprint/x:biblScope[@unit='page']/@from");
        xpath.addNamespace("x", source.getNamespaceURI());
        Attribute fPage = (Attribute)xpath.selectSingleNode(source);
        String fp = "";
        if (fPage != null) {
            fp = fPage.getValue();
        }
        xpath = XPath.newInstance(".//x:monogr/x:imprint/x:biblScope[@unit='page']/@to");
        xpath.addNamespace("x", source.getNamespaceURI());
        Attribute lPage = (Attribute)xpath.selectSingleNode(source);
        String lp = "";
        if (lPage != null) {
            lp = lPage.getValue();
        }
        metadata.setPages(fp, lp);
    }    
    
    private void convertKeywords(Element source, DocumentMetadata metadata) throws JDOMException {
        XPath xpath = XPath.newInstance(".//x:keywords//x:term");
        xpath.addNamespace("x", source.getNamespaceURI());
        List keywords = xpath.selectNodes(source);
        for (Object keyword : keywords) {
            metadata.addKeyword(XMLTools.getTextContent((Element)keyword));
        }
    }
    
    private void convertDoi(Element source, DocumentMetadata metadata) throws JDOMException {
        XPath xpath = XPath.newInstance(".//x:idno[@type='DOI']");
        xpath.addNamespace("x", source.getNamespaceURI());
        Element doi = (Element)xpath.selectSingleNode(source);
        if (doi != null) {
            metadata.addId(DocumentMetadata.ID_DOI, XMLTools.getTextContent(doi));
        }
    }
    
    private void convertYear(Element source, DocumentMetadata metadata) throws JDOMException {
        XPath xpath = XPath.newInstance(".//x:date[@type='published']/@when");
        xpath.addNamespace("x", source.getNamespaceURI());
        Attribute pubYear = (Attribute)xpath.selectSingleNode(source);
        if (pubYear != null) {
            metadata.setDate(DocumentDate.DATE_PUBLISHED, null, null, pubYear.getValue());
        }
    }
    
    private void convertAuthors(Element source, DocumentMetadata metadata) throws JDOMException {
        XPath xpath = XPath.newInstance(".//x:sourceDesc/x:biblStruct//x:author");
        xpath.addNamespace("x", source.getNamespaceURI());
        List authors = xpath.selectNodes(source);
        for (Object a : authors) {
            Element author = (Element)a;
            
            Element persName = author.getChild("persName", source.getNamespace());
            String name = XMLTools.getTextContent(persName);
            if (name == null) {
                name = XMLTools.getTextContent(author.getChild("collab"));
            }
            
            Element emailNode = author.getChild("email", source.getNamespace());
            String email = XMLTools.getTextContent(emailNode);
            
            List<String> affiliations = new ArrayList<String>();
            for (Object ch: author.getChildren("affiliation", source.getNamespace())) {
                affiliations.add(XMLTools.getTextContent((Element) ch));
            }
            
            metadata.addAuthorWithAffiliations(name, email, affiliations);
        }
    }
    
    @Override
    public List<DocumentMetadata> convertAll(List<Element> source, Object... hints) throws TransformationException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
