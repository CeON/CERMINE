package pl.edu.icm.yadda.analysis.metadata.evaluation;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import pl.edu.icm.yadda.analysis.metadata.evaluation.AbstractEvaluator.Results;
import pl.edu.icm.yadda.analysis.textr.ZoneClassifier;
import pl.edu.icm.yadda.analysis.textr.model.BxDocument;
import pl.edu.icm.yadda.analysis.textr.model.BxPage;
import pl.edu.icm.yadda.analysis.textr.model.BxZone;
import pl.edu.icm.yadda.analysis.textr.model.BxZoneLabel;
import pl.edu.icm.yadda.analysis.textr.tools.DocumentFlattener;
import pl.edu.icm.yadda.analysis.textr.tools.UnclassifiedZonesFlattener;

/**
 *
 * @author acz
 */
public class ClassificationEvaluator extends AbstractBxModelEvaluator<ClassificationEvaluator.Results> {

    static private final EnumMap<BxZoneLabel, BxZoneLabel> DEFAULT_LABEL_MAP
            = new EnumMap<BxZoneLabel, BxZoneLabel>(BxZoneLabel.class);

    static {
        for (BxZoneLabel label : BxZoneLabel.values()) {
            DEFAULT_LABEL_MAP.put(label, label);
        }
    }

    private static final String DEFAULT_CONFIGURATION_PATH =
            "pl/edu/icm/yadda/analysis/metadata/evaluation/classification-configuration.xml";
    private ZoneClassifier zoneClassifier;
    private DocumentFlattener flattener = new UnclassifiedZonesFlattener();
    private final Map<BxZoneLabel, BxZoneLabel> labelMap = DEFAULT_LABEL_MAP.clone();

    public void setZoneClassifier(ZoneClassifier zoneClassifier) {
        this.zoneClassifier = zoneClassifier;
    }

    public void setLabelMap(Map<BxZoneLabel, BxZoneLabel> value) {
        labelMap.putAll(DEFAULT_LABEL_MAP);
        labelMap.putAll(value);
    }

    @Override
    protected void flattenDocument(BxDocument document) {
        flattener.flatten(document);
    }
    
    public void setFlattener(DocumentFlattener flattener) {
    	this.flattener = flattener;
    }
    
    public DocumentFlattener getFlattener() {
    	return this.flattener;
    }

    @Override
    protected BxDocument processDocument(BxDocument document) throws Exception {
    	
        this.zoneClassifier.classifyZones(document);
        return document;
    }

    @Override
    protected Results compareItems(BxPage expected, BxPage actual) {
        List<BxZone> expectedZones = expected.getZones();
        List<BxZone> actualZones = actual.getZones();

        Results pageResults = newResults();

        for (BxZone zone1 : expectedZones) {
            for (BxZone zone2 : actualZones) {
                if (zone1.getBounds().equals(zone2.getBounds())) {
                    pageResults.addOneZoneResult(zone1.getLabel(), zone2.getLabel());
                    break;
                }
            }
        }
        return pageResults;
        /*
        List<BxZone> expectedZones = expected.getZones();
        List<BxZone> actualZones = actual.getZones();

        Results pageResults = newResults();

        assert expected.getZones().size() == actual.getZones().size();
        
        for(int idx=0; idx < expected.getZones().size(); ++idx) {
        	BxZoneLabel expectedLabel = expectedZones.get(idx).getLabel();
        	BxZoneLabel actualLabel = actualZones.get(idx).getLabel();
        	
        	pageResults.addOneZoneResult(expectedLabel, actualLabel);
        }
        	
        return pageResults;*/
    }

    @Override
    protected Results newResults() {
        return new Results(labelMap);
    }

    @Override
    protected void printItemResults(int index, ClassificationEvaluator.Results results) {
    	
    }
    
