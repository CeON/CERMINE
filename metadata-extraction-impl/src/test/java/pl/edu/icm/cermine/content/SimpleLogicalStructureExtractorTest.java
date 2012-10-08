package pl.edu.icm.cermine.content;

import com.thoughtworks.xstream.XStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import pl.edu.icm.cermine.content.features.line.*;
import pl.edu.icm.cermine.content.model.DocumentContentStructure;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.structure.HierarchicalReadingOrderResolver;
import pl.edu.icm.cermine.structure.model.*;
import pl.edu.icm.cermine.structure.transformers.TrueVizToBxDocumentReader;
import pl.edu.icm.cermine.tools.classification.features.FeatureCalculator;
import pl.edu.icm.cermine.tools.classification.features.FeatureVectorBuilder;
import pl.edu.icm.cermine.tools.classification.general.SimpleFeatureVectorBuilder;
import pl.edu.icm.cermine.tools.classification.knn.KnnModel;

/**
 *
 * @author Dominika Tkaczyk
 */
public class SimpleLogicalStructureExtractorTest {
    
    String dir = "/pl/edu/icm/cermine/content/";

    String testZip = "test.zip";
    String sourceDir = "source/";
    String structureDir = "structure/";
    
    String modelsZip = "models.zip";
    String headerModelFile = "headerClassModel.xml";
    String junkModelFile = "junkModel.xml";
    
    double minHeaderPrecission = 90.0;
    double minHeaderRecall = 90.0;
    
    List<BxDocument> testDocuments = new ArrayList<BxDocument>();
    List<DocumentContentStructure> testHeaderStructures = new ArrayList<DocumentContentStructure>();
       
    FeatureVectorBuilder<BxLine, BxPage> classVectorBuilder;
    FeatureVectorBuilder<BxLine, BxPage> clustVectorBuilder;
    FeatureVectorBuilder<BxZone, BxPage> junkVectorBuilder;
    
    KnnModel<BxZoneLabel> classModel;
    KnnModel<BxZoneLabel> junkModel;

    
    @Before
    public void setUp() throws IOException, TransformationException, AnalysisException, URISyntaxException, JDOMException {
        classVectorBuilder = new SimpleFeatureVectorBuilder<BxLine, BxPage>();
        classVectorBuilder.setFeatureCalculators(Arrays.<FeatureCalculator<BxLine, BxPage>>asList(
                new DigitDotSchemaFeature(),
                new DigitParSchemaFeature(),
                new DoubleDigitSchemaFeature(),
                new HeightFeature(),
                new IndentationFeature(),
                new IsHeigherThanNeighborsFeature(),
                new LengthFeature(),
                new LowercaseSchemaFeature(),
                new NextLineIndentationFeature(),
                new PrevSpaceFeature(),
                new RomanDigitsSchemaFeature(),
                new TripleDigitSchemaFeature(),
                new UppercaseSchemaFeature(),
                new WordsAllUppercaseFeature(),
                new WordsUppercaseFeature()
                ));
        
        clustVectorBuilder = new SimpleFeatureVectorBuilder<BxLine, BxPage>();
        clustVectorBuilder.setFeatureCalculators(Arrays.<FeatureCalculator<BxLine, BxPage>>asList(
                new DigitDotSchemaFeature(),
                new DigitParSchemaFeature(),
                new DoubleDigitSchemaFeature(),
                new LowercaseSchemaFeature(),
                new RomanDigitsSchemaFeature(),
                new TripleDigitSchemaFeature(),
                new UppercaseSchemaFeature()
                ));
 
        junkVectorBuilder = new SimpleFeatureVectorBuilder<BxZone, BxPage>();
        junkVectorBuilder.setFeatureCalculators(Arrays.<FeatureCalculator<BxZone, BxPage>>asList(
                new pl.edu.icm.cermine.content.features.zone.AreaFeature(),
                new pl.edu.icm.cermine.content.features.zone.FigureTableFeature(),
                new pl.edu.icm.cermine.content.features.zone.GreekLettersFeature(),
                new pl.edu.icm.cermine.content.features.zone.RelativeMeanLengthFeature(),
                new pl.edu.icm.cermine.content.features.zone.MathSymbolsFeature(),
                new pl.edu.icm.cermine.content.features.zone.XVarianceFeature()
                ));
        
        classModel = readModel(dir + modelsZip, headerModelFile);
        junkModel = readModel(dir + modelsZip, junkModelFile);
 
        fillLists(dir+testZip, testDocuments, testHeaderStructures);
    }
    
