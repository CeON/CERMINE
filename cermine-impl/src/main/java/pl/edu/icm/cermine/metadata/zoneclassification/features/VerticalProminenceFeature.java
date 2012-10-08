package pl.edu.icm.cermine.metadata.zoneclassification.features;

import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;

/**
 * @author Pawel Szostek (p.szostek@icm.edu.pl)
 */

public class VerticalProminenceFeature implements
		FeatureCalculator<BxZone, BxPage> {

	private static String featureName = "VerticalProminence";
	private static Double ZONE_EPSILON = 1.0;
	@Override
	public String getFeatureName() {
		return featureName;
	}
	
	@Override
	public double calculateFeatureValue(BxZone zone, BxPage page) {		
		if(page.getZones().size() == 1)
			return 0.0; //there is only one zone - no prominence can be measured
		BxZone prevZone = zone.getPrev();
		BxZone nextZone = zone.getNext();
		while(true) {
			if(prevZone == null) { //given zone is the first one in the set - there is none before it
				if(nextZone == null) {
					return page.getHeight() - zone.getHeight();
				} else if(nextZone.getY() - (zone.getY() + zone.getHeight()) > ZONE_EPSILON) {
					return nextZone.getY() - (zone.getY() + zone.getHeight());
				} else {
					nextZone = nextZone.getNext();
					continue;
				}
			} else if(nextZone == null) { //given zone is the last one in the set - there is none after it
				if(zone.getY() - (prevZone.getY() + prevZone.getHeight()) > ZONE_EPSILON) {
					return zone.getY() - (prevZone.getY()+prevZone.getHeight());
				} else {
					 prevZone = prevZone.getPrev();
					 continue;
				}
			} else { //there is a zone before and after the given one
				if(zone.getY() - (prevZone.getY() + prevZone.getHeight()) > ZONE_EPSILON) { //previous zone lies in the same column
					if(nextZone.getY() - (zone.getY() + zone.getHeight()) > ZONE_EPSILON) { //next zone lies in the same column
						return nextZone.getY() - (prevZone.getY()+prevZone.getHeight()) - zone.getHeight();
					} else {
						nextZone = nextZone.getNext();
						continue;
					}
				} else {
					if(nextZone.getY() - (zone.getY() + zone.getHeight()) > ZONE_EPSILON) {
						prevZone = prevZone.getPrev();
						continue;
					} else { //neither previous zone nor next zone lies in natural geometrical order
						prevZone = prevZone.getPrev();
						nextZone = nextZone.getNext();
						continue;
					}
				}
			}
		}
	}
};
