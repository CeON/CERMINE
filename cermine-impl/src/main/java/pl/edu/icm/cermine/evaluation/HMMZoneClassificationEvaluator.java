package pl.edu.icm.cermine.evaluation;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.ParseException;

import pl.edu.icm.cermine.evaluation.tools.ClassificationResults;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.metadata.zoneclassification.features.AffiliationFeature;
import pl.edu.icm.cermine.metadata.zoneclassification.features.AuthorFeature;
import pl.edu.icm.cermine.metadata.zoneclassification.features.AuthorNameRelativeFeature;
import pl.edu.icm.cermine.metadata.zoneclassification.features.BibinfoFeature;
import pl.edu.icm.cermine.metadata.zoneclassification.features.BracketRelativeCount;
import pl.edu.icm.cermine.metadata.zoneclassification.features.BracketedLineRelativeCount;
import pl.edu.icm.cermine.metadata.zoneclassification.features.CharCountFeature;
import pl.edu.icm.cermine.metadata.zoneclassification.features.CharCountRelativeFeature;
import pl.edu.icm.cermine.metadata.zoneclassification.features.CommaCountFeature;
import pl.edu.icm.cermine.metadata.zoneclassification.features.CommaRelativeCountFeature;
import pl.edu.icm.cermine.metadata.zoneclassification.features.ContainsPageNumberFeature;
import pl.edu.icm.cermine.metadata.zoneclassification.features.CuePhrasesRelativeCountFeature;
import pl.edu.icm.cermine.metadata.zoneclassification.features.DateFeature;
import pl.edu.icm.cermine.metadata.zoneclassification.features.DigitCountFeature;
import pl.edu.icm.cermine.metadata.zoneclassification.features.DigitRelativeCountFeature;
import pl.edu.icm.cermine.metadata.zoneclassification.features.DistanceFromNearestNeighbourFeature;
import pl.edu.icm.cermine.metadata.zoneclassification.features.DotCountFeature;
import pl.edu.icm.cermine.metadata.zoneclassification.features.DotRelativeCountFeature;
import pl.edu.icm.cermine.metadata.zoneclassification.features.EmptySpaceRelativeFeature;
import pl.edu.icm.cermine.metadata.zoneclassification.features.FontHeightMeanFeature;
import pl.edu.icm.cermine.metadata.zoneclassification.features.FreeSpaceWithinZoneFeature;
import pl.edu.icm.cermine.metadata.zoneclassification.features.FullWordsRelativeFeature;
import pl.edu.icm.cermine.metadata.zoneclassification.features.HeightFeature;
import pl.edu.icm.cermine.metadata.zoneclassification.features.HeightRelativeFeature;
import pl.edu.icm.cermine.metadata.zoneclassification.features.HorizontalRelativeProminenceFeature;
import pl.edu.icm.cermine.metadata.zoneclassification.features.IsAnywhereElseFeature;
import pl.edu.icm.cermine.metadata.zoneclassification.features.IsFirstPageFeature;
import pl.edu.icm.cermine.metadata.zoneclassification.features.IsFontBiggerThanNeighboursFeature;
import pl.edu.icm.cermine.metadata.zoneclassification.features.IsGreatestFontOnPageFeature;
import pl.edu.icm.cermine.metadata.zoneclassification.features.IsHighestOnThePageFeature;
import pl.edu.icm.cermine.metadata.zoneclassification.features.IsItemizeFeature;
import pl.edu.icm.cermine.metadata.zoneclassification.features.IsLastButOnePageFeature;
import pl.edu.icm.cermine.metadata.zoneclassification.features.IsLastPageFeature;
import pl.edu.icm.cermine.metadata.zoneclassification.features.IsLeftFeature;
import pl.edu.icm.cermine.metadata.zoneclassification.features.IsLongestOnThePageFeature;
import pl.edu.icm.cermine.metadata.zoneclassification.features.IsLowestOnThePageFeature;
import pl.edu.icm.cermine.metadata.zoneclassification.features.IsOnSurroundingPagesFeature;
import pl.edu.icm.cermine.metadata.zoneclassification.features.IsPageNumberFeature;
import pl.edu.icm.cermine.metadata.zoneclassification.features.IsRightFeature;
import pl.edu.icm.cermine.metadata.zoneclassification.features.IsSingleWordFeature;
import pl.edu.icm.cermine.metadata.zoneclassification.features.IsWidestOnThePageFeature;
import pl.edu.icm.cermine.metadata.zoneclassification.features.LetterCountFeature;
import pl.edu.icm.cermine.metadata.zoneclassification.features.LetterRelativeCountFeature;
import pl.edu.icm.cermine.metadata.zoneclassification.features.LineCountFeature;
import pl.edu.icm.cermine.metadata.zoneclassification.features.LineHeightMeanFeature;
import pl.edu.icm.cermine.metadata.zoneclassification.features.LineRelativeCountFeature;
import pl.edu.icm.cermine.metadata.zoneclassification.features.LineWidthMeanFeature;
import pl.edu.icm.cermine.metadata.zoneclassification.features.LineXPositionDiffFeature;
import pl.edu.icm.cermine.metadata.zoneclassification.features.LineXPositionMeanFeature;
import pl.edu.icm.cermine.metadata.zoneclassification.features.LineXWidthPositionDiffFeature;
import pl.edu.icm.cermine.metadata.zoneclassification.features.LowercaseCountFeature;
import pl.edu.icm.cermine.metadata.zoneclassification.features.LowercaseRelativeCountFeature;
import pl.edu.icm.cermine.metadata.zoneclassification.features.PageNumberFeature;
import pl.edu.icm.cermine.metadata.zoneclassification.features.PreviousZoneFeature;
import pl.edu.icm.cermine.metadata.zoneclassification.features.ProportionsFeature;
import pl.edu.icm.cermine.metadata.zoneclassification.features.PunctuationRelativeCountFeature;
import pl.edu.icm.cermine.metadata.zoneclassification.features.ReferencesFeature;
import pl.edu.icm.cermine.metadata.zoneclassification.features.StartsWithDigitFeature;
import pl.edu.icm.cermine.metadata.zoneclassification.features.StartsWithHeaderFeature;
import pl.edu.icm.cermine.metadata.zoneclassification.features.UppercaseCountFeature;
import pl.edu.icm.cermine.metadata.zoneclassification.features.UppercaseRelativeCountFeature;
import pl.edu.icm.cermine.metadata.zoneclassification.features.UppercaseWordCountFeature;
import pl.edu.icm.cermine.metadata.zoneclassification.features.UppercaseWordRelativeCountFeature;
import pl.edu.icm.cermine.metadata.zoneclassification.features.VerticalProminenceFeature;
import pl.edu.icm.cermine.metadata.zoneclassification.features.WhitespaceCountFeature;
import pl.edu.icm.cermine.metadata.zoneclassification.features.WhitespaceRelativeCountLogFeature;
import pl.edu.icm.cermine.metadata.zoneclassification.features.WidthFeature;
import pl.edu.icm.cermine.metadata.zoneclassification.features.WidthRelativeFeature;
import pl.edu.icm.cermine.metadata.zoneclassification.features.WordCountFeature;
import pl.edu.icm.cermine.metadata.zoneclassification.features.WordCountRelativeFeature;
import pl.edu.icm.cermine.metadata.zoneclassification.features.WordLengthMeanFeature;
import pl.edu.icm.cermine.metadata.zoneclassification.features.WordLengthMedianFeature;
import pl.edu.icm.cermine.metadata.zoneclassification.features.WordWidthMeanFeature;
import pl.edu.icm.cermine.metadata.zoneclassification.features.XPositionFeature;
import pl.edu.icm.cermine.metadata.zoneclassification.features.XPositionRelativeFeature;
import pl.edu.icm.cermine.metadata.zoneclassification.features.YPositionFeature;
import pl.edu.icm.cermine.metadata.zoneclassification.features.YPositionRelativeFeature;
import pl.edu.icm.cermine.metadata.zoneclassification.features.YearFeature;
import pl.edu.icm.cermine.metadata.zoneclassification.tools.BxDocsToHMMConverter;
import pl.edu.icm.cermine.structure.HMMZoneClassifier;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;
import pl.edu.icm.cermine.structure.model.BxZoneLabelCategory;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;
import pl.edu.icm.cermine.tools.classification.features.FeatureVectorBuilder;
import pl.edu.icm.cermine.tools.classification.general.TrainingSample;
import pl.edu.icm.cermine.tools.classification.hmm.HMMServiceImpl;
import pl.edu.icm.cermine.tools.classification.hmm.model.HMMProbabilityInfo;
import pl.edu.icm.cermine.tools.classification.hmm.model.HMMProbabilityInfoFactory;
import pl.edu.icm.cermine.tools.classification.svm.SVMZoneClassifier;

