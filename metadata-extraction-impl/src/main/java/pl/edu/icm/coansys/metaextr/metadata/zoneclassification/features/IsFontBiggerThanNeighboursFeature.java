package pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features;

import java.util.List;

import pl.edu.icm.coansys.metaextr.tools.classification.features.FeatureCalculator;
import pl.edu.icm.coansys.metaextr.structure.model.BxPage;
import pl.edu.icm.coansys.metaextr.structure.model.BxZone;

/** 
 * @author Pawel Szostek (p.szostek@icm.edu.pl) 
 */

public class IsFontBiggerThanNeighboursFeature implements
		FeatureCalculator<BxZone, BxPage> {
	private static String featureName = "IsFontBiggerThanNeighbours";

	@Override
	public String getFeatureName() {
		return featureName;
	}

	@Override
	public double calculateFeatureValue(BxZone zone, BxPage page) {
		List<BxZone> pageZones = page.getZones();
		if (pageZones.size() == 0)
			return 0.0;
		if (pageZones.size() == 1)
			return 1.0;

		Integer thisZoneIdx = null;
		for (Integer zoneIdx = 0; zoneIdx < pageZones.size(); ++zoneIdx) {
			if (zone == pageZones.get(zoneIdx)) {
				thisZoneIdx = zoneIdx;
				break;
			}
		}
		assert thisZoneIdx != null : "No zone in zone's context found";
		if (thisZoneIdx == 0) {
			FeatureCalculator<BxZone, BxPage> fontHeight = new FontHeightMeanFeature();
			double nextZoneFont = fontHeight.calculateFeatureValue(
					pageZones.get(thisZoneIdx + 1), page);
			double thisZoneFont = fontHeight.calculateFeatureValue(
					pageZones.get(thisZoneIdx), page);
			return thisZoneFont > nextZoneFont ? 1.0 : 0.0;
		} else if (thisZoneIdx == pageZones.size() - 1) {
			FeatureCalculator<BxZone, BxPage> fontHeight = new FontHeightMeanFeature();
			double prevZoneFont = fontHeight.calculateFeatureValue(
					pageZones.get(thisZoneIdx - 1), page);
			double thisZoneFont = fontHeight.calculateFeatureValue(
					pageZones.get(thisZoneIdx), page);
			return thisZoneFont > prevZoneFont ? 1.0 : 0.0;
		} else {
			FeatureCalculator<BxZone, BxPage> fontHeight = new FontHeightMeanFeature();
			double prevZoneFont = fontHeight.calculateFeatureValue(
					pageZones.get(thisZoneIdx - 1), page);
			double thisZoneFont = fontHeight.calculateFeatureValue(
					pageZones.get(thisZoneIdx), page);
			double nextZoneFont = fontHeight.calculateFeatureValue(
					pageZones.get(thisZoneIdx + 1), page);

			return (thisZoneFont > prevZoneFont && thisZoneFont > nextZoneFont) ? 1.0
					: 0.0;
		}
	}

}
