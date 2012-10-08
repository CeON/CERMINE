package pl.edu.icm.cermine.metadata.zoneclassification.features;

import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;

public class IsOnSurroundingPagesFeature implements FeatureCalculator<BxZone, BxPage>{
	private String featureName = "IsOnSurroundingPages";

	@Override
	public String getFeatureName()
	{
		return featureName;
	}

	@Override
	public double calculateFeatureValue(BxZone object, BxPage context)
	{
		BxPage nextPage = context.getNext();
		BxPage prevPage = context.getPrev();
		
		if(nextPage != null) {
			for(BxZone zone: nextPage.getZones())
				if(zone.toText().equals(object.toText()))
					return 1.0;
		}
		
		if(prevPage != null) {
			for(BxZone zone: prevPage.getZones())
				if(zone.toText().equals(object.toText()))
					return 1.0;
		}
		
		return 0.0;
	}
}