/*
 *  @author Pawel Szostek (p.szostek@icm.edu.pl)
 */

//public class HMMZoneClassificationEvaluator extends CrossvalidatingZoneClassificationEvaluator
//{
//	@Override
//	protected HMMZoneClassifier getZoneClassifier(List<BxDocument> trainingDocuments)
//	{
//		FeatureVectorBuilder<BxZone, BxPage> featureVectorBuilder = getFeatureVectorBuilder();
//        BxDocsToHMMConverter node = new BxDocsToHMMConverter();
//        node.setFeatureVectorBuilder(featureVectorBuilder);
//        node.setLabelMap(BxZoneLabel.getLabelToGeneralMap());
//        
//        List<HMMTrainingSample<BxZoneLabel>> trainingElements;
//        try {
//        	trainingElements = node.process(trainingDocuments);
//        } catch (Exception e) {
//			throw new RuntimeException("Unable to process the delivered training documents!", e);
//		}
//        
//		HMMProbabilityInfo<BxZoneLabel> hmmProbabilities;
//		try {
//			hmmProbabilities = HMMProbabilityInfoFactory.getFVHMMProbability(trainingElements, featureVectorBuilder);
//		} catch (Exception e) {
//			throw new RuntimeException("Unable to figure out HMM probability information!", e);
//		}
//		
//		HMMZoneClassifier zoneClassifier = new HMMZoneClassifier(
//				new HMMServiceImpl(),
//				hmmProbabilities,
//				BxZoneLabel.valuesOfCategory(BxZoneLabelCategory.CAT_GENERAL),
//				featureVectorBuilder);
//		return zoneClassifier;
//	}
//	
//    public FeatureVectorBuilder<BxZone, BxPage> getFeatureVectorBuilder() {
//        FeatureVectorBuilder<BxZone, BxPage> vectorBuilder = new FeatureVectorBuilder<BxZone, BxPage>();
//        vectorBuilder.setFeatureCalculators(Arrays.<FeatureCalculator<BxZone, BxPage>>asList(
//                new AffiliationFeature(),
//                new AuthorFeature(),
//                new AuthorNameRelativeFeature(),
//                new BibinfoFeature(),
//                new BracketRelativeCount(),
//                new BracketedLineRelativeCount(),
//                new CharCountFeature(),
//                new CharCountRelativeFeature(),
//                new CommaCountFeature(),
//                new CommaRelativeCountFeature(),
//                new ContainsPageNumberFeature(),
//                new CuePhrasesRelativeCountFeature(),
//                new DateFeature(),
//                new DigitCountFeature(),
//                new DigitRelativeCountFeature(),
//                new DistanceFromNearestNeighbourFeature(),
//                new DotCountFeature(),
//                new DotRelativeCountFeature(),
//                new EmptySpaceRelativeFeature(),
//                new FontHeightMeanFeature(),
//                new FreeSpaceWithinZoneFeature(),
//                new FullWordsRelativeFeature(),
//                new HeightFeature(),
//                new HeightRelativeFeature(),
//                new HorizontalRelativeProminenceFeature(),
//                new IsAnywhereElseFeature(),
//                new IsFirstPageFeature(),
//                new IsFontBiggerThanNeighboursFeature(),
//                new IsGreatestFontOnPageFeature(),
//                new IsHighestOnThePageFeature(),
//                new IsItemizeFeature(),
//                new IsWidestOnThePageFeature(),
//                new IsLastButOnePageFeature(),
//                new IsLastPageFeature(),
//                new IsLeftFeature(),
//                new IsLongestOnThePageFeature(),
//                new IsLowestOnThePageFeature(),
//                new IsItemizeFeature(),
//                new IsOnSurroundingPagesFeature(),
//                new IsPageNumberFeature(),
//                new IsRightFeature(),
//        		new IsSingleWordFeature(),
//                new LineCountFeature(),
//                new LineRelativeCountFeature(),
//                new LineHeightMeanFeature(),
//                new LineWidthMeanFeature(),
//                new LineXPositionMeanFeature(),
//                new LineXPositionDiffFeature(),
//                new LineXWidthPositionDiffFeature(),
//                new LetterCountFeature(),
//                new LetterRelativeCountFeature(),
//                new LowercaseCountFeature(),
//                new LowercaseRelativeCountFeature(),
//                new PageNumberFeature(),
//                new PreviousZoneFeature(),
//                new ProportionsFeature(),
//                new PunctuationRelativeCountFeature(),
//                new ReferencesFeature(),
//                new StartsWithDigitFeature(),
//                new StartsWithHeaderFeature(),
//                new UppercaseCountFeature(),
//                new UppercaseRelativeCountFeature(),
//                new UppercaseWordCountFeature(),
//                new UppercaseWordRelativeCountFeature(),
//                new VerticalProminenceFeature(),
//                new WidthFeature(),
//                new WordCountFeature(),
//                new WordCountRelativeFeature(),
//                new WordWidthMeanFeature(),
//                new WordLengthMeanFeature(),
//                new WordLengthMedianFeature(),
//                new WhitespaceCountFeature(),
//                new WhitespaceRelativeCountLogFeature(),
//                new WidthRelativeFeature(),
//                new XPositionFeature(),
//                new XPositionRelativeFeature(),
//                new YPositionFeature(),
//                new YPositionRelativeFeature(),
//                new YearFeature()));
//        return vectorBuilder;
//    }
//	
//	public static void main(String[] args) throws ParseException, AnalysisException, IOException, TransformationException {
//		CrossvalidatingZoneClassificationEvaluator.main(args, new HMMZoneClassificationEvaluator());
//	}
//
//	protected ClassificationResults compareSamples(BxDocument expected, BxDocument actual) {
//		ClassificationResults ret = newResults();
//		for(Integer idx=0; idx < actual.asZones().size(); ++idx) {
//			ClassificationResults itemResults =
//					compareItems(expected.asZones().get(idx).getLabel(),
//							actual.asZones().get(idx).getLabel());
//			ret.add(itemResults);
//		}
//		return ret;
//	}
//
//	protected void preprocessDocumentForEvaluation(BxDocument doc) {
//		for(BxZone zone: doc.asZones()) {
//			zone.setLabel(zone.getLabel().getGeneralLabel());
//        }
//	}
//
//}
