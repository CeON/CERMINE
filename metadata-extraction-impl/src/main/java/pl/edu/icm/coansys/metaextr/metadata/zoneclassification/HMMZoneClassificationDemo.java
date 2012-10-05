package pl.edu.icm.coansys.metaextr.metadata.zoneclassification;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import pl.edu.icm.coansys.metaextr.exception.TransformationException;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.*;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.tools.BxDocsToHMMConverter;
import pl.edu.icm.coansys.metaextr.structure.HMMZoneClassifier;
import pl.edu.icm.coansys.metaextr.structure.model.BxDocument;
import pl.edu.icm.coansys.metaextr.structure.model.BxPage;
import pl.edu.icm.coansys.metaextr.structure.model.BxZone;
import pl.edu.icm.coansys.metaextr.structure.model.BxZoneLabel;
import pl.edu.icm.coansys.metaextr.tools.classification.features.FeatureCalculator;
import pl.edu.icm.coansys.metaextr.tools.classification.features.FeatureVectorBuilder;
import pl.edu.icm.coansys.metaextr.tools.classification.general.FileExtractor;
import pl.edu.icm.coansys.metaextr.tools.classification.general.SimpleFeatureVectorBuilder;
import pl.edu.icm.coansys.metaextr.tools.classification.hmm.HMMServiceImpl;
import pl.edu.icm.coansys.metaextr.tools.classification.hmm.model.HMMProbabilityInfo;
import pl.edu.icm.coansys.metaextr.tools.classification.hmm.model.HMMProbabilityInfoFactory;
import pl.edu.icm.coansys.metaextr.tools.classification.hmm.training.TrainingElement;

/**
 *
 * @author Dominika Tkaczyk
 */
public class HMMZoneClassificationDemo {

    protected static final String hmmTestFile = "plik.xml";

    public static void main(String[] args) throws TransformationException, Exception {
        
        // 1. construct vector of features builder
        FeatureVectorBuilder<BxZone, BxPage> vectorBuilder =
                new SimpleFeatureVectorBuilder<BxZone, BxPage>();
        vectorBuilder.setFeatureCalculators(Arrays.<FeatureCalculator<BxZone, BxPage>>asList(
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
		InputStream is = HMMZoneClassificationDemo.class.getResourceAsStream(hmmTestFile);
		FileExtractor fe = new FileExtractor(is);
		BxDocument document = fe.getDocument();
        List<BxDocument> documents = new ArrayList<BxDocument>(1);
        documents.add(document);
        
        BxDocsToHMMConverter node = new BxDocsToHMMConverter();
        node.setFeatureVectorBuilder(vectorBuilder);
        List<TrainingElement<BxZoneLabel>> trainingElements = node.process(documents);
        // 3. HMM training. The resulting probabilities object should be serialized for further usage
        HMMProbabilityInfo<BxZoneLabel> hmmProbabilities
                = HMMProbabilityInfoFactory.getFVHMMProbability(trainingElements, vectorBuilder);


        // 4. zone classifier instance
        HMMZoneClassifier zoneClassifier = new HMMZoneClassifier(new HMMServiceImpl(), hmmProbabilities, vectorBuilder);

        // 5. find the most probable labels for HMM objects
        zoneClassifier.classifyZones(document);
        
        for (BxPage page : document.getPages()) {
            for (BxZone zone : page.getZones()) {
                System.out.println();
                System.out.println(zone.toText());
                System.out.println("["+zone.getLabel()+"]");
            }
        }
    }
}
