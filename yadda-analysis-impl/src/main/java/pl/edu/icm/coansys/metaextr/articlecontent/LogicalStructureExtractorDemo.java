package pl.edu.icm.coansys.metaextr.articlecontent;

import pl.edu.icm.coansys.metaextr.articlecontent.features.line.IndentationFeature;
import pl.edu.icm.coansys.metaextr.articlecontent.features.line.HeightFeature;
import pl.edu.icm.coansys.metaextr.articlecontent.features.line.WordsUppercaseFeature;
import pl.edu.icm.coansys.metaextr.articlecontent.features.line.IsHeigherThanNeighborsFeature;
import pl.edu.icm.coansys.metaextr.articlecontent.features.line.DigitParSchemaFeature;
import pl.edu.icm.coansys.metaextr.articlecontent.features.line.WordsAllUppercaseFeature;
import pl.edu.icm.coansys.metaextr.articlecontent.features.line.LowercaseSchemaFeature;
import pl.edu.icm.coansys.metaextr.articlecontent.features.line.DoubleDigitSchemaFeature;
import pl.edu.icm.coansys.metaextr.articlecontent.features.line.RomanDigitsSchemaFeature;
import pl.edu.icm.coansys.metaextr.articlecontent.features.line.LengthFeature;
import pl.edu.icm.coansys.metaextr.articlecontent.features.line.PrevSpaceFeature;
import pl.edu.icm.coansys.metaextr.articlecontent.features.line.UppercaseSchemaFeature;
import pl.edu.icm.coansys.metaextr.articlecontent.features.line.DigitDotSchemaFeature;
import pl.edu.icm.coansys.metaextr.articlecontent.features.line.NextLineIndentationFeature;
import pl.edu.icm.coansys.metaextr.articlecontent.features.line.TripleDigitSchemaFeature;
import pl.edu.icm.coansys.metaextr.textr.model.BxLine;
import pl.edu.icm.coansys.metaextr.textr.model.BxZone;
import pl.edu.icm.coansys.metaextr.textr.model.BxPage;
import pl.edu.icm.coansys.metaextr.textr.model.BxZoneLabel;
import pl.edu.icm.coansys.metaextr.textr.model.BxDocument;
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
import pl.edu.icm.coansys.metaextr.AnalysisException;
import pl.edu.icm.coansys.metaextr.TransformationException;
import pl.edu.icm.coansys.metaextr.articlecontent.model.DocumentContentStructure;
import pl.edu.icm.coansys.metaextr.classification.features.FeatureCalculator;
import pl.edu.icm.coansys.metaextr.classification.features.FeatureVectorBuilder;
import pl.edu.icm.coansys.metaextr.classification.features.SimpleFeatureVectorBuilder;
import pl.edu.icm.coansys.metaextr.classification.knn.model.KnnModel;
import pl.edu.icm.coansys.metaextr.textr.readingorder.ReadingOrderAnalyzer;
import pl.edu.icm.coansys.metaextr.textr.transformers.TrueVizToBxDocumentReader;

/**
 *
 * @author Dominika Tkaczyk
 */
public class LogicalStructureExtractorDemo {
    
    String dir = "/pl/edu/icm/yadda/analysis/articlecontent/";
    String trainZip = "train.zip";
    String testZip = "test.zip";
    String sourceDir = "source/";
    String structureDir = "structure/";
    
    List<BxDocument> trainDocuments = new ArrayList<BxDocument>();
    List<DocumentContentStructure> trainHeaderStructures = new ArrayList<DocumentContentStructure>();
        
    List<BxDocument> testDocuments = new ArrayList<BxDocument>();
    List<DocumentContentStructure> testHeaderStructures = new ArrayList<DocumentContentStructure>();
       
    FeatureVectorBuilder<BxLine, BxPage> classVectorBuilder;
    FeatureVectorBuilder<BxLine, BxPage> clustVectorBuilder;
    FeatureVectorBuilder<BxZone, BxPage> junkVectorBuilder;
    
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
                new pl.edu.icm.coansys.metaextr.articlecontent.features.zone.AreaFeature(),
                new pl.edu.icm.coansys.metaextr.articlecontent.features.zone.FigureTableFeature(),
                new pl.edu.icm.coansys.metaextr.articlecontent.features.zone.GreekLettersFeature(),
                new pl.edu.icm.coansys.metaextr.articlecontent.features.zone.RelativeMeanLengthFeature(),
                new pl.edu.icm.coansys.metaextr.articlecontent.features.zone.MathSymbolsFeature(),
                new pl.edu.icm.coansys.metaextr.articlecontent.features.zone.XVarianceFeature()
                ));
        
 
        ZipFile trainZipFile = new ZipFile(new File(this.getClass().getResource(dir + trainZip).toURI()));
        List<ZipEntry> trainZipEntries = getEntries(trainZipFile);
        fillLists(trainZipFile, trainZipEntries, trainDocuments, trainHeaderStructures);
        
        ZipFile testZipFile = new ZipFile(new File(this.getClass().getResource(dir+testZip).toURI()));
        List<ZipEntry> testZipEntries = getEntries(testZipFile);
        fillLists(testZipFile, testZipEntries, testDocuments, testHeaderStructures);
    }
    
    public void test() throws IOException, TransformationException, AnalysisException, URISyntaxException {
        LogicalStructureExtractor extractor = new LogicalStructureExtractor();
        
        KnnModel<BxZoneLabel> classModel = new ContentHeaderExtractor().buildModel(classVectorBuilder, trainDocuments, trainHeaderStructures);
        KnnModel<BxZoneLabel> junkModel = new ContentJunkFilter().buildModel(junkVectorBuilder, trainDocuments);

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
    
    private void fillLists(ZipFile zipFile, List<ZipEntry> entries, List<BxDocument> documents, 
            List<DocumentContentStructure> headerStructures) throws IOException, TransformationException, JDOMException {
        ReadingOrderAnalyzer roa = new ReadingOrderAnalyzer();
        for (ZipEntry ze : entries) {
            if (ze.getName().matches("^.*/"+sourceDir+".*$")) {
                InputStream xis = zipFile.getInputStream(ze);
                InputStreamReader xisr = new InputStreamReader(xis);
                TrueVizToBxDocumentReader reader = new TrueVizToBxDocumentReader();
                List<BxPage> pages = reader.read(xisr);
                documents.add(roa.setReadingOrder(new BxDocument().setPages(pages)));
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
    
    public static void main(String[] args) throws IOException, TransformationException, AnalysisException, URISyntaxException, JDOMException {
        LogicalStructureExtractorDemo test = new LogicalStructureExtractorDemo();
        test.setUp();
        test.test();
    }
    
}