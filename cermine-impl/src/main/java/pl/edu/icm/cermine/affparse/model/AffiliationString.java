package pl.edu.icm.cermine.affparse.model;

import org.jdom.Element;

import pl.edu.icm.cermine.affparse.tools.*;

public class AffiliationString extends TokenizedString<AffiliationLabel, AffiliationToken> {

	public AffiliationString(String text) {
		this.text = AffiliationNormalizer.normalize(text);
		this.tokens = AffiliationTokenizer.tokenize(this.text);	
	}

	@Override
	public Element toNLM() {
		return AffiliationExporter.toNLM(text, tokens); // TODO FIXME Jeszcze jest jakies ID, ale poki co nie wiem skad ono sie bierze
	}

}
