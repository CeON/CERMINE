package pl.edu.icm.yadda.analysis.metadata.zoneclassification;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import pl.edu.icm.yadda.analysis.classification.features.FeatureCalculator;
import pl.edu.icm.yadda.analysis.classification.features.FeatureVector;
import pl.edu.icm.yadda.analysis.classification.features.FeatureVectorBuilder;
import pl.edu.icm.yadda.analysis.classification.features.SimpleFeatureVectorBuilder;
import pl.edu.icm.yadda.analysis.classification.hmm.HMMServiceImpl;
import pl.edu.icm.yadda.analysis.classification.hmm.probability.HMMProbabilityInfo;
import pl.edu.icm.yadda.analysis.classification.hmm.probability.HMMProbabilityInfoFactory;
import pl.edu.icm.yadda.analysis.classification.hmm.training.HMMTrainingElement;
import pl.edu.icm.yadda.analysis.classification.hmm.training.SimpleHMMTrainingElement;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.*;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.nodes.BxDocsToFVHMMTrainingElementsConverterNode;
import pl.edu.icm.yadda.analysis.textr.HMMZoneGeneralClassifier;
import pl.edu.icm.yadda.analysis.textr.model.*;
import pl.edu.icm.yadda.analysis.textr.transformers.TrueVizToBxDocumentReader;
import pl.edu.icm.yadda.metadata.transformers.TransformationException;

/**
 *
 * @author Pawel Szostek (p.szostek@icm.edu.pl)
 */
@Deprecated
public class HMMZoneGeneralClassificationDemo {

    protected static final String hmmTestFile = "/pl/edu/icm/yadda/analysis/metadata/zoneclassification/09629351.xml";

    public static void main(String[] args) throws TransformationException, Exception {
        
        // 1. construct vector of features builder
        FeatureVectorBuilder<BxZone, BxPage> vectorBuilder =
                new SimpleFeatureVectorBuilder<BxZone, BxPage>();
        vectorBuilder.setFeatureCalculators(Arrays.<FeatureCalculator<BxZone, BxPage>>asList(
        		new FigureFeature(),
        		new BracketRelativeCount(),
        		new BracketedLineRelativeCount(),
        		new EmptySpaceRelativeFeature(),
        		new IsHighestOnThePageFeature(),
        		new IsLowestOnThePageFeature(),
        		new ContainsCuePhrasesFeature(),
        		new CuePhrasesRelativeCountFeature(),
        		new WordLengthMeanFeature(),
        		new WordLengthMedianFeature(),
        		new WhitespaceCountFeature(),
        		new WhitespaceRelativeCountLogFeature(),
        		new VerticalProminenceFeature(),
        		new HorizontalRelativeProminenceFeature(),
        		new IsFontBiggerThanNeighboursFeature(),
        		new DistanceFromNearestNeighbourFeature(),
        		new AcknowledgementFeature(),
        		new FontHeightMeanFeature(),
                new ProportionsFeature(),
                new HeightFeature(),
                new WidthFeature(),
                new XPositionFeature(),
                new YPositionFeature(),
                new HeightRelativeFeature(),
                new WidthRelativeFeature(),
                new XPositionRelativeFeature(),
                new YPositionRelativeFeature(),
                new LineCountFeature(),
                new LineRelativeCountFeature(),
                new LineHeightMeanFeature(),
                new LineWidthMeanFeature(),
                new LineXPositionMeanFeature(),
                new LineXPositionDiffFeature(),
                new LineXWidthPositionDiffFeature(),
                new WordCountFeature(),
                new WordCountRelativeFeature(),
                new CharCountFeature(),
                new CharCountRelativeFeature(),
                new DigitCountFeature(),
                new DigitRelativeCountFeature(),
                new LetterCountFeature(),
                new LetterRelativeCountFeature(),
                new LowercaseCountFeature(),
                new LowercaseRelativeCountFeature(),
                new UppercaseCountFeature(),
                new UppercaseRelativeCountFeature(),
                new UppercaseWordCountFeature(),
                new UppercaseWordRelativeCountFeature(),
                new AtCountFeature(),
                new AtRelativeCountFeature(),
                new CommaCountFeature(),
                new CommaRelativeCountFeature(),
                new DotCountFeature(),
                new DotRelativeCountFeature(),
                new WordWidthMeanFeature()
                ));

        // 2. import and generate training set based on sequences and vector of features
        InputStream is = HMMZoneGeneralClassificationDemo.class.getResourceAsStream(hmmTestFile);
        InputStreamReader isr = new InputStreamReader(is);
        
        TrueVizToBxDocumentReader reader = new TrueVizToBxDocumentReader();
        BxDocument document = new BxDocument().setPages(reader.read(isr));
        List<BxDocument> documents = new ArrayList<BxDocument>(1);

        BxDocsToFVHMMTrainingElementsConverterNode node = new BxDocsToFVHMMTrainingElementsConverterNode();
        node.setFeatureVectorBuilder(vectorBuilder);
        List<HMMTrainingElement<BxZoneLabel>> unconvertedTrainingElements = node.process(documents, null);
        List<HMMTrainingElement<BxZoneGeneralLabel>> trainingElements = new ArrayList<HMMTrainingElement<BxZoneGeneralLabel>>();
       
        BxZoneLabelDetailedToGeneralMapper mapper = new BxZoneLabelDetailedToGeneralMapper();
        
        SimpleHMMTrainingElement<BxZoneGeneralLabel> prev = null;
        for(HMMTrainingElement<BxZoneLabel> elem: unconvertedTrainingElements) {
        	SimpleHMMTrainingElement<BxZoneGeneralLabel> convertedElem = 
        			new SimpleHMMTrainingElement<BxZoneGeneralLabel>(elem.getObservation(), mapper.map(elem.getLabel()),  elem.isFirst());
        	if(prev != null)
        		prev.setNextLabel(convertedElem.getLabel());
        	trainingElements.add(convertedElem);
        	prev = convertedElem;
        }
        for(Integer idx = 0; idx < trainingElements.size(); ++idx) {
        	FeatureVector fv = trainingElements.get(idx).getObservation();
        	BxZoneGeneralLabel lab = trainingElements.get(idx).getLabel();
        	System.out.println("--------------");
        	System.out.println(lab);
        	System.out.println(document.asZones().get(idx).toText());
        	System.out.println(fv.dump());
        }
        
      
        // 3. HMM training. The resulting probabilities object should be serialized for further usage
        HMMProbabilityInfo<BxZoneGeneralLabel> hmmProbabilities
                = HMMProbabilityInfoFactory.getFVHMMProbability(trainingElements, vectorBuilder);


        // 4. zone classifier instance
        HMMZoneGeneralClassifier zoneClassifier = new HMMZoneGeneralClassifier(new HMMServiceImpl(), hmmProbabilities, vectorBuilder);

        // 5. find the most probable labels for HMM objects
        List<BxZoneGeneralLabel> labels = zoneClassifier.classifyZones(document);
        
        Integer idx = 0;
        for (BxPage page : document.getPages()) {
            for (BxZone zone : page.getZones()) {
                System.out.println(labels.get(idx++));
            }
        }
    }
}
