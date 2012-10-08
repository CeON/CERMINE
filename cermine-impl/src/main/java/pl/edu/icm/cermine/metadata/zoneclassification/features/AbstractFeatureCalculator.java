package pl.edu.icm.cermine.metadata.zoneclassification.features;

import java.util.ArrayList;
import java.util.List;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;

public abstract class AbstractFeatureCalculator<S,T> extends FeatureCalculator<S, T> {
	
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
