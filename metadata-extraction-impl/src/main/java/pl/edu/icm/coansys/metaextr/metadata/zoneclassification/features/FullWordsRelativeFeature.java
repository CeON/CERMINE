package pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features;

import pl.edu.icm.coansys.metaextr.bibref.parsing.features.AbstractFeatureCalculator;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.tools.ZoneClassificationUtils;
import pl.edu.icm.coansys.metaextr.structure.model.BxPage;
import pl.edu.icm.coansys.metaextr.structure.model.BxZone;

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
