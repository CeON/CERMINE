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

import java.util.Comparator;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class SimpleInformationResult implements SingleInformationDocResult<String> {

    private EvalInformationType type;
    private String expectedValue;
    private String extractedValue;
    private Boolean correct;
    private Comparator<String> comp;

 
    public SimpleInformationResult(EvalInformationType type, String expectedValue, String extractedValue) {
        this(type, EvaluationUtils.defaultComparator, expectedValue, extractedValue);
    }
    
    public SimpleInformationResult(EvalInformationType type, Comparator<String> comp, String expectedValue, String extractedValue) {
        this.expectedValue = expectedValue;
        this.extractedValue = extractedValue;
        this.correct = null;
        this.type = type;
        this.comp = comp;
    }

    private boolean isCorrect() {
        if (correct != null) {
            return correct;
        }
        correct = hasExpected() && hasExtracted() && comp.compare(expectedValue, extractedValue) == 0;
        return correct;
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
    public String getExpected() {
        return expectedValue;
    }

    @Override
    public void setExpected(String expectedValue) {
        this.expectedValue = expectedValue;
    }

    @Override
    public String getExtracted() {
        return extractedValue;
    }

    @Override
    public void setExtracted(String extractedValue) {
        this.extractedValue = extractedValue;
    }

    @Override
    public Double getPrecision() {
        if (!hasExtracted()) {
            return null;
        }
        return isCorrect() ? 1. : 0.;
    }

    @Override
    public Double getRecall() {
        if (!hasExpected()) {
            return null;
        }
        return isCorrect() ? 1. : 0.;
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
        System.out.println("Expected " + type + ": " + expectedValue);
        System.out.println("Extracted " + type + ": " + extractedValue);
        System.out.println("Correct: " + (isCorrect() ? "yes" : "no"));
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
    
}
