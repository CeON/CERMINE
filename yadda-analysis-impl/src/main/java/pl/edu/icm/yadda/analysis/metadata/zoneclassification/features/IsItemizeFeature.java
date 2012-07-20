package pl.edu.icm.yadda.analysis.metadata.zoneclassification.features;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pl.edu.icm.yadda.analysis.classification.features.FeatureCalculator;
import pl.edu.icm.yadda.analysis.textr.model.BxPage;
import pl.edu.icm.yadda.analysis.textr.model.BxZone;

public class IsItemizeFeature implements FeatureCalculator<BxZone, BxPage> {
	private static String featureName = "IsItemize";

	@Override
	public String getFeatureName() {
		return featureName;
	}

	@Override
	public double calculateFeatureValue(BxZone zone, BxPage page) {
		String text = zone.toText();
		Pattern itemizePattern = Pattern.compile("^\\d+\\..*|(IX|IV|V?I{0,3})(\\.)?\\s.*|\\p{Upper}\\..*|\\p{Lower}\\).*");
		Matcher matcher = itemizePattern.matcher(text);
		return matcher.matches() ? 1.0 : 0.0;
	}
}
