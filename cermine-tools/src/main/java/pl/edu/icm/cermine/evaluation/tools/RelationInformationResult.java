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

package pl.edu.icm.cermine.evaluation.tools;

import java.util.*;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class RelationInformationResult implements SingleInformationDocResult<Set<RelationInformationResult.StringRelation>> {

    private EvalInformationType type;
    
    private Set<StringRelation> expectedValue;
    private Set<StringRelation> extractedValue;

    private Double precision;
    private Double recall;
    
    private Comparator<String> comp1;
    private Comparator<String> comp2;

    public RelationInformationResult(EvalInformationType type, Set<StringRelation> expected, Set<StringRelation> extracted) {
        this(type, EvaluationUtils.defaultComparator, EvaluationUtils.defaultComparator,
                expected, extracted);
    }
    
    public RelationInformationResult(EvalInformationType type, Comparator<String> comp1,
            Comparator<String> comp2, Set<StringRelation> expected, Set<StringRelation> extracted) {
        this.type = type;
        this.comp1 = comp1;
        this.comp2 = comp2;
        this.expectedValue = expected;
        this.extractedValue = extracted;
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

    @Override
    public EvalInformationType getType() {
        return type;
    }

    @Override
    public void prettyPrint() {
        System.out.println("");
        System.out.println("Expected " + type + ":");
        for (StringRelation expected : expectedValue) {
            System.out.println("    " + expected);
        }
        System.out.println("Extracted " + type + ":");
        for (StringRelation extracted : extractedValue) {
            System.out.println("    " + extracted);
        }
        System.out.printf("Precision: %4.2f\n", getPrecision());
        System.out.printf("Recall: %4.2f\n", getRecall());
    }

    @Override
    public void printCSV() {
        if (!hasExtracted() && !hasExpected()) {
            System.out.print("NA");
        } else if (!hasExtracted() || !hasExpected()) {
            System.out.print("0");
        } else {
            System.out.print(getF1());
        }
    }

    @Override
    public void setExpected(Set<StringRelation> expected) {
        this.expectedValue = expected;
    }

    @Override
    public void setExtracted(Set<StringRelation> extracted) {
        this.extractedValue = extracted;
    }

    @Override
    public Set<StringRelation> getExpected() {
        return expectedValue;
    }

    @Override
    public Set<StringRelation> getExtracted() {
        return extractedValue;
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