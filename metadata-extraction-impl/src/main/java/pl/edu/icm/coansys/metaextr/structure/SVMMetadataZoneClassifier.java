package pl.edu.icm.coansys.metaextr.structure;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import pl.edu.icm.coansys.metaextr.AnalysisException;
import pl.edu.icm.coansys.metaextr.tools.classification.features.FeatureCalculator;
import pl.edu.icm.coansys.metaextr.tools.classification.features.FeatureVectorBuilder;
import pl.edu.icm.coansys.metaextr.tools.classification.features.SimpleFeatureVectorBuilder;
import pl.edu.icm.coansys.metaextr.tools.classification.svm.SVMZoneClassifier;
import pl.edu.icm.coansys.metaextr.metadata.evaluation.EvaluationUtils;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.AbstractFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.AffiliationFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.AuthorFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.AuthorNameRelativeFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.BibinfoFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.BracketRelativeCount;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.BracketedLineRelativeCount;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.CharCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.CharCountRelativeFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.CommaCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.CommaRelativeCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.ContainsPageNumberFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.CuePhrasesRelativeCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.DateFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.DigitCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.DigitRelativeCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.DistanceFromNearestNeighbourFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.DotCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.DotRelativeCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.EmailFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.EmptySpaceRelativeFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.FontHeightMeanFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.FreeSpaceWithinZoneFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.FullWordsRelativeFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.HeightFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.HeightRelativeFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.HorizontalRelativeProminenceFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.IsAfterMetTitleFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.IsAnywhereElseFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.IsFirstPageFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.IsFontBiggerThanNeighboursFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.IsGreatestFontOnPageFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.IsHighestOnThePageFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.IsItemizeFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.IsLastButOnePageFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.IsLastPageFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.IsLeftFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.IsLongestOnThePageFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.IsLowestOnThePageFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.IsOnSurroundingPagesFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.IsRightFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.IsWidestOnThePageFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.KeywordsFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.LetterCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.LetterRelativeCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.LineCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.LineHeightMeanFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.LineRelativeCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.LineWidthMeanFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.LineXPositionDiffFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.LineXPositionMeanFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.LineXWidthPositionDiffFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.LowercaseCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.LowercaseRelativeCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.PageNumberFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.PreviousZoneFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.ProportionsFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.PunctuationRelativeCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.ReferencesFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.StartsWithDigitFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.StartsWithHeaderFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.UppercaseCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.UppercaseRelativeCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.UppercaseWordCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.UppercaseWordRelativeCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.VerticalProminenceFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.WhitespaceCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.WhitespaceRelativeCountLogFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.WidthFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.WidthRelativeFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.WordCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.WordCountRelativeFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.WordLengthMeanFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.WordLengthMedianFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.WordWidthMeanFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.XPositionFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.XPositionRelativeFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.YPositionFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.YPositionRelativeFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.YearFeature;
import pl.edu.icm.coansys.metaextr.structure.model.BxDocument;
import pl.edu.icm.coansys.metaextr.structure.model.BxPage;
import pl.edu.icm.coansys.metaextr.structure.model.BxZone;

public class SVMMetadataZoneClassifier extends SVMZoneClassifier {

	public SVMMetadataZoneClassifier(BufferedReader modelFile, BufferedReader rangeFile) throws IOException {
		super(getFeatureVectorBuilder());
		loadModel(modelFile, rangeFile);
	}

	public SVMMetadataZoneClassifier(String modelFilePath, String rangeFilePath) throws IOException {
		super(getFeatureVectorBuilder());
		InputStreamReader modelISR = new InputStreamReader(Thread.currentThread().getClass()
				.getResourceAsStream(modelFilePath));
		BufferedReader modelFile = new BufferedReader(modelISR);
		
		InputStreamReader rangeISR = new InputStreamReader(Thread.currentThread().getClass()
				.getResourceAsStream(rangeFilePath));
		BufferedReader rangeFile = new BufferedReader(rangeISR);
		loadModel(modelFile, rangeFile);
	}

	public static FeatureVectorBuilder<BxZone, BxPage> getFeatureVectorBuilder() {
		FeatureVectorBuilder<BxZone, BxPage> vectorBuilder = new SimpleFeatureVectorBuilder<BxZone, BxPage>();
		vectorBuilder.setFeatureCalculators(Arrays
				.<FeatureCalculator<BxZone, BxPage>> asList(
						new AbstractFeature(),
						new AffiliationFeature(),
						new AuthorFeature(),
						new AuthorNameRelativeFeature(),
						new BibinfoFeature(),
						new CharCountFeature(),
						new CharCountRelativeFeature(),
						new DateFeature(),
						new DistanceFromNearestNeighbourFeature(),
						new DotCountFeature(),
						new DotRelativeCountFeature(),
						new EmailFeature(),
						new EmptySpaceRelativeFeature(),
						new FontHeightMeanFeature(),
						new FreeSpaceWithinZoneFeature(),
						new FullWordsRelativeFeature(),
						new HeightFeature(),
						new HeightRelativeFeature(),
						new HorizontalRelativeProminenceFeature(),
						new IsAfterMetTitleFeature(),
						new IsFontBiggerThanNeighboursFeature(),
						new IsGreatestFontOnPageFeature(),
						new IsWidestOnThePageFeature(),
						new KeywordsFeature(),
						new LineCountFeature(),
						new LineRelativeCountFeature(),
						new LineHeightMeanFeature(),
						new LineWidthMeanFeature(),
						new LineXPositionMeanFeature(),
						new LineXWidthPositionDiffFeature(),
						new LetterCountFeature(),
						new LetterRelativeCountFeature(),
						new LowercaseCountFeature(),
						new LowercaseRelativeCountFeature(),
						new PreviousZoneFeature(),
						new ProportionsFeature(),
						new PunctuationRelativeCountFeature(),
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
						)
				);
		return vectorBuilder;
	}

	public static void main(String[] args) throws AnalysisException, IOException {
		// args[0] path to the directory containing XML files
		InputStreamReader modelISR = new InputStreamReader(Thread.currentThread().getClass()
				.getResourceAsStream("/pl/edu/icm/coansys/metaextr/textr/svm_metadata_classifier"));
		BufferedReader modelFile = new BufferedReader(modelISR);
		
		InputStreamReader rangeISR = new InputStreamReader(Thread.currentThread().getClass()
				.getResourceAsStream("/pl/edu/icm/coansys/metaextr/textr/svm_metadata_classifier.range"));
		BufferedReader rangeFile = new BufferedReader(rangeISR);
		
		SVMZoneClassifier classifier = new SVMMetadataZoneClassifier(modelFile, rangeFile);
		
		List<BxDocument> docs = EvaluationUtils.getDocumentsFromPath(args[0]);
		for(BxDocument doc: docs) {
			classifier.classifyZones(doc);
			// Attention!
			// All the zones will be classified using one of labels from metadata category
			// For a correct operation the zones should be filtered.
			// Possible SVMZoneClassifier.predictZoneLabel(BxZone zone) may be employed.
			// For instance:
			// for(BxZone zone: doc.asZones())
			//     if(zone.getLabel().getCategory() == BxZoneLabelCategory.CAT_METADATA)
			//         zone.setLabel(classifier.predictZoneLabel(zone));
			for(BxZone zone: doc.asZones()) {
				System.out.println("****** " + zone.getLabel());
				System.out.println(zone.toText());
			}
		}
	}
}
