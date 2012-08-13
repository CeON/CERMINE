package pl.edu.icm.yadda.analysis.metadata.evaluation;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import pl.edu.icm.yadda.analysis.classification.features.FeatureCalculator;
import pl.edu.icm.yadda.analysis.classification.features.FeatureVectorBuilder;
import pl.edu.icm.yadda.analysis.classification.features.SimpleFeatureVectorBuilder;
import pl.edu.icm.yadda.analysis.classification.hmm.HMMServiceImpl;
import pl.edu.icm.yadda.analysis.classification.hmm.probability.HMMProbabilityInfo;
import pl.edu.icm.yadda.analysis.classification.hmm.probability.HMMProbabilityInfoFactory;
import pl.edu.icm.yadda.analysis.classification.hmm.tools.DirExtractor;
import pl.edu.icm.yadda.analysis.classification.hmm.tools.DocumentsExtractor;
import pl.edu.icm.yadda.analysis.classification.hmm.training.HMMTrainingElement;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.*;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.nodes.BxDocsToFVHMMTrainingElementsConverterNode;
import pl.edu.icm.yadda.analysis.textr.HMMZoneClassifier;
import pl.edu.icm.yadda.analysis.textr.HierarchicalReadingOrderResolver;
import pl.edu.icm.yadda.analysis.textr.ReadingOrderResolver;
import pl.edu.icm.yadda.analysis.textr.ZoneClassifier;
import pl.edu.icm.yadda.analysis.textr.model.BxPage;
import pl.edu.icm.yadda.analysis.textr.model.BxZone;
import pl.edu.icm.yadda.analysis.textr.model.BxZoneLabel;
import pl.edu.icm.yadda.analysis.textr.model.BxDocument;
import pl.edu.icm.yadda.analysis.textr.model.BxZoneLabelCategory;
import pl.edu.icm.yadda.analysis.textr.tools.DocumentFlattener;
import pl.edu.icm.yadda.analysis.textr.tools.InitiallyClassifiedZonesFlattener;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class SimplifiedClassificationEvaluator extends ClassificationEvaluator
{
	protected Double ratio;
	protected Integer iterations;
	
	protected List<BxDocument> getEvaluationDocuments(String inputDirPath)
	{
		if (inputDirPath == null) {
			throw new NullPointerException("Input directory must not be null.");
		}

		if (!inputDirPath.endsWith(File.separator)) {
			inputDirPath += File.separator;
		}
		DocumentsExtractor extractor = new DirExtractor(inputDirPath);
		
		List<BxDocument> evaluationDocuments;
		try {
			 evaluationDocuments = extractor.getDocuments();
		} catch(Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Unable to get evaluation documents from the indicated location! Got exception: " + e);
		}
		return evaluationDocuments;
	}
	
	protected HMMZoneClassifier getZoneClassifier(List<BxDocument> trainingDocuments, FeatureVectorBuilder<BxZone, BxPage> featureVectorBuilder)
	{
        BxDocsToFVHMMTrainingElementsConverterNode node = new BxDocsToFVHMMTrainingElementsConverterNode();
        node.setFeatureVectorBuilder(featureVectorBuilder);
        
        List<HMMTrainingElement<BxZoneLabel>> trainingElements;
        try {
        	trainingElements = node.process(trainingDocuments, null);
        } catch(Exception e) {
			throw new RuntimeException("Unable to process the delivered training documents!");
		}
        
		HMMProbabilityInfo<BxZoneLabel> hmmProbabilities;
		try {
			hmmProbabilities = HMMProbabilityInfoFactory.getFVHMMProbability(trainingElements, featureVectorBuilder);
		} catch(Exception e) {
			throw new RuntimeException("Unable to figure out HMM probability information!");
		}
		
		HMMZoneClassifier zoneClassifier = new HMMZoneClassifier(
				new HMMServiceImpl(),
				hmmProbabilities,
				BxZoneLabel.valuesOfCategory(BxZoneLabelCategory.CAT_GENERAL),
				featureVectorBuilder);
		return zoneClassifier;
	}

	protected FeatureVectorBuilder<BxZone, BxPage> getFeatureVectorBuilder()
	{
		FeatureVectorBuilder<BxZone, BxPage> vectorBuilder =
                new SimpleFeatureVectorBuilder<BxZone, BxPage>();
        vectorBuilder.setFeatureCalculators(Arrays.<FeatureCalculator<BxZone, BxPage>>asList(
        		new AbstractFeature(),
        		new AcknowledgementFeature(),
        		new AffiliationFeature(),
                new AtCountFeature(),
                new AtRelativeCountFeature(),
                new AuthorFeature(),
                new BibinfoFeature(),
        		new BracketRelativeCount(),
        		new BracketedLineRelativeCount(),
                new CharCountFeature(),
                new CharCountRelativeFeature(),
                new CommaCountFeature(),
                new CommaRelativeCountFeature(),
        		new CuePhrasesCountFeature(),
        		new CuePhrasesRelativeCountFeature(),
        		new DatesFeature(),
                new DigitCountFeature(),
                new DigitRelativeCountFeature(),
        		new DistanceFromNearestNeighbourFeature(),
        		new DotCountFeature(),
        		new DotRelativeCountFeature(),
                new EmptySpaceRelativeFeature(),
        		new FontHeightMeanFeature(),
        		new FigureFeature(),
        		new FreeSpaceWithinZoneFeature(),
                new HeightFeature(),
                new HeightRelativeFeature(),
        		new HorizontalRelativeProminenceFeature(),
        		new IsFirstPageFeature(),
        		new IsFontBiggerThanNeighboursFeature(),
        		new IsHighestOnThePageFeature(),
        		new IsLastPageFeature(),
        		new IsLowestOnThePageFeature(),
        		new IsItemizeFeature(),
                new KeywordsFeature(),
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
                new ProportionsFeature(),
                new PunctuationRelativeCountFeature(),
                new ReferencesFeature(),
                new ReferencesTitleFeature(),
                new StartsWithDigitFeature(),
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
        		new WhitespaceRelativeCountFeature(),
                new WidthRelativeFeature(),
                new XPositionFeature(),
                new XPositionRelativeFeature(),
                new YPositionFeature(),
                new YPositionRelativeFeature(),
                new YearFeature()
                ));
        return vectorBuilder;
	}
	
	protected DividedEvaluationSet divideEvaluationSet(List<BxDocument> documents)
	{
		Integer numberOfTrainingDocs = (int)Math.ceil((documents.size()*ratio)/(1+ratio));
		// based on the equations:
		// size(training) / size(test) = ratio
		// size(training) + size(test) = size(documents)
		
		List<Integer> trainingIndices = new ArrayList<Integer>(numberOfTrainingDocs);
		Random randomGenerator = new Random();
		
		while(trainingIndices.size() < (documents.size()*ratio)/(1+ratio)) {
			Integer randomInt = randomGenerator.nextInt(documents.size());
			if(!trainingIndices.contains(randomInt)) {
				trainingIndices.add(randomInt);
			}
		}
		
		List<BxDocument> trainingDocs = new ArrayList<BxDocument>(numberOfTrainingDocs);
		List<BxDocument> testDocs = new ArrayList<BxDocument>(documents.size()-numberOfTrainingDocs);
		
		for(Integer index=0; index<documents.size(); ++index) {
			if(trainingIndices.contains(index)) {
				trainingDocs.add(documents.get(index));
			} else {
				testDocs.add(documents.get(index));
			}
		}
		return new DividedEvaluationSet(trainingDocs, testDocs);
	}

	@Override
	public void run(String inDir, String outDir) throws RuntimeException
	{
		List<BxDocument> evaluationDocuments = getEvaluationDocuments(inDir);
		ClassificationEvaluator.Results summary = newResults();
        FeatureVectorBuilder<BxZone, BxPage> featureVectorBuilder = getFeatureVectorBuilder();

        for(BxDocument doc: evaluationDocuments)
        	flattenDocument(doc);
        
        Integer iterationCounter = 1;
		for (int i = 0; i < iterations; ++i) {
			DividedEvaluationSet fileSets = divideEvaluationSet(evaluationDocuments);
			List<BxDocument> trainingDocuments = fileSets.getTrainingSet();
			List<BxDocument> testDocuments = fileSets.getTestSet();
			ClassificationEvaluator.Results iterationResults = newResults();
			
			ZoneClassifier zoneClassifier = getZoneClassifier(trainingDocuments, featureVectorBuilder);
			setZoneClassifier(zoneClassifier);
				
			for (BxDocument testDocument: testDocuments) {
				Documents<BxDocument> documentsPair;
				try {
					documentsPair = getDocuments(testDocument);
				} catch(Exception e) {
					e.printStackTrace();
					throw new RuntimeException("Unable to process test document: " + testDocument);
				}
				if (documentsPair == null) {
					throw new NullPointerException();
				}
				if (detail != Detail.MINIMAL) {
					System.out.println("=== Document " + testDocument.getFilename());
					printDocumentStart();
				}

				ClassificationEvaluator.Results documentResults = compareDocuments(documentsPair.getExpected(), documentsPair.getActual());
				if (detail != Detail.MINIMAL) {
					printDocumentResults(documentResults);
					printDocumentEnd();
				}
				iterationResults.add(documentResults);
			}
			summary.add(iterationResults);
			System.out.println("=== Single iteration summary (" + iterationCounter + "/" + this.iterations + ")");
			printFinalResults(iterationResults);
			iterationCounter += 1;
		}
		System.out.println("=== General summary (" + this.iterations + " iterations)");
		printFinalResults(summary);
	}

	public static void main(String[] args) throws ParseException
	{
		Options options = new Options();
		options.addOption("compact", false, "do not print results for pages");
		options.addOption("ratio", true, "cross-valifation training-to-testing files ratio");
		options.addOption("iter", true, "number of cross-validation iterations");
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
			SimplifiedClassificationEvaluator evaluator = new SimplifiedClassificationEvaluator();
			String[] remaining = line.getArgs();
			
			if (remaining.length != 1) {
				throw new ParseException("Input directory is missing!");
			}
			if (!line.hasOption("ratio")) {
				throw new ParseException("Cross-valdation ratio is not given!");
			} else {
				evaluator.ratio = Double.valueOf(line.getOptionValue("ratio"));
			}

			if (!line.hasOption("iter")) {
				throw new ParseException("Number of iterations for cross-validation is not given!");
			} else {
				evaluator.iterations = Integer.valueOf(line.getOptionValue("iter"));
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
			evaluator.setFlattener(new InitiallyClassifiedZonesFlattener());
			evaluator.run(inputDir, null);
		}
	}
	
	private static class DividedEvaluationSet
	{
		List<BxDocument> trainingSet;
		List<BxDocument> testSet;

		public DividedEvaluationSet(List<BxDocument> trainingSet, List<BxDocument> testSet) {
			this.trainingSet = trainingSet;
			this.testSet = testSet;
		}
		public List<BxDocument> getTrainingSet() {
			return trainingSet;
		}
		public List<BxDocument> getTestSet() {
			return testSet;
		}
	}
}
