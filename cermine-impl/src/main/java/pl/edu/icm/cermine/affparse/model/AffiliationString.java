package pl.edu.icm.cermine.affparse.model;

import pl.edu.icm.cermine.affparse.tools.*;

public class AffiliationString extends TokenizedString<AffiliationLabel, AffiliationToken> {

	public AffiliationString(String text) {
		this.text = AffiliationNormalizer.normalize(text);
		this.tokens = AffiliationTokenizer.tokenize(this.text);	
	}

}
