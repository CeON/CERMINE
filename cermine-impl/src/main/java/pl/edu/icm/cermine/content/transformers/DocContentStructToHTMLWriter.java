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

package pl.edu.icm.cermine.content.transformers;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import pl.edu.icm.cermine.content.model.DocumentContentStructure;
import pl.edu.icm.cermine.content.model.DocumentHeader;
import pl.edu.icm.cermine.content.model.DocumentParagraph;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.tools.transformers.ModelToFormatWriter;

/**
 * Writes DocumentContentStructure model to HTML format.
 *
 * @author Dominika Tkaczyk
 */
public class DocContentStructToHTMLWriter implements ModelToFormatWriter<DocumentContentStructure> {

    @Override
    public String write(DocumentContentStructure object, Object... hints) throws TransformationException {
        StringWriter sw = new StringWriter();
        write(sw, object, hints);
        return sw.toString();
    }

    @Override
    public void write(Writer writer, DocumentContentStructure object, Object... hints) throws TransformationException {
        try {
            Element element = toHTML(object);
            XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
            outputter.output(element, writer);
        } catch (IOException ex) {
            throw new TransformationException(ex);
        }
    }
    
    private Element toHTML(DocumentContentStructure dcs) {
        Element element = new Element("html");
        if (dcs.getHeader() != null) {
            element.addContent(toHTML(dcs.getHeader()));
        }
        for (DocumentParagraph paragraph : dcs.getParagraphs()) {
            element.addContent(toHTML(paragraph));
        }
        for (DocumentContentStructure part : dcs.getParts()) {
            element.addContent(toHTML(part).cloneContent());
        }
        return element;
    }

    public Element toHTML(DocumentHeader header) {
        Element element = new Element("H" + header.getLevel());
        element.setText(header.getText());
        return element;
    }
    
    public Element toHTML(DocumentParagraph paragraph) {
        Element element = new Element("p");
        element.setText(paragraph.getText());
        return element;
    }

    @Override
    public String writeAll(List<DocumentContentStructure> objects, Object... hints) throws TransformationException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void writeAll(Writer writer, List<DocumentContentStructure> objects, Object... hints) throws TransformationException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
