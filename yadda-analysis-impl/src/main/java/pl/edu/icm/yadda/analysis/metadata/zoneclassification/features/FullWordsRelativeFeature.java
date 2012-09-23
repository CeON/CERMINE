package pl.edu.icm.yadda.analysis.metadata.zoneclassification.features;

import pl.edu.icm.yadda.analysis.bibref.parsing.features.AbstractFeatureCalculator;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.tools.ZoneClassificationUtils;
import pl.edu.icm.yadda.analysis.textr.model.BxPage;
import pl.edu.icm.yadda.analysis.textr.model.BxZone;

public class FullWordsRelativeFeature extends AbstractFeatureCalculator<BxZone, BxPage> {
	@Override
	public double calculateFeatureValue(BxZone object, BxPage context) {
		String text = object.toText();
		String[] words = text.split("\\s");
		Integer numberOfWords = 0;
		Integer numberOfFullWords = 0;
		for(String word: words) {
			if(ZoneClassificationUtils.isConjunction(word)) {
				++numberOfFullWords;
			}
			else if(word.length() <= 2) {
				;
			}
			else if(word.matches(".*\\d.*")) {
				;
			}
			else if(word.matches(".*[^\\p{Alnum}].*")) {
				;
			} else {
				++numberOfFullWords;
			}
			++numberOfWords;
		}
		double ret = (double)numberOfFullWords/numberOfWords;
		return ret;
	}

}
