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

package pl.edu.icm.cermine.content.headers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import pl.edu.icm.cermine.content.model.BxContentStructure;
import pl.edu.icm.cermine.structure.model.BxLine;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class SimpleHeadersClusterizer implements HeadersClusterizer {
    
    public static final double DEFAULT_MAX_HEIGHT_DIFF = 1;
    
    private double maxHeightDiff = DEFAULT_MAX_HEIGHT_DIFF;
    
    @Override
    public void clusterHeaders(BxContentStructure contentStructure) {
        List<BxLine> lines = contentStructure.getFirstHeaderLines();
        contentStructure.setHeaderLevelIds(clusterLines(lines));
    }

    public int[] clusterLines(List<BxLine> lines) {
        int[] clusters = new int[lines.size()];
        Set<BxLine> done = new HashSet<BxLine>();
        int i = 0;
        for (BxLine line : lines) {
            if (line.getMostPopularFontName() == null) {
                clusters[lines.indexOf(line)] = 0;
                done.add(line);
            }
            if (done.contains(line)) {
                continue;
            }
            for (BxLine line2 : lines) {
                if (line2.getMostPopularFontName() == null) {
                    clusters[lines.indexOf(line2)] = 0;
                    done.add(line2);
                }
                if (line.getMostPopularFontName().equals(line2.getMostPopularFontName())
                       && Math.abs(line.getHeight()-line2.getHeight()) < maxHeightDiff) {
                    clusters[lines.indexOf(line2)] = i;
                    done.add(line2);
                }
            }
            i++;
        }

        return clusters;
    }
    
    public double getMaxHeightDiff() {
        return maxHeightDiff;
    }

    public void setMaxHeightDiff(double maxHeightDiff) {
        this.maxHeightDiff = maxHeightDiff;
    }
    
}
