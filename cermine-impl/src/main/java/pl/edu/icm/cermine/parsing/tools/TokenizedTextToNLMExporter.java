package pl.edu.icm.cermine.parsing.tools;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;

import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.parsing.model.Token;


public abstract class TokenizedTextToNLMExporter {

	// Removes trailing commas from the tagged parts
	public static void enhanceElement(Element element) throws TransformationException {
		@SuppressWarnings("unchecked")
		List<Object> oldContent = element.getContent();
		List<Object> newContent = new ArrayList<Object>();
		for (Object subobject : oldContent) {
			newContent.add(subobject);
			if (subobject instanceof Element) { 
				Element subelement = (Element)subobject;
				if (!subelement.getChildren().isEmpty()) {
					throw new TransformationException("This function is not suitable for " +
							"processing nested subelements.");
				}
				String subtext = subelement.getText();
				if (subtext.endsWith(",")) {
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
	
	public static<L, T extends Token<L>> void addText(Element el, String text, List<T> tokens)
			throws TransformationException {
		
		int lastEnd = 0;
		L lastLabel = null;
		Element currentElement = null;
		
		for (T t : tokens) {
			if (t.getLabel() == null) {
				throw new TransformationException("Token with no label!");
			}
			String textBetween = text.substring(lastEnd, t.getStartIndex());
			if (t.getLabel() != lastLabel) {
				if (currentElement != null) {
					el.addContent(currentElement);
				}
				if (!textBetween.equals("")) {
					el.addContent(textBetween);
				}
				currentElement = new Element(t.getXmlLabelString());
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
