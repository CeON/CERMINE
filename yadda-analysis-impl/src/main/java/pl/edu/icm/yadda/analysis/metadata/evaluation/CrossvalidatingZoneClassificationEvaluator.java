package pl.edu.icm.yadda.analysis.metadata.evaluation;

import java.io.File;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

import pl.edu.icm.yadda.analysis.AnalysisException;
import pl.edu.icm.yadda.analysis.classification.features.FeatureCalculator;
import pl.edu.icm.yadda.analysis.classification.features.FeatureVectorBuilder;
import pl.edu.icm.yadda.analysis.classification.features.SimpleFeatureVectorBuilder;
import pl.edu.icm.yadda.analysis.classification.hmm.tools.DirExtractor;
import pl.edu.icm.yadda.analysis.classification.hmm.tools.DocumentsExtractor;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.AbstractFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.AcknowledgementFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.AffiliationFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.AuthorFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.BibinfoFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.BracketRelativeCount;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.BracketedLineRelativeCount;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.CharCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.CharCountRelativeFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.CommaCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.CommaRelativeCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.ContainsCuePhrasesFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.CuePhrasesRelativeCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.DatesFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.DigitCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.DigitRelativeCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.DistanceFromNearestNeighbourFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.DotCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.DotRelativeCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.EmptySpaceRelativeFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.FigureFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.FontHeightMeanFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.FreeSpaceWithinZoneFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.HeightFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.HeightRelativeFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.HorizontalRelativeProminenceFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.IsFirstPageFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.IsFontBiggerThanNeighboursFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.IsHighestOnThePageFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.IsItemizeFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.IsLastButOnePageFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.IsLastPageFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.IsLowestOnThePageFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.KeywordsFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.LetterCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.LetterRelativeCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.LineCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.LineHeightMeanFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.LineRelativeCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.LineWidthMeanFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.LineXPositionDiffFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.LineXPositionMeanFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.LineXWidthPositionDiffFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.LowercaseCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.LowercaseRelativeCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.ContainsPageNumberFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.PageNumberFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.PreviousZoneFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.ProportionsFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.PunctuationRelativeCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.ReferencesFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.ReferencesTitleFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.StartsWithDigitFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.UppercaseCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.UppercaseRelativeCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.UppercaseWordCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.UppercaseWordRelativeCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.VerticalProminenceFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.WhitespaceCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.WhitespaceRelativeCountLogFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.WidthFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.WidthRelativeFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.WordCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.WordCountRelativeFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.WordLengthMeanFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.WordLengthMedianFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.WordWidthMeanFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.XPositionFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.XPositionRelativeFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.YPositionFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.YPositionRelativeFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.YearFeature;
import pl.edu.icm.yadda.analysis.textr.HierarchicalReadingOrderResolver;
import pl.edu.icm.yadda.analysis.textr.ReadingOrderResolver;
import pl.edu.icm.yadda.analysis.textr.ZoneClassifier;
import pl.edu.icm.yadda.analysis.textr.model.BxDocument;
import pl.edu.icm.yadda.analysis.textr.model.BxPage;
import pl.edu.icm.yadda.analysis.textr.model.BxZone;
import pl.edu.icm.yadda.analysis.textr.model.BxZoneLabel;
import pl.edu.icm.yadda.analysis.textr.tools.BxModelUtils;
import pl.edu.icm.yadda.analysis.textr.tools.DocumentPreprocessor;
import pl.edu.icm.yadda.analysis.textr.tools.UnclassifiedZonesPreprocessor;
import pl.edu.icm.yadda.analysis.textr.transformers.BxDocumentToTrueVizWriter;
import pl.edu.icm.yadda.analysis.textr.transformers.TrueVizToBxDocumentReader;

/**
 *
 * @author acz
 * @author Pawel Szostek (p.szostek@icm.edu.pl)
 */
