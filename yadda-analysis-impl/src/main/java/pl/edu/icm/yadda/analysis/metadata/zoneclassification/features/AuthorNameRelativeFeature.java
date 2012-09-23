package pl.edu.icm.yadda.analysis.metadata.zoneclassification.features;

import pl.edu.icm.yadda.analysis.bibref.parsing.features.AbstractFeatureCalculator;
import pl.edu.icm.yadda.analysis.classification.features.FeatureCalculator;
import pl.edu.icm.yadda.analysis.textr.model.BxPage;
import pl.edu.icm.yadda.analysis.textr.model.BxZone;

public class AuthorNameRelativeFeature extends AbstractFeatureCalculator<BxZone, BxPage> {

	@Override
	public double calculateFeatureValue(BxZone object, BxPage context) {
		String text = object.toText();
		String[] parts = text.split(",|and");
		Integer numberOfNames = 0;
		for(String part: parts) {
			if(part.length() == 0) {
				++numberOfNames;
				continue;
			}
			String[] words = part.split("\\s");
			
			Boolean isName = true;
			for(String word: words) {
				if(word.length() == 1 && word.matches("\\*|"))
					continue;
				if(word.length() == 2 && word.matches("\\w\\."))
					continue;
				if(word.matches("\\d+"))
					continue;
				if(word.matches("\\p{Upper}.*"))
					continue;
				else if(word.equals("van") || word.equals("von"))
					continue;
				
				isName = false;
				break;
			}
			if(isName)
				++numberOfNames;
		}
		return numberOfNames/(double)parts.length;
	}

}
