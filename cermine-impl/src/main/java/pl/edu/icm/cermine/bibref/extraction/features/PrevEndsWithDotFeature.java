/**
 * This file is part of CERMINE project.
 * Copyright (c) 2011-2013 ICM-UW
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

import java.util.regex.Pattern;
import pl.edu.icm.cermine.bibref.extraction.model.BxDocumentBibReferences;
import pl.edu.icm.cermine.structure.model.BxLine;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;

/**
 *
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public class PrevEndsWithDotFeature extends FeatureCalculator<BxLine, BxDocumentBibReferences> {

    @Override
    public double calculateFeatureValue(BxLine refLine, BxDocumentBibReferences refs) {
        if (refs.getLines().indexOf(refLine) == 0) {
            return 0.9;
        }
        String text = refs.getLines().get(refs.getLines().indexOf(refLine)-1).toText();
        if (Pattern.matches("^.*\\d\\.$", text)) {
            return 1;
        }
        return Pattern.matches("^.*\\.$", text) ? 0.8 : 0;
    }
    
}