public abstract class CrossvalidatingZoneClassificationEvaluator extends 
	AbstractSingleInputEvaluator<BxDocument, BxDocument, BxPage, CrossvalidatingZoneClassificationEvaluator.Results> {

	static private final Pattern FILENAME_PATTERN = Pattern.compile("(.+)\\.xml");

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
    private DocumentPreprocessor flattener = new UnclassifiedZonesPreprocessor();

    protected Integer iterations;
    protected Double ratio;
    
    private final Map<BxZoneLabel, BxZoneLabel> labelMap = DEFAULT_LABEL_MAP.clone();
	private final ReadingOrderResolver resolver = new HierarchicalReadingOrderResolver();

	private TrueVizToBxDocumentReader reader = new TrueVizToBxDocumentReader();
	private BxDocumentToTrueVizWriter writer = new BxDocumentToTrueVizWriter();
    
    protected abstract ZoneClassifier getZoneClassifier(List<BxDocument> trainingDocuments, FeatureVectorBuilder<BxZone, BxPage> featureVectorBuilder);
   
	public static FeatureVectorBuilder<BxZone, BxPage> getFeatureVectorBuilder()
	{
		FeatureVectorBuilder<BxZone, BxPage> vectorBuilder =
                new SimpleFeatureVectorBuilder<BxZone, BxPage>();
        vectorBuilder.setFeatureCalculators(Arrays.<FeatureCalculator<BxZone, BxPage>>asList(
        		new AbstractFeature(),
        		new AcknowledgementFeature(),
        		new AffiliationFeature(),
                new AuthorFeature(),
                new BibinfoFeature(),
        		new BracketRelativeCount(),
        		new BracketedLineRelativeCount(),
                new CharCountFeature(),
                new CharCountRelativeFeature(),
                new CommaCountFeature(),
                new CommaRelativeCountFeature(),
                new ContainsPageNumberFeature(),
        		new ContainsCuePhrasesFeature(),
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
        		new IsLastButOnePageFeature(),
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
                new PreviousZoneFeature(),
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

	protected Pattern getFilenamePattern() {
		return FILENAME_PATTERN;
	}

    @Override
    protected Results newResults() {
        return new Results(labelMap);
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
	protected BxDocument readDocument(Reader input) throws Exception {
	    return new BxDocument().setPages(reader.read(input));
	}
	
    @Override
    protected BxDocument processDocument(BxDocument document) throws AnalysisException {
    	
        this.zoneClassifier.classifyZones(document);
        return document;
    }
  
    @Override
    protected void preprocessDocument(BxDocument document) {
        flattener.process(document);
    }

    public static List<BxDocument> getEvaluationDocuments(String inputDirPath)
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

	@Override
	public void run(String inDir, String outDir) throws RuntimeException
	{
		List<BxDocument> evaluationDocuments = getEvaluationDocuments(inDir);
		Results summary = newResults();
        FeatureVectorBuilder<BxZone, BxPage> featureVectorBuilder = getFeatureVectorBuilder();

        for(BxDocument doc: evaluationDocuments)
        	preprocessDocument(doc);
        
        Integer iterationCounter = 1;
		for (int i = 0; i < iterations; ++i) {
			DividedEvaluationSet fileSets = DividedEvaluationSet.build(evaluationDocuments, ratio);
			List<BxDocument> trainingDocuments = fileSets.getTrainingSet();
			List<BxDocument> testDocuments = fileSets.getTestSet();
			Results iterationResults = newResults();
			
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

				Results documentResults = compareDocuments(documentsPair.getExpected(), documentsPair.getActual());
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

	// sample launch config: -compact -iter 5 -ratio 0.2 /path/to/your/xml/directory
	
    public void setZoneClassifier(ZoneClassifier zoneClassifier) {
        this.zoneClassifier = zoneClassifier;
    }

    public void setLabelMap(Map<BxZoneLabel, BxZoneLabel> value) {
        labelMap.putAll(DEFAULT_LABEL_MAP);
        labelMap.putAll(value);
    }

    public void setPreprocessor(DocumentPreprocessor flattener) {
    	this.flattener = flattener;
    }


	@Override
	protected void writeDocument(BxDocument document, Writer output) throws Exception {
	    writer.write(output, document.getPages());
	}

	@Override
	protected Iterator<BxPage> iterateItems(BxDocument document) {
	    return document.getPages().iterator();
	}
	
    @Override
    protected BxDocument prepareActualDocument(BxDocument document) throws Exception {
        document = BxModelUtils.deepClone(document);
        resolver.resolve(document);
        preprocessDocument(document);
        return processDocument(document);
    }
    
    protected BxDocument prepareExpectedDocument(BxDocument document) throws AnalysisException {
    	resolver.resolve(document);
        return document;
    }

    @Override
	protected void printItemResults(BxPage expected, BxPage actual, int itemIndex, Results results) {
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

    
	public static class Results implements AbstractEvaluator.Results<Results> {

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

	        System.out.println("Good recognitions per zone type:");
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
}
