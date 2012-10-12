package pl.edu.icm.cermine.evaluation;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import pl.edu.icm.cermine.bibref.BibReferenceExtractor;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.transformers.TrueVizToBxDocumentReader;

/**
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 * @author krusek
 */
public class ReferenceExtractionEvaluator extends AbstractDualInputEvaluator<String[],
        ReferenceExtractionEvaluator.EvalResult> {

    private static final Pattern FILENAME_PATTERN = Pattern.compile("(.+)\\.xml");
    private static final String EXPECTED_FILENAME_REPLACEMENT = "$1.txt";

    private static final String DEFAULT_CONFIGURATION_PATH =
            "pl/edu/icm/cermine/metadata/evaluation/referenceExtraction-configuration.xml";

    private BibReferenceExtractor referenceExtractor;

    public void setReferenceExtractor(BibReferenceExtractor referenceExtractor) {
        this.referenceExtractor = referenceExtractor;
    }

    @Override
    protected Pattern getActualFilenamePattern() {
        return FILENAME_PATTERN;
    }

    @Override
    protected String getExpectedFilenameReplacement() {
        return EXPECTED_FILENAME_REPLACEMENT;
    }

    @Override
    protected String[] getExpectedDocument(Reader input) throws Exception {
        List<String> lines = new ArrayList<String>();
        String line;
        BufferedReader lineInput = new BufferedReader(input);
        while ((line = lineInput.readLine()) != null) {
            lines.add(line);
        }
        return lines.toArray(new String[lines.size()]);
    }

    private TrueVizToBxDocumentReader reader = new TrueVizToBxDocumentReader();

    @Override
    protected String[] getActualDocument(Reader input) throws Exception {
        BxDocument document = new BxDocument().setPages(reader.read(input));
        return referenceExtractor.extractBibReferences(document);
    }

    @Override
    protected EvalResult newResults() {
        return new EvalResult();
    }

    @Override
    protected EvalResult compareDocuments(String[] expected, String[] tested) {
        EvalResult result = new EvalResult();

        int extracted = 0;
        List<String> extractedRefs = Arrays.asList(tested);
        for (String ref : expected) {
            if (extractedRefs.contains(ref)) {
                result.addExtractedReference();
                extracted++;
            } else {
                result.addFailedReference();
            }
        }
        if (extracted == expected.length) {
            result.addPerfectDocument();
        } else if (extracted * 10 >= expected.length * 8) {
            result.addGoodDocument();
        } else {
            result.addFailedDocument();
        }

        return result;
    }

    @Override
    protected void printDocumentResults(EvalResult result) {
        result.print();
    }

    @Override
    protected void printFinalResults(EvalResult result) {
        result.print();
    }

    public static void main(String[] args) throws Exception {
        AbstractEvaluator.main("ReferenceExtractionEvaluator", args, DEFAULT_CONFIGURATION_PATH);
    }

    protected static class EvalResult implements AbstractEvaluator.Results<EvalResult> {

        private int totalDocuments = 0;
        private int perfectDocuments = 0;
        private int goodDocuments = 0;

        private int totalReferences = 0;
        private int extractedReferences = 0;

        private void addPerfectDocument() {
            totalDocuments++;
            perfectDocuments++;
        }

        private void addGoodDocument() {
            totalDocuments++;
            goodDocuments++;
        }

        private void addFailedDocument() {
            totalDocuments++;
        }

        private void addExtractedReference() {
            totalReferences++;
            extractedReferences++;
        }

        private void addFailedReference() {
            totalReferences++;
        }

        @Override
        public void add(EvalResult partialResult) {
            goodDocuments += partialResult.getGoodDocuments();
            perfectDocuments += partialResult.getPerfectDocuments();
            totalDocuments += partialResult.getTotalDocuments();
            extractedReferences += partialResult.getExtractedReferences();
            totalReferences += partialResult.getTotalReferences();
        }

        private void print() {
            System.out.println("References:");
            System.out.print("    " + (100d * extractedReferences / totalReferences) + "%");
            System.out.println(" (" + extractedReferences + " out of " + totalReferences + ")");
            System.out.println("Documents with 100% references extracted:");
            System.out.print("    " + (100d * perfectDocuments / totalDocuments) + "%");
            System.out.println(" (" + perfectDocuments + " out of " + totalDocuments + ")");
            System.out.println("Documents with at least 80% references extracted:");
            System.out.print("    " + (100d * (perfectDocuments + goodDocuments) / totalDocuments) + "%");
            System.out.println(" (" + (perfectDocuments + goodDocuments) + " out of " + totalDocuments + ")");
        }

        public int getGoodDocuments() {
            return goodDocuments;
        }

        public int getPerfectDocuments() {
            return perfectDocuments;
        }

        public int getTotalDocuments() {
            return totalDocuments;
        }

        public int getExtractedReferences() {
            return extractedReferences;
        }

        public int getTotalReferences() {
            return totalReferences;
        }

    }
}
