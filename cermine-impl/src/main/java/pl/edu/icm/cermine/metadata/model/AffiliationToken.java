package pl.edu.icm.cermine.metadata.model;

import pl.edu.icm.cermine.parsing.model.Token;

/**
 * Document affiliation token. Represents an atomic part of affiliation. It has a label which
 * corresponds to its type (institution, addr-line, country, author or text).
 * 
 * @author Bartosz Tarnawski
 */
public class AffiliationToken extends Token<AffiliationLabel> {
	
	/**
	 * @param text the normalized (ASCII) string corresponding to the
	 * substring(startIndex, endIndex) of the DocumentAffiliation the token belongs to
	 * @param startIndex
	 * @param endIndex
	 * @param label may be null if the token is not classified yet
	 */
	public AffiliationToken(String text, int startIndex, int endIndex, AffiliationLabel label) {
		super(text, startIndex, endIndex, label);
	}
	
	/**
	 * @param text the normalized (ASCII) string corresponding to the
	 * substring(startIndex, endIndex) of the DocumentAffiliation the token belongs to
	 * @param startIndex
	 * @param endIndex
	 */
	public AffiliationToken(String text, int startIndex, int endIndex) {
		super(text, startIndex, endIndex);
	}
	
	public AffiliationToken(String text) {
		super(text);
	}

	public AffiliationToken() {
		super();
	}

	@Override
	public String getGrmmLabelString() {
		if (label == null) {
			return AffiliationLabel.TEXT.getGrmmLabel();
		} else {
			return label.getGrmmLabel();
		}
	}

	@Override
	public String getXmlTagString() {
		if (label == null) {
			return AffiliationLabel.TEXT.getXmlTag();
		} else {
			return label.getXmlTag();
		}
	}
}
