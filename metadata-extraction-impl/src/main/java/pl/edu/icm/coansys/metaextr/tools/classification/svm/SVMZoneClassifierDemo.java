package pl.edu.icm.coansys.metaextr.tools.classification.svm;

import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.IsHighestOnThePageFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.LineWidthMeanFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.WhitespaceCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.LineHeightMeanFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.WordWidthMeanFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.CommaRelativeCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.ProportionsFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.LetterRelativeCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.LineXPositionMeanFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.HeightFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.ReferencesFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.StartsWithDigitFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.WordCountRelativeFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.VerticalProminenceFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.XPositionFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.LineXPositionDiffFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.DigitRelativeCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.DotCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.AcknowledgementFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.DateFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.UppercaseRelativeCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.FigureFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.YearFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.ReferencesTitleFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.AtCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.IsFontBiggerThanNeighboursFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.AbstractFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.HeightRelativeFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.AffiliationFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.ContainsPageNumberFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.CuePhrasesRelativeCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.IsLastPageFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.LetterCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.CommaCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.CharCountRelativeFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.KeywordsFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.UppercaseWordRelativeCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.LowercaseCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.IsItemizeFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.XPositionRelativeFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.BibinfoFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.EmptySpaceRelativeFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.CharCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.YPositionFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.AuthorFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.AtRelativeCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.DigitCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.ContainsCuePhrasesFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.UppercaseWordCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.WidthRelativeFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.WidthFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.DistanceFromNearestNeighbourFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.BracketedLineRelativeCount;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.BracketRelativeCount;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.WhitespaceRelativeCountLogFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.WordCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.LineRelativeCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.YPositionRelativeFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.DotRelativeCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.WordLengthMedianFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.WordLengthMeanFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.IsLowestOnThePageFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.UppercaseCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.HorizontalRelativeProminenceFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.IsFirstPageFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.LineCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.FontHeightMeanFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.FreeSpaceWithinZoneFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.PunctuationRelativeCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.LowercaseRelativeCountFeature;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.features.LineXWidthPositionDiffFeature;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import pl.edu.icm.coansys.metaextr.AnalysisException;
import pl.edu.icm.coansys.metaextr.TransformationException;
import pl.edu.icm.coansys.metaextr.tools.classification.features.FeatureCalculator;
import pl.edu.icm.coansys.metaextr.tools.classification.features.FeatureVectorBuilder;
import pl.edu.icm.coansys.metaextr.tools.classification.features.SimpleFeatureVectorBuilder;
import pl.edu.icm.coansys.metaextr.tools.classification.hmm.training.TrainingElement;
import pl.edu.icm.coansys.metaextr.tools.classification.tools.DocumentsExtractor;
import pl.edu.icm.coansys.metaextr.tools.classification.tools.ZipExtractor;
import pl.edu.icm.coansys.metaextr.metadata.zoneclassification.tools.BxDocsToHMMConverter;
import pl.edu.icm.coansys.metaextr.structure.model.BxDocument;
import pl.edu.icm.coansys.metaextr.structure.model.BxPage;
import pl.edu.icm.coansys.metaextr.structure.model.BxZone;
import pl.edu.icm.coansys.metaextr.structure.model.BxZoneLabel;
import pl.edu.icm.coansys.metaextr.structure.transformers.TrueVizToBxDocumentReader;

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
        BxDocsToHMMConverter node = new BxDocsToHMMConverter();
        node.setFeatureVectorBuilder(vectorBuilder);
        node.setLabelMap(BxZoneLabel.getLabelToGeneralMap());
        List<TrainingElement<BxZoneLabel>> trainingElementsUnrevised = node.process(trainingList);
        
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
        List<TrainingElement<BxZoneLabel>> testElement = node.process(testList);

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
