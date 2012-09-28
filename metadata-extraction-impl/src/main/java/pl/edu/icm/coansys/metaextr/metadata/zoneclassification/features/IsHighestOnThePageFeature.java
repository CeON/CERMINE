package pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import pl.edu.icm.coansys.metaextr.tools.classification.features.FeatureCalculator;
import pl.edu.icm.coansys.metaextr.structure.model.BxPage;
import pl.edu.icm.coansys.metaextr.structure.model.BxZone;
public class IsHighestOnThePageFeature implements FeatureCalculator<BxZone, BxPage>{
    private static String featureName = "IsHighestOnThePage";

	@Override
	public String getFeatureName() {
		return featureName;
	}
	
	private static class yCoordinateComparator implements Comparator<BxZone> {
		@Override
		public int compare(BxZone z1, BxZone z2) {
			return (z1.getY() + z1.getHeight() < z2.getY() + z2.getHeight() ? -1
					: (z1.getY() + z1.getHeight() == z2.getY() + z2.getHeight() ? 0
							: 1));
		}
	}
	
	@Override
	public double calculateFeatureValue(BxZone zone, BxPage page) {	
		List<BxZone> zones = new ArrayList<BxZone>(page.getZones());
		Collections.sort(zones, new yCoordinateComparator());
		BxZone firstZone = zones.get(0);
		final double IDENT_TRESHOLD = 1.0;
		if(zone == firstZone)
			return 1.0;
		else
			if(Math.abs(zone.getY() - firstZone.getY()) <= IDENT_TRESHOLD) {
				return 1.0;
			} else {
				return 0.0;
			}
	}
}
