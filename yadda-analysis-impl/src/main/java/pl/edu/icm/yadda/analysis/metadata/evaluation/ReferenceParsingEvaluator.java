package pl.edu.icm.yadda.analysis.metadata.evaluation;

import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pl.edu.icm.yadda.analysis.AnalysisException;
import pl.edu.icm.yadda.analysis.bibref.BibEntry;
import pl.edu.icm.yadda.analysis.bibref.BibEntryToYTransformer;
import pl.edu.icm.yadda.analysis.bibref.BibReferenceParser;
import pl.edu.icm.yadda.analysis.bibref.YToBibEntryTransformer;
import pl.edu.icm.yadda.analysis.textr.tools.TableFormatter;
import pl.edu.icm.yadda.bwmeta.model.YElement;
import pl.edu.icm.yadda.bwmeta.model.YExportable;
import pl.edu.icm.yadda.bwmeta.transformers.BwmetaTransformers;
import pl.edu.icm.yadda.metadata.transformers.IMetadataReader;
import pl.edu.icm.yadda.metadata.transformers.IMetadataWriter;

/**
 *
 * @author acz
 * @author krusek
 */
public class ReferenceParsingEvaluator extends AbstractSingleInputEvaluator<List<YExportable>, List<BibEntry>, BibEntry, ReferenceParsingEvaluator.Results> {

    private static final Pattern FILENAME_PATTERN = Pattern.compile("(.+)\\.xml");
    private static final String DEFAULT_CONFIGURATION_PATH =
            "pl/edu/icm/yadda/analysis/metadata/evaluation/referenceParsing-configuration.xml";
    private BibReferenceParser<BibEntry> bibReferenceParser;
    private int minOverlap;
    private final Set<String> ignoredFieldKeys = new HashSet<String>();

    public void setReferenceParser(BibReferenceParser<BibEntry> parser) {
        bibReferenceParser = parser;
    }

    public void setMinOverlap(int minOverlap) {
        this.minOverlap = minOverlap;
    }

    public void setIgnoredFieldKeys(Collection<String> fieldKeys) {
        ignoredFieldKeys.clear();
        ignoredFieldKeys.addAll(fieldKeys);
    }

    @Override
    protected Pattern getFilenamePattern() {
        return FILENAME_PATTERN;
    }

    @Override
    protected Results newResults() {
        return new Results();
    }
    private IMetadataReader<YExportable> yReader = BwmetaTransformers.BTF.getReader(
            BwmetaTransformers.BWMETA_2_1, BwmetaTransformers.Y);

    @Override
    protected List<YExportable> readDocument(Reader input) throws Exception {
        return yReader.read(input);
    }
    private IMetadataWriter<YExportable> yWriter = BwmetaTransformers.BTF.getWriter(
            BwmetaTransformers.Y, BwmetaTransformers.BWMETA_2_1);

    @Override
    protected void writeDocument(List<BibEntry> document, Writer output) throws Exception {
        List<YExportable> yDocument = new ArrayList<YExportable>(document.size());
        int id = 0;
        for (BibEntry entry : document) {
            YElement element = (YElement) bibEntryToY.convert(entry);
            element.setId(Integer.toString(++id));
            yDocument.add(element);
        }
        yWriter.write(output, yDocument);
    }
    private YToBibEntryTransformer yToBibEntry = new YToBibEntryTransformer();
    
    @Override
    protected List<BibEntry> prepareExpectedDocument(List<YExportable> document) throws Exception {
        List<BibEntry> entries = new ArrayList<BibEntry>(document.size());
        for (YExportable element : document) {
            entries.add(yToBibEntry.convert(element));
        }
        return entries;
    }
    private BibEntryToYTransformer bibEntryToY = new BibEntryToYTransformer();

    @Override
    protected List<BibEntry> prepareActualDocument(List<YExportable> document) throws Exception {
        List<BibEntry> entries = new ArrayList<BibEntry>(document.size());
        for (YExportable element : document) {
            String text = ((YElement) element).getOneAttributeSimpleValue("text");
            BibEntry entry = bibReferenceParser.parseBibReference(text);
            // Provide default values for missing fields
            //entry = yToBibEntry.convert(bibEntryToY.convert(entry));
            entry.setText(text);
            entries.add(entry);
        }
        return entries;
    }

    @Override
    protected Iterator<BibEntry> iterateItems(List<BibEntry> document) {
        return document.iterator();
    }

