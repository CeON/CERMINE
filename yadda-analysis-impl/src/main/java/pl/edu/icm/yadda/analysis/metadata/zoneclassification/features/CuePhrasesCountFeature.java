package pl.edu.icm.yadda.analysis.metadata.zoneclassification.features;

import pl.edu.icm.yadda.analysis.classification.features.FeatureCalculator;
import pl.edu.icm.yadda.analysis.textr.model.BxChunk;
import pl.edu.icm.yadda.analysis.textr.model.BxLine;
import pl.edu.icm.yadda.analysis.textr.model.BxPage;
import pl.edu.icm.yadda.analysis.textr.model.BxWord;
import pl.edu.icm.yadda.analysis.textr.model.BxZone;

public class CuePhrasesCountFeature implements
		FeatureCalculator<BxZone, BxPage> {

	private static String featureName = "CuePhrasesCount";
	private static String[] cuePhrases = {"although", "therefore", "therein", "hereby",
			"nevertheless", "to this end", "however", "moreover", "nonetheless" };

	@Override
	public String getFeatureName() {
		return featureName;
	}

	@Override
	public double calculateFeatureValue(BxZone zone, BxPage page) {
		StringBuilder contentBuilder = new StringBuilder();
		for(BxLine line: zone.getLines()) {
			for(BxWord word: line.getWords()) {
				for(BxChunk chunk: word.getChunks()) {
					contentBuilder.append(chunk.getText());
				}
				contentBuilder.append(" ");
			}
		}
		Integer count = 0;
		String contentString = contentBuilder.toString();
		for(String cuePhrase: cuePhrases) {
			while(contentString.contains(cuePhrase)) {
				contentString = contentString.replaceFirst(cuePhrase, "");
				++count;
			}
		}
		return (double) count; 
	}
}
