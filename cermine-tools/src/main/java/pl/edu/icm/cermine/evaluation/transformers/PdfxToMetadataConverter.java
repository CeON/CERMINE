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
public class PdfxToMetadataConverter implements ModelToModelConverter<Element, DocumentMetadata> {
   
    @Override
    public DocumentMetadata convert(Element source, Object... hints) throws TransformationException {
        try {
            DocumentMetadata metadata = new DocumentMetadata();
            
            convertTitle(source, metadata);
            convertAbstract(source, metadata);
            convertDoi(source, metadata);
            convertEmails(source, metadata);

            return metadata;
        } catch (JDOMException ex) {
            throw new TransformationException(ex);
        }
    }

    private void convertTitle(Element source, DocumentMetadata metadata) throws JDOMException {
        XPath xpath = XPath.newInstance(".//article-title[@class='DoCO:Title']");
        Element title = (Element)xpath.selectSingleNode(source);
        if (title != null) {
            metadata.setTitle(XMLTools.getTextContent(title));
        }
    }

    private void convertAbstract(Element source, DocumentMetadata metadata) throws JDOMException {
        XPath xpath = XPath.newInstance(".//abstract[@class='DoCO:Abstract']");
        Element abstrakt = (Element)xpath.selectSingleNode(source);
        if (abstrakt != null) {
            metadata.setAbstrakt(XMLTools.getTextContent(abstrakt));
        }
    }

    private void convertDoi(Element source, DocumentMetadata metadata) throws JDOMException {
        XPath xpath = XPath.newInstance("/pdfx/meta/doi");
        Element doi = (Element)xpath.selectSingleNode(source);
        if (doi != null) {
            metadata.addId(DocumentMetadata.ID_DOI, XMLTools.getTextContent(doi));
        }
    }
    
    private void convertEmails(Element source, DocumentMetadata metadata) throws JDOMException {
        XPath xpath = XPath.newInstance("//email");
        List emailElems = xpath.selectNodes(source);
        for (Object emailElem: emailElems) {
            Element email = (Element) emailElem;
            metadata.addEmail(XMLTools.getTextContent(email));
        }
    }
    
    @Override
    public List<DocumentMetadata> convertAll(List<Element> source, Object... hints) throws TransformationException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
