package pl.edu.icm.cermine.content.transformers;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
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
 * @author krusek
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
}
