package pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pl.edu.icm.coansys.metaextr.tools.classification.features.FeatureCalculator;
import pl.edu.icm.coansys.metaextr.structure.model.BxChunk;
import pl.edu.icm.coansys.metaextr.structure.model.BxLine;
import pl.edu.icm.coansys.metaextr.structure.model.BxPage;
import pl.edu.icm.coansys.metaextr.structure.model.BxWord;
import pl.edu.icm.coansys.metaextr.structure.model.BxZone;

/** 
 * @author Pawel Szostek (p.szostek@icm.edu.pl) 
 */

public class WordLengthMedianFeature implements
		FeatureCalculator<BxZone, BxPage> {

	private static String featureName = "WordLengthMedian";
	@Override
	public String getFeatureName() {
		return featureName;
	}

	@Override
	public double calculateFeatureValue(BxZone object, BxPage context) {
		String text = object.toText();
		String[] words = text.split("\\s");
		List<Integer> wordLengths = new ArrayList<Integer>(words.length);
		for(String word: words)
			wordLengths.add(word.length());
		Collections.sort(wordLengths);
		return wordLengths.get(wordLengths.size()/2);
	}

}