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

import java.util.Arrays;
import java.util.List;
import pl.edu.icm.cermine.bibref.parsing.model.Citation;
import pl.edu.icm.cermine.bibref.parsing.model.CitationToken;
import pl.edu.icm.cermine.tools.classification.general.FeatureCalculator;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class IsCommonPublisherWordFeature extends FeatureCalculator<CitationToken, Citation> {

    private static final List<String> KEYWORDS = Arrays.asList(
            "academic",
            "birkhäuser",
            "cambridge", "company",
            "dunod",
            "france",
            "gauthier",
            "hermann", "holland",
            "interscience",
            "john",
            "masson", "math",
            "north", "nostrand",
            "paris", "polytechnique", "press", "princeton", "publ", "publishers",
            "sons", "springer",
            "univ", "université", "university",
            "verlag", "villars",
            "wiley", "world"
            );

    @Override
    public double calculateFeatureValue(CitationToken object, Citation context) {
        return (KEYWORDS.contains(object.getText().toLowerCase())) ? 1 : 0;
    }
}
