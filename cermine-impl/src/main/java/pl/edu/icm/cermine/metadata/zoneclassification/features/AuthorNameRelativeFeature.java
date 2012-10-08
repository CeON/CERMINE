package pl.edu.icm.cermine.metadata.zoneclassification.features;

import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;

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
