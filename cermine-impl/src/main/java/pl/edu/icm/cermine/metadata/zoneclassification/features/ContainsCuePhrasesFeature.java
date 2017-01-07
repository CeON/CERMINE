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

import java.util.Locale;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.tools.classification.general.FeatureCalculator;

/**
 * @author Pawel Szostek
 */
public class ContainsCuePhrasesFeature extends FeatureCalculator<BxZone, BxPage> {

    private static final String[] cuePhrases = {"although", "therefore", "therein", "hereby",
        "nevertheless", "to this end", "however", "moreover", "nonetheless"};

    @Override
    public String getFeatureName() {
        return "ContainsCuePhrases";
    }

    @Override
    public double calculateFeatureValue(BxZone zone, BxPage page) {
        String zoneText = zone.toText().toLowerCase(Locale.ENGLISH);

        for (String cuePhrase : cuePhrases) {
            if (zoneText.contains(cuePhrase)) {
                return 1.0;
            }
        }
        return 0.0;
    }
}
