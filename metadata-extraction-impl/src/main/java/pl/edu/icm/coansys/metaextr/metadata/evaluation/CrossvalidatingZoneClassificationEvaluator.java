package pl.edu.icm.coansys.metaextr.metadata.evaluation;

import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.IsHighestOnThePageFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.LineWidthMeanFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.WhitespaceCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.LineHeightMeanFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.WordWidthMeanFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.CommaRelativeCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.LetterRelativeCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.LineXPositionMeanFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.HeightFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.ProportionsFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.ReferencesFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.StartsWithDigitFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.FullWordsRelativeFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.IsLastButOnePageFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.WordCountRelativeFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.PreviousZoneFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.VerticalProminenceFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.XPositionFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.LineXPositionDiffFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.DigitRelativeCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.DotCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.IsRightFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.DateFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.UppercaseRelativeCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.YearFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.IsFontBiggerThanNeighboursFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.AffiliationFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.HeightRelativeFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.ContainsPageNumberFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.CuePhrasesRelativeCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.IsLastPageFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.IsOnSurroundingPagesFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.LetterCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.CommaCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.CharCountRelativeFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.IsWidestOnThePageFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.UppercaseWordRelativeCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.LowercaseCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.IsItemizeFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.XPositionRelativeFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.BibinfoFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.EmptySpaceRelativeFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.CharCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.YPositionFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.AuthorFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.PageNumberFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.IsLeftFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.DigitCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.UppercaseWordCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.WidthRelativeFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.IsAnywhereElseFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.IsGreatestFontOnPageFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.WidthFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.DistanceFromNearestNeighbourFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.BracketedLineRelativeCount;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.BracketRelativeCount;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.WhitespaceRelativeCountLogFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.WordCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.LineRelativeCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.YPositionRelativeFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.DotRelativeCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.WordLengthMedianFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.WordLengthMeanFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.IsLowestOnThePageFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.UppercaseCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.HorizontalRelativeProminenceFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.IsFirstPageFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.LineCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.FontHeightMeanFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.FreeSpaceWithinZoneFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.PunctuationRelativeCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.LowercaseRelativeCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.AuthorNameRelativeFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.LineXWidthPositionDiffFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.StartsWithHeaderFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.IsLongestOnThePageFeature;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import pl.edu.icm.coansys.metaextr.AnalysisException;
import pl.edu.icm.coansys.metaextr.tools.classification.features.FeatureCalculator;
import pl.edu.icm.coansys.metaextr.tools.classification.features.FeatureVectorBuilder;
import pl.edu.icm.coansys.metaextr.tools.classification.general.SimpleFeatureVectorBuilder;
import pl.edu.icm.coansys.metaextr.tools.classification.sampleselection.SampleSelector;
import pl.edu.icm.coansys.metaextr.metadata.evaluation.AbstractEvaluator.Detail;
import pl.edu.icm.coansys.metaextr.structure.HierarchicalReadingOrderResolver;
import pl.edu.icm.coansys.metaextr.structure.ReadingOrderResolver;
import pl.edu.icm.coansys.metaextr.structure.ZoneClassifier;
import pl.edu.icm.coansys.metaextr.structure.model.BxDocument;
import pl.edu.icm.coansys.metaextr.structure.model.BxPage;
import pl.edu.icm.coansys.metaextr.structure.model.BxZone;
import pl.edu.icm.coansys.metaextr.structure.model.BxZoneLabel;
import pl.edu.icm.coansys.metaextr.structure.tools.BxModelUtils;
import pl.edu.icm.coansys.metaextr.structure.tools.DocumentPreprocessor;
import pl.edu.icm.coansys.metaextr.structure.tools.InitiallyClassifiedZonesPreprocessor;
import pl.edu.icm.coansys.metaextr.structure.tools.UnclassifiedZonesPreprocessor;
import pl.edu.icm.coansys.metaextr.structure.transformers.BxDocumentToTrueVizWriter;
import pl.edu.icm.coansys.metaextr.structure.transformers.TrueVizToBxDocumentReader;

/**
 * Class for performing cross-validating classifier performance in
 * zone classification task
 * 
 * @author Pawel Szostek (p.szostek@icm.edu.pl)
 */
public abstract class CrossvalidatingZoneClassificationEvaluator {

    static private final EnumMap<BxZoneLabel, BxZoneLabel> DEFAULT_LABEL_MAP
            = new EnumMap<BxZoneLabel, BxZoneLabel>(BxZoneLabel.class);

