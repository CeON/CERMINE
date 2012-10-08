package pl.edu.icm.cermine.metadata.zoneclassification.features;

import pl.edu.icm.cermine.structure.model.*;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;

/** 
 * @author Pawel Szostek (p.szostek@icm.edu.pl) 
 */

public class WordLengthMeanFeature implements FeatureCalculator<BxZone, BxPage> {

    private static String featureName = "WordLengthMean";

	@Override
	public String getFeatureName() {
		return featureName;
	}

	@Override
	public double calculateFeatureValue(BxZone object, BxPage context) {
		Integer wordsLengthsSum = 0;
		Integer numberOfWords = 0;
		for(BxLine line: object.getLines())
			for(BxWord word: line.getWords()) {
				Integer curLength = 0;
				for(BxChunk chunk: word.getChunks())
					curLength += chunk.getText().length();
				wordsLengthsSum += curLength;
			    ++numberOfWords;
		}
		return wordsLengthsSum.doubleValue()/numberOfWords;
	}

}
