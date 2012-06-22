package pl.edu.icm.yadda.analysis.textr;

import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.LineXPositionDiffFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.LetterCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.WordCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.HeightFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.UppercaseWordCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.LineRelativeCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.CommaCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.AtCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.WidthFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.ProportionsFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.XPositionRelativeFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.WidthRelativeFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.UppercaseRelativeCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.UppercaseWordRelativeCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.WordWidthMeanFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.WordCountRelativeFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.DigitRelativeCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.CharCountRelativeFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.DigitCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.LetterRelativeCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.CharCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.LineCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.YPositionRelativeFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.LowercaseCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.LowercaseRelativeCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.LineXPositionMeanFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.LineWidthMeanFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.UppercaseCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.YPositionFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.XPositionFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.LineHeightMeanFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.DotRelativeCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.DotCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.LineXWidthPositionDiffFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.HeightRelativeFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.CommaRelativeCountFeature;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.features.AtRelativeCountFeature;
import com.thoughtworks.xstream.XStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.zip.ZipException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import pl.edu.icm.yadda.analysis.AnalysisException;
import pl.edu.icm.yadda.analysis.textr.model.BxDocument;
import org.junit.Before;
import org.junit.Test;
import pl.edu.icm.yadda.analysis.classification.hmm.HMMService;
import pl.edu.icm.yadda.analysis.classification.hmm.HMMServiceImpl;
import pl.edu.icm.yadda.analysis.classification.features.FeatureCalculator;
import pl.edu.icm.yadda.analysis.classification.features.FeatureVector;
import pl.edu.icm.yadda.analysis.classification.features.FeatureVectorBuilder;
import pl.edu.icm.yadda.analysis.classification.features.SimpleFeatureVectorBuilder;
import pl.edu.icm.yadda.analysis.classification.hmm.probability.HMMProbabilityInfo;
import pl.edu.icm.yadda.analysis.textr.model.BxPage;
import pl.edu.icm.yadda.analysis.textr.model.BxZone;
import pl.edu.icm.yadda.analysis.textr.model.BxZoneLabel;
import pl.edu.icm.yadda.analysis.textr.tools.UnclassifiedZonesFlattener;
import pl.edu.icm.yadda.metadata.transformers.TransformationException;

/**
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class HMMZoneClassifierTest extends AbstractDocumentProcessorTest {

    protected static final double testSuccessPercentage = 90;

    protected static final String hmmProbabilitiesFile = "/pl/edu/icm/yadda/analysis/textr/hmmZoneProbabilities.xml";
    protected static final String[] zipResources = {"/pl/edu/icm/yadda/analysis/textr/margSmallSample.zip"};

    private HMMService hmmService = new HMMServiceImpl();
    private ZoneClassifier zoneClassifier;

    int allZones = 0;
    int badZones = 0;

    @Before
    public void setUp() {
        this.startProcessFlattener = new UnclassifiedZonesFlattener();

        InputStream is = this.getClass().getResourceAsStream(hmmProbabilitiesFile);
        XStream xstream = new XStream();
        HMMProbabilityInfo<BxZoneLabel, FeatureVector> hmmProbabilities
                = (HMMProbabilityInfo<BxZoneLabel, FeatureVector>) xstream.fromXML(is);

        FeatureVectorBuilder<BxZone, BxPage> vBuilder = new SimpleFeatureVectorBuilder<BxZone, BxPage>();
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

        zoneClassifier = new HMMZoneClassifier(hmmService, hmmProbabilities, vBuilder);
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