    @Override
    protected void printItemResults(BxPage expected, BxPage actual, int itemIndex, ClassificationEvaluator.Results results) {
    	List<BxZone> expectedZones = expected.getZones();
    	List<BxZone> actualZones = actual.getZones();
    	for(int i=0; i < expectedZones.size(); ++i) {
    		BxZone expectedZone = expectedZones.get(i);
    		BxZone actualZone = actualZones.get(i);
    		if(expectedZone.getLabel() != actualZone.getLabel()) {
    			System.out.println("Expected " + expectedZone.getLabel() + ", got " + actualZone.getLabel());
    			System.out.println(expectedZone.toText() + "\n");
    		}
    	}
    }

    @Override
    protected void printDocumentResults(Results results) {
        results.printLongSummary();
        results.printShortSummary();
    }

    @Override
    protected void printFinalResults(Results results) {
        results.printMatrix();
        results.printLongSummary();
        results.printShortSummary();
    }

    public static class Results implements AbstractBxModelEvaluator.Results<Results> {

        protected Map<BxZoneLabel, BxZoneLabel> labelMap;
        protected int nbOfZoneTypes = BxZoneLabel.values().length;
        protected Integer[][] classificationMatrix = new Integer[nbOfZoneTypes][nbOfZoneTypes];
        protected int goodRecognitions = 0;
        protected int badRecognitions = 0;

        public Results(Map<BxZoneLabel, BxZoneLabel> labelMap) {
            this.labelMap = labelMap;
            for (BxZoneLabel label1 : BxZoneLabel.values()) {
                for (BxZoneLabel label2 : BxZoneLabel.values()) {
                    classificationMatrix[label1.ordinal()][label2.ordinal()] = 0;
                }
            }
        }

        public void addOneZoneResult(BxZoneLabel label1, BxZoneLabel label2) {
            label1 = labelMap.get(label1);
            label2 = labelMap.get(label2);
            classificationMatrix[label1.ordinal()][label2.ordinal()]++;
            if (label1.equals(label2)) {
                goodRecognitions++;
            } else {
                badRecognitions++;
            }
        }

        @Override
        public void add(Results results) {
            for (BxZoneLabel label1 : BxZoneLabel.values()) {
                for (BxZoneLabel label2 : BxZoneLabel.values()) {
                    classificationMatrix[label1.ordinal()][label2.ordinal()] +=
                            results.classificationMatrix[label1.ordinal()][label2.ordinal()];
                }
            }
            goodRecognitions += results.goodRecognitions;
            badRecognitions += results.badRecognitions;
        }

