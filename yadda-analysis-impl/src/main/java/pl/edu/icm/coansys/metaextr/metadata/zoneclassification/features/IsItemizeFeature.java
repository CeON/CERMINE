package pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pl.edu.icm.coansys.metaextr.classification.features.FeatureCalculator;
import pl.edu.icm.coansys.metaextr.textr.model.BxPage;
import pl.edu.icm.coansys.metaextr.textr.model.BxZone;

public class IsItemizeFeature implements FeatureCalculator<BxZone, BxPage> {
	private static String featureName = "IsItemize";

	@Override
	public String getFeatureName() {
		return featureName;
	}

	@Override
	public double calculateFeatureValue(BxZone zone, BxPage page) {
		String text = zone.toText();

		String itemizeString = "";
		itemizeString += "|^\\d+\\.\\d+\\.\\s+\\p{Upper}.+";
		itemizeString += "|^\\d+\\.\\s+\\p{Upper}.+";
		//pattern += "|^(IX|IV|V?I{0,3})(\\.)?\\s*\\p{Upper}.+";
		itemizeString += "|^\\p{Upper}\\.\\s[^\\.]+";
		itemizeString += "|^\\p{Lower}\\)\\s+.+";
		Pattern itemizePattern = Pattern.compile(itemizeString);

		String subpointsString = "";
		subpointsString += "^\\d\\.\\d\\.\\s+\\p{Upper}.+";
		subpointsString += "|^\\d\\.\\d\\.\\d\\.\\s+\\p{Upper}.+";
		Pattern subpointsPattern = Pattern.compile(subpointsString, Pattern.DOTALL); //for multiline matching

		Matcher matcher1 = itemizePattern.matcher(text);
		Matcher matcher2 = subpointsPattern.matcher(text);

	/*	if(matcher1.matches() || matcher2.matches()) {
			System.out.println("++++");
			System.out.println(zone.toText());
		} else {
			System.out.println("----");
			System.out.println(zone.toText());
		} */
		return (matcher1.matches() || matcher2.matches()) ? 1.0 : 0.0;
	}
}
