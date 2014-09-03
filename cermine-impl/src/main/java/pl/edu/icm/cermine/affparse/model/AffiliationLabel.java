package pl.edu.icm.cermine.affparse.model;

import java.util.HashMap;
import java.util.Map;

import pl.edu.icm.cermine.exception.AnalysisException;

public enum AffiliationLabel {
	INSTITUTION ("institution"),
	ADDRESS ("addr-line"),
	COUNTRY ("country");
	
	private final String tag;
	
	private AffiliationLabel(String tag) {
		this.tag = tag;
	}
	
	@Override
	public String toString() {
		return tag;
	}
	
	// TODO this should be kind of an overridden abstract method, but it is static :(
	public static AffiliationLabel createLabel(String text) throws AnalysisException {
		if (!LABEL_MAP.containsKey(text)) {
			throw new AnalysisException("No shuch label: " + text);
		}
		return LABEL_MAP.get(text);
	}

	private static final Map<String, AffiliationLabel> LABEL_MAP;
	
	static {
		LABEL_MAP = new HashMap<String, AffiliationLabel>();
		LABEL_MAP.put("INST", INSTITUTION);
		LABEL_MAP.put("ADDR", ADDRESS);
		LABEL_MAP.put("COUN", COUNTRY);
	}

}