    @Override
    protected Results compareItems(BibEntry expected, BibEntry actual) {
        Results results = newResults();
        for (String key : expected.getFieldKeys()) {
            if (ignoredFieldKeys.contains(key)) {
                continue;
            }

            List<String> actualValues = new ArrayList<String>();
            for (String value : actual.getAllFieldValues(key)) {
                actualValues.add(value);
            }

            List<String> expectedValues = new ArrayList<String>();
            for (String value : expected.getAllFieldValues(key)) {
                if (!value.isEmpty()) {
                    expectedValues.add(value);
                }
            }

            for (ResultQuality rq : ResultQuality.values()) {
                Iterator<String> expectedIterator = expectedValues.iterator();
                expectedLoop:
                while (expectedIterator.hasNext()) {
                    String expectedValue = expectedIterator.next();
                    Iterator<String> actualIterator = actualValues.iterator();
                    while (actualIterator.hasNext()) {
                        if (rq.matches(expectedValue, actualIterator.next(), actual.getText(), this)) {
                            expectedIterator.remove();
                            actualIterator.remove();
                            results.addResult(key, rq);
                            continue expectedLoop;
                        }
                    }
                }
            }
            for (String expectedValue : expectedValues) {
                if (!expectedValue.isEmpty()) {
                    results.addResult(key, ResultQuality.BAD);
                }
            }
        }
        return results;
    }
    private static final Pattern normalizationPattern = Pattern.compile("^[\\s\"'\\.,\u00BB\u00AB]*(.*[^\\s\"'\\.,])?[\\s\"'\\.,\u00BB\u00AB]*$");

    private static String normalizeFieldValue(String value) {
        Matcher matcher = normalizationPattern.matcher(value);
        matcher.matches(); // this should always return true
        String matched = matcher.group(1);
        return (matched == null) ? "" : matched;
    }

    @Override
    protected void printItemResults(BibEntry expected, BibEntry actual, int itemIndex, Results results) {
        System.out.println(actual.getText());
        System.out.println();
    }

    @Override
    protected void printDocumentResults(Results results) {
        results.printLong();
    }

    @Override
    protected void printFinalResults(Results results) {
        results.printLong();
        results.printTotalSummary();
    }

    @Override
    protected List<BibEntry> processDocument(List<YExportable> document)
    		throws AnalysisException {
    	try {
    		return prepareExpectedDocument(document);
    	} catch(Exception e) {
    		return null;
    	}
	}

	@Override
	protected void preprocessDocument(List<YExportable> document) {
	}

	public static class Results implements AbstractEvaluator.Results<Results> {

        private static final int MAX_KEY_LENGTH = 10;
        private static final int MAX_RQ_LENGTH = 16;
        EnumMap<ResultQuality, Integer> referencesQuality;
        HashMap<String, EnumMap<ResultQuality, Integer>> fieldsQuality;
        HashMap<String, Integer> fieldCount;

        public Results() {
            referencesQuality = new EnumMap<ResultQuality, Integer>(ResultQuality.class);
            fieldsQuality = new HashMap<String, EnumMap<ResultQuality, Integer>>();
            fieldCount = new HashMap<String, Integer>();
        }

        private void printLong() {
            TableFormatter formatter = new TableFormatter(System.out, Locale.US);

            formatter.startRow();
            formatter.center("Type", MAX_KEY_LENGTH);
            for (ResultQuality rq : ResultQuality.values()) {
                formatter.center(rq.toString(), MAX_RQ_LENGTH);
            }
            formatter.endRow();
            formatter.startSeparator();
            formatter.separator(MAX_KEY_LENGTH);
            for (ResultQuality rq : ResultQuality.values()) {
                formatter.separator(MAX_RQ_LENGTH);
            }
            formatter.endSeparator();
            for (Map.Entry<String, EnumMap<ResultQuality, Integer>> entry : fieldsQuality.entrySet()) {
                formatter.startRow();
                formatter.left(entry.getKey(), MAX_KEY_LENGTH);
                for (ResultQuality rq : ResultQuality.values()) {
                    Integer value = entry.getValue().get(rq);
                    if (value == null) {
                        value = 0;
                    }
                    formatter.rightFormat("%d (%3.0f%%)", MAX_RQ_LENGTH, value,
                            value * 100.0 / fieldCount.get(entry.getKey()));
                }
                formatter.endRow();
            }
        }

        private void printTotalSummary() {
            int goodCount = 0;
            int badCount = 0;
            int totalCount = 0;
            for (int i : referencesQuality.values()) {
                totalCount += i;
            }

            if (totalCount > 0) {
                System.out.println();
                System.out.println("==== Total summary:");

                for (ResultQuality rq : ResultQuality.values()) {
                    if (referencesQuality.containsKey(rq)) {
                        int count = referencesQuality.get(rq);
                        System.out.format("%s: %d/%d (%.2f%%)%n", rq, count, totalCount, 100.0 * count / totalCount);
                        if (rq.isPositive()) {
                            goodCount += count;
                        } else {
                            badCount += count;
                        }
                    }
                }
                System.out.println();
                System.out.format("Total good results: %d/%d (%.2f%%)%n", goodCount, totalCount, 100.0 * goodCount / totalCount);
                System.out.format("Total bad results: %d/%d (%.2f%%)%n", badCount, totalCount, 100.0 * badCount / totalCount);

            }
        }

