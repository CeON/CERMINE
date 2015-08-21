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
    
        public int correct;
        public int expected;
        public int extracted;
        
        public Double precision;
        public Double recall;
        public Double f1;
        
        public PrecisionRecall() {
            correct = 0;
            expected = 0;
            extracted = 0;
        }
        
        public PrecisionRecall buildForSingle(List<MetadataSingle> metadataList) {
            for (MetadataSingle metadata : metadataList) {
                correct += metadata.correctSize();
                expected += metadata.expectedSize();
                extracted += metadata.extractedSize();
            }
            return this;
        }
        
        public PrecisionRecall buildForList(List<MetadataList> metadataList) {
            int precisions = 0;
            int recalls = 0;
            int f1s = 0;
            precision = 0.;
            recall = 0.;
            f1 = 0.;
            for (MetadataList metadata : metadataList) {
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
        
        public PrecisionRecall buildForRelation(List<MetadataRelation> metadataList) {
            int precisions = 0;
            int recalls = 0;
            int f1s = 0;
            precision = 0.;
            recall = 0.;
            f1 = 0.;
            for (MetadataRelation metadata : metadataList) {
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

        @Override
        public String toString() {
            return "PrecisionRecall{" + "correct=" + correct + ", expected=" + expected + ", extracted=" + extracted + '}';
        }
        
        public void print(String name) {
            System.out.println(name + ": " + toString());
            System.out.printf(name + ": precision: %4.2f" + ", recall: %4.2f" + ", F1: %4.2f\n\n",
                    calculatePrecision() == null ? -1. : 100 * calculatePrecision(), 
                    calculateRecall() == null ? -1. : 100 * calculateRecall(), 
                    calculateF1() == null ? -1. : 100 * calculateF1());
        }

        public Double calculateRecall() {
            if (recall != null) {
                return recall;
            }
            if (expected == 0) {
                return null;
            }
            return (double) correct / expected;
        }
        
        public Double calculatePrecision() {
            if (precision != null) {
                return precision;
            }
            if (extracted == 0) {
                return null;
            }
            return (double) correct / extracted;
        }
        
        public Double calculateF1() {
            if (f1 != null) {
                return f1;
            }
            Double prec = calculatePrecision();
            Double rec = calculateRecall();
            if (prec == null || rec == null) {
                return null;
            }
            return 2 * prec * rec / (prec + rec);
        }
}
