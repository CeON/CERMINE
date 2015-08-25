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

import java.util.Comparator;
import javax.xml.xpath.XPathExpressionException;
import org.w3c.dom.Document;

public class MetadataSingle implements ComparisonResult {

    private String expectedValue;
    private String extractedValue;
    private Boolean correct;
    
    private Comparator<String> comp = EvaluationUtils.defaultComparator;

    public MetadataSingle(Document originalNlm, String originalXPath, Document extractedNlm, String extractedXPath) throws XPathExpressionException {
        this(XMLTools.extractTextFromNode(originalNlm, originalXPath).trim(),
                XMLTools.extractTextFromNode(extractedNlm, extractedXPath).trim());
    }
    
    public MetadataSingle(String expectedValue, String extractedValue) {
        this.expectedValue = expectedValue;
        this.extractedValue = extractedValue;
        this.correct = null;
    }

    public boolean isCorrect() {
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

    public String getExpectedValue() {
        return expectedValue;
    }

    public void setExpectedValue(String expectedValue) {
        this.expectedValue = expectedValue;
    }

    public String getExtractedValue() {
        return extractedValue;
    }

    public void setExtractedValue(String extractedValue) {
        this.extractedValue = extractedValue;
    }

    public void setComp(Comparator<String> comp) {
        this.comp = comp;
    }
    
    public void print(int mode, String name) {
        if (mode == 0) {
            System.out.println("");
            System.out.println("Expected " + name + ": " + expectedValue);
            System.out.println("Extracted " + name + ": " + extractedValue);
            System.out.println("Correct: " + (isCorrect() ? "yes" : "no"));
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
        if (getPrecision() == null || getRecall() == null) {
            return null;
        }
        return isCorrect() ? 1. : 0.;
    }
    
}
