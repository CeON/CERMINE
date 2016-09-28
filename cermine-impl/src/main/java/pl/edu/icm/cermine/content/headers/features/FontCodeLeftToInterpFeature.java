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

package pl.edu.icm.cermine.content.headers.features;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import pl.edu.icm.cermine.structure.model.BxChunk;
import pl.edu.icm.cermine.structure.model.BxLine;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxWord;
import pl.edu.icm.cermine.tools.CountMap;
import pl.edu.icm.cermine.tools.classification.general.FeatureCalculator;

/**
 * @author Jan Lasek
 */
public class FontCodeLeftToInterpFeature extends FeatureCalculator<BxLine, BxPage> {
    String[] fontNames;

    public FontCodeLeftToInterpFeature(List<Map.Entry<String, Integer>> fontNames2) {
        String[] fontNamesTmp = new String[fontNames2.size()];
        for(int i = 0; i < fontNamesTmp.length; i++) {
            fontNamesTmp[i] = fontNames2.get(i).getKey();
        }
        fontNames = fontNamesTmp;       
    }
       
    @Override
    public double calculateFeatureValue(BxLine line, BxPage context) {
        int i = 0;
        for (BxWord word : line){ 
            i++;
            String wordProcessed = word.toText().replaceAll("[^a-zA-Z\\.:\\?\\)\\(]", "");
            if (wordProcessed.matches("([a-zA-Z]{2,}|(.*?\\)))(\\.|:|\\?)")) {
                break;
            }
        }
        CountMap<String> mapLeft = new CountMap<String>();
        for (BxWord word : Lists.newArrayList(line).subList(0, i)) {
            for (BxChunk chunk : word) {
                if (chunk.getFontName() != null) {
                    mapLeft.add(chunk.getFontName());
                }
            }
        }

        String fontNameLeft = mapLeft.getMaxCountObject();
        if (fontNameLeft == null) {
            return -1;
        } else {
            int l = 0;
            while (l < fontNames.length && !fontNameLeft.equals(this.fontNames[l])) {
                l++;
            }
            return (double) l;
        }
    }

}
