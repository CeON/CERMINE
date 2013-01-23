package pl.edu.icm.cermine.evaluation;

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