        public void printMatrix() {
            int maxLabelLength = 0;
            int labelCount = BxZoneLabel.values().length;

            int[] labelLengths = new int[labelCount];

            for (BxZoneLabel label : BxZoneLabel.values()) {
                if (! labelMap.get(label).equals(label)) {
                    continue;
                }
                int labelLength = label.toString().length();
                if (labelLength > maxLabelLength) {
                    maxLabelLength = labelLength;
                }
                labelLengths[label.ordinal()] = labelLength;
            }

            StringBuilder oneLine = new StringBuilder();
            oneLine.append("+-").append(new String(new char[maxLabelLength]).replace('\0', '-')).append("-+");
            for (BxZoneLabel label : BxZoneLabel.values()) {
                if (! labelMap.get(label).equals(label)) {
                    continue;
                }
                oneLine.append(new String(new char[labelLengths[label.ordinal()] + 2]).replace('\0', '-'));
                oneLine.append("+");
            }
            System.out.println(oneLine);

            oneLine = new StringBuilder();
            oneLine.append("| ").append(new String(new char[maxLabelLength]).replace('\0', ' ')).append(" |");
            for (BxZoneLabel label : BxZoneLabel.values()) {
                if (! labelMap.get(label).equals(label)) {
                    continue;
                }
                oneLine.append(' ').append(label).append(" |");
            }
            System.out.println(oneLine);

            oneLine = new StringBuilder();
            oneLine.append("+-").append(new String(new char[maxLabelLength]).replace('\0', '-')).append("-+");
            for (BxZoneLabel label : BxZoneLabel.values()) {
                if (! labelMap.get(label).equals(label)) {
                    continue;
                }
                oneLine.append(new String(new char[labelLengths[label.ordinal()] + 2]).replace('\0', '-'));
                oneLine.append("+");
            }
            System.out.println(oneLine);

            for (BxZoneLabel label1 : BxZoneLabel.values()) {
                if (! labelMap.get(label1).equals(label1)) {
                    continue;
                }
                oneLine = new StringBuilder();
                oneLine.append("| ").append(label1);
                oneLine.append(new String(new char[maxLabelLength - labelLengths[label1.ordinal()]]).replace('\0', ' '));
                oneLine.append(" |");
                for (BxZoneLabel label2 : BxZoneLabel.values()) {
                    if (! labelMap.get(label2).equals(label2)) {
                        continue;
                    }
                    String nbRecognitions = classificationMatrix[label1.ordinal()][label2.ordinal()].toString();
                    oneLine.append(" ").append(nbRecognitions);
                    oneLine.append(new String(new char[Math.max(0, labelLengths[label2.ordinal()] - nbRecognitions.length() + 1)]).replace('\0', ' '));
                    oneLine.append("|");
                }
                System.out.println(oneLine);
            }

            oneLine = new StringBuilder();
            oneLine.append("+-").append(new String(new char[maxLabelLength]).replace('\0', '-')).append("-+");
            for (BxZoneLabel label : BxZoneLabel.values()) {
                if (! labelMap.get(label).equals(label)) {
                    continue;
                }
                oneLine.append(new String(new char[labelLengths[label.ordinal()] + 2]).replace('\0', '-'));
                oneLine.append("+");
            }
            System.out.println(oneLine);
            System.out.println();
        }

        void printShortSummary() {
            int allRecognitions = goodRecognitions + badRecognitions;
            System.out.print("Good recognitions: " + goodRecognitions + "/" + allRecognitions);
            if (allRecognitions > 0) {
                System.out.format(" (%.1f%%)%n", 100.0 * goodRecognitions / allRecognitions);
            }
            System.out.print("Bad recognitions: " + badRecognitions + "/" + allRecognitions);
            if (allRecognitions > 0) {
                System.out.format(" (%.1f%%)%n", 100.0 * badRecognitions / allRecognitions);
            }
        }

        void printLongSummary() {
            int maxLabelLength = 0;
            for (BxZoneLabel label : BxZoneLabel.values()) {
                if (! labelMap.get(label).equals(label)) {
                    continue;
                }
                int labelLength = label.toString().length();
                if (labelLength > maxLabelLength) {
                    maxLabelLength = labelLength;
                }
            }

            System.out.println("Good recognitions par zone type:");
            for (BxZoneLabel label1 : BxZoneLabel.values()) {
                if (! labelMap.get(label1).equals(label1)) {
                    continue;
                }
                String spaces;
                int labelGoodRecognitions = 0;
                int labelAllRecognitions = 0;
                for (BxZoneLabel label2 : BxZoneLabel.values()) {
                    if (! labelMap.get(label2).equals(label2)) {
                        continue;
                    }
                    if (label1.equals(label2)) {
                        labelGoodRecognitions += classificationMatrix[label1.ordinal()][label2.ordinal()];
                    }
                    labelAllRecognitions += classificationMatrix[label1.ordinal()][label2.ordinal()];
                }

                spaces = new String(new char[maxLabelLength - label1.toString().length() + 1]).replace('\0', ' ');
                System.out.format("%s:%s%d/%d", label1, spaces, labelGoodRecognitions, labelAllRecognitions);
                if (labelAllRecognitions > 0) {
                    System.out.format(" (%.1f%%)", 100.0 * labelGoodRecognitions / labelAllRecognitions);
                }
                System.out.println();
            }
            System.out.println();
        }
    }

    public static void main(String[] args) throws Exception {
        main("ClassificationEvaluator", args, DEFAULT_CONFIGURATION_PATH);
    }
}
