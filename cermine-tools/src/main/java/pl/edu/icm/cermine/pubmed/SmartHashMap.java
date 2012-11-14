package pl.edu.icm.cermine.pubmed;

import java.util.HashMap;
import java.util.List;

import pl.edu.icm.cermine.structure.model.BxZoneLabel;

public class SmartHashMap extends HashMap<String, BxZoneLabel> {
	private static final long serialVersionUID = 74383628471L;

	public SmartHashMap putIf(String string, BxZoneLabel label) {
		if(string != null && !string.isEmpty()) {
			put(string, label);
		}
		return this;
	}
	
	public SmartHashMap putIf(List<String> strings, BxZoneLabel label) {
		for(String string: strings) {
			putIf(string, label);
		}
		return this;
	}
	
}
