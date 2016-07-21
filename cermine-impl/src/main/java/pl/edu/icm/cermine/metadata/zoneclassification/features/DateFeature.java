/**
 * This file is part of CERMINE project.
 * Copyright (c) 2011-2016 ICM-UW
 *
 * CERMINE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CERMINE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with CERMINE. If not, see <http://www.gnu.org/licenses/>.
 */

package pl.edu.icm.cermine.metadata.zoneclassification.features;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;

/** 
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class DateFeature extends AbstractFeatureCalculator<BxZone, BxPage> {
	static final String[] MONTH_REGEXPS = new String[36];
	static final String[] DIGIT_REGEXPS = {
			"\\d{4}[ \\.-/]\\d{2}[ \\.-/]\\d{2}",
			"\\d{2}[ \\.-/]\\d{2}[ \\.-/]\\d{4}"
	};
	static final String[] MONTHS = { "january", "february", "march", "april", "may", "june", "july", "august", "september", "october", "november", "december" };
	
	static {
		int idx=0;
		for(String month: MONTHS) {
			MONTH_REGEXPS[idx] = "\\d{4}" + "[ \\.-/]" + month + "[ \\.-/]" + "\\d{2}";
			MONTH_REGEXPS[idx+12] = "\\d{2}" + "[ \\.-/]" + month + "[ \\.-/]" + "\\d{4}";
			MONTH_REGEXPS[idx+24] = "\\d{4}" + "[ ]" + month.substring(0, 3) + "\\.?[ ]" + "\\d{2}";
			++idx;
		}
	}
	
	@Override
	public double calculateFeatureValue(BxZone zone, BxPage page) {
		String text = zone.toText().toLowerCase();
		for(String regex: MONTH_REGEXPS) {
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(text);
			if (matcher.find()) {
				return 1.0;
            }
		}

		for(String regex: DIGIT_REGEXPS) {
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(text);
			if (matcher.find()) {
				return 1.0;
            }
		}
		return 0.0;
	}

}
