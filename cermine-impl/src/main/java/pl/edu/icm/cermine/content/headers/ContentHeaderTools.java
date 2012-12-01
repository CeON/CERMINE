package pl.edu.icm.cermine.content.headers;

import java.util.Arrays;
import pl.edu.icm.cermine.content.headers.features.*;
import pl.edu.icm.cermine.structure.model.BxLine;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;
import pl.edu.icm.cermine.tools.classification.features.FeatureVectorBuilder;
import pl.edu.icm.cermine.tools.classification.general.SimpleFeatureVectorBuilder;

/**
 *
 * @author Dominika Tkaczyk
 */
public class ContentHeaderTools {

    public static final FeatureVectorBuilder<BxLine, BxPage> vectorBuilder = new SimpleFeatureVectorBuilder<BxLine, BxPage>();
    static {
        vectorBuilder.setFeatureCalculators(Arrays.<FeatureCalculator<BxLine, BxPage>>asList(
                new DigitDotSchemaFeature(),
                new DigitParSchemaFeature(),
                new DoubleDigitSchemaFeature(),
                new HeightFeature(),
                new IndentationFeature(),
                new IsHigherThanNeighborsFeature(),
                new LengthFeature(),
                new LowercaseSchemaFeature(),
                new NextLineIndentationFeature(),
                new PrevSpaceFeature(),
                new RomanDigitsSchemaFeature(),
                new TripleDigitSchemaFeature(),
                new UppercaseSchemaFeature(),
                new WordsAllUppercaseFeature(),
                new WordsUppercaseFeature()
                ));
    }
    
    public static final FeatureVectorBuilder<BxLine, BxPage> clustVectorBuilder = new SimpleFeatureVectorBuilder<BxLine, BxPage>();
    static {
        clustVectorBuilder.setFeatureCalculators(Arrays.<FeatureCalculator<BxLine, BxPage>>asList(
                new DigitDotSchemaFeature(),
                new DigitParSchemaFeature(),
                new DoubleDigitSchemaFeature(),
                new LowercaseSchemaFeature(),
                new RomanDigitsSchemaFeature(),
                new TripleDigitSchemaFeature(),
                new UppercaseSchemaFeature()
                ));
    }
    /*
    public static List<TrainingElement<BxZoneLabel>> toTrainingElements(String trainPath) throws AnalysisException {
        List<BxDocument> documents = EvaluationUtils.getDocumentsFromPath(trainPath);
        return toTrainingElements(documents);
    }*/
/*
    public static List<TrainingElement<BxZoneLabel>> toTrainingElements(List<BxDocument> documents) throws AnalysisException {
        List<TrainingElement<BxZoneLabel>> trainingElements;

        SampleSelector<BxZoneLabel> selector = new OversamplingSelector<BxZoneLabel>(1.0);
        
        BxDocsToHMMConverter node = new BxDocsToHMMConverter();
        
        Map<BxZoneLabel, BxZoneLabel> map = new EnumMap<BxZoneLabel, BxZoneLabel>(BxZoneLabel.class);
        map.put(BxZoneLabel.BODY_HEADER, BxZoneLabel.BODY_CONTENT);
        node.setLabelMap(map);
        
        node.setFeatureVectorBuilder(vectorBuilder);
        
        trainingElements = node.process(documents);
        trainingElements = ClassificationUtils.filterElements(trainingElements, BxZoneLabelCategory.CAT_BODY);
        trainingElements = selector.pickElements(trainingElements);
        
        return trainingElements;
    }
  */  
}
