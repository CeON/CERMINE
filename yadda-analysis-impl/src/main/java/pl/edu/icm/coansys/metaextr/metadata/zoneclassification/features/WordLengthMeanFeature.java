package pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features;

import pl.edu.icm.coansys.metaextr.classification.features.FeatureCalculator;
import pl.edu.icm.coansys.metaextr.textr.model.BxChunk;
import pl.edu.icm.coansys.metaextr.textr.model.BxLine;
import pl.edu.icm.coansys.metaextr.textr.model.BxPage;
import pl.edu.icm.coansys.metaextr.textr.model.BxWord;
import pl.edu.icm.coansys.metaextr.textr.model.BxZone;

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
