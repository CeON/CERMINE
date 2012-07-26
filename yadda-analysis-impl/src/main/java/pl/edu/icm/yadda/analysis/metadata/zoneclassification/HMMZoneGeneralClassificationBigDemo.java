package pl.edu.icm.yadda.analysis.metadata.zoneclassification;

import com.thoughtworks.xstream.XStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
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
import pl.edu.icm.yadda.analysis.classification.hmm.training.HMMTrainingElement;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.*;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.nodes.BxDocsToFVHMMTrainingElementsConverterNode;
import pl.edu.icm.yadda.analysis.textr.HMMZoneClassifier;
import pl.edu.icm.yadda.analysis.textr.HierarchicalReadingOrderResolver;
import pl.edu.icm.yadda.analysis.textr.ReadingOrderResolver;
import pl.edu.icm.yadda.analysis.textr.model.*;
import pl.edu.icm.yadda.analysis.textr.tools.DocumentFlattener;
import pl.edu.icm.yadda.analysis.textr.tools.InitiallyClassifiedZonesFlattener;
import pl.edu.icm.yadda.analysis.textr.transformers.TrueVizToBxDocumentReader;
import pl.edu.icm.yadda.metadata.transformers.TransformationException;

/**
 *
 * @author Pawel Szostek (p.szostek@icm.edu.pl)
 */

public class HMMZoneGeneralClassificationBigDemo {

	protected static final String hmmTrainingFile = "/pl/edu/icm/yadda/analysis/metadata/zoneclassification/xmls.zip";
	private static final String hmmTestFile = "/pl/edu/icm/yadda/analysis//metadata/zoneclassification/09629351.xml";
    
	public static BxDocument getTestFile() throws TransformationException, AnalysisException {
        InputStream is = HMMZoneClassificationDemo.class.getResourceAsStream(hmmTestFile);
        InputStreamReader isr = new InputStreamReader(is);
        
        TrueVizToBxDocumentReader reader = new TrueVizToBxDocumentReader();
        List<BxPage> pages = reader.read(isr);
        BxDocument testDocument = new BxDocument().setPages(pages);
        return testDocument;
	}

	 public static List<BxDocument> getDocumentsFromZip(String file)
			throws ZipException, IOException, URISyntaxException, ParserConfigurationException, SAXException, AnalysisException, TransformationException {
		List<BxDocument> documents = new ArrayList<BxDocument>();

        ZipFile zipFile = new ZipFile(new File(file.getClass().getResource(file).toURI()));
		TrueVizToBxDocumentReader tvReader = new TrueVizToBxDocumentReader();
		Enumeration<? extends ZipEntry> entries = zipFile.entries();
        int i = 0;
		while (entries.hasMoreElements()) {
			ZipEntry zipEntry = (ZipEntry) entries.nextElement();
            if (i == 82) {
                i++;
                break;    
            }
			if (zipEntry.getName().endsWith("xml")) {
                i++;
				List<BxPage> pages = tvReader.read(new InputStreamReader(zipFile.getInputStream(zipEntry)));
				BxDocument newDoc = new BxDocument();
				newDoc.setPages(pages);
				documents.add(newDoc);
			}
		}
		return documents;
	}

  
    public static void main(String[] args) throws TransformationException, Exception {
        
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
        		new CuePhrasesCountFeature(),
        		new CuePhrasesRelativeCountFeature(),
        		new DatesFeature(),
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
                new PageNumberFeature(),
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
        		new WhitespaceRelativeCountFeature(),
                new WidthRelativeFeature(),
                new XPositionFeature(),
                new XPositionRelativeFeature(),
                new YPositionFeature(),
                new YPositionRelativeFeature(),
                new YearFeature()
                ));

        // 1.2 reading order resolver and labels flattener (changes specific labels to general ones)
        ReadingOrderResolver ror = new HierarchicalReadingOrderResolver();
        DocumentFlattener flattener = new InitiallyClassifiedZonesFlattener();
        
        // 1.3 open test file
        BxDocument testDocument = getTestFile();
        List<BxDocument> testList = new ArrayList<BxDocument>();
        testList.add(ror.resolve(testDocument));
                
        // 2.1 import training documents
        List<BxDocument> documents = getDocumentsFromZip(hmmTrainingFile);
        List<BxDocument> trainingList = new ArrayList<BxDocument>();
        for (BxDocument doc : documents) {
            flattener.flatten(doc);
            trainingList.add(ror.resolve(doc));
        }
        
        // 2.2 generate training set based on sequences and vector of features
        BxDocsToFVHMMTrainingElementsConverterNode node = new BxDocsToFVHMMTrainingElementsConverterNode();
        node.setFeatureVectorBuilder(vectorBuilder);
        List<HMMTrainingElement<BxZoneLabel>> trainingElements = node.process(documents, null);

        // 3. HMM training. The resulting probabilities object should be serialized for further usage
        HMMProbabilityInfo<BxZoneLabel> hmmProbabilities
                = HMMProbabilityInfoFactory.getFVHMMProbability(trainingElements, vectorBuilder);

        XStream xs = new XStream();
        System.out.println(xs.toXML(hmmProbabilities));
        
        // 4. zone classifier instance
        HMMZoneClassifier zoneClassifier = new HMMZoneClassifier(new HMMServiceImpl(), hmmProbabilities, 
                BxZoneLabel.valuesOfCategory(BxZoneLabelCategory.CAT_GENERAL), vectorBuilder);

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
