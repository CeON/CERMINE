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

import java.util.*;

/**
 *
 * @author Dominika Tkaczyk
 */
public class MetadataRelation implements ComparisonResult {

    private final Set<StringRelation> expectedValue = new HashSet<StringRelation>();
    private final Set<StringRelation> extractedValue = new HashSet<StringRelation>();

    private Double precision;
    private Double recall;
    
    private Comparator<String> comp1 = EvaluationUtils.defaultComparator;
    private Comparator<String> comp2 = EvaluationUtils.defaultComparator;

    public void addExpected(StringRelation relation) {
        expectedValue.add(relation);
    }

    public void addExtracted(StringRelation relation) {
        extractedValue.add(relation);
    }

    @Override
    public boolean hasExpected() {
        return expectedValue != null && !expectedValue.isEmpty();
    }

    @Override
    public boolean hasExtracted() {
        return extractedValue != null && !extractedValue.isEmpty();
    }

    @Override
    public Double getPrecision() {
        if (!hasExtracted()) {
            return null;
        }
        if (precision != null) {
            return precision;
        }
        int correct = 0;
        List<StringRelation> tmp = new ArrayList<StringRelation>(expectedValue);
        external:
        for (StringRelation partExt : extractedValue) {
            for (StringRelation partExp : tmp) {
                if (comp1.compare(partExt.element1, partExp.element1) == 0
                        && comp2.compare(partExt.element2, partExp.element2) == 0) {
                    ++correct;
                    tmp.remove(partExp);
                    continue external;
                }
            }
        }
        precision = (double) correct / extractedValue.size();
        return precision;
    }

    @Override
    public Double getRecall() {
        if (!hasExpected()) {
            return null;
        }
        if (recall != null) {
            return recall;
        }
        int correct = 0;
        List<StringRelation> tmp = new ArrayList<StringRelation>(expectedValue);
        external:
        for (StringRelation partExt : extractedValue) {
            internal:
            for (StringRelation partExp : tmp) {
                if (comp1.compare(partExt.element1, partExp.element1) == 0
                        && comp2.compare(partExt.element2, partExp.element2) == 0) {
                    ++correct;
                    tmp.remove(partExp);
                    continue external;
                }
            }
        }
        recall =  (double) correct / expectedValue.size();
        return recall;
    }
    
    @Override
    public Double getF1() {
        if (getPrecision() == null && getRecall() == null) {
            return null;
        }
        if (getPrecision() == null || getRecall() == null || getPrecision() + getRecall() == 0) {
            return 0.;
        }
        return 2*getPrecision()*getRecall()/(getPrecision()+getRecall());
    }

    public void setComp1(Comparator<String> comp1) {
        this.comp1 = comp1;
    }

    public void setComp2(Comparator<String> comp2) {
        this.comp2 = comp2;
    }
    
    public void print(int mode, String name) {
        if (mode == 0) {
            System.out.println("");
            System.out.println("Expected " + name + ":");
            for (StringRelation expected : expectedValue) {
                System.out.println("    " + expected);
            }
            System.out.println("Extracted " + name + ":");
            for (StringRelation extracted : extractedValue) {
                System.out.println("    " + extracted);
            }
            System.out.printf("Precision: %4.2f\n", getPrecision());
            System.out.printf("Recall: %4.2f\n", getRecall());
        }
        if (mode == 1) {
            if (!hasExtracted() && !hasExpected()) {
                System.out.print("null");
            } else if (!hasExtracted() || !hasExpected()) {
                System.out.print("0");
            } else {
                System.out.print(getF1());
            }
            System.out.print(",");
        }
    }

    public static class StringRelation {

        private String element1;
        private String element2;

        public StringRelation(String element1, String element2) {
            this.element1 = element1;
            this.element2 = element2;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final StringRelation other = (StringRelation) obj;
            if ((this.element1 == null) ? (other.element1 != null) : !this.element1.equals(other.element1)) {
                return false;
            }
            return (this.element2 == null) ? (other.element2 == null) : this.element2.equals(other.element2);
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 67 * hash + (this.element1 != null ? this.element1.hashCode() : 0);
            hash = 67 * hash + (this.element2 != null ? this.element2.hashCode() : 0);
            return hash;
        }

        @Override
        public String toString() {
            return element1 + " --- " + element2;
        }
    }
}