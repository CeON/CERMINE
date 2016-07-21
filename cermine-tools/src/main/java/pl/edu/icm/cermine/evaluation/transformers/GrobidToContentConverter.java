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
import pl.edu.icm.cermine.content.model.ContentStructure;
import pl.edu.icm.cermine.content.model.DocumentSection;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.tools.XMLTools;
import pl.edu.icm.cermine.tools.transformers.ModelToModelConverter;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class GrobidToContentConverter implements ModelToModelConverter<Element, ContentStructure> {

    @Override
    public ContentStructure convert(Element source, Object... hints) throws TransformationException {
        try {
            ContentStructure struct = new ContentStructure();
            
            if (source == null) {
                return struct;
            }
            
            XPath xpath = XPath.newInstance(".//x:div/x:head");
            xpath.addNamespace("x", source.getNamespaceURI());
            for (Object secElem : xpath.selectNodes(source)) {
                Element sectionNode = (Element) secElem;
                DocumentSection section = new DocumentSection();
                section.setTitle(XMLTools.getTextContent(sectionNode));
                struct.addSection(section);
            }
            return struct;
        } catch (JDOMException ex) {
            throw new TransformationException(ex);
        }
    }

    @Override
    public List<ContentStructure> convertAll(List<Element> source, Object... hints) throws TransformationException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
