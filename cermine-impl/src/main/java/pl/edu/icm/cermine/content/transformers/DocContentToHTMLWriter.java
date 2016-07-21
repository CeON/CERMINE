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

package pl.edu.icm.cermine.content.transformers;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import pl.edu.icm.cermine.content.model.ContentStructure;
import pl.edu.icm.cermine.content.model.DocumentSection;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.tools.XMLTools;
import pl.edu.icm.cermine.tools.transformers.ModelToFormatWriter;

/**
 * Writes DocumentContentStructure model to HTML format.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class DocContentToHTMLWriter implements ModelToFormatWriter<ContentStructure> {

    @Override
    public String write(ContentStructure object, Object... hints) throws TransformationException {
        StringWriter sw = new StringWriter();
        write(sw, object, hints);
        return sw.toString();
    }

    @Override
    public void write(Writer writer, ContentStructure object, Object... hints) throws TransformationException {
        try {
            Element element = toHTML(object);
            XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
            outputter.output(element, writer);
        } catch (IOException ex) {
            throw new TransformationException(ex);
        }
    }
    
    private Element toHTML(ContentStructure dcs) {
        Element element = new Element("html");
        for (DocumentSection part : dcs.getSections()) {
            element.addContent(toHTML(part));
        }
        return element;
    }
    
    private List<Element> toHTML(DocumentSection part) {
        List<Element> elements = new ArrayList<Element>();
        elements.add(toHTML(part.getLevel(), part.getTitle()));
        for (String paragraph : part.getParagraphs()) {
            elements.add(toHTML(paragraph));
        }
        for (DocumentSection subpart : part.getSubsections()) {
            elements.addAll(toHTML(subpart));
        }
        return elements;
    }

    public Element toHTML(int level, String header) {
        Element element = new Element("H" + level);
        element.setText(XMLTools.removeInvalidXMLChars(header));
        return element;
    }
    
    public Element toHTML(String paragraph) {
        Element element = new Element("p");
        element.setText(XMLTools.removeInvalidXMLChars(paragraph));
        return element;
    }

    @Override
    public String writeAll(List<ContentStructure> objects, Object... hints) throws TransformationException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void writeAll(Writer writer, List<ContentStructure> objects, Object... hints) throws TransformationException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
