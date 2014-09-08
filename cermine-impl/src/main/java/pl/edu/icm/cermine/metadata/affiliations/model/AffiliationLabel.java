package pl.edu.icm.cermine.metadata.affiliations.model;

import java.util.HashMap;
import java.util.Map;

import pl.edu.icm.cermine.exception.AnalysisException;

public enum AffiliationLabel {
	INSTITUTION("institution", "INST"),
	ADDRESS("addr-line", "ADDR"),
	COUNTRY("country", "COUN"),
	TEXT("text", "TEXT");

	private final String xmlTag;
	private final String grmmLabel;

	private AffiliationLabel(String tag, String label) {
		this.xmlTag = tag;
		this.grmmLabel = label;
	}

	public String getXmlTag() {
		return xmlTag;
	}

	public String getGrmmLabel() {
		return grmmLabel;
	}

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
		LABEL_MAP.put("TEXT", TEXT);
	}

}
