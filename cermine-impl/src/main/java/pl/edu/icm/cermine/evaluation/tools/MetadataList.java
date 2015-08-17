
package pl.edu.icm.cermine.evaluation.tools;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.xml.xpath.XPathExpressionException;
import org.w3c.dom.Document;

/**
 *
 * @author Dominika Tkaczyk
 */
public class MetadataList {

    private List<String> expectedValue;
    private List<String> extractedValue;
    private Double precision;
    private Double recall;
    private Comparator<String> comp = EvaluationUtils.defaultComparator;

    public MetadataList(Document originalNlm, String originalXPath, Document extractedNlm, String extractedXPath) throws XPathExpressionException {
        this(XMLTools.extractTextAsList(originalNlm, originalXPath),
                XMLTools.extractTextAsList(extractedNlm, extractedXPath));
    }
    
    public MetadataList(List<String> expectedValue, List<String> extractedValue) {
        this.expectedValue = expectedValue;
        this.extractedValue = extractedValue;
    }

    public boolean hasExpected() {
        return expectedValue != null && !expectedValue.isEmpty();
    }

    public boolean hasExtracted() {
        return extractedValue != null && !extractedValue.isEmpty();
    }

    public Double getPrecision() {
        if (!hasExtracted()) {
            return null;
        }
        if (precision != null) {
            return precision;
        }
       
        int correct = 0;
        List<String> tmp = new ArrayList<String>(expectedValue);
        external:
        for (String partExt : extractedValue) {
            for (String partExp : tmp) {
                if (comp.compare(partExp, partExt) == 0) {
                    ++correct;
                    tmp.remove(partExp);
                    continue external;
                }
            }
        }
        precision = (double) correct / extractedValue.size();

        return precision;
    }

    public Double getRecall() {
        if (!hasExpected()) {
            return null;
        }
        if (recall != null) {
            return recall;
        }
        
        int correct = 0;
        List<String> tmp = new ArrayList<String>(expectedValue);
        external:
        for (String partExt : extractedValue) {
            internal:
            for (String partExp : tmp) {
                if (comp.compare(partExp, partExt) == 0) {
                    ++correct;
                    tmp.remove(partExp);
                    continue external;
                }
            }
        }
        recall = (double) correct / expectedValue.size();
        
        return recall;
    }

    public void print(String name) {
        System.out.println("");
        System.out.println("Expected " + name + ":");
        for (String expected : expectedValue) {
            System.out.println("    " + expected);
        }
        System.out.println("Extracted " + name + ":");
        for (String extracted : extractedValue) {
            System.out.println("    " + extracted);
        }
        System.out.printf("Precision: %4.2f\n", getPrecision());
        System.out.printf("Recall: %4.2f\n", getRecall());
    }

    public void setComp(Comparator<String> comp) {
        this.comp = comp;
    }

}
