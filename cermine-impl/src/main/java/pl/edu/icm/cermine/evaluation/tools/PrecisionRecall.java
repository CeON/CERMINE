/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
        
        public double precision;
        public double recall;
        
        public PrecisionRecall() {
            correct = 0;
            expected = 0;
            extracted = 0;
            precision = -1.;
            recall = -1.;
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
            precision = 0;
            recall = 0;
            for (MetadataList metadata : metadataList) {
                if (metadata.getPrecision() != null) {
                    precision += metadata.getPrecision();
                    precisions++;
                }
                if (metadata.getRecall() != null) {
                    recall += metadata.getRecall();
                    recalls++;
                }
            }
            precision /= precisions;
            recall /= recalls;
            return this;
        }
        
        public PrecisionRecall buildForRelation(List<MetadataRelation> metadataList) {
            int precisions = 0;
            int recalls = 0;
            precision = 0;
            recall = 0;
            for (MetadataRelation metadata : metadataList) {
                if (metadata.getPrecision() != null) {
                    precision += metadata.getPrecision();
                    precisions++;
                }
                if (metadata.getRecall() != null) {
                    recall += metadata.getRecall();
                    recalls++;
                }
            }
            precision /= precisions;
            recall /= recalls;
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
            if (recall != -1.) {
                return recall;
            }
            if (expected == 0) {
                return null;
            }
            return (double) correct / expected;
        }
        
        public Double calculatePrecision() {
            if (precision != -1.) {
                return precision;
            }
            if (extracted == 0) {
                return null;
            }
            return (double) correct / extracted;
        }
        
        public Double calculateF1() {
            Double prec = calculatePrecision();
            Double rec = calculateRecall();
            if (prec == null || rec == null) {
                return null;
            }
            return 2 * prec * rec / (prec + rec);
        }
}
