package pl.edu.icm.yadda.analysis.metadata.zoneclassification;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
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
import pl.edu.icm.yadda.analysis.textr.model.BxDocument;
import pl.edu.icm.yadda.analysis.textr.model.BxPage;
import pl.edu.icm.yadda.analysis.textr.model.BxZone;
import pl.edu.icm.yadda.analysis.textr.model.BxZoneGeneralLabel;
import pl.edu.icm.yadda.analysis.textr.model.BxZoneLabel;
import pl.edu.icm.yadda.analysis.textr.transformers.TrueVizToBxDocumentReader;
import pl.edu.icm.yadda.metadata.transformers.TransformationException;

/**
 *
 * @author Pawel Szostek (p.szostek@icm.edu.pl)
 */

public class HMMZoneGeneralClassificationBigDemo {

	protected static final String hmmTrainingFile = "/pl/edu/icm/yadda/analysis/metadata/zoneclassification/xmls.zip";
//	private static final String hmmTestFile = "/pl/edu/icm/yadda/analysis/logicstr/train/01.xml";
	private static final String hmmTestFile = "/pl/edu/icm/yadda/analysis/logicstr/train/02.xml";

	private static BxDocument getTestFile() throws TransformationException {
        InputStream is = HMMZoneClassificationDemo.class.getResourceAsStream(hmmTestFile);
        InputStreamReader isr = new InputStreamReader(is);
        
        TrueVizToBxDocumentReader reader = new TrueVizToBxDocumentReader();
        List<BxPage> pages = reader.read(isr);
        BxDocument testDocument = new BxDocument().setPages(pages);
        return testDocument;
	}

	private static List<BxDocument> getDocumentsFromZip(String file)
			throws ZipException, IOException, URISyntaxException, ParserConfigurationException, SAXException, AnalysisException, TransformationException {
		List<BxDocument> documents = new ArrayList<BxDocument>();

		ZipFile zipFile = new ZipFile(new File(file.getClass().getResource(file).toURI()));
		TrueVizToBxDocumentReader tvReader = new TrueVizToBxDocumentReader();
		Enumeration<? extends ZipEntry> entries = zipFile.entries();
		while (entries.hasMoreElements()) {
			ZipEntry zipEntry = (ZipEntry) entries.nextElement();
			if (zipEntry.getName().endsWith("xml")) {
				List<BxPage> pages = tvReader.read(new InputStreamReader(zipFile.getInputStream(zipEntry)));
				BxDocument newDoc = new BxDocument();
				newDoc.setPages(pages);
				documents.add(newDoc);
			}
		}
		return documents;
	}

    public static void main(String[] args) throws TransformationException, Exception {
        
        // 1. construct vector of features builder
        FeatureVectorBuilder<BxZone, BxPage> vectorBuilder =
                new SimpleFeatureVectorBuilder<BxZone, BxPage>();
        vectorBuilder.setFeatureCalculators(Arrays.<FeatureCalculator<BxZone, BxPage>>asList(
        		new AbstractFeature(),
        		new FigureFeature(),
        		new IsFirstPageFeature(),
        		new IsLastPageFeature(),
        		new BracketRelativeCount(),
        		new BracketedLineRelativeCount(),
        		new EmptySpaceRelativeFeature(),
        		new IsHighestOnThePageFeature(),
        		new IsLowestOnThePageFeature(),
        		new CuePhrasesCountFeature(),
        		new CuePhrasesRelativeCountFeature(),
        	//	new WordLengthMeanFeature(),
        		new WordLengthMedianFeature(),
        	//	new WhitespaceCountFeature(),
        	//	new WhitespaceRelativeCountFeature(),
        	//	new VerticalProminenceFeature(),
        	//	new HorizontalRelativeProminenceFeature(),
        	//	new IsFontBiggerThanNeighboursFeature(),
        	//	new DistanceFromNearestNeighbourFeature(),
        		new AcknowledgementFeature(),
        	//	new FontHeightMeanFeature(),
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
                new PageNumberFeature(),
                new YearFeature(),
                new PunctuationRelativeCountFeature()
        		,              new KeywordsFeature()
        		, new WordWidthMeanFeature()
                ));

        // 1.5. open test file
        BxDocument testDocument = getTestFile();
        
        System.out.println("zz");
        // 2. import and generate training set based on sequences and vector of features
        List<BxDocument> documents = getDocumentsFromZip(hmmTrainingFile);
        List<BxZone> allZones = new ArrayList<BxZone>();
        for(BxDocument doc: documents)
        	allZones.addAll(doc.asZones());

        System.out.println("yy");
        BxDocsToFVHMMTrainingElementsConverterNode node = new BxDocsToFVHMMTrainingElementsConverterNode();
        node.setFeatureVectorBuilder(vectorBuilder);
        List<HMMTrainingElement<BxZoneLabel>> unconvertedTrainingElements = node.process(documents, null);
        List<HMMTrainingElement<BxZoneGeneralLabel>> trainingElements = new ArrayList<HMMTrainingElement<BxZoneGeneralLabel>>(unconvertedTrainingElements.size());
       
        System.out.println("xxx");
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
    /*    for(int i=0; i<trainingElements.size(); ++i) {
        	System.out.println(allZones.get(i).toText());
        	System.out.println(trainingElements.get(i).getObservation().dump());
        	System.out.println();
        }*/
        // 3. HMM training. The resulting probabilities object should be serialized for further usage
        HMMProbabilityInfo<BxZoneGeneralLabel> hmmProbabilities
                = HMMProbabilityInfoFactory.getFVHMMProbability(trainingElements, vectorBuilder);

        // 4. zone classifier instance
        HMMZoneGeneralClassifier zoneClassifier = new HMMZoneGeneralClassifier(new HMMServiceImpl(), hmmProbabilities, vectorBuilder);

        // 5. find the most probable labels for HMM objects
        List<BxZoneGeneralLabel> labels = zoneClassifier.classifyZones(testDocument);
        
        Integer idx = 0;
        for (BxPage page : testDocument.getPages()) {
            for (BxZone zone : page.getZones()) {
                System.out.println("--------");
                System.out.println(zone.toText());
                System.out.println("[" + labels.get(idx++) + "]");
            }
        }
    }
}
