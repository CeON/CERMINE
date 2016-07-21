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
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.tools.transformers.ModelToFormatWriter;

/**
 * Writes DocumentContentStructure model to NLM format.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class NLMToHTMLWriter implements ModelToFormatWriter<Element> {

    @Override
    public String write(Element object, Object... hints) throws TransformationException {
        StringWriter sw = new StringWriter();
        write(sw, object, hints);
        return sw.toString();
    }

    @Override
    public void write(Writer writer, Element object, Object... hints) throws TransformationException {
        Element html = new Element("html");
        Element body = object.getChild("body");
        if (body != null) {
            List<Element> sections = body.getChildren("sec");
            for (Element section : sections) {
                for (Element el : toHTML(section, 1)) {
                    html.addContent(el);
                }
            }
        }
        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
        try {
            outputter.output(html, writer);
        } catch (IOException ex) {
            throw new TransformationException("", ex);
        }
    }
    
    private List<Element> toHTML(Element section, int level) {
        List<Element> elements = new ArrayList<Element>();
        List<Element> children = section.getChildren();
        for (Element child : children) {
            if ("title".equals(child.getName())) {
                Element element = new Element("H"+level);
                element.setText(child.getText());
                elements.add(element);
            } else if ("p".equals(child.getName())) {
                Element el = new Element("p");
                el.setText(child.getText());
                elements.add(el);
            } else if ("sec".equals(child.getName())) {
                elements.addAll(toHTML(child, level+1));
            }
        }
        return elements;
    }

    @Override
    public String writeAll(List<Element> objects, Object... hints) throws TransformationException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void writeAll(Writer writer, List<Element> objects, Object... hints) throws TransformationException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
