package pl.edu.icm.cermine.parsing.tools;

import java.util.List;

import org.jdom.Content;
import org.jdom.Element;
import org.jdom.Text;

import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.parsing.model.Token;


/**
 * Parsable string to NLM XML exporter.
 * 
 * @author Bartosz Tarnawski
 */
public abstract class ParsableStringToNLMExporter {
	
	protected static void addElement(Element parent, String tag, String text) {
        Element element = new Element(tag);
        element.addContent(text);
        parent.addContent(element);
	}
	
	public static Content createContent(String text, String tag) {
		if (tag == null) {
			return new Text(text);
		} else {
			Element el = new Element(tag);
			el.addContent(text);
			return el;
		}
	}
	
	/**
	 * Adds the tagged text to the XML Element
	 * 
	 * @param el the element to be processed
	 * @param text the text to be added
	 * @param tokens the tokens with information about text tagging
	 * @throws TransformationException if any token has no label
	 */
	public static<L, T extends Token<L>> void addText(Element el, String text, List<T> tokens)
			throws TransformationException {
		
		int lastEnd = 0;
		L lastLabel = null;
		StringBuilder currentText = null;
		String currentTag = null;
		
		for (T t : tokens) {
			if (t.getLabel() == null) {
				throw new TransformationException("Token with no label!");
			}
			String textBetween = text.substring(lastEnd, t.getStartIndex());
			if (t.getLabel() != lastLabel) {
				if (currentText != null) {
					el.addContent(createContent(currentText.toString(), currentTag));
				}
				if (!textBetween.equals("")) {
					el.addContent(textBetween);
				}
				currentText = new StringBuilder();
				currentTag = t.getXmlTagString();
			} else {
				if (!textBetween.equals("")) {
					currentText.append(textBetween);
				}
			}
			
			currentText.append(text.substring(t.getStartIndex(), t.getEndIndex()));
			lastLabel = t.getLabel();
			lastEnd = t.getEndIndex();
		}
		
        String textBetween = text.substring(lastEnd, text.length());
        if (currentText != null) {
        	el.addContent(createContent(currentText.toString(), currentTag));
        }
        if (!textBetween.equals("")) {
        	el.addContent(textBetween);
        }
	}
}
