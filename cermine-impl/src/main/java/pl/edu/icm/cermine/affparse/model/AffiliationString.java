package pl.edu.icm.cermine.affparse.model;

import org.jdom.Element;

import pl.edu.icm.cermine.affparse.tools.*;
import pl.edu.icm.cermine.exception.AnalysisException;

public class AffiliationString extends TokenizedString<AffiliationLabel, AffiliationToken> {

	public AffiliationString(String text, String label) {
		this.text = new AffiliationNormalizer().normalize(text);
		this.tokens = new AffiliationTokenizer().tokenize(this.text);	
		this.label = label;
	}

	public AffiliationString(String text) {
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
