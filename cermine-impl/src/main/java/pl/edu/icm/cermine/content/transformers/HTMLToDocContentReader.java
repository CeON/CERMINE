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
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import pl.edu.icm.cermine.content.model.ContentStructure;
import pl.edu.icm.cermine.content.model.DocumentSection;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.tools.transformers.FormatToModelReader;

/**
 * Reads DocumentContentStructure model from HTML format.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class HTMLToDocContentReader implements FormatToModelReader<ContentStructure> {

    @Override
    public ContentStructure read(String string, Object... hints) throws TransformationException {
        return read(new StringReader(string), hints);
    }

    @Override
    public ContentStructure read(Reader reader, Object... hints) throws TransformationException {
        try {
            Element root = getRoot(reader);
            ContentStructure dcs = new ContentStructure();
            List<Element> elements = root.getChildren();
            if (elements.isEmpty()) {
                return dcs;
            }
        
            int index = 0;
            Element current = elements.get(0);
            while (current != null && isParagraph(current)) {
                current = getNext(++index, elements);
            }
            if (current == null) {
                return dcs;
            }
        
            int nextLevel = getHeaderLevel(current);
            List<Element> els = new ArrayList<Element>();
            while (current != null) {
                if (isHeader(current) && nextLevel == getHeaderLevel(current) && !els.isEmpty()) {
                    DocumentSection n = createPart(els, 1);
                    dcs.addSection(n);
                    els.clear();
                }
                els.add(current);
                current = getNext(++index, elements);
            }
            if (!els.isEmpty()) {
                DocumentSection n = createPart(els, 1);
                dcs.addSection(n);
            }
        
            return dcs;
        } catch (JDOMException ex) {
            throw new TransformationException(ex);
        } catch (IOException ex) {
            throw new TransformationException(ex);
        }
    }
   
    private Element getRoot(Reader reader) throws JDOMException, IOException {
        SAXBuilder saxBuilder = new SAXBuilder("org.apache.xerces.parsers.SAXParser");
        org.jdom.Document dom = saxBuilder.build(reader);
        return dom.getRootElement();
    }
    
    private DocumentSection createPart(List<Element> elements, int level) {
        DocumentSection section = new DocumentSection();
        
        if (elements.isEmpty()) {
            return section;
        }
        
        int index = 0;
        Element current = elements.get(0);
        
        section.setLevel(level);
        section.setTitle(current.getValue());
        current = getNext(++index, elements);
                
        while (current != null && isParagraph(current)) {
            section.addParagraph(current.getValue());
            current = getNext(++index, elements);
        }
        
        if (current == null) {
            return section;
        }
        
        int nextLevel = getHeaderLevel(current);
        List<Element> els = new ArrayList<Element>();
        while (current != null) {
            if (isHeader(current) && nextLevel == getHeaderLevel(current) && !els.isEmpty()) {
                DocumentSection n = createPart(els, level+1);
                section.addSection(n);
                els.clear();
            }
            els.add(current);
            current = getNext(++index, elements);
        }
        
        if (!els.isEmpty()) {
            DocumentSection n = createPart(els, level+1);
            section.addSection(n);
        }
        
        return section;
    }
    
    private boolean isHeader(Element element) {
        return element.getName().toLowerCase().startsWith("h");
    }
    
    private boolean isParagraph(Element element) {
        return element.getName().equals("p");
    }
    
    private int getHeaderLevel(Element element) {
        return Integer.parseInt(element.getName().replaceAll("[^0-9]+", ""));
    }
    
    private Element getNext(int index, List<Element> elements) {
        if (index < elements.size()) {
            return elements.get(index);
        }
        return null;
    }

    @Override
    public List<ContentStructure> readAll(String string, Object... hints) throws TransformationException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<ContentStructure> readAll(Reader reader, Object... hints) throws TransformationException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
