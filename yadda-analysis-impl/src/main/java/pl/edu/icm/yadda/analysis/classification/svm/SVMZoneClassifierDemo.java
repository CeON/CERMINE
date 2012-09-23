package pl.edu.icm.yadda.analysis.classification.svm;

import com.thoughtworks.xstream.XStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import pl.edu.icm.yadda.analysis.AnalysisException;
import pl.edu.icm.yadda.analysis.classification.features.FeatureCalculator;
import pl.edu.icm.yadda.analysis.classification.features.FeatureVectorBuilder;
import pl.edu.icm.yadda.analysis.classification.features.SimpleFeatureVectorBuilder;
import pl.edu.icm.yadda.analysis.classification.hmm.HMMServiceImpl;
import pl.edu.icm.yadda.analysis.classification.hmm.probability.HMMProbabilityInfo;
import pl.edu.icm.yadda.analysis.classification.hmm.probability.HMMProbabilityInfoFactory;
import pl.edu.icm.yadda.analysis.classification.hmm.training.TrainingElement;
import pl.edu.icm.yadda.analysis.classification.tools.DocumentsExtractor;
import pl.edu.icm.yadda.analysis.classification.tools.ZipExtractor;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.*;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.nodes.BxDocsToTrainingElementsConverterNode;
import pl.edu.icm.yadda.analysis.classification.hmm.HMMZoneClassifier;
import pl.edu.icm.yadda.analysis.textr.HierarchicalReadingOrderResolver;
import pl.edu.icm.yadda.analysis.textr.ReadingOrderResolver;
import pl.edu.icm.yadda.analysis.textr.ZoneClassifier;
import pl.edu.icm.yadda.analysis.textr.model.*;
import pl.edu.icm.yadda.analysis.textr.tools.DocumentPreprocessor;
import pl.edu.icm.yadda.analysis.textr.tools.InitiallyClassifiedZonesPreprocessor;
import pl.edu.icm.yadda.analysis.textr.transformers.TrueVizToBxDocumentReader;
import pl.edu.icm.yadda.metadata.transformers.TransformationException;

/**
 *
 * @author Pawel Szostek (p.szostek@icm.edu.pl)
 */

public class SVMZoneClassifierDemo {

	protected static final String hmmTrainingFile = "/pl/edu/icm/yadda/analysis/metadata/zoneclassification/xmls.zip";
	private static final String hmmTestFile = "/pl/edu/icm/yadda/analysis//metadata/zoneclassification/09629351.xml";
    
	public static BxDocument getTestFile() throws TransformationException, AnalysisException {
        InputStream is = SVMZoneClassifierDemo.class.getResourceAsStream(hmmTestFile);
        InputStreamReader isr = new InputStreamReader(is);
        
        TrueVizToBxDocumentReader reader = new TrueVizToBxDocumentReader();
        List<BxPage> pages = reader.read(isr);
        BxDocument testDocument = new BxDocument().setPages(pages);
        return testDocument;
	}
  
    public static void main(String[] args) throws TransformationException, Exception {
        
        // 1.1 construct vector of features builder
    	List<FeatureCalculator<BxZone, BxPage>> featureCalculators = Arrays.<FeatureCalculator<BxZone, BxPage>>asList(
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
                );
        FeatureVectorBuilder<BxZone, BxPage> vectorBuilder =
                new SimpleFeatureVectorBuilder<BxZone, BxPage>();
        vectorBuilder.setFeatureCalculators(featureCalculators);

        /* import training documents */
        DocumentsExtractor extractor = new ZipExtractor(hmmTrainingFile);
        List<BxDocument> trainingList = extractor.getDocuments();
        
        /* open test file */
        //BxDocument testDocument = getTestFile();
        //Random randomGenerator = new Random();
        int testDocIdx = 41;//randomGenerator.nextInt(trainingList.size());
        BxDocument testDocument = trainingList.get(testDocIdx);

        /* generate training set based on sequences and vector of features */
        BxDocsToTrainingElementsConverterNode node = new BxDocsToTrainingElementsConverterNode();
        node.setFeatureVectorBuilder(vectorBuilder);
        node.setLabelMap(BxZoneLabel.getLabelToGeneralMap());
        List<TrainingElement<BxZoneLabel>> trainingElementsUnrevised = node.process(trainingList, null);
        
        Map<BxZoneLabel, Integer> labelCount = new HashMap<BxZoneLabel, Integer>();
        labelCount.put(BxZoneLabel.GEN_BODY, 0);
        labelCount.put(BxZoneLabel.GEN_METADATA, 0);
        labelCount.put(BxZoneLabel.GEN_OTHER, 0);
        labelCount.put(BxZoneLabel.GEN_REFERENCES, 0);
        
        for(TrainingElement<BxZoneLabel> elem: trainingElementsUnrevised) {
        	labelCount.put(elem.getLabel(), labelCount.get(elem.getLabel())+1);
        }
        
        Integer max = Integer.MAX_VALUE;
        for(BxZoneLabel lab: labelCount.keySet()) {
        	if(labelCount.get(lab) < max)
        		max = labelCount.get(lab);
        	System.out.println(lab + " " + labelCount.get(lab));
        }
        
        labelCount.put(BxZoneLabel.GEN_BODY, 0);
        labelCount.put(BxZoneLabel.GEN_METADATA, 0);
        labelCount.put(BxZoneLabel.GEN_OTHER, 0);
        labelCount.put(BxZoneLabel.GEN_REFERENCES, 0);
        List<TrainingElement<BxZoneLabel>> trainingElements = new ArrayList<TrainingElement<BxZoneLabel>>();
        
        for(TrainingElement<BxZoneLabel> elem: trainingElementsUnrevised) {
        	if(labelCount.get(elem.getLabel()) < max*1.3) {
        		trainingElements.add(elem);
        		labelCount.put(elem.getLabel(), labelCount.get(elem.getLabel())+1);
        	}
        }
        System.out.println(max);
        System.out.println(trainingElements.size());

        List<BxDocument> testList = new ArrayList<BxDocument>(1);
        testList.add(testDocument);
        List<TrainingElement<BxZoneLabel>> testElement = node.process(testList, null);

        /* build a classifier */
        SVMZoneClassifier zoneClassifier = new SVMZoneClassifier(vectorBuilder);
        zoneClassifier.buildClassifier(trainingElements);
        
        /* classify zones from the test file */
        zoneClassifier.classifyZones(testDocument);
        assert testDocument.asZones().size() == testList.size();
        for(int zoneIdx=0; zoneIdx < testDocument.asZones().size(); ++zoneIdx) {
        	System.out.println("Recognized label: " + testDocument.asZones().get(zoneIdx).getLabel() + "[" + testElement.get(zoneIdx).getLabel() + "]");
        	System.out.println(testDocument.asZones().get(zoneIdx).toText());
        }
    }
}
