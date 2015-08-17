package pl.edu.icm.cermine.evaluation.tools;

import java.util.Comparator;
import javax.xml.xpath.XPathExpressionException;
import org.w3c.dom.Document;

public class MetadataSingle {

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
        correct = comp.compare(expectedValue, extractedValue) == 0;
        return correct;
    }

    public boolean hasExpected() {
        return expectedValue != null && !expectedValue.isEmpty();
    }

    public boolean hasExtracted() {
        return extractedValue != null && !extractedValue.isEmpty();
    }

    public int expectedSize() {
        return expectedValue == null || expectedValue.isEmpty() ? 0 : 1;
    }

    public int extractedSize() {
        return extractedValue == null || extractedValue.isEmpty() ? 0 : 1;
    }

    public int correctSize() {
        return hasExpected() && hasExtracted() && isCorrect() ? 1 : 0;
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
    
    public void print(String name) {
        System.out.println("");
        System.out.println("Expected " + name + ": " + expectedValue);
        System.out.println("Extracted " + name + ": " + extractedValue);
        System.out.println("Correct: " + (isCorrect() ? "yes" : "no"));
    }
    
}
