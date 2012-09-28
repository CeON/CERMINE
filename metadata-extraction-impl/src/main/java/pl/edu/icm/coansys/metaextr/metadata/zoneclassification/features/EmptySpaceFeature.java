package pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features;

import pl.edu.icm.coansys.metaextr.tools.classification.features.FeatureCalculator;
import pl.edu.icm.coansys.metaextr.structure.model.BxChunk;
import pl.edu.icm.coansys.metaextr.structure.model.BxLine;
import pl.edu.icm.coansys.metaextr.structure.model.BxPage;
import pl.edu.icm.coansys.metaextr.structure.model.BxWord;
import pl.edu.icm.coansys.metaextr.structure.model.BxZone;

public class EmptySpaceFeature implements FeatureCalculator<BxZone, BxPage> {

	private static String featureName = "EmptySpace";
	
	@Override
	public String getFeatureName() {
		return featureName;
	}

	@Override
	public double calculateFeatureValue(BxZone zone, BxPage page) {
		double charSpace = 0.0;
		for (BxLine line : zone.getLines()) {
			for (BxWord word : line.getWords()) {
				for (BxChunk chunk : word.getChunks()) {
					charSpace += chunk.getArea();
				}
			}
		}
		double ret = zone.getArea() - charSpace;
		if(ret < 0) {
			return 0.0;
		} else {
			return ret;
		}
	}
}
