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

package pl.edu.icm.cermine.bibref.parsing.features;

import java.util.List;
import pl.edu.icm.cermine.bibref.parsing.model.Citation;
import pl.edu.icm.cermine.bibref.parsing.model.CitationToken;
import pl.edu.icm.cermine.tools.classification.general.FeatureCalculator;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class StartingNumberFeature extends FeatureCalculator<CitationToken, Citation> {

    @Override
    public double calculateFeatureValue(CitationToken object, Citation context) {
        List<CitationToken> tokens = context.getTokens();
        int index = context.getTokens().indexOf(object);

        if (tokens.size() > 0 && tokens.get(0).getText().matches("^\\d+$") && index == 0) {
            return 1;
        }

        if (tokens.size() > 1) {
            String two = tokens.get(0).getText() + tokens.get(1).getText();
            if (two.matches("^\\d+\\.$") && index < 2) {
                return 1;
            }
        }

        if (tokens.size() > 2) {
            String three = tokens.get(0).getText() + tokens.get(1).getText() + tokens.get(2).getText();
            if (three.matches("^\\[\\d+\\]$") && index < 3) {
                return 1;
            }
            if (three.matches("^.\\d+\\.$") && index < 3) {
                return 1;
            }
        }

        return 0;
    }
}
