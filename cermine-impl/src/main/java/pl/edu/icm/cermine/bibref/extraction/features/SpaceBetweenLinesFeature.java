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

import pl.edu.icm.cermine.bibref.extraction.model.BxDocumentBibReferences;
import pl.edu.icm.cermine.structure.model.BxLine;
import pl.edu.icm.cermine.tools.classification.general.FeatureCalculator;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class SpaceBetweenLinesFeature extends FeatureCalculator<BxLine, BxDocumentBibReferences> {

    @Override
    public double calculateFeatureValue(BxLine refLine, BxDocumentBibReferences refs) {
        double minSpace = Double.POSITIVE_INFINITY;
        double maxSpace = Double.NEGATIVE_INFINITY;
        double lineSpace = 0;
        BxLine prevLine = null;
        for (BxLine line : refs.getLines()) {
            if (prevLine != null && line.getBounds().getY() > prevLine.getBounds().getY()) {
                double difference = line.getBounds().getY() - prevLine.getBounds().getY();
                if (minSpace > difference) {
                    minSpace = difference;
                }
                if (maxSpace < difference) {
                    maxSpace = difference;
                }
                if (line.equals(refLine)) {
                    lineSpace = difference;
                }
            }
            prevLine = line;
        }
        
        if (refs.getLines().indexOf(refLine) == 0 && maxSpace > minSpace * 1.2) {
            return 0.5;
        }
        
        return (lineSpace > minSpace * 1.2) ? 0.5 : 0;
    }
    
}