    static {
        for (BxZoneLabel label : BxZoneLabel.values()) {
            DEFAULT_LABEL_MAP.put(label, label);
        }
    }

    private DocumentPreprocessor flattener = new UnclassifiedZonesPreprocessor();
    private AbstractEvaluator.Detail detail;

    protected Integer foldness;
    
    private final Map<BxZoneLabel, BxZoneLabel> labelMap = DEFAULT_LABEL_MAP.clone();
	private final ReadingOrderResolver resolver = new HierarchicalReadingOrderResolver();

	private TrueVizToBxDocumentReader reader = new TrueVizToBxDocumentReader();
	private BxDocumentToTrueVizWriter writer = new BxDocumentToTrueVizWriter();
    
	//sample launch: -fold 5 /path/to/your/xml/catalog
	public static void main(String[] args, CrossvalidatingZoneClassificationEvaluator evaluator)
			throws ParseException, RuntimeException, AnalysisException, IOException
	{
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
			evaluator.setPreprocessor(new InitiallyClassifiedZonesPreprocessor());
		
			evaluator.run(inputDir, null);
		
		}
	}

	public void run(String inDir, String outDir) throws RuntimeException, AnalysisException, IOException
	{
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

			for (BxDocument testDocument: testDocuments) {
				BxDocument processedDocument = BxModelUtils.deepClone(testDocument);
				for(BxZone zone: processedDocument.asZones())
					zone.setLabel(null);
				ClassificationResults documentResults = newResults();

				if (detail != Detail.MINIMAL) {
					System.out.println("=== Document " + testDocument.getFilename());
				}
				try {
					zoneClassifier.classifyZones(processedDocument);
				} catch (AnalysisException e) {
					e.printStackTrace();
					System.exit(1);
				}
				preprocessDocumentForEvaluation(testDocument);
				BxModelUtils.setReadingOrder(testDocument);
				documentResults = compareDocuments(testDocument, processedDocument);
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
	
	public FeatureVectorBuilder<BxZone, BxPage> getFeatureVectorBuilder()
	{
		FeatureVectorBuilder<BxZone, BxPage> vectorBuilder =
                new SimpleFeatureVectorBuilder<BxZone, BxPage>();
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
        		new IsRightFeature(),
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
                new YearFeature()
                ));
        return vectorBuilder;
	}

    protected ClassificationResults newResults() {
        return new ClassificationResults();
    }

    protected ClassificationResults compareItems(BxZone expected, BxZone actual)
    {
        ClassificationResults pageResults = newResults();
        if(expected.getLabel() == BxZoneLabel.GEN_OTHER && actual.getLabel() != BxZoneLabel.GEN_OTHER)
        	System.out.println(expected.toText());
    	pageResults.addOneZoneResult(expected.getLabel(), actual.getLabel());
    	return pageResults;
    }
    
    protected abstract ClassificationResults compareDocuments(BxDocument expected, BxDocument actual);

    protected abstract void preprocessDocumentForEvaluation(BxDocument doc);
    
	protected BxDocument readDocument(Reader input) throws Exception
	{
		List<BxPage> pages = reader.read(input);
		BxDocument ret = new BxDocument();
		for(BxPage page: pages)
			page.setContext(ret);
	    return ret.setPages(pages);
	}
	
    public void setLabelMap(Map<BxZoneLabel, BxZoneLabel> value)
    {
        labelMap.putAll(DEFAULT_LABEL_MAP);
        labelMap.putAll(value);
    }

    public void setPreprocessor(DocumentPreprocessor flattener)
    {
    	this.flattener = flattener;
    }

	protected void writeDocument(BxDocument document, Writer output) throws Exception
	{
	    writer.write(output, document.getPages());
	}

	protected void printItemResults(BxZone expected, BxZone actual, int itemIndex, ClassificationResults results)
	{
    	if(expected.getLabel() != actual.getLabel()) {
    		System.out.println("Expected " + expected.getLabel() + ", got " + actual.getLabel());
    		System.out.println(expected.toText() + "\n");
    	}
    }

    protected void printDocumentResults(ClassificationResults results)
    {
        results.printLongSummary();
        results.printShortSummary();
    }

    protected void printFinalResults(ClassificationResults results)
    {
        results.printMatrix();
        results.printLongSummary();
        results.printShortSummary();
    }

    protected abstract ZoneClassifier getZoneClassifier(List<BxDocument> trainingDocuments) throws AnalysisException, IOException;
}
