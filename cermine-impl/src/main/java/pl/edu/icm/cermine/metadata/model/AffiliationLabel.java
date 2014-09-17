package pl.edu.icm.cermine.metadata.model;

import java.util.HashMap;
import java.util.Map;
import pl.edu.icm.cermine.exception.AnalysisException;

/**
 * Document affiliation label. It is represented by a pair: XML tag used in the NML format
 * and the GRMM label used in the ACRF.
 * 
 * @author Bartosz Tarnawski
 */
public enum AffiliationLabel {
	INSTITUTION("institution", "INST"),
	ADDRESS("addr-line", "ADDR"),
	COUNTRY("country", "COUN"),
	AUTHOR("author", "AUTH"),
	TEXT("text", "TEXT");

	private final String xmlTag;
	private final String grmmLabel;

	private AffiliationLabel(String xmlTag, String grmmLabel) {
		this.xmlTag = xmlTag;
		this.grmmLabel = grmmLabel;
	}

	/**
	 * @return appropriate XML tag or null for text content
	 */
	public String getXmlTag() {
		if (xmlTag.equals("text")) {
			return null;
		}
		return xmlTag;
	}

	/**
	 * @return appropriate GRMM label
	 */
	public String getGrmmLabel() {
		return grmmLabel;
	}

	/**
	 * @param grmmLabel
	 * @return the AffiliationLabel with the given GRMM label
	 * @throws AnalysisException when no AffiliationLabel has the given GRMM label
	 */
	public static AffiliationLabel createLabel(String grmmLabel) throws AnalysisException {
		if (!LABEL_MAP.containsKey(grmmLabel)) {
			throw new AnalysisException("No shuch label: " + grmmLabel);
		}
		return LABEL_MAP.get(grmmLabel);
	}

	private static final Map<String, AffiliationLabel> LABEL_MAP;

	static {
		LABEL_MAP = new HashMap<String, AffiliationLabel>();
		LABEL_MAP.put("INST", INSTITUTION);
		LABEL_MAP.put("ADDR", ADDRESS);
		LABEL_MAP.put("COUN", COUNTRY);
		LABEL_MAP.put("AUTH", AUTHOR);
		LABEL_MAP.put("TEXT", TEXT);
	}

}
