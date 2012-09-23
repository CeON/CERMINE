package pl.edu.icm.yadda.analysis.metadata.evaluation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import libsvm.svm_parameter;

import org.apache.commons.cli.ParseException;
import org.apache.commons.collections.iterators.ArrayIterator;

import pl.edu.icm.yadda.analysis.classification.features.FeatureCalculator;
import pl.edu.icm.yadda.analysis.classification.features.FeatureVectorBuilder;
import pl.edu.icm.yadda.analysis.classification.features.SimpleFeatureVectorBuilder;
import pl.edu.icm.yadda.analysis.classification.hmm.training.TrainingElement;
import pl.edu.icm.yadda.analysis.classification.svm.SVMZoneClassifier;
import pl.edu.icm.yadda.analysis.metadata.sampleselection.OversamplingSelector;
import pl.edu.icm.yadda.analysis.metadata.sampleselection.SampleFilter;
import pl.edu.icm.yadda.analysis.metadata.sampleselection.SampleSelector;
import pl.edu.icm.yadda.analysis.metadata.sampleselection.UndersamplingSelector;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.AbstractFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.AffiliationFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.AuthorFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.AuthorNameRelativeFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.BibinfoFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.BracketRelativeCount;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.BracketedLineRelativeCount;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.CharCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.CharCountRelativeFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.CommaCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.CommaRelativeCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.ContainsCuePhrasesFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.ContainsPageNumberFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.DateFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.DigitCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.DigitRelativeCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.DistanceFromNearestNeighbourFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.DotCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.DotRelativeCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.EmailFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.EmptySpaceRelativeFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.FigureFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.FontHeightMeanFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.FreeSpaceWithinZoneFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.FullWordsRelativeFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.HeightFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.HeightRelativeFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.HorizontalRelativeProminenceFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.IsAfterMetTitleFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.IsAnywhereElseFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.IsFirstPageFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.IsFontBiggerThanNeighboursFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.IsGreatestFontOnPageFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.IsHighestOnThePageFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.IsWidestOnThePageFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.IsLastButOnePageFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.IsLastPageFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.IsLongestOnThePageFeature;
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
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.PageNumberFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.PreviousZoneFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.ProportionsFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.PunctuationRelativeCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.ReferencesFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.ReferencesTitleFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.StartsWithDigitFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.StartsWithHeaderFeature;
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
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.nodes.BxDocsToTrainingElementsConverterNode;
import pl.edu.icm.yadda.analysis.textr.ZoneClassifier;
import pl.edu.icm.yadda.analysis.textr.model.BxDocument;
import pl.edu.icm.yadda.analysis.textr.model.BxPage;
import pl.edu.icm.yadda.analysis.textr.model.BxZone;
import pl.edu.icm.yadda.analysis.textr.model.BxZoneLabel;
import pl.edu.icm.yadda.analysis.textr.model.BxZoneLabelCategory;

public class SVMMetadataClassificationEvaluator extends
		CrossvalidatingZoneClassificationEvaluator {

	@Override
	public FeatureVectorBuilder<BxZone, BxPage> getFeatureVectorBuilder() {
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
	
	@Override
	protected SVMZoneClassifier getZoneClassifier(List<BxDocument> trainingDocuments) {
		FeatureVectorBuilder<BxZone, BxPage> featureVectorBuilder = getFeatureVectorBuilder();
		
		Map<BxZoneLabel, BxZoneLabel> labelMapper = BxZoneLabel.getLabelToGeneralMap();
		for(BxDocument doc: trainingDocuments) {
			for(BxZone zone: doc.asZones()) {
				if(zone.getLabel().getCategory() != BxZoneLabelCategory.CAT_METADATA) {
					zone.setLabel(labelMapper.get(zone.getLabel()));
				}
			}
		}
		
		List<TrainingElement<BxZoneLabel>> trainingElementsUnrevised = EvaluationUtils.getTrainingElements(trainingDocuments, featureVectorBuilder);
		EvaluationUtils.filterElements(trainingElementsUnrevised, BxZoneLabelCategory.CAT_METADATA);

		double inequalityFactor = 1.5;
		
		SampleSelector<BxZoneLabel> selector = new OversamplingSelector<BxZoneLabel>(0.7);
		List<TrainingElement<BxZoneLabel>> trainingElements = selector.pickElements(trainingElementsUnrevised);


		SVMZoneClassifier zoneClassifier = new SVMZoneClassifier(featureVectorBuilder);
		svm_parameter param = SVMZoneClassifier.getDefaultParam();
		param.svm_type = svm_parameter.C_SVC;
		param.gamma = 1.0/8.0;
		param.C = 16.0;
		//param.degree = 3;
		param.kernel_type = svm_parameter.RBF;
		zoneClassifier.setParameter(param);

		zoneClassifier.buildClassifier(trainingElements);
		zoneClassifier.printWeigths(featureVectorBuilder);

		return zoneClassifier;
	}

	public static void main(String[] args) throws ParseException {
		CrossvalidatingZoneClassificationEvaluator.main(args, new SVMMetadataClassificationEvaluator());
	}

	@Override
	protected SampleSelector<BxZoneLabel> getSampleFilter() {
		return new SampleSelector<BxZoneLabel>() {
			@Override
			public List<TrainingElement<BxZoneLabel>> pickElements(List<TrainingElement<BxZoneLabel>> inputElements) {
				List<TrainingElement<BxZoneLabel>> ret = new ArrayList<TrainingElement<BxZoneLabel>>();
				for(TrainingElement<BxZoneLabel> elem: inputElements)
					if(elem.getLabel().getCategory() == BxZoneLabelCategory.CAT_METADATA)
						ret.add(elem);
				return ret;
			}
		};
	}

	@Override
	protected ClassificationResults compareDocuments(BxDocument expected, BxDocument actual) {
		assert expected.asZones().size() == actual.asZones().size();
		
		ClassificationResults ret = newResults();
		for(Integer idx=0; idx < expected.asZones().size(); ++idx) {
			BxZone expectedZone = expected.asZones().get(idx);
			BxZone actualZone = actual.asZones().get(idx);
			if(expectedZone.getLabel().getCategory() != BxZoneLabelCategory.CAT_METADATA)
				continue;
				
			System.out.println("--- " + actualZone.getLabel() + " " + expectedZone.getLabel());
			ret.add(compareItems(expectedZone, actualZone));
		}
		return ret;
	}

	@Override
	protected void preprocessDocumentForEvaluation(BxDocument doc) {
	}
}
