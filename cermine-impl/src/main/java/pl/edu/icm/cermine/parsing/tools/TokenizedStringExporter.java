package pl.edu.icm.cermine.parsing.tools;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;

import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.parsing.model.Token;


public abstract class TokenizedStringExporter<L, T extends Token<L>> {

	// Removes trailing commas from the tagged parts
	public static void enhanceElement(Element element) {
		@SuppressWarnings("unchecked")
		List<Object> oldContent = element.getContent();
		List<Object> newContent = new ArrayList<Object>();
		for (Object subobject : oldContent) {
			newContent.add(subobject);
			// Doesn't look like good OOP but I have no other ideas.
			if (subobject instanceof Element) { 
				Element subelement = (Element)subobject;
				String subtext = subelement.getText();
				if (subtext.endsWith(",")) {
					// NOTE Assume that there are no nested elements in "subelement"
					// TODO maybe this should perform a test and throw an exception if this is a case
					subelement.setText(subtext.substring(0, subtext.length() - 1));
					newContent.add(",");
				}
			}
		}
		element.removeContent();
		element.setContent(newContent);
	}
	
	protected static void addElement(Element parent, String tag, String text) {
        Element element = new Element(tag);
        element.addContent(text);
        parent.addContent(element);
	}
	
	protected static<L, T extends Token<L>> void addText(Element el, String text, List<T> tokens)
			throws AnalysisException {
		
		int lastEnd = 0;
		L lastLabel = null;
		Element currentElement = null;
		
		for (T t : tokens) {
			if (t.getLabel() == null) {
				throw new AnalysisException("Token with no label!");
			}
			String textBetween = text.substring(lastEnd, t.getStartIndex());
			if (t.getLabel() != lastLabel) {
				if (currentElement != null) {
					el.addContent(currentElement);
				}
				if (!textBetween.equals("")) {
					el.addContent(textBetween);
				}
				currentElement = new Element(t.getLabel().toString());
			} else {
				if (!textBetween.equals("")) {
					currentElement.addContent(textBetween);
				}
			}
			
			currentElement.addContent(text.substring(t.getStartIndex(), t.getEndIndex()));
			lastLabel = t.getLabel();
			lastEnd = t.getEndIndex();
		}
		
        String textBetween = text.substring(lastEnd, text.length());
        if (currentElement != null) {
        	el.addContent(currentElement);
        }
        if (!textBetween.equals("")) {
        	el.addContent(textBetween);
        }
				
		enhanceElement(el);
	}
}
