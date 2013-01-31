package pl.edu.icm.cermine.evaluation;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import pl.edu.icm.cermine.evaluation.AbstractEvaluator.Detail;
import pl.edu.icm.cermine.evaluation.tools.ClassificationResults;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;
import pl.edu.icm.cermine.structure.transformers.BxDocumentToTrueVizWriter;
import pl.edu.icm.cermine.structure.transformers.TrueVizToBxDocumentReader;
import pl.edu.icm.cermine.tools.classification.features.FeatureVectorBuilder;
import pl.edu.icm.cermine.tools.classification.general.TrainingSample;
import pl.edu.icm.cermine.tools.classification.svm.SVMZoneClassifier;

/**
 * Class for performing cross-validating classifier performance in zone classification task
 *
 * @author Pawel Szostek (p.szostek@icm.edu.pl)
 */
public abstract class CrossvalidatingZoneClassificationEvaluator {

    private static final EnumMap<BxZoneLabel, BxZoneLabel> DEFAULT_LABEL_MAP = new EnumMap<BxZoneLabel, BxZoneLabel>(BxZoneLabel.class);

    static {
        for (BxZoneLabel label : BxZoneLabel.values()) {
            DEFAULT_LABEL_MAP.put(label, label);
        }
    }
    private AbstractEvaluator.Detail detail;
    protected Integer foldness;
    private final Map<BxZoneLabel, BxZoneLabel> labelMap = DEFAULT_LABEL_MAP.clone();
    private TrueVizToBxDocumentReader reader = new TrueVizToBxDocumentReader();
    private BxDocumentToTrueVizWriter writer = new BxDocumentToTrueVizWriter();

    //sample launch: -fold 5 /path/to/your/xml/catalog
    public static void main(String[] args, CrossvalidatingZoneClassificationEvaluator evaluator)
            throws ParseException, AnalysisException, IOException, TransformationException, CloneNotSupportedException {
        Options options = new Options();
        options.addOption("compact", false, "do not print results for pages");
        options.addOption("fold", true, "foldness of cross-validation");
        options.addOption("help", false, "print this help message");
        options.addOption("minimal", false, "print only final summary");
        options.addOption("full", false, "print all possible messages");

        CommandLineParser parser = new GnuParser();
        CommandLine line = parser.parse(options, args);

        if (line.hasOption("help")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(args[0] + " [-options] input-file",
                    options);
        } else {
            String[] remaining = line.getArgs();

            if (remaining.length != 1) {
                throw new ParseException("Input file is missing!");
            }

            if (!line.hasOption("fold")) {
                throw new ParseException("Foldness of cross-validation is not given!");
            } else {
                evaluator.foldness = Integer.valueOf(line.getOptionValue("fold"));
            }
            String inputFile = remaining[0];

            if (line.hasOption("minimal")) {
                evaluator.detail = Detail.MINIMAL;
            } else if (line.hasOption("compact")) {
                evaluator.detail = Detail.COMPACT;
            } else if (line.hasOption("full")) {
                evaluator.detail = Detail.FULL;
            }
            evaluator.setLabelMap(BxZoneLabel.getLabelToGeneralMap());
            evaluator.run(inputFile);

        }
    }

    public void run(String inputFile) throws AnalysisException, IOException, TransformationException, CloneNotSupportedException {
        ClassificationResults summary = newResults();
        List<TrainingSample<BxZoneLabel>> samples = SVMZoneClassifier.loadProblem(inputFile, getFeatureVectorBuilder());
        List<DividedEvaluationSet> sampleSets = DividedEvaluationSet.build(samples, foldness);
        System.out.println("All training elements: " +  samples.size());
        for (int fold = 0; fold < foldness; ++fold) {
        	List<TrainingSample<BxZoneLabel>> trainingSamples = sampleSets.get(fold).getTrainingDocuments();
        	List<TrainingSample<BxZoneLabel>> testSamples = sampleSets.get(fold).getTestDocuments();
//        	for(TrainingSample<BxZoneLabel> sample: testSamples) {
//        		System.out.println(sample.getFeatures().getFeatures().length + " " + sample.getFeatures().getFeatureNames());
//        	}
            System.out.println("Fold number " + fold);
        	System.out.println("Training elements " + trainingSamples.size());
            System.out.println("Test elements  " + testSamples.size());

            ClassificationResults iterationResults = newResults();

            SVMZoneClassifier zoneClassifier = getZoneClassifier(trainingSamples);

            for (TrainingSample<BxZoneLabel> testSample : testSamples) {
            	BxZoneLabel expectedClass = testSample.getLabel();
                BxZoneLabel inferedClass = zoneClassifier.predictLabel(testSample);
                ClassificationResults documentResults = compareItems(expectedClass, inferedClass);
                iterationResults.add(documentResults);
            }
            summary.add(iterationResults);
            System.out.println("=== Single iteration summary (" + (fold + 1) + "/" + this.foldness + ")");
            printFinalResults(iterationResults);
        }
        System.out.println("=== General summary (" + this.foldness + " iterations)");
        printFinalResults(summary);
    }



    protected ClassificationResults newResults() {
        return new ClassificationResults();
    }

    protected ClassificationResults compareItems(BxZoneLabel expected, BxZoneLabel actual) {
        ClassificationResults pageResults = newResults();
        pageResults.addOneZoneResult(expected, actual);
        return pageResults;
    }

    protected BxDocument readDocument(Reader input) throws Exception {
        List<BxPage> pages = reader.read(input);
        BxDocument ret = new BxDocument();
        for (BxPage page : pages) {
            page.setParent(ret);
        }
        return ret.setPages(pages);
    }

    public void setLabelMap(Map<BxZoneLabel, BxZoneLabel> value) {
        labelMap.putAll(DEFAULT_LABEL_MAP);
        labelMap.putAll(value);
    }

    protected void writeDocument(BxDocument document, Writer output) throws Exception {
        writer.write(output, document.getPages());
    }

    protected void printItemResults(BxZone expected, BxZone actual, int itemIndex, ClassificationResults results) {
        if (expected.getLabel() != actual.getLabel()) {
            System.out.println("Expected " + expected.getLabel() + ", got " + actual.getLabel());
            System.out.println(expected.toText() + "\n");
        }
    }

    protected void printDocumentResults(ClassificationResults results) {
        results.printLongSummary();
        results.printShortSummary();
    }

    protected void printFinalResults(ClassificationResults results) {
        results.printMatrix();
        results.printLongSummary();
        results.printShortSummary();
        results.printQualityMeasures();
    }

    protected abstract SVMZoneClassifier getZoneClassifier(List<TrainingSample<BxZoneLabel>> trainingSamples) throws AnalysisException, IOException, CloneNotSupportedException;
    protected abstract FeatureVectorBuilder<BxZone, BxPage> getFeatureVectorBuilder();
}
