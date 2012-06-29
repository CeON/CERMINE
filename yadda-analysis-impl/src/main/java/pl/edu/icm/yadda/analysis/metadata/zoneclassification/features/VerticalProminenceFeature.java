package pl.edu.icm.yadda.analysis.metadata.zoneclassification.features;

import java.util.List;

import pl.edu.icm.yadda.analysis.classification.features.FeatureCalculator;
import pl.edu.icm.yadda.analysis.textr.model.BxPage;
import pl.edu.icm.yadda.analysis.textr.model.BxZone;

/**
 * @author Pawel Szostek (p.szostek@icm.edu.pl)
 */

public class VerticalProminenceFeature implements
		FeatureCalculator<BxZone, BxPage> {

	private static String featureName = "RelativeProminence";

	@Override
	public String getFeatureName() {
		return featureName;
	}
	
	@Override
	public double calculateFeatureValue(BxZone zone, BxPage page) {		
		if(page.getZones().size() == 1)
			return 0.0; //there is only one zone - no prominence can be measures
		List<BxZone> zones = page.getZones();
		Integer thisZoneIdx = null;
		for(Integer zoneIdx = 0; zoneIdx < page.getZones().size(); ++zoneIdx) {
			if(page.getZones().get(zoneIdx) == zone) {
				thisZoneIdx = zoneIdx;
				break;
			}
		}
		assert thisZoneIdx != null : "Given zone not found in the context";
		if(thisZoneIdx == 0) { //given zone is the first one in the set - there is none before it
			BxZone nextZone = zones.get(1);
			BxZone thisZone = zones.get(0);
			if(thisZone.getY() < nextZone.getY()) {
				return nextZone.getY()-(thisZone.getY()+thisZone.getHeight());
			} else {
				return 0.0;
			}
		} else if(thisZoneIdx == page.getZones().size()-1) { //given zone is the last one ine the set - there is none after it
			BxZone prevZone = zones.get(thisZoneIdx-1);
			BxZone thisZone = zones.get(thisZoneIdx);
			if(prevZone.getY() < thisZone.getY()) {
				return thisZone.getY()-(prevZone.getY()+prevZone.getHeight());
			} else {
				return 0.0;
			}
		} else { //there is a zone before and after the given one
			BxZone prevZone = zones.get(thisZoneIdx-1);
			BxZone thisZone = zones.get(thisZoneIdx);
			BxZone nextZone = zones.get(thisZoneIdx+1);
			if(prevZone.getY() < thisZone.getY()) { //check if previous zone lies in the same column
				if(thisZone.getY() < nextZone.getY()) { //check if next zone lies in the same column
					return nextZone.getY()-(prevZone.getY()+prevZone.getHeight());
				} else {
					return thisZone.getY()-(prevZone.getY()+prevZone.getHeight());
				}
			} else {
				if(thisZone.getY() < nextZone.getY()) {
					return thisZone.getY()-(prevZone.getY()+prevZone.getHeight());
				} else { //neither previous zone nor next zone lies in natural geometrical order
					return 0.0; //say there is no prominence in this case
				}
			}
		}
	}
};
