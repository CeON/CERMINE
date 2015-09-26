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

package pl.edu.icm.cermine.evaluation.tools;

import java.util.List;

/**
 *
 * @author Dominika Tkaczyk
 */
public class PrecisionRecall {
    
    public Double precision;
    public Double recall;
    public Double f1;
        
    public PrecisionRecall build(List<ComparisonResult> metadataList) {
        if (metadataList == null || metadataList.isEmpty()) {
            return this;
        }
        int precisions = 0;
        int recalls = 0;
        int f1s = 0;
        precision = 0.;
        recall = 0.;
        f1 = 0.;
        for (ComparisonResult metadata : metadataList) {
            if (metadata.getPrecision() != null) {
                precision += metadata.getPrecision();
                precisions++;
            }
            if (metadata.getRecall() != null) {
                recall += metadata.getRecall();
                recalls++;
            }
            if (metadata.getF1() != null) {
                f1 += metadata.getF1();
                f1s++;
            }
        }
        precision /= precisions;
        recall /= recalls;
        f1 /= f1s;

        return this;
    }
        
    public void print(String name) {
        System.out.printf(name + ": precision: %4.2f" + ", recall: %4.2f" + ", F1: %4.2f\n\n",
                    getPrecision() == null ? -1. : 100 * getPrecision(), 
                    getRecall() == null ? -1. : 100 * getRecall(), 
                    getF1() == null ? -1. : 100 * getF1());
    }

    public Double getF1() {
        return f1;
    }

    public Double getPrecision() {
        return precision;
    }

    public Double getRecall() {
        return recall;
    }

}
