package pl.edu.icm.cermine.metadata.affiliations.model;

import org.jdom.Element;

import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.metadata.affiliations.tools.AffiliationCRFTokenClassifier;
import pl.edu.icm.cermine.metadata.affiliations.tools.AffiliationExporter;
import pl.edu.icm.cermine.metadata.affiliations.tools.AffiliationFeatureExtractor;
import pl.edu.icm.cermine.metadata.affiliations.tools.AffiliationNormalizer;
import pl.edu.icm.cermine.metadata.affiliations.tools.AffiliationTokenizer;
import pl.edu.icm.cermine.parsing.model.TokenizedString;

public class AffiliationTokenizedString extends TokenizedString<AffiliationLabel, AffiliationToken> {

	public AffiliationTokenizedString(String text, String label) {
		this.text = new AffiliationNormalizer().normalize(text);
		this.tokens = new AffiliationTokenizer().tokenize(this.text);	
		this.label = label;
	}

	public AffiliationTokenizedString(String text) {
		this(text, null);
	}

	@Override
	public void calculateFeatures() {
		new AffiliationFeatureExtractor().extractFeatures(this.tokens);
	}
	
	@Override
	public void classify() throws AnalysisException {
		AffiliationCRFTokenClassifier.getInstance().classify(this.tokens);
	}

	@Override
	public Element toNLM() throws AnalysisException {
		return AffiliationExporter.toNLM(label, text, tokens);
	}

}
