package pl.edu.icm.cermine.metadata.affiliations.model;

import pl.edu.icm.cermine.parsing.model.Token;

public class AffiliationToken extends Token<AffiliationLabel> {
	public AffiliationToken(String text, int startIndex, int endIndex, AffiliationLabel label) {
		super(text, startIndex, endIndex, label);
	}
	
	public AffiliationToken(String text, int startIndex, int endIndex) {
		super(text, startIndex, endIndex);
	}

	public AffiliationToken(String text) {
		super(text);
	}

	public AffiliationToken() {
		super();
	}

	// HACK
	@Override
	public String getValidLabel() {
		return "INST";
	}
}
