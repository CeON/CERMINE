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
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.metadata.model.DocumentMetadata;
import pl.edu.icm.cermine.tools.XMLTools;
import pl.edu.icm.cermine.tools.transformers.ModelToModelConverter;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class ParscitToMetadataConverter implements ModelToModelConverter<Element, DocumentMetadata> {
   
    @Override
    public DocumentMetadata convert(Element source, Object... hints) throws TransformationException {
        try {
            DocumentMetadata metadata = new DocumentMetadata();
            
            convertTitle(source, metadata);
            convertAbstract(source, metadata);
            convertAuthors(source, metadata);
            convertEmails(source, metadata);
            convertAffiliations(source, metadata);

            return metadata;
        } catch (JDOMException ex) {
            throw new TransformationException(ex);
        }
    }

    private void convertTitle(Element source, DocumentMetadata metadata) throws JDOMException {
        XPath xpath = XPath.newInstance("//algorithm[@name='ParsHed']//title");
        metadata.setTitle(selectWithMaxConfidence(xpath.selectNodes(source)));
    }

    private void convertAbstract(Element source, DocumentMetadata metadata) throws JDOMException {
        XPath xpath = XPath.newInstance("//algorithm[@name='ParsHed']//abstract");
        metadata.setAbstrakt(selectWithMaxConfidence(xpath.selectNodes(source)));
    }
    
    private void convertAuthors(Element source, DocumentMetadata metadata) throws JDOMException {
        XPath xpath = XPath.newInstance("//algorithm[@name='ParsHed']//author");
        List authors = xpath.selectNodes(source);
        for (Object a : authors) {
            Element author = (Element) a;
            metadata.addAuthor(XMLTools.getTextContent(author), new ArrayList<String>());
        }
    }
    
    private void convertEmails(Element source, DocumentMetadata metadata) throws JDOMException {
        XPath xpath = XPath.newInstance("//algorithm[@name='ParsHed']//email");
        List authors = xpath.selectNodes(source);
        for (Object a : authors) {
            Element author = (Element) a;
            metadata.addEmail(XMLTools.getTextContent(author));
        }
    }
    
    private void convertAffiliations(Element source, DocumentMetadata metadata) throws JDOMException {
        XPath xpath = XPath.newInstance("//algorithm[@name='ParsHed']//affiliation");
        List affs = xpath.selectNodes(source);
        for (Object a : affs) {
            Element affiliation = (Element)a;
            metadata.addAffiliationToAllAuthors(XMLTools.getTextContent(affiliation));
        }
    }

    private String selectWithMaxConfidence(List elements) {
        double maxConf = 0;
        String content = null;
        for (Object element: elements) {
            Element node = (Element) element;
            if (content == null) {
                content = XMLTools.getTextContent(node);
            }
            if (node.getAttribute("confidence") != null) {
                double conf = Double.valueOf(node.getAttributeValue("confidence"));
                if (conf > maxConf) {
                    maxConf = conf;
                    content = XMLTools.getTextContent(node);
                }
            }
        }
        return content;
    }
    
    @Override
    public List<DocumentMetadata> convertAll(List<Element> source, Object... hints) throws TransformationException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