        private static <K> void addUp(Map<K, Integer> map, Map<K, Integer> other) {
            for (Map.Entry<K, Integer> entry : other.entrySet()) {
                Integer value = map.get(entry.getKey());
                if (value == null) {
                    map.put(entry.getKey(), entry.getValue());
                } else {
                    map.put(entry.getKey(), value + entry.getValue());
                }
            }
        }

        private static <K> void increment(Map<K, Integer> map, K key) {
            if (map.containsKey(key)) {
                map.put(key, map.get(key) + 1);
            } else {
                map.put(key, 1);
            }
        }

        private void addResult(String fieldKey, ResultQuality rq) {
            increment(referencesQuality, rq);
            if (!fieldsQuality.containsKey(fieldKey)) {
                fieldsQuality.put(fieldKey, new EnumMap<ResultQuality, Integer>(ResultQuality.class));
            }
            increment(fieldsQuality.get(fieldKey), rq);
            increment(fieldCount, fieldKey);
        }

        @Override
        public void add(Results results) {
            addUp(this.referencesQuality, results.referencesQuality);
            addUp(this.fieldCount, results.fieldCount);
            for (String field : results.fieldsQuality.keySet()) {
                EnumMap<ResultQuality, Integer> thisFieldsCounts;
                if (this.fieldsQuality.containsKey(field)) {
                    thisFieldsCounts = this.fieldsQuality.get(field);
                } else {
                    thisFieldsCounts = new EnumMap<ResultQuality, Integer>(ResultQuality.class);
                    this.fieldsQuality.put(field, thisFieldsCounts);
                }
                addUp(thisFieldsCounts, results.fieldsQuality.get(field));
            }
        }
    }

    public static void main(String[] args) throws Exception {
        main("ReferenceParsingEvaluator", args, DEFAULT_CONFIGURATION_PATH);
    }

    private static List<Integer> allOccurences(String searchFor, String base) {
        List<Integer> result = new ArrayList<Integer>();
        int position = 0;
        int occurrenceIndex = -1;
        int baseLength = base.length();
        int searchForLength = searchFor.length();

        while (position + searchForLength <= baseLength) {
            occurrenceIndex = base.indexOf(searchFor, position);
            if (occurrenceIndex != -1) {
                result.add(occurrenceIndex);
                position = occurrenceIndex + 1;
            } else {
                break;
            }
        }

        return result;
    }

    private static enum ResultQuality {

        PERFECT {

            @Override
            boolean matches(String expected, String actual, String text, ReferenceParsingEvaluator self) {
                return expected.equals(actual);
            }

            @Override
            boolean isPositive() {
                return true;
            }
        },
        GOOD {

            @Override
            boolean matches(String expected, String actual, String text, ReferenceParsingEvaluator self) {
                /* identical after normalisation */
                expected = normalizeFieldValue(expected);
                actual = normalizeFieldValue(actual);
                return expected.equals(actual);
            }

            @Override
            boolean isPositive() {
                return true;
            }
        },
        SUPERSTRING {

            @Override
            boolean matches(String expected, String actual, String text, ReferenceParsingEvaluator self) {
                /* actual contains expected after normalisation */
                expected = normalizeFieldValue(expected);
                actual = normalizeFieldValue(actual);
                return actual.indexOf(expected) != -1;
            }
        },
        SUBSTRING {

            @Override
            boolean matches(String expected, String actual, String text, ReferenceParsingEvaluator self) {
                /* actual contains expected after normalisation */
                expected = normalizeFieldValue(expected);
                actual = normalizeFieldValue(actual);
                return expected.indexOf(actual) != -1;
            }
        },
        OVERLAP {

            @Override
            boolean matches(String expected, String actual, String text, ReferenceParsingEvaluator self) {

                List<Integer> list1 = allOccurences(actual, text);
                List<Integer> list2 = allOccurences(expected, text);
                int list1Index = 0;
                int list2Index = 0;
                int length1 = actual.length();
                int length2 = actual.length();

                while (list1Index < list1.size() && list2Index < list2.size()) {
                    if (list1.get(list1Index) > list2.get(list2Index)) {
                        List<Integer> tmpList = list1;
                        list1 = list2;
                        list2 = tmpList;
                        int tmpIndex = list1Index;
                        list1Index = list2Index;
                        list2Index = tmpIndex;
                        int tmpLength = length1;
                        length1 = length2;
                        length2 = tmpLength;
                    }

                    if ((list2.get(list2Index) + self.minOverlap) <= list1.get(list1Index) + length1) {
                        return true;
                    } else {
                        list1Index++;
                    }
                }
                return false;
            }
        },
        BAD {

            @Override
            boolean matches(String expected, String actual, String text, ReferenceParsingEvaluator self) {
                return true;
            }
        };

        boolean matches(String expected, String actual, String text, ReferenceParsingEvaluator self) {
            return false;
        }

        boolean isPositive() {
            return false;
        }
    }

}
