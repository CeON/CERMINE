package pl.edu.icm.yadda.analysis.metadata.zoneclassification.features;

import pl.edu.icm.yadda.analysis.classification.features.FeatureCalculator;
import pl.edu.icm.yadda.analysis.textr.model.BxPage;
import pl.edu.icm.yadda.analysis.textr.model.BxZone;

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
