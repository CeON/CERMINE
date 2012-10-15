package pl.edu.icm.cermine.metadata.zoneclassification;

import com.thoughtworks.xstream.XStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipException;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.metadata.zoneclassification.features.*;
import pl.edu.icm.cermine.metadata.zoneclassification.tools.BxDocsToHMMConverter;
import pl.edu.icm.cermine.structure.HMMInitialZoneClassifier;
import pl.edu.icm.cermine.structure.ZoneClassifier;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;
import pl.edu.icm.cermine.structure.tools.DocumentProcessor;
import pl.edu.icm.cermine.structure.tools.InitiallyClassifiedZonesPreprocessor;
import pl.edu.icm.cermine.structure.transformers.TrueVizToBxDocumentReader;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;
import pl.edu.icm.cermine.tools.classification.features.FeatureVectorBuilder;
import pl.edu.icm.cermine.tools.classification.general.DocumentsExtractor;
import pl.edu.icm.cermine.tools.classification.general.SimpleFeatureVectorBuilder;
import pl.edu.icm.cermine.tools.classification.general.ZipExtractor;
import pl.edu.icm.cermine.tools.classification.hmm.HMMServiceImpl;
import pl.edu.icm.cermine.tools.classification.hmm.model.HMMProbabilityInfo;
import pl.edu.icm.cermine.tools.classification.hmm.model.HMMProbabilityInfoFactory;
import pl.edu.icm.cermine.tools.classification.hmm.training.TrainingElement;

/**
 *
 * @author Pawel Szostek (p.szostek@icm.edu.pl)
 */

public class HMMZoneGeneralClassificationBigDemo {

	protected static final String HMM_TRAIN_FILE = "xmls.zip";
	private static final String HMM_TEST_FILE = "/09629351.xml";

    public HMMZoneGeneralClassificationBigDemo() {}
    
	public static BxDocument getTestFile() throws TransformationException, AnalysisException {
        InputStream is = HMMZoneClassificationDemo.class.getResourceAsStream(HMM_TEST_FILE);
        InputStreamReader isr = new InputStreamReader(is);
        
        TrueVizToBxDocumentReader reader = new TrueVizToBxDocumentReader();
        List<BxPage> pages = reader.read(isr);
        return new BxDocument().setPages(pages);
	}

    public static void main(String[] args) throws TransformationException, AnalysisException, ZipException, IOException, URISyntaxException {
        
        // 1.1 construct vector of features builder
        FeatureVectorBuilder<BxZone, BxPage> vectorBuilder =
                new SimpleFeatureVectorBuilder<BxZone, BxPage>();
        vectorBuilder.setFeatureCalculators(Arrays.<FeatureCalculator<BxZone, BxPage>>asList(
        		new AbstractFeature(),
        		new AcknowledgementFeature(),
        		new AffiliationFeature(),
                new AtCountFeature(),
                new AtRelativeCountFeature(),
                new AuthorFeature(),
                new BibinfoFeature(),
        		new BracketRelativeCount(),
        		new BracketedLineRelativeCount(),
                new CharCountFeature(),
                new CharCountRelativeFeature(),
                new CommaCountFeature(),
                new CommaRelativeCountFeature(),
        		new ContainsCuePhrasesFeature(),
        		new CuePhrasesRelativeCountFeature(),
        		new DateFeature(),
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
                new ContainsPageNumberFeature(),
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

        // 1.2 labels flattener (changes specific labels to general ones)
        DocumentProcessor flattener = new InitiallyClassifiedZonesPreprocessor();
        
        // 1.3 open test file
        BxDocument testDocument = getTestFile();
        List<BxDocument> testList = new ArrayList<BxDocument>();
        testList.add(testDocument);
                
        // 2.1 import training documents
        DocumentsExtractor extractor = new ZipExtractor(HMM_TRAIN_FILE);
        List<BxDocument> documents = extractor.getDocuments();
        List<BxDocument> trainingList = new ArrayList<BxDocument>();
        for (BxDocument doc : documents) {
            flattener.process(doc);
            trainingList.add(doc);
        }
        
        // 2.2 generate training set based on sequences and vector of features
        BxDocsToHMMConverter node = new BxDocsToHMMConverter();
        node.setFeatureVectorBuilder(vectorBuilder);
        List<TrainingElement<BxZoneLabel>> trainingElements = node.process(documents);

        // 3. HMM training. The resulting probabilities object should be serialized for further usage
        HMMProbabilityInfo<BxZoneLabel> hmmProbabilities
                = HMMProbabilityInfoFactory.getFVHMMProbability(trainingElements, vectorBuilder);

        XStream xs = new XStream();
        System.out.println(xs.toXML(hmmProbabilities));
        
        // 4. zone classifier instance
        ZoneClassifier zoneClassifier = new HMMInitialZoneClassifier(new HMMServiceImpl(), hmmProbabilities, vectorBuilder);

        // 5. find the most probable labels for HMM objects
        zoneClassifier.classifyZones(testDocument);
        
        for (BxPage page : testDocument.getPages()) {
            for (BxZone zone : page.getZones()) {
                System.out.println("--------");
                System.out.println(zone.toText());
                System.out.println("[" + zone.getLabel() + "]");
            }
        }
        
    }
}
