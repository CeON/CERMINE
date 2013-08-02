package pl.edu.icm.cermine.metadata.zoneclassification.features;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import pl.edu.icm.cermine.metadata.zoneclassification.tools.ZoneClassificationUtils;
import pl.edu.icm.cermine.structure.model.BxLine;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;

public class StartsWithHeaderFeature extends FeatureCalculator<BxZone, BxPage> {

	@Override
	public double calculateFeatureValue(BxZone object, BxPage context) {
		BxLine firstLine = object.getLines().get(0);
		String lineText = firstLine.toText();
		String text = object.toText();
		
		String itemizeString = "";
		itemizeString += "|^\\d+\\.\\d+\\.\\s+\\p{Upper}.+";
		itemizeString += "|^\\d+\\.\\s+\\p{Upper}.+";
		itemizeString += "|^\\p{Upper}\\.\\s[^\\.]+";
		itemizeString += "|^\\p{Lower}\\)\\s+.+";
		Pattern itemizePattern = Pattern.compile(itemizeString);

		String subpointsString = "";
		subpointsString += "^\\d\\.\\d\\.\\s+\\p{Upper}.+";
		subpointsString += "|^\\d\\.\\d\\.\\d\\.\\s+\\p{Upper}.+";
		Pattern subpointsPattern = Pattern.compile(subpointsString, Pattern.DOTALL); //for multiline matching

		Matcher matcher1 = itemizePattern.matcher(text);
		Matcher matcher2 = subpointsPattern.matcher(text);

		if(matcher1.matches() || matcher2.matches()) {
			return 1.0;
		}
		
		if(object.getLines().size() <= 2) {			
			return 0;
		}
		if(!lineText.contains(" ") && !lineText.matches(".*\\d.*") && lineText.matches("\\p{Upper}.+")) {
			return 1;
		}
		String[] words = lineText.split(" ");
		Boolean capitals = true;
		for(String word: words) {
			if(!(word.matches("\\{Upper}.+") || ZoneClassificationUtils.isConjunction(word))) {
				capitals = false;
				break;
			}
		}
		if(capitals) {
			return 1.0;
		} 

		return 0;
	}
}
