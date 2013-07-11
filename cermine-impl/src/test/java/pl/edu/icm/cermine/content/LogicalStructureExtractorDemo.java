package pl.edu.icm.cermine.content;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.jdom.JDOMException;
import pl.edu.icm.cermine.content.filtering.KnnContentFilter;
import pl.edu.icm.cermine.content.headers.KnnContentHeadersExtractor;
import pl.edu.icm.cermine.content.model.DocumentContentStructure;
import pl.edu.icm.cermine.content.transformers.HTMLToDocContentStructReader;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.structure.HierarchicalReadingOrderResolver;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;
import pl.edu.icm.cermine.structure.transformers.TrueVizToBxDocumentReader;
import pl.edu.icm.cermine.tools.classification.knn.KnnModel;

/**
 *
 * @author Dominika Tkaczyk
 */
public class LogicalStructureExtractorDemo {
    
    private String dir = "/pl/edu/icm/cermine/content/";
    private String trainZip = "train.zip";
    private String testZip = "train.zip";
    private String sourceDir = "source/";
    private String structureDir = "structure/";
    
    private List<BxDocument> trainDocuments = new ArrayList<BxDocument>();
    private List<DocumentContentStructure> trainHeaderStructures = new ArrayList<DocumentContentStructure>();
        
    private List<BxDocument> testDocuments = new ArrayList<BxDocument>();
    private List<DocumentContentStructure> testHeaderStructures = new ArrayList<DocumentContentStructure>();
       
   
    public void setUp() throws IOException, TransformationException, AnalysisException, URISyntaxException, JDOMException {
        ZipFile trainZipFile = new ZipFile(new File(this.getClass().getResource(dir + trainZip).toURI()));
        List<ZipEntry> trainZipEntries = getEntries(trainZipFile);
        fillLists(trainZipFile, trainZipEntries, trainDocuments, trainHeaderStructures);
        
        ZipFile testZipFile = new ZipFile(new File(this.getClass().getResource(dir+testZip).toURI()));
        List<ZipEntry> testZipEntries = getEntries(testZipFile);
        fillLists(testZipFile, testZipEntries, testDocuments, testHeaderStructures);
    }
    
    public void test() throws IOException, TransformationException, AnalysisException, URISyntaxException {
        
        KnnModel<BxZoneLabel> classModel = KnnContentHeadersExtractor.buildModel(trainDocuments, trainHeaderStructures);
        KnnModel<BxZoneLabel> junkModel = KnnContentFilter.buildModel(trainDocuments);
        
        LogicalStructureExtractor extractor = new KnnLogicalStructureExtractor(junkModel, classModel);

        int headerCount = 0;
        int goodHeaderCount = 0;
        int recognizedHeaderCount = 0;

        for (int i = 0; i < testDocuments.size(); i++) {
           
            BxDocument document = testDocuments.get(i);
            
            System.out.println("");
            System.out.println(i);
            DocumentContentStructure hdrs = testHeaderStructures.get(i);
            
            headerCount += hdrs.getAllHeaderCount();

            System.out.println();
            System.out.println("ORIGINAL: ");
            hdrs.printHeaders();
            
            DocumentContentStructure extractedHdrs = extractor.extractStructure(document);
                    
            System.out.println("EXTRACTED:");
            extractedHdrs.printHeaders();
            
            recognizedHeaderCount += extractedHdrs.getAllHeaderCount();
            
            for (String header : hdrs.getAllHeaderTexts()) {
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
    
    private List<ZipEntry> getEntries(ZipFile zipFile) throws URISyntaxException, IOException {
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
        HierarchicalReadingOrderResolver roa = new HierarchicalReadingOrderResolver();
        TrueVizToBxDocumentReader bxReader = new TrueVizToBxDocumentReader();
        HTMLToDocContentStructReader dcsReader = new HTMLToDocContentStructReader();
        for (ZipEntry ze : entries) {
            if (ze.getName().matches("^.*/"+sourceDir+".*$")) {
                InputStream xis = zipFile.getInputStream(ze);
                InputStreamReader xisr = new InputStreamReader(xis);
                List<BxPage> pages = bxReader.read(xisr);
                documents.add(roa.resolve(new BxDocument().setPages(pages)));
            }
            if (ze.getName().matches("^.*/"+structureDir+".*$")) {
                InputStream cis = zipFile.getInputStream(ze);
                InputStreamReader cisr = new InputStreamReader(cis);
                
                DocumentContentStructure hs = dcsReader.read(cisr);
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