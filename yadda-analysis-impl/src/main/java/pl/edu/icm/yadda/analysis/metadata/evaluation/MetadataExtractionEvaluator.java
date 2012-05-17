package pl.edu.icm.yadda.analysis.metadata.evaluation;

import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import pl.edu.icm.yadda.analysis.textr.MetadataExtractor;
import pl.edu.icm.yadda.analysis.textr.model.BxDocument;
import pl.edu.icm.yadda.analysis.textr.transformers.TrueVizToBxDocumentReader;
import pl.edu.icm.yadda.bwmeta.model.YAffiliation;
import pl.edu.icm.yadda.bwmeta.model.YAncestor;
import pl.edu.icm.yadda.bwmeta.model.YAttribute;
import pl.edu.icm.yadda.bwmeta.model.YContributor;
import pl.edu.icm.yadda.bwmeta.model.YDate;
import pl.edu.icm.yadda.bwmeta.model.YDescription;
import pl.edu.icm.yadda.bwmeta.model.YElement;
import pl.edu.icm.yadda.bwmeta.model.YExportable;
import pl.edu.icm.yadda.bwmeta.model.YId;
import pl.edu.icm.yadda.bwmeta.model.YName;
import pl.edu.icm.yadda.bwmeta.model.YStructure;
import pl.edu.icm.yadda.bwmeta.model.YTagList;
import pl.edu.icm.yadda.bwmeta.transformers.BwmetaTransformers;
import pl.edu.icm.yadda.metadata.transformers.IMetadataReader;
import pl.edu.icm.yadda.metadata.transformers.IMetadataWriter;

/**
 *
 * @author acz
 */
public class MetadataExtractionEvaluator extends AbstractDualInputEvaluator<YElement, MetadataExtractionEvaluator.EvalResult> {

    private static final Pattern FILENAME_PATTERN = Pattern.compile("(.+)\\.xml");
    private static final String EXPECTED_FILENAME_REPLACEMENT = "$1.bwmeta";
    private static final String DEFAULT_CONFIGURATION_PATH =
            "pl/edu/icm/yadda/analysis/metadata/evaluation/metadataExtraction-configuration.xml";
    private MetadataExtractor<YElement> metadataExtractor;

