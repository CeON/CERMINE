package pl.edu.icm.cermine.structure;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import pl.edu.icm.cermine.evaluation.EvaluationUtils;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.metadata.zoneclassification.features.*;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;
import pl.edu.icm.cermine.structure.transformers.BxDocumentToTrueVizWriter;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;
import pl.edu.icm.cermine.tools.classification.features.FeatureVectorBuilder;
import pl.edu.icm.cermine.tools.classification.general.SimpleFeatureVectorBuilder;
import pl.edu.icm.cermine.tools.classification.svm.SVMClassifier;
import pl.edu.icm.cermine.tools.classification.svm.SVMZoneClassifier;

/**
 * Classifying zones as: METADATA, BODY, REFERENCES, OTHER. 
 * 
 * @author Pawel Szostek (p.szostek@icm.edu.pl)
 */
public class SVMInitialZoneClassifier extends SVMZoneClassifier {
	
	public SVMInitialZoneClassifier(BufferedReader modelFile, BufferedReader rangeFile) throws AnalysisException {
		super(getFeatureVectorBuilder());
        try {
            loadModel(modelFile, rangeFile);
        } catch (IOException ex) {
            throw new AnalysisException("Cannot create SVM classifier!", ex);
        }
	}

	public SVMInitialZoneClassifier(String modelFilePath, String rangeFilePath) throws AnalysisException {
		super(getFeatureVectorBuilder());
		InputStreamReader modelISR = new InputStreamReader(Thread.currentThread().getClass()
				.getResourceAsStream(modelFilePath));
		BufferedReader modelFile = new BufferedReader(modelISR);
		
		InputStreamReader rangeISR = new InputStreamReader(Thread.currentThread().getClass()
				.getResourceAsStream(rangeFilePath));
		BufferedReader rangeFile = new BufferedReader(rangeISR);
        try {
            loadModel(modelFile, rangeFile);
        } catch (IOException ex) {
            throw new AnalysisException("Cannot create SVM classifier!", ex);
        }
	}

	public static FeatureVectorBuilder<BxZone, BxPage> getFeatureVectorBuilder()
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
                new YearFeature()
                ));
        return vectorBuilder;
	}
	
	public static void main(String[] args) throws AnalysisException, TransformationException, IOException {
		// args[0] path to xml directory
		if(args.length != 1) {
			System.err.println("Source directory needed!");
			System.exit(1);
		}
		InputStreamReader modelISR = new InputStreamReader(Thread.currentThread().getClass()
				.getResourceAsStream("/pl/edu/icm/cermine/structure/svm_initial_classifier"));
		BufferedReader modelFile = new BufferedReader(modelISR);
		
		InputStreamReader rangeISR = new InputStreamReader(Thread.currentThread().getClass()
				.getResourceAsStream("/pl/edu/icm/cermine/structure/svm_initial_classifier.range"));
		BufferedReader rangeFile = new BufferedReader(rangeISR);
		
		SVMZoneClassifier classifier = new SVMInitialZoneClassifier(modelFile, rangeFile);

		ReadingOrderResolver ror = new HierarchicalReadingOrderResolver();
		BxDocumentToTrueVizWriter tvw = new BxDocumentToTrueVizWriter();
		
		List<BxDocument> docs = EvaluationUtils.getDocumentsFromPath(args[0]);
		for(BxDocument doc: docs) {
			System.out.println(">> " + doc.getFilename());
			ror.resolve(doc);
			classifier.classifyZones(doc);
			BufferedWriter out = null;

			try {
				// Create file 
				FileWriter fstream = new FileWriter(doc.getFilename());
				out = new BufferedWriter(fstream);
				out.write(tvw.write(doc.getPages()));
				out.close();
			} catch (Exception e){//Catch exception if any
				System.err.println("Error: " + e.getMessage());
			} finally {
				if(out != null) {
					out.close();
				}
			}
		}
	}
}