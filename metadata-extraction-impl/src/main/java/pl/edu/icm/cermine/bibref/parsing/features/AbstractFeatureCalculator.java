package pl.edu.icm.cermine.bibref.parsing.features;

import java.util.ArrayList;
import java.util.List;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;

public abstract class AbstractFeatureCalculator<S,T> implements FeatureCalculator<S, T> {
	@Override
	public String getFeatureName() {
		String className = this.getClass().getName();
		String[] classNameParts = className.split("\\.");
		className = classNameParts[classNameParts.length-1];
		
		if(className.contains("Feature")) {
			return className.replace("Feature", "");
		} else {
			return className;
		}
	}
	
	protected static List<BxPage> getOtherPages(BxPage page) {
		List<BxPage> pages = new ArrayList<BxPage>();
		BxPage prevPage = page.getPrev();
		BxPage nextPage = page.getNext();
		
		while(prevPage != null) {
			pages.add(0, prevPage);
			prevPage = prevPage.getPrev();
		}
		while(nextPage != null) {
			pages.add(nextPage);
			nextPage = nextPage.getNext();
		}
		return pages;
	}
	
	protected static List<BxZone> getOtherZones(BxZone zone) {
		List<BxZone> zones = new ArrayList<BxZone>();
		BxZone prevZone = zone.getPrev();
		BxZone nextZone = zone.getNext();
		
		while(prevZone != null) {
			zones.add(0, prevZone);
			prevZone = prevZone.getPrev();
		}
		while(nextZone != null) {
			zones.add(nextZone);
			nextZone = nextZone.getNext();
		}
		return zones;
	}
	
}
