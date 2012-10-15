package pl.edu.icm.cermine.metadata.zoneclassification.features;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;

/** 
 * @author Pawel Szostek (p.szostek@icm.edu.pl) 
 */

public class WordLengthMedianFeature extends FeatureCalculator<BxZone, BxPage> {

	@Override
	public double calculateFeatureValue(BxZone object, BxPage context) {
		String text = object.toText();
		String[] words = text.split("\\s");
		List<Integer> wordLengths = new ArrayList<Integer>(words.length);
		for(String word: words) {
			wordLengths.add(word.length());
		}
		Collections.sort(wordLengths);
		return wordLengths.get(wordLengths.size()/2);
	}

}