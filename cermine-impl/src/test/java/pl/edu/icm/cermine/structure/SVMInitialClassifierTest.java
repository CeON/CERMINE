package pl.edu.icm.cermine.structure;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.zip.ZipException;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.structure.tools.DocumentProcessor;
import pl.edu.icm.cermine.tools.classification.svm.SVMZoneClassifier;

public class SVMInitialClassifierTest extends AbstractDocumentProcessorTest {
	protected static final String zipResources = "/pl/edu/icm/cermine/structure/roa_test_small.zip";
	protected static final String modelPath = "/pl/edu/icm/cermine/structure/svm_initial_classifier";
	protected static final String rangeFilePath = "/pl/edu/icm/cermine/structure/svm_initial_classifier.range";
	
	protected static final double testSuccessPercentage = 80;
	
	protected SVMZoneClassifier classifier;
    
    protected ReadingOrderResolver ror;
	
    int allZones = 0;
    int badZones = 0;
    
    @Before
    public void setUp() throws IOException, AnalysisException {
    	classifier = new SVMInitialZoneClassifier(getModel(), getRange());
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
    }
    
	public BufferedReader getModel() {
		InputStream stream = this.getClass()
			    .getResourceAsStream(modelPath);
		BufferedReader modelFileReader = new BufferedReader(new InputStreamReader(stream));
		return modelFileReader;
	}
	
	public BufferedReader getRange() {
		InputStream stream = this.getClass()
			    .getResourceAsStream(rangeFilePath);
		BufferedReader rangeFileReader = new BufferedReader(new InputStreamReader(stream));
		return rangeFileReader;
	}
	
    @Test
    public void SVMInitialZoneClassifierTest() throws URISyntaxException, ZipException, IOException, 
            ParserConfigurationException, SAXException, AnalysisException, TransformationException {
        testAllFilesFromZip(Arrays.asList(zipResources), testSuccessPercentage);

        System.out.println("all zones: "+this.allZones);
        System.out.println("bad zones: "+this.badZones);
    }

	@Override
	protected boolean compareDocuments(BxDocument testDoc,	BxDocument expectedDoc) {
		if(testDoc.asPages().size() != expectedDoc.asPages().size())
			return false;
		
		Integer correctZones = 0;
		Integer zones = testDoc.asZones().size();
		for(Integer zoneIdx=0; zoneIdx < testDoc.asZones().size(); ++zoneIdx) {
			BxZone testZone = testDoc.asZones().get(zoneIdx);
			BxZone expectedZone = expectedDoc.asZones().get(zoneIdx);
			System.out.println(expectedZone.getLabel() + " " + testZone.getLabel());
			++allZones;
			if(testZone.getLabel() == expectedZone.getLabel().getGeneralLabel())
				++correctZones;
			else
				++badZones;
		}
		System.out.println(((double)correctZones/(double)zones));
		if(((double)correctZones/(double)zones) >= testSuccessPercentage/100.0)
			return true;
		else
			return false;
	}

	@Override
	protected BxDocument process(BxDocument doc) throws AnalysisException {
		classifier.classifyZones(doc);
		return doc;
	}

}
