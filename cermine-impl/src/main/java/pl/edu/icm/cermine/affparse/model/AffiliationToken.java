package pl.edu.icm.cermine.affparse.model;

public class AffiliationToken extends Token<AffiliationLabel> {
	public AffiliationToken(String text, int startIndex, int endIndex, AffiliationLabel label) {
		super(text, startIndex, endIndex, label);
	}
	
	public AffiliationToken(String text, int startIndex, int endIndex) {
		super(text, startIndex, endIndex);
	}
}
