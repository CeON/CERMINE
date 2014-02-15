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

package pl.edu.icm.cermine.metadata.zoneclassification.features;

import pl.edu.icm.cermine.structure.model.*;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;

/**
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class AuthorFeature extends FeatureCalculator<BxZone, BxPage> {

    @Override
    public double calculateFeatureValue(BxZone zone, BxPage page) {
        String[] keywords = {"author"};

        int count = 0;
        for (String keyword : keywords) {
            if (zone.toText().toLowerCase().startsWith(keyword)) {
                count++;
            }
        }

        for (BxLine line : zone.getLines()) {
            for (BxWord word : line.getWords()) {
                for (BxChunk chunk : word.getChunks()) {
                    BxBounds chb = chunk.getBounds();
                    BxBounds lb = line.getBounds();
                    String cht = chunk.toText();
                    if ((cht.matches("\\d") || cht.equals("*")) 
                            && word.getChunks().indexOf(chunk) > word.getChunks().size() - 3
                            && chb.getHeight() < 3 * lb.getHeight() / 4
                            && chb.getY() + chb.getHeight() < lb.getY() + lb.getHeight()) {
                        count++;
                    }
                }
            }
        }

        return count;
    }

}
