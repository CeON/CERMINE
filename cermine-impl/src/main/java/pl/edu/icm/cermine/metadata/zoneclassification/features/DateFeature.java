package pl.edu.icm.cermine.metadata.zoneclassification.features;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import pl.edu.icm.cermine.bibref.parsing.features.AbstractFeatureCalculator;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;

/**
 * 
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class DateFeature extends AbstractFeatureCalculator<BxZone, BxPage> {
	protected static String[] monthsRegexps = new String[36];
	protected static String[] digitRegexps = {
			"\\d{4}[ \\.-/]\\d{2}[ \\.-/]\\d{2}",
			"\\d{2}[ \\.-/]\\d{2}[ \\.-/]\\d{4}"
	};
	protected static String[] months = { "january", "febraury", "march", "april", "may", "june", "july", "august", "september", "october", "november", "december" };
	
	static {
		Integer idx=0;
		for(String month: months) {
			monthsRegexps[idx] = "\\d{4}" + "[ \\.-/]" + month + "[ \\.-/]" + "\\d{2}";
			monthsRegexps[idx+12] = "\\d{2}" + "[ \\.-/]" + month + "[ \\.-/]" + "\\d{4}";
			monthsRegexps[idx+24] = "\\d{4}" + "[ ]" + month.substring(0, 3) + "\\.?[ ]" + "\\d{2}";
			++idx;
		}
	}
	
	@Override
	public double calculateFeatureValue(BxZone zone, BxPage page) {
		String text = zone.toText().toLowerCase();
		for(String regex: monthsRegexps) {
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(text);
			if(matcher.find())
				return 1.0;
		}

		for(String regex: digitRegexps) {
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(text);
			if(matcher.find())
				return 1.0;
		}
		return 0.0;
	}

}
