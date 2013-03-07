package pl.edu.icm.cermine.content.transformers;

import java.util.ArrayList;
import java.util.List;
import org.jdom.Element;
import pl.edu.icm.cermine.content.model.DocumentContentStructure;
import pl.edu.icm.cermine.content.model.DocumentHeader;
import pl.edu.icm.cermine.content.model.DocumentParagraph;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.tools.transformers.ModelToModelConverter;

/**
 * Writes DocumentContentStructure model to NLM format.
 *
 * @author Dominika Tkaczyk
 */
public class DocContentStructToNLMElementConverter implements ModelToModelConverter<DocumentContentStructure, Element> {

    @Override
    public Element convert(DocumentContentStructure source, Object... hints) throws TransformationException {
        Element body = new Element("body");
        body.addContent(toHTML(source));
        addSectionIds(body);
        return body;
    }
    
    private List<Element> toHTML(DocumentContentStructure dcs) {
        List<Element> elements = new ArrayList<Element>();
        if (dcs.getHeader() == null) {
            for (DocumentContentStructure part : dcs.getParts()) {
                elements.addAll(toHTML(part));
            }
        } else {
            Element element = new Element("sec");
            element.addContent(toHTML(dcs.getHeader()));
            for (DocumentParagraph paragraph : dcs.getParagraphs()) {
                element.addContent(toHTML(paragraph));
            }
            for (DocumentContentStructure part : dcs.getParts()) {
                element.addContent(toHTML(part));
            }
            elements.add(element);
        }
        return elements;
    }

    public Element toHTML(DocumentHeader header) {
        Element element = new Element("title");
        element.setText(header.getText()+"\n");
        return element;
    }
    
    public Element toHTML(DocumentParagraph paragraph) {
        Element element = new Element("p");
        element.setText(paragraph.getText()+"\n");
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
        String id = prefix + String.valueOf(index);
        element.setAttribute("id", id);
        List<Element> sections = element.getChildren("sec");
        int i = 1;
        for (Element section : sections) {
            addSectionIds(section, id, i++);
        }
    }

    @Override
    public List<Element> convertAll(List<DocumentContentStructure> source, Object... hints) throws TransformationException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
