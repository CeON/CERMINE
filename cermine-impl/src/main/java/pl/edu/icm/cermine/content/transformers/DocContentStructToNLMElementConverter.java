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

import java.util.ArrayList;
import java.util.List;
import org.jdom.Element;
import pl.edu.icm.cermine.content.model.ContentStructure;
import pl.edu.icm.cermine.content.model.DocumentSection;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.tools.transformers.ModelToModelConverter;

/**
 * Writes DocumentContentStructure model to NLM format.
 *
 * @author Dominika Tkaczyk
 */
public class DocContentStructToNLMElementConverter implements ModelToModelConverter<ContentStructure, Element> {

    @Override
    public Element convert(ContentStructure source, Object... hints) throws TransformationException {
        Element body = new Element("body");
        body.addContent(toHTML(source));
        addSectionIds(body);
        return body;
    }
    
    private List<Element> toHTML(ContentStructure dcs) {
        List<Element> elements = new ArrayList<Element>();
        for (DocumentSection part : dcs.getSections()) {
            elements.addAll(toHTML(part));
        }
        return elements;
    }
    
    private List<Element> toHTML(DocumentSection part) {
        List<Element> elements = new ArrayList<Element>();
        Element element = new Element("sec");
        element.addContent(toHTMLTitle(part.getTitle()));
        for (String paragraph : part.getParagraphs()) {
            element.addContent(toHTMLParagraph(paragraph));
        }
        for (DocumentSection subpart : part.getSubsections()) {
            element.addContent(toHTML(subpart));
        }
        elements.add(element);
        return elements;
    }

    public Element toHTMLTitle(String header) {
        Element element = new Element("title");
        element.setText(header+"\n");
        return element;
    }
    
    public Element toHTMLParagraph(String paragraph) {
        Element element = new Element("p");
        element.setText(paragraph+"\n");
        return element;
    }
  
    private void addSectionIds(Element element) {
        List<Element> sections = element.getChildren("sec");
        int index = 1;
        for (Element section : sections) {
            addSectionIds(section, "", index++);
        }
    }
    
    private void addSectionIds(Element element, String prefix, int index) {
        if (!prefix.isEmpty()) {
            prefix += "-";
        }
        String id = prefix + index;
        element.setAttribute("id", id);
        List<Element> sections = element.getChildren("sec");
        int i = 1;
        for (Element section : sections) {
            addSectionIds(section, id, i++);
        }
    }

    @Override
    public List<Element> convertAll(List<ContentStructure> source, Object... hints) throws TransformationException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
