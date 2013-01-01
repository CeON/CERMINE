package pl.edu.icm.cermine.evaluation;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import libsvm.svm_parameter;
import org.apache.commons.cli.ParseException;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.metadata.zoneclassification.features.*;
import pl.edu.icm.cermine.structure.SVMMetadataZoneClassifier;
import pl.edu.icm.cermine.structure.model.*;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;
import pl.edu.icm.cermine.tools.classification.features.FeatureVectorBuilder;
import pl.edu.icm.cermine.tools.classification.general.BxDocsToTrainingSamplesConverter;
import pl.edu.icm.cermine.tools.classification.general.ClassificationUtils;
import pl.edu.icm.cermine.tools.classification.general.TrainingSample;
import pl.edu.icm.cermine.tools.classification.sampleselection.OversamplingSelector;
import pl.edu.icm.cermine.tools.classification.sampleselection.SampleSelector;
import pl.edu.icm.cermine.tools.classification.svm.SVMZoneClassifier;

public class SVMMetadataClassificationEvaluator extends CrossvalidatingZoneClassificationEvaluator {

    @Override
    public FeatureVectorBuilder<BxZone, BxPage> getFeatureVectorBuilder() {
        FeatureVectorBuilder<BxZone, BxPage> vectorBuilder = new FeatureVectorBuilder<BxZone, BxPage>();
        vectorBuilder.setFeatureCalculators(Arrays.<FeatureCalculator<BxZone, BxPage>>asList(
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
                new YearFeature()));
        return vectorBuilder;
    }

    @Override
    protected SVMZoneClassifier getZoneClassifier(List<BxDocument> trainingDocuments) throws IOException, AnalysisException {
        FeatureVectorBuilder<BxZone, BxPage> featureVectorBuilder = getFeatureVectorBuilder();

        Map<BxZoneLabel, BxZoneLabel> labelMapper = BxZoneLabel.getLabelToGeneralMap();
        for (BxDocument doc : trainingDocuments) {
            for (BxZone zone : doc.asZones()) {
                if (zone.getLabel().getCategory() != BxZoneLabelCategory.CAT_METADATA) {
                    zone.setLabel(labelMapper.get(zone.getLabel()));
                }
            }
        }

        List<TrainingSample<BxZoneLabel>> TrainingSamplesUnrevised = BxDocsToTrainingSamplesConverter.getZoneTrainingSamples(trainingDocuments, featureVectorBuilder);
        TrainingSamplesUnrevised = ClassificationUtils.filterElements(TrainingSamplesUnrevised, BxZoneLabelCategory.CAT_METADATA);

        SampleSelector<BxZoneLabel> selector = new OversamplingSelector<BxZoneLabel>(0.7);
        List<TrainingSample<BxZoneLabel>> TrainingSamples = selector.pickElements(TrainingSamplesUnrevised);


        SVMZoneClassifier zoneClassifier = new SVMZoneClassifier(featureVectorBuilder);
        svm_parameter param = SVMZoneClassifier.getDefaultParam();
        param.svm_type = svm_parameter.C_SVC;
        param.gamma = 1.0 / 8.0;
        param.C = 16.0;
        //param.degree = 3;
        param.kernel_type = svm_parameter.RBF;
        zoneClassifier.setParameter(param);

        zoneClassifier.buildClassifier(TrainingSamples);
        zoneClassifier.saveModel("metadata_classifier");
        zoneClassifier.printWeigths(featureVectorBuilder);

        return zoneClassifier;
    }

    public static void main(String[] args)
            throws ParseException, AnalysisException, IOException, TransformationException {
        CrossvalidatingZoneClassificationEvaluator.main(args, new SVMMetadataClassificationEvaluator());
    }

    @Override
    protected ClassificationResults compareDocuments(BxDocument expected, BxDocument actual) {
        assert expected.asZones().size() == actual.asZones().size();

        ClassificationResults ret = newResults();
        for (Integer idx = 0; idx < expected.asZones().size(); ++idx) {
            BxZone expectedZone = expected.asZones().get(idx);
            BxZone actualZone = actual.asZones().get(idx);
            if (expectedZone.getLabel().getCategory() != BxZoneLabelCategory.CAT_METADATA) {
                continue;
            }

            System.out.println("--- " + actualZone.getLabel() + " " + expectedZone.getLabel());
            ret.add(compareItems(expectedZone, actualZone));
        }
        return ret;
    }

    @Override
    protected void preprocessDocumentForEvaluation(BxDocument doc) {
    }
}
