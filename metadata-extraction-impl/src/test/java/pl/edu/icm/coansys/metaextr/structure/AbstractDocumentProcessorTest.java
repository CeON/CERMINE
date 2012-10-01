package pl.edu.icm.coansys.metaextr.structure;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import javax.xml.parsers.ParserConfigurationException;
import static org.junit.Assert.assertTrue;
import org.xml.sax.SAXException;
import pl.edu.icm.coansys.metaextr.AnalysisException;
import pl.edu.icm.coansys.metaextr.TransformationException;
import pl.edu.icm.coansys.metaextr.structure.model.BxDocument;
import pl.edu.icm.coansys.metaextr.structure.tools.DocumentPreprocessor;
import pl.edu.icm.coansys.metaextr.structure.transformers.TrueVizToBxDocumentReader;

/**
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public abstract class AbstractDocumentProcessorTest {

    private static String xmlFilenameSuffix = ".xml";

    protected DocumentPreprocessor startProcessFlattener;
    protected DocumentPreprocessor endProcessFlattener;


    protected abstract boolean compareDocuments(BxDocument testDoc, BxDocument expectedDoc);

    protected abstract BxDocument process(BxDocument doc) throws AnalysisException;

    
    protected void testFiles(Collection<String> files, double percentage)
            throws IOException, ParserConfigurationException, SAXException, AnalysisException, TransformationException {
        List<Reader> testReaders = new ArrayList<Reader>();
        List<Reader> expectedReaders = new ArrayList<Reader>();

        for (String file : files) {
            testReaders.add(new InputStreamReader(this.getClass().getResourceAsStream(file)));
            expectedReaders.add(new InputStreamReader(this.getClass().getResourceAsStream(file)));
        }

        testCollection(testReaders, expectedReaders, percentage);
    }

    protected void testAllFilesFromZip(Collection<String> zipFiles, double percentage) throws URISyntaxException,
            ZipException, IOException, ParserConfigurationException, SAXException, AnalysisException,
            TransformationException {
        testFilesFromZip(zipFiles, percentage, 1, -1);
    }

    protected void testSampleFilesFromZip(Collection<String> zipFiles, double percentage) throws ZipException,
            IOException, URISyntaxException, ParserConfigurationException, SAXException, AnalysisException,
            TransformationException {
        testFilesFromZip(zipFiles, percentage, 1, 1);
    }

    protected void testSampleFilesFromZip(Collection<String> zipFiles, int from, int to, double percentage)
            throws ZipException, IOException, URISyntaxException, ParserConfigurationException, SAXException,
            AnalysisException, TransformationException {
        testFilesFromZip(zipFiles, percentage, from, to);
    }


    private void testFilesFromZip(Collection<String> zipFiles, double percentage, int samplesFrom, int samplesTo)
            throws ZipException, IOException, URISyntaxException, ParserConfigurationException, SAXException,
            AnalysisException, TransformationException {
        List<Reader> testReaders = new ArrayList<Reader>();
        List<Reader> expectedReaders = new ArrayList<Reader>();

        Map<String, Integer> dirSamplesCount = new HashMap<String, Integer>();

        for (String file : zipFiles) {
            ZipFile zipFile = new ZipFile(new File(this.getClass().getResource(file).toURI()));
            Enumeration entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry zipEntry = (ZipEntry) entries.nextElement();
                if (zipEntry.getName().endsWith(xmlFilenameSuffix)) {
                    String parent = new File(zipEntry.getName()).getParent();
                //    System.out.println(zipEntry.getName());
                    if (dirSamplesCount.containsKey(parent)) {
                        dirSamplesCount.put(parent, dirSamplesCount.get(parent) + 1);
                    } else {
                        dirSamplesCount.put(parent, 1);
                    }

                    if (dirSamplesCount.get(parent) >= samplesFrom &&
                            (samplesTo < 0 || dirSamplesCount.get(parent) <= samplesTo)) {
                        testReaders.add(new InputStreamReader(zipFile.getInputStream(zipEntry)));
                        expectedReaders.add(new InputStreamReader(zipFile.getInputStream(zipEntry)));
                    }
                }
            }
        }

        testCollection(testReaders, expectedReaders, percentage);
    }

    private void testCollection(List<Reader> testReaders, List<Reader> expectedReaders, double percentage)
            throws IOException, ParserConfigurationException, SAXException, AnalysisException, TransformationException {
        int passed = 0;
        TrueVizToBxDocumentReader reader = new TrueVizToBxDocumentReader();
        for (int i = 0; i < testReaders.size(); i++) {
            BxDocument testDoc = new BxDocument().setPages(reader.read(testReaders.get(i)));
            BxDocument expectedDoc = new BxDocument().setPages(reader.read(expectedReaders.get(i)));
            
            if (checkDocument(testDoc, expectedDoc)) {
                passed++;
            }
        }

        if (!testReaders.isEmpty()) {
            assertTrue((double) passed * 100.0f / (double) testReaders.size() >= percentage);
        }
    }

    private boolean checkDocument(BxDocument testDoc, BxDocument expectedDoc)
            throws IOException, ParserConfigurationException, SAXException, AnalysisException {
        if (startProcessFlattener != null) {
            startProcessFlattener.process(testDoc);
        }
        testDoc = process(testDoc);

        if (endProcessFlattener != null) {
            endProcessFlattener.process(expectedDoc);
        }
        
        //BxObjectDump dump = new BxObjectDump();
        //System.out.println("--------------------");
        //System.out.println(dump.dump(testDoc, 2, 2, false, true));
        //System.out.println(dump.dump(expectedDoc, 2, 2, false, true));
        boolean ret =  compareDocuments(testDoc, expectedDoc);
        //System.out.println(ret);
        return ret;
    }
    
}
