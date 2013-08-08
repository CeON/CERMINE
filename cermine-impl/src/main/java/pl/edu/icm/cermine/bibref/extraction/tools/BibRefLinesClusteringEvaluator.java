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

package pl.edu.icm.cermine.bibref.extraction.tools;

import pl.edu.icm.cermine.tools.classification.clustering.ClusteringEvaluator;

/**
 *
 * @author Dominika Tkaczyk
 */
public class BibRefLinesClusteringEvaluator implements ClusteringEvaluator {
    
    public static final int DEFAULT_MAX_REF_LINES = 10;
    
    private int maxRefLinesCount = DEFAULT_MAX_REF_LINES;

    @Override
    public boolean isAcceptable(int[] clusters) {
        int first = clusters[0];

        int prevIndex = 0;
        for (int index = 0; index < clusters.length; index++) {
            if (first == clusters[index]) {
                if (index - prevIndex > maxRefLinesCount) {
                    return false;
                }
                prevIndex = index;
            }
        }
        if (clusters.length - prevIndex > maxRefLinesCount) {
            return false;
        }
        
        return true;
    }

    public int getMaxRefLinesCount() {
        return maxRefLinesCount;
    }

    public void setMaxRefLinesCount(int maxRefLinesCount) {
        this.maxRefLinesCount = maxRefLinesCount;
    }
    
}
