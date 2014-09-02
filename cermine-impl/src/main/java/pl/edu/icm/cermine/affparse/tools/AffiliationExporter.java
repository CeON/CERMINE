package pl.edu.icm.cermine.affparse.tools;

import java.util.List;
import org.jdom.Element;
import pl.edu.icm.cermine.affparse.model.AffiliationToken;
import pl.edu.icm.cermine.affparse.model.AffiliationLabel;
import pl.edu.icm.cermine.exception.AnalysisException;


public class AffiliationExporter {

	public static Element toNLM(String text, List<AffiliationToken> tokens) throws AnalysisException {
		Element aff = new Element(TAG_AFFILIATION);
		
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
				currentElement = new Element(t.getLabel().getTag());
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
				
		return aff;
	}
	
	private static final String TAG_AFFILIATION = "aff";
}
