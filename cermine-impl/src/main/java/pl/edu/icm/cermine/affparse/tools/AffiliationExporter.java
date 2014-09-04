package pl.edu.icm.cermine.affparse.tools;

import java.util.List;

import org.jdom.Element;

import pl.edu.icm.cermine.affparse.model.AffiliationToken;
import pl.edu.icm.cermine.affparse.model.AffiliationLabel;
import pl.edu.icm.cermine.exception.AnalysisException;


public class AffiliationExporter extends
TokenizedStringExporter<AffiliationLabel, AffiliationToken> {
	
	public static Element toNLM(String label, String text, List<AffiliationToken> tokens)
			throws AnalysisException {
		Element aff = new Element(TAG_AFFILIATION);
		if (label != null) {
			aff.setAttribute(ATTR_ID, label);
			addElement(aff, TAG_LABEL, label);
		}
		
		addText(aff, text, tokens);
		return aff;
	}
	
	// NOTE This is a copy-paste from DocumentMetadataToNLMElementConverter
	private static final String TAG_AFFILIATION = "aff";
    private static final String TAG_LABEL = "label";

    private static final String ATTR_ID = "id";
}