    public void setMetadataExtractor(MetadataExtractor metadataExtractor) {
        this.metadataExtractor = metadataExtractor;
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
    protected YElement getExpectedDocument(Reader input) throws Exception {
        IMetadataReader<YExportable> yReader = BwmetaTransformers.BTF.getReader(
                BwmetaTransformers.BWMETA_2_1, BwmetaTransformers.Y);
        List<YExportable> yList = yReader.read(input);
        return (YElement) yList.get(0);
    }
    private TrueVizToBxDocumentReader reader = new TrueVizToBxDocumentReader();

    @Override
    protected YElement getActualDocument(Reader input) throws Exception {
        BxDocument document = new BxDocument().setPages(reader.read(input));
        return metadataExtractor.extractMetadata(document);
    }

    @Override
    protected EvalResult newResults() {
        return new EvalResult();
    }

    @Override
    protected EvalResult compareDocuments(YElement expected, YElement tested) {
        EvalResult result = newResults();

        for (Comparators field : Comparators.values()) {
            field.compare(expected, tested, result);
        }

        return result;
    }

    @Override
    protected void writeDocument(YElement document, Writer writer) throws Exception {
        IMetadataWriter<YExportable> metadataWriter = BwmetaTransformers.BTF.getWriter(BwmetaTransformers.Y, BwmetaTransformers.BWMETA_2_1);
        List<YExportable> yList = new ArrayList<YExportable>();
        yList.add(document);
        metadataWriter.write(writer, yList);
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
        AbstractEvaluator.main("MetadataExtractionEvaluator", args, DEFAULT_CONFIGURATION_PATH);
    }

    protected static class EvalResult implements AbstractEvaluator.Results<EvalResult> {

        Map<String, EnumMap<ResultStatus, Integer>> fieldResults;
        int totalCount;

        public EvalResult() {
            fieldResults = new HashMap<String, EnumMap<ResultStatus, Integer>>();
            totalCount = 0;
        }

        @Override
        public void add(EvalResult partialResult) {
            for (String field : partialResult.fieldResults.keySet()) {
                for (ResultStatus rq : partialResult.fieldResults.get(field).keySet()) {
                    this.append(field, rq, partialResult.fieldResults.get(field).get(rq));
                }
            }
        }

        private void append(String field, ResultStatus rs, int number) {
            if (rs != null && number > 0) {
                EnumMap<ResultStatus, Integer> resCount;

                if (fieldResults.containsKey(field)) {
                    resCount = fieldResults.get(field);
                } else {
                    resCount = new EnumMap<ResultStatus, Integer>(ResultStatus.class);
                    for (ResultStatus rsLoop : ResultStatus.values()) {
                        resCount.put(rsLoop, 0);
                    }
                    fieldResults.put(field, resCount);
                }

                resCount.put(rs, resCount.get(rs) + number);

                if (rs != ResultStatus.REDUNDANT) {
                    totalCount += number;
                }
            }
        }

        private void print() {
            EnumMap<ResultStatus, Integer> statusCounts = new EnumMap<ResultStatus, Integer>(ResultStatus.class);
            for (ResultStatus rs : ResultStatus.values()) {
                statusCounts.put(rs, 0);
            }

            System.out.println("Results by field:");
            for (String field : fieldResults.keySet()) {
                System.out.println(field + ":");
                int recognCount = fieldResults.get(field).get(ResultStatus.RECOGNIZED);
                int failedCount = fieldResults.get(field).get(ResultStatus.FAILED);
                int redundCount = fieldResults.get(field).get(ResultStatus.REDUNDANT);

                if (recognCount > 0) {
                    System.out.format("    recognized: %d/%d (%.2f%%)%n", recognCount, recognCount + failedCount, 100.0 * recognCount / (recognCount + failedCount));
                    statusCounts.put(ResultStatus.RECOGNIZED, statusCounts.get(ResultStatus.RECOGNIZED) + recognCount);
                }
                if (failedCount > 0) {
                    System.out.format("    failed: %d/%d (%.2f%%)%n", failedCount, recognCount + failedCount, 100.0 * failedCount / (recognCount + failedCount));
                    statusCounts.put(ResultStatus.FAILED, statusCounts.get(ResultStatus.FAILED) + failedCount);
                }
                if (redundCount > 0) {
                    System.out.format("    redundant: %d%n", redundCount);
                    statusCounts.put(ResultStatus.REDUNDANT, statusCounts.get(ResultStatus.REDUNDANT) + failedCount);
                }
            }

            System.out.format("%nAll fields:%n");
            for (ResultStatus rq : EnumSet.of(ResultStatus.RECOGNIZED, ResultStatus.FAILED)) {
                System.out.format("    %s: %d/%d (%.2f%%)%n", rq, statusCounts.get(rq), totalCount, 100.0 * statusCounts.get(rq) / totalCount);
            }
            System.out.format("    %s: %d%n", ResultStatus.REDUNDANT, statusCounts.get(ResultStatus.REDUNDANT));
        }
    }

    private enum ResultStatus {

        RECOGNIZED,
        FAILED,
        REDUNDANT;
    }

    private static <T> void compareLists(List<T> expected, List<T> actual, String label, EvalResult result) {

        int recognCount = 0;
        int failedCount = 0;
        int redundCount = 0;

        Set<T> tSet = new HashSet(actual);

        for (T item : expected) {
            if (tSet.remove(item)) {
                recognCount++;
            } else {
                failedCount++;
            }
        }
        redundCount = tSet.size() - failedCount;

        result.append(label, ResultStatus.RECOGNIZED, recognCount);
        result.append(label, ResultStatus.FAILED, failedCount);
        result.append(label, ResultStatus.REDUNDANT, redundCount);
    }

    private enum Comparators {

        DATES {

            @Override
            void compare(YElement expected, YElement actual, EvalResult result) {
                List<YDate> expDates = expected.getDates();

                Map<String, YDate> actDatesMap = new HashMap<String, YDate>();
                for (YDate date : actual.getDates()) {
                    String type = date.getType();
                    actDatesMap.put(type, date);
                }

                for (YDate date : expDates) {
                    String type = date.getType();
                    if (actDatesMap.containsKey(type)) {
                        if (date.equals(actDatesMap.get(type))) {
                            result.append("Date - " + type, ResultStatus.RECOGNIZED, 1);
                        } else {
                            result.append("Date - " + type, ResultStatus.FAILED, 1);
                        }
                        actDatesMap.remove(type);
                    } else {
                        result.append("Date - " + type, ResultStatus.FAILED, 1);
                    }
                }

                for (String type : actDatesMap.keySet()) {
                    result.append("Date - " + type, ResultStatus.REDUNDANT, 1);
                }
            }
        },
        ATTRIBUTES {

            @Override
            void compare(YElement expected, YElement actual, EvalResult result) {

                List<YAttribute> expAttributes = expected.getAttributes();

                Map<String, List<String>> actAttMap = new HashMap<String, List<String>>();
                for (YAttribute yatt : actual.getAttributes()) {
                    String key = yatt.getKey();
                    if (actAttMap.get(key) == null) {
                        actAttMap.put(key, new ArrayList<String>());
                    }

                    actAttMap.get(key).add(yatt.getValue());
                }

                for (YAttribute yatt : expAttributes) {
                    String key = yatt.getKey();
                    if (actAttMap.containsKey(key)) {
                        if (actAttMap.get(key).contains(yatt.getValue())) {
                            result.append(key, ResultStatus.RECOGNIZED, 1);
                        } else {
                            result.append(key, ResultStatus.FAILED, 1);
                        }
                        //actAttMap.remove(key);
                    } else {
                        result.append(key, ResultStatus.FAILED, 1);
                    }
                }

                for (String key : actAttMap.keySet()) {
                    result.append(key, ResultStatus.REDUNDANT, 1);
                }
            }
        },
        AFFILIATION {

            @Override
            void compare(YElement expected, YElement actual, EvalResult result) {

                List<YAffiliation> expAff = expected.getAffiliations();

                Map<String, YAffiliation> actAffMap = new HashMap<String, YAffiliation>();
                for (YAffiliation yaff : actual.getAffiliations()) {
                    String id = yaff.getId();
                    actAffMap.put(id, yaff);
                }

                for (YAffiliation yaff : expAff) {
                    String id = yaff.getId();

                    if (actAffMap.containsKey(id)) {
                        if (yaff.getSimpleText().equalsIgnoreCase(actAffMap.get(id).getSimpleText())) {
                            result.append("Affiliation", ResultStatus.RECOGNIZED, 1);
                        } else {
                            result.append("Affiliation", ResultStatus.FAILED, 1);
                        }
                        actAffMap.remove(id);
                    } else {
                        result.append("Affiliation", ResultStatus.FAILED, 1);
                    }
                }
                for (String id : actAffMap.keySet()) {
                    result.append("Affiliation", ResultStatus.REDUNDANT, 1);
                }
            }
        },
        CONTRIBUTORS { // author, editor

            @Override
            void compare(YElement expected, YElement actual, EvalResult result) {
                Map<String, List<String>> expContrMap = new HashMap<String, List<String>>();
                for (YContributor contr : expected.getContributors()) {
                    String role = contr.getRole();

                    String name = contr.getOneName().getText().toLowerCase();
                    if (expContrMap.containsKey(role)) {
                        expContrMap.get(role).add(name);
                    } else {
                        //expContrMap.put(role, Arrays.asList(name));
                        List<String> list = new ArrayList<String>();
                        list.add(name);
                        expContrMap.put(role, list);
                    }
                }

                Map<String, List<String>> actContrMap = new HashMap<String, List<String>>();
                for (YContributor contr : actual.getContributors()) {
                    String role = contr.getRole();
                    String name = contr.getOneName().getText().toLowerCase();
                    if (actContrMap.containsKey(role)) {
                        actContrMap.get(role).add(name);
                    } else {
                        //actContrMap.put(role, Arrays.asList(name));
                        List<String> list = new ArrayList<String>();
                        list.add(name);
                        actContrMap.put(role, list);
                    }
                }

                for (String role : expContrMap.keySet()) {
                    if (actContrMap.containsKey(role)) {
                        // 1. compare lists and save results
                        compareLists(expContrMap.get(role), actContrMap.get(role), role, result);

                        // 2. remove entry from actContrMap
                        actContrMap.remove(role);
                    } else {
                        // save to result the number
                        result.append(role, ResultStatus.FAILED, expContrMap.get(role).size());
                    }
                }

                for (String role : actContrMap.keySet()) {
                    // only keys not found in expContrMap
                    result.append(role, ResultStatus.REDUNDANT, actContrMap.get(role).size());
                }
            }
        },
        DESCRIPTION {

            @Override
            void compare(YElement expected, YElement actual, EvalResult result) {

                Map<String, YDescription> actYDescMap = new HashMap<String, YDescription>();
                for (YDescription ydescr : actual.getDescriptions()) {
                    String type = ydescr.getType();
                    actYDescMap.put(type, ydescr);
                }

                for (YDescription ydescr : expected.getDescriptions()) {
                    String type = ydescr.getType();
                    if (actYDescMap.containsKey(type)) {
                        if (ydescr.getText().equals(actYDescMap.get(type).getText())) {
                            result.append(type, ResultStatus.RECOGNIZED, 1);
                        } else {
                            result.append(type, ResultStatus.FAILED, 1);
                        }
                        actYDescMap.remove(type);
                    } else {
                        result.append(type, ResultStatus.FAILED, 1);
                    }
                }
                for (String type : actYDescMap.keySet()) {
                    result.append(type, ResultStatus.REDUNDANT, 1);
                }
            }
        },
        ID { // doi, issn, urn

            @Override
            void compare(YElement expected, YElement actual, EvalResult result) {

                Map<String, YId> actYIdMap = new HashMap<String, YId>();
                for (YId yid : actual.getIds()) {
                    String scheme = yid.getScheme();
                    actYIdMap.put(scheme, yid);
                }

                for (YId yid : expected.getIds()) {
                    String scheme = yid.getScheme();
                    if (actYIdMap.containsKey(scheme)) {
                        if (yid.getValue().equals(actYIdMap.get(scheme).getValue())) {
                            result.append(scheme, ResultStatus.RECOGNIZED, 1);
                        } else {
                            result.append(scheme, ResultStatus.FAILED, 1);
                        }
                        actYIdMap.remove(scheme);
                    } else {
                        result.append(scheme, ResultStatus.FAILED, 1);
                    }
                }

                for (String scheme : actYIdMap.keySet()) {
                    result.append(scheme, ResultStatus.REDUNDANT, 1);
                }
            }
        },
        STRUCTURES { //publisher, journal, volume, number (issue)

            @Override
            void compare(YElement expected, YElement actual, EvalResult result) {

                Map<String, YStructure> actStructMap = new HashMap<String, YStructure>();
                for (YStructure ystruct : actual.getStructures()) {
                    String hierarchy = ystruct.getHierarchy();
                    actStructMap.put(hierarchy, ystruct);
                }

                for (YStructure expYStruct : expected.getStructures()) {
                    String hierarchy = expYStruct.getHierarchy();

                    if (actStructMap.containsKey(hierarchy)) {

                        // the structures in the same hierarchy and at the same level are compared

                        YStructure actYStruct = actStructMap.get(hierarchy);

                        Map<String, YAncestor> actAncMap = new HashMap<String, YAncestor>();

                        for (YAncestor actAnc : actYStruct.getAncestors()) {
                            String level = actAnc.getLevel();
                            actAncMap.put(level, actAnc);
                        }

                        for (YAncestor expAnc : expYStruct.getAncestors()) {
                            String level = expAnc.getLevel();
                            if (actAncMap.containsKey(level)) {
                                // compare expAnc.getNames() and actAncMap.get(level).getNames()
                                List<String> expNames = new ArrayList<String>();
                                List<String> actNames = new ArrayList<String>();

                                for (YName yname : expAnc.getNames()) {
                                    expNames.add(yname.getText());
                                }
                                for (YName yname : actAncMap.get(level).getNames()) {
                                    actNames.add(yname.getText());
                                }

                                compareLists(expNames, actNames, level, result);

                                actAncMap.remove(level);
                            } else {
                                result.append(level, ResultStatus.FAILED, expAnc.getNames().size());
                            }
                        }

                        for (String level : actAncMap.keySet()) {
                            result.append(level, ResultStatus.REDUNDANT, 1);
                        }


                    } else {
                        // YStructure in some hierachy is present in expected and absent in actual
                        for (YAncestor yanc : expYStruct.getAncestors()) {
                            String level = yanc.getLevel();
                            result.append(level, ResultStatus.FAILED, 1);
                        }

                    }
                    actStructMap.remove(hierarchy);
                }

                for (String hierarchy : actStructMap.keySet()) {
                    // YStructure in some hierachy is present in actual and absent in expected
                    for (YAncestor yanc : actStructMap.get(hierarchy).getAncestors()) {
                        String level = yanc.getLevel();
                        result.append(level, ResultStatus.REDUNDANT, 1);
                    }

                }
            }
        },
        TAGLISTS {

            @Override
            void compare(YElement expected, YElement actual, EvalResult result) {

                Map<String, List<String>> actTagLists = new HashMap<String, List<String>>();
                for (YTagList yTagList : actual.getTagLists()) {
                    String type = yTagList.getType();
                    List<String> values = yTagList.getValues();
                    actTagLists.put(type, values);
                }

                for (YTagList yTagList : expected.getTagLists()) {
                    String type = yTagList.getType();
                    if (actTagLists.containsKey(type)) {
                        compareLists(yTagList.getValues(), actTagLists.get(type), type, result);
                        actTagLists.remove(type);
                    } else {
                        result.append(type, ResultStatus.FAILED, yTagList.size());
                    }
                }

                for (String type : actTagLists.keySet()) {
                    result.append(type, ResultStatus.REDUNDANT, actTagLists.get(type).size());
                }
            }
        },
        TITLE {

            @Override
            void compare(YElement expected, YElement actual, EvalResult result) {
                List<String> expNames = new ArrayList<String>();
                List<String> actNames = new ArrayList<String>();

                for (YName yname : expected.getNames()) {
                    expNames.add(yname.getText().toLowerCase().replaceAll(" ", ""));
                }
                for (YName yname : actual.getNames()) {
                    actNames.add(yname.getText().toLowerCase().replaceAll(" ", ""));
                }

                compareLists(expNames, actNames, "Title", result);
            }
        };

        abstract void compare(YElement expected, YElement actual, EvalResult result);
    }
}
