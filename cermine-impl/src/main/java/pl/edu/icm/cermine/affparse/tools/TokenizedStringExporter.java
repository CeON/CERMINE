package pl.edu.icm.cermine.affparse.tools;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;

import pl.edu.icm.cermine.affparse.model.AffiliationToken;
import pl.edu.icm.cermine.affparse.model.AffiliationLabel;
import pl.edu.icm.cermine.affparse.model.Token;
import pl.edu.icm.cermine.exception.AnalysisException;


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
        labelElement.addContent(label);
        aff.addContent(labelElement);
	}
	
	public static Element toNLM(String label, String text, List<AffiliationToken> tokens)
			throws AnalysisException {
		Element aff = new Element(TAG_AFFILIATION);
		if (label != null) {
			addLabel(aff, label);
		}
		
		int lastEnd = 0;
		AffiliationLabel lastLabel = null;
		Element currentElement = null;
		
		for (AffiliationToken t : tokens) {
			if (t.getLabel() == null) {
				throw new AnalysisException("Token with no label!");
			}
			String textBetween = text.substring(lastEnd, t.getStartIndex());
			if (t.getLabel() != lastLabel) {
				if (currentElement != null) {
					aff.addContent(currentElement);
				}
				if (!textBetween.equals("")) {
					aff.addContent(textBetween);
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
        	aff.addContent(currentElement);
        }
        if (!textBetween.equals("")) {
        	aff.addContent(textBetween);
        }
				
		enhanceElement(aff);
		return aff;
	}
	
	// NOTE This is a copy-paste from DocumentMetadataToNLMElementConverter
	private static final String TAG_AFFILIATION = "aff";
    private static final String TAG_LABEL = "label";

    private static final String ATTR_ID = "id";
}