    @Test
    public void test() throws IOException, TransformationException, AnalysisException, URISyntaxException {
        LogicalStructureExtractor extractor = new LogicalStructureExtractor();
        
        int headerCount = 0;
        int goodHeaderCount = 0;
        int recognizedHeaderCount = 0;

        for (int i = 0; i < testDocuments.size(); i++) {
            BxDocument document = testDocuments.get(i);
            
            System.out.println("");
            System.out.println(i);
            DocumentContentStructure hdrs = testHeaderStructures.get(i);
            
            headerCount += hdrs.getHeaderCount();

            System.out.println();
            System.out.println("ORIGINAL: ");
            hdrs.printHeaders();
            
            DocumentContentStructure extractedHdrs = extractor.extractStructure(junkModel, classModel, junkVectorBuilder, classVectorBuilder, clustVectorBuilder, document);
                    
            System.out.println("EXTRACTED:");
            extractedHdrs.printHeaders();
            
            recognizedHeaderCount += extractedHdrs.getHeaderCount();
            
            for (String header : hdrs.getHeaderTexts()) {
                if (extractedHdrs.containsHeaderText(header)) {
                    goodHeaderCount++;
                } else {
                    System.out.println("NOT EXTR: " + header);
                }
            }
        }
        
        double hPrecission = (double) goodHeaderCount / (double) recognizedHeaderCount * 100;
        double hRecall = (double) goodHeaderCount / (double) headerCount * 100;
        
        System.out.println("Header Precission: " + hPrecission + "%");
        System.out.println("Header Recall: " + hRecall + "%");
       
        assertTrue(hPrecission >= minHeaderPrecission);
        assertTrue(hRecall >= minHeaderRecall);
    }
    
    private List<ZipEntry> getEntries(ZipFile zipFile) throws URISyntaxException, ZipException, IOException {
        List<ZipEntry> entries = new ArrayList<ZipEntry>();
               
        Enumeration enumeration = zipFile.entries();
        while (enumeration.hasMoreElements()) {
            ZipEntry zipEntry = (ZipEntry) enumeration.nextElement();
            if (zipEntry.getName().endsWith(".xml")) {
                entries.add(zipEntry);
            }
        }
        
        Collections.sort(entries, new Comparator<ZipEntry>() {

            @Override
            public int compare(ZipEntry t1, ZipEntry t2) {
                return t1.getName().compareTo(t2.getName());
            }
        
        });
        
        return entries;
    }

    private KnnModel<BxZoneLabel> readModel(String zipFileName, String modelFileName) throws IOException, TransformationException, JDOMException, URISyntaxException {
        ZipFile zipFile = new ZipFile(new File(this.getClass().getResource(zipFileName).toURI()));
        List<ZipEntry> entries = getEntries(zipFile);
        
        for (ZipEntry ze : entries) {
            if (ze.getName().matches("^"+modelFileName+"$") || ze.getName().matches("^.*/"+modelFileName+"$")) {
                InputStream xis = zipFile.getInputStream(ze);
                XStream xs = new XStream();
                return (KnnModel<BxZoneLabel>) xs.fromXML(xis);
            }
        }
        return null;
    }
    
    private void fillLists(String zipFileName, List<BxDocument> documents, List<DocumentContentStructure> headerStructures) 
            throws IOException, TransformationException, JDOMException, URISyntaxException {
        ZipFile zipFile = new ZipFile(new File(this.getClass().getResource(zipFileName).toURI()));
        List<ZipEntry> entries = getEntries(zipFile);
        
        HierarchicalReadingOrderResolver roa = new HierarchicalReadingOrderResolver();
        for (ZipEntry ze : entries) {
            if (ze.getName().matches("^.*/"+sourceDir+".*$")) {
                InputStream xis = zipFile.getInputStream(ze);
                InputStreamReader xisr = new InputStreamReader(xis);
                TrueVizToBxDocumentReader reader = new TrueVizToBxDocumentReader();
                System.out.println(ze.getName());
                List<BxPage> pages = reader.read(xisr);
                documents.add(roa.resolve(new BxDocument().setPages(pages)));
            }
            if (ze.getName().matches("^.*/"+structureDir+".*$")) {
                InputStream cis = zipFile.getInputStream(ze);
                InputStreamReader cisr = new InputStreamReader(cis);
                
                SAXBuilder saxBuilder = new SAXBuilder("org.apache.xerces.parsers.SAXParser");
                org.jdom.Document dom = saxBuilder.build(cisr);
                Element root = dom.getRootElement();
                List<Element> elements = root.getChildren();
                
                DocumentContentStructure hs = new DocumentContentStructure();
                hs.build(elements);
                headerStructures.add(hs);
            }
        }
    }

}