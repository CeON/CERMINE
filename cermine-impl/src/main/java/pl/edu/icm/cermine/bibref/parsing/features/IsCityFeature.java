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
public class IsCityFeature extends FeatureCalculator<CitationToken, Citation> {

    private static final List<String> KEYWORDS = Arrays.asList(
            "angeles", "antonio", "amsterdam", "ankara", "athens",
            "bangkok", "basel", "beijing", "belgrade", "berkeley", "berlin", "bern", "bologna", "bombay", "boston",
            "bratislava", "brussels", "bucharest", "budapest",
            "cambridge", "calgary", "chicago", "copenhagen",
            "dallas", "delhi", "dhaka", "diego", "dordrecht", "dublin",
            "edmonton",
            "francisco",
            "grenoble", "göttingen",
            "heidelberg", "helsinki", "houston",
            "indianapolis", "istanbul",
            "jakarta", "jacksonville", "jose",
            "karachi", "kiev",
            "leipzig", "lisbon", "ljubljana", "london", "londres", "los",
            "madrid", "manila", "mass", "minsk", "montreal", "moscou", "moscow", "mumbai",
            "new",
            "orsay", "oslo", "ottawa", "oxford",
            "paris", "phoenix", "philadelphia", "prague", "princeton", "providence",
            "reading", "reykjavik", "riga", "roma", "rome",
            "san", "sarajevo", "seoul", "shanghai", "skopje", "sofia", "stockholm",
            "tallinn", "tehran", "tirana", "tokyo", "toronto", "toulouse",
            "vancouver", "vienna", "vilnius",
            "warsaw", "warszawa",
            "york",
            "zagreb");
    

    @Override
    public double calculateFeatureValue(CitationToken object, Citation context) {
        return (KEYWORDS.contains(object.getText().toLowerCase())) ? 1 : 0;
    }
}
