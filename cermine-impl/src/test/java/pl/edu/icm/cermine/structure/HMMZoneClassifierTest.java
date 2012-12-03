package pl.edu.icm.cermine.structure;

import com.thoughtworks.xstream.XStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.ZipException;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.metadata.zoneclassification.features.*;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;
import pl.edu.icm.cermine.structure.tools.DocumentProcessor;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;
import pl.edu.icm.cermine.tools.classification.features.FeatureVectorBuilder;
import pl.edu.icm.cermine.tools.classification.hmm.HMMService;
import pl.edu.icm.cermine.tools.classification.hmm.HMMServiceImpl;
import pl.edu.icm.cermine.tools.classification.hmm.model.HMMProbabilityInfo;
import pl.edu.icm.cermine.tools.classification.hmm.model.SimpleHMMProbabilityInfo;

/**
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class HMMZoneClassifierTest extends AbstractDocumentProcessorTest {

    protected static final double testSuccessPercentage = 85;

    protected static final String hmmProbabilitiesFile = "/pl/edu/icm/cermine/structure/hmmZoneProbabilities.xml";
    static final String[] zipResources = {"/pl/edu/icm/cermine/structure/margSmallSample.zip"};

    private HMMService hmmService = new HMMServiceImpl();
    private ZoneClassifier zoneClassifier;
    protected ReadingOrderResolver ror;

    int allZones = 0;
    int badZones = 0;

    @Before
    public void setUp() throws IOException {
        ror = new HierarchicalReadingOrderResolver();
    	startProcessFlattener = new DocumentProcessor() {
			@Override
			public void process(BxDocument document) throws AnalysisException {
				ror.resolve(document);
			}
		};
        endProcessFlattener = new DocumentProcessor() {
			@Override
			public void process(BxDocument document) throws AnalysisException {
				ror.resolve(document);
			}
		};
        InputStream is = this.getClass().getResourceAsStream(hmmProbabilitiesFile);
        XStream xstream = new XStream();
        HMMProbabilityInfo<BxZoneLabel> hmmProbabilities;
        try {
            hmmProbabilities = (SimpleHMMProbabilityInfo<BxZoneLabel>) xstream.fromXML(is);
        } finally {
            is.close();
        }

        FeatureVectorBuilder<BxZone, BxPage> vBuilder = new FeatureVectorBuilder<BxZone, BxPage>();
        vBuilder.setFeatureCalculators(Arrays.<FeatureCalculator<BxZone, BxPage>>asList(
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
        zoneClassifier = new HMMZoneClassifier(hmmService, 
        		hmmProbabilities, 
        		new ArrayList<BxZoneLabel>(){{
        			add(BxZoneLabel.MET_AUTHOR);
        			add(BxZoneLabel.MET_TITLE);
        			add(BxZoneLabel.MET_AFFILIATION); 
        			add(BxZoneLabel.MET_ABSTRACT);
        			}},
        		vBuilder);
    }
    @Test
    public void hmmZonesClassifierTest() throws URISyntaxException, ZipException, IOException, 
            ParserConfigurationException, SAXException, AnalysisException, TransformationException {
        testAllFilesFromZip(Arrays.asList(zipResources), testSuccessPercentage);

        System.out.println("all zones: "+this.allZones);
        System.out.println("bad zones: "+this.badZones);
    }

    @Override
    protected boolean compareDocuments(BxDocument testDoc, BxDocument expectedDoc) {
        boolean ret = true;
        for (int i = 0; i < testDoc.getPages().size(); i++) {
            BxPage testPage = testDoc.getPages().get(i);
            BxPage expectedPage = expectedDoc.getPages().get(i);

            for (int j = 0; j < testPage.getZones().size(); j++) {
                BxZone testZone = testPage.getZones().get(j);
                BxZone expectedZone = expectedPage.getZones().get(j);
                System.out.println(testZone.getLabel() + " " + expectedZone.getLabel());

                this.allZones++;
                if (!testZone.getLabel().equals(expectedZone.getLabel())) {
                    this.badZones++;
                    ret = false;
                }
            }
        }

        return ret;
    }

    @Override
    protected BxDocument process(BxDocument doc) throws AnalysisException {
        zoneClassifier.classifyZones(doc);
        return doc;
    }
}
