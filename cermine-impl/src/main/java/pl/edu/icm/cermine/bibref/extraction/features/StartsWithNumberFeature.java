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

package pl.edu.icm.cermine.bibref.extraction.features;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import pl.edu.icm.cermine.bibref.extraction.model.BxDocumentBibReferences;
import pl.edu.icm.cermine.structure.model.BxLine;
import pl.edu.icm.cermine.tools.classification.general.FeatureCalculator;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class StartsWithNumberFeature extends FeatureCalculator<BxLine, BxDocumentBibReferences> {

    @Override
    public double calculateFeatureValue(BxLine refLine, BxDocumentBibReferences refs) {
        String[] patterns = {"^\\[(\\d{1,3})\\] .*", "^(\\d{1,3})\\.? .*"};
        for (String pText : patterns) {
            Pattern pattern = Pattern.compile(pText);
            Matcher matcher = pattern.matcher(refLine.toText());
            if (!matcher.matches()) {
                continue;
            }
                       
            int index = refs.getLines().indexOf(refLine);
            String objectMatch = matcher.group(1);
            String prevMatch = null;
            String nextMatch = null;
            for (int i = index - 1; i >= 0; i--) {
                BxLine line = refs.getLines().get(i);
                Matcher prevMatcher = pattern.matcher(line.toText());
                if (prevMatcher.matches()) {
                    prevMatch = prevMatcher.group(1);
                    break;
                }
            }
            for (int i = index + 1; i < refs.getLines().size(); i++) {
                BxLine line = refs.getLines().get(i);
                Matcher nextMatcher = pattern.matcher(line.toText());
                if (nextMatcher.matches()) {
                    nextMatch = nextMatcher.group(1);
                    break;
                }
            }

            if (prevMatch != null && objectMatch != null
                    && Integer.parseInt(prevMatch) + 1 == Integer.parseInt(objectMatch)) {
                return 1;
            }

            if (nextMatch != null && objectMatch != null
                    && Integer.parseInt(objectMatch) + 1 == Integer.parseInt(nextMatch)) {
                return 1;
            }
        }

        return 0;
    }
    
}
