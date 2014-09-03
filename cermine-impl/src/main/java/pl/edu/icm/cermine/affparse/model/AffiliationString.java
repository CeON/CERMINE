package pl.edu.icm.cermine.affparse.model;

import org.jdom.Element;

import pl.edu.icm.cermine.affparse.tools.*;
import pl.edu.icm.cermine.exception.AnalysisException;

public class AffiliationString extends TokenizedString<AffiliationLabel, AffiliationToken> {

	public AffiliationString(String text, String label) {
		this.text = AffiliationNormalizer.normalize(text);
		this.tokens = AffiliationTokenizer.tokenize(this.text);	
		this.label = label;
	}

	public AffiliationString(String text) {
		this(text, null);
	}

	@Override
	public void calculateFeatures() {
		AffiliationFeatureExtractor.extractFeatures(this.tokens);
	}
	
	@Override
	public void classify() throws AnalysisException {
		AffiliationCRFTokenClassifier.getInstance().classify(this.tokens);
	}

	@Override
	public Element toNLM() throws AnalysisException {
		return AffiliationExporter.toNLM(label, text, tokens); // TODO FIXME Jeszcze jest jakies ID, ale poki co nie wiem skad ono sie bierze
	}

}
