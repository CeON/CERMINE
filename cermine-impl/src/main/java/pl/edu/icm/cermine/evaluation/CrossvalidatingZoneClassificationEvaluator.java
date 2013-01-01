package pl.edu.icm.cermine.evaluation;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.cli.*;
import pl.edu.icm.cermine.evaluation.AbstractEvaluator.Detail;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.metadata.zoneclassification.features.*;
import pl.edu.icm.cermine.structure.HierarchicalReadingOrderResolver;
import pl.edu.icm.cermine.structure.ReadingOrderResolver;
import pl.edu.icm.cermine.structure.ZoneClassifier;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;
import pl.edu.icm.cermine.structure.tools.BxModelUtils;
import pl.edu.icm.cermine.structure.transformers.BxDocumentToTrueVizWriter;
import pl.edu.icm.cermine.structure.transformers.TrueVizToBxDocumentReader;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;
import pl.edu.icm.cermine.tools.classification.features.FeatureVectorBuilder;

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
    private final ReadingOrderResolver resolver = new HierarchicalReadingOrderResolver();
    private TrueVizToBxDocumentReader reader = new TrueVizToBxDocumentReader();
    private BxDocumentToTrueVizWriter writer = new BxDocumentToTrueVizWriter();

    //sample launch: -fold 5 /path/to/your/xml/catalog
    public static void main(String[] args, CrossvalidatingZoneClassificationEvaluator evaluator)
            throws ParseException, AnalysisException, IOException, TransformationException {
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
            formatter.printHelp(args[0] + " [-options] input-directory",
                    options);
        } else {
            String[] remaining = line.getArgs();

            if (remaining.length != 1) {
                throw new ParseException("Input directory is missing!");
            }

            if (!line.hasOption("fold")) {
                throw new ParseException("Foldness of cross-validation is not given!");
            } else {
                evaluator.foldness = Integer.valueOf(line.getOptionValue("fold"));
            }
            String inputDir = remaining[0];

            if (line.hasOption("minimal")) {
                evaluator.detail = Detail.MINIMAL;
            } else if (line.hasOption("compact")) {
                evaluator.detail = Detail.COMPACT;
            } else if (line.hasOption("full")) {
                evaluator.detail = Detail.FULL;
            }
            evaluator.setLabelMap(BxZoneLabel.getLabelToGeneralMap());

            evaluator.run(inputDir, null);

        }
    }

    public void run(String inDir, String outDir) throws AnalysisException, IOException, TransformationException {
        List<BxDocument> evaluationDocuments = EvaluationUtils.getDocumentsFromPath(inDir);
        ClassificationResults summary = newResults();

        List<DividedEvaluationSet> fileSets = DividedEvaluationSet.build(BxModelUtils.deepClone(evaluationDocuments), foldness);

        for (int fold = 0; fold < foldness; ++fold) {
            List<BxDocument> trainingDocuments = fileSets.get(fold).getTrainingDocuments();
            List<BxDocument> testDocuments = fileSets.get(fold).getTestDocuments();

            System.out.println("Training documents " + trainingDocuments.size());
            System.out.println("Test documents " + testDocuments.size());

            ClassificationResults iterationResults = newResults();

            ZoneClassifier zoneClassifier = getZoneClassifier(BxModelUtils.deepClone(trainingDocuments));
            
            ReadingOrderResolver ror = new HierarchicalReadingOrderResolver();

            for (BxDocument testDocument : testDocuments) {
                BxDocument processedDocument = BxModelUtils.deepClone(testDocument);
                processedDocument = ror.resolve(processedDocument);
                for (BxZone zone : processedDocument.asZones()) {
                    zone.setLabel(null);
                }

                if (detail != Detail.MINIMAL) {
                    System.out.println("=== Document " + testDocument.getFilename());
                }
                
                zoneClassifier.classifyZones(processedDocument);

                preprocessDocumentForEvaluation(testDocument);
                testDocument = ror.resolve(testDocument);
                ClassificationResults documentResults = compareDocuments(testDocument, processedDocument);
                if (detail != Detail.MINIMAL) {
                    printDocumentResults(documentResults);
                }
                iterationResults.add(documentResults);
            }
            summary.add(iterationResults);
            System.out.println("=== Single iteration summary (" + (fold + 1) + "/" + this.foldness + ")");
            printFinalResults(iterationResults);
        }
        System.out.println("=== General summary (" + this.foldness + " iterations)");
        printFinalResults(summary);
    }

    public FeatureVectorBuilder<BxZone, BxPage> getFeatureVectorBuilder() {
        FeatureVectorBuilder<BxZone, BxPage> vectorBuilder = new FeatureVectorBuilder<BxZone, BxPage>();
        vectorBuilder.setFeatureCalculators(Arrays.<FeatureCalculator<BxZone, BxPage>>asList(
                new AffiliationFeature(),
                new AuthorFeature(),
                new AuthorNameRelativeFeature(),
                new BibinfoFeature(),
                new BracketRelativeCount(),
                new BracketedLineRelativeCount(),
                new CharCountFeature(),
                new CharCountRelativeFeature(),
                new CommaCountFeature(),
                new CommaRelativeCountFeature(),
                new ContainsPageNumberFeature(),
                new CuePhrasesRelativeCountFeature(),
                new DateFeature(),
                new DigitCountFeature(),
                new DigitRelativeCountFeature(),
                new DistanceFromNearestNeighbourFeature(),
                new DotCountFeature(),
                new DotRelativeCountFeature(),
                new EmptySpaceRelativeFeature(),
                new FontHeightMeanFeature(),
                new FreeSpaceWithinZoneFeature(),
                new FullWordsRelativeFeature(),
                new HeightFeature(),
                new HeightRelativeFeature(),
                new HorizontalRelativeProminenceFeature(),
                new IsAnywhereElseFeature(),
                new IsFirstPageFeature(),
                new IsFontBiggerThanNeighboursFeature(),
                new IsGreatestFontOnPageFeature(),
                new IsHighestOnThePageFeature(),
                new IsItemizeFeature(),
                new IsWidestOnThePageFeature(),
                new IsLastButOnePageFeature(),
                new IsLastPageFeature(),
                new IsLeftFeature(),
                new IsLongestOnThePageFeature(),
                new IsLowestOnThePageFeature(),
                new IsItemizeFeature(),
                new IsOnSurroundingPagesFeature(),
                new IsPageNumberFeature(),
                new IsRightFeature(),
        		new IsSingleWordFeature(),
                new LineCountFeature(),
                new LineRelativeCountFeature(),
                new LineHeightMeanFeature(),
                new LineWidthMeanFeature(),
                new LineXPositionMeanFeature(),
                new LineXPositionDiffFeature(),
                new LineXWidthPositionDiffFeature(),
                new LetterCountFeature(),
                new LetterRelativeCountFeature(),
                new LowercaseCountFeature(),
                new LowercaseRelativeCountFeature(),
                new PageNumberFeature(),
                new PreviousZoneFeature(),
                new ProportionsFeature(),
                new PunctuationRelativeCountFeature(),
                new ReferencesFeature(),
                new StartsWithDigitFeature(),
                new StartsWithHeaderFeature(),
                new UppercaseCountFeature(),
                new UppercaseRelativeCountFeature(),
                new UppercaseWordCountFeature(),
                new UppercaseWordRelativeCountFeature(),
                new VerticalProminenceFeature(),
                new WidthFeature(),
                new WordCountFeature(),
                new WordCountRelativeFeature(),
                new WordWidthMeanFeature(),
                new WordLengthMeanFeature(),
                new WordLengthMedianFeature(),
                new WhitespaceCountFeature(),
                new WhitespaceRelativeCountLogFeature(),
                new WidthRelativeFeature(),
                new XPositionFeature(),
                new XPositionRelativeFeature(),
                new YPositionFeature(),
                new YPositionRelativeFeature(),
                new YearFeature()));
        return vectorBuilder;
    }

    protected ClassificationResults newResults() {
        return new ClassificationResults();
    }

    protected ClassificationResults compareItems(BxZone expected, BxZone actual) {
        ClassificationResults pageResults = newResults();
        if (expected.getLabel() == BxZoneLabel.GEN_OTHER && actual.getLabel() != BxZoneLabel.GEN_OTHER) {
            System.out.println(expected.toText());
        }
        pageResults.addOneZoneResult(expected.getLabel(), actual.getLabel());
        return pageResults;
    }

    protected abstract ClassificationResults compareDocuments(BxDocument expected, BxDocument actual);

    protected abstract void preprocessDocumentForEvaluation(BxDocument doc);

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

    protected abstract ZoneClassifier getZoneClassifier(List<BxDocument> trainingDocuments) throws AnalysisException, IOException;
}
