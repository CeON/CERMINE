/**
 * This file is part of CERMINE project.
 * Copyright (c) 2011-2016 ICM-UW
 *
 * CERMINE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CERMINE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with CERMINE. If not, see <http://www.gnu.org/licenses/>.
 */

package pl.edu.icm.cermine.structure;

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
import org.xml.sax.SAXException;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.tools.DocumentProcessor;
import pl.edu.icm.cermine.structure.transformers.TrueVizToBxDocumentReader;
import static org.junit.Assert.assertTrue;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public abstract class AbstractDocumentProcessorTest {

    private static final String XML_FILENAME_EX = ".xml";

    protected DocumentProcessor startProcessFlattener;
    protected DocumentProcessor endProcessFlattener;


    protected abstract boolean compareDocuments(BxDocument testDoc, BxDocument expectedDoc);

    protected abstract BxDocument process(BxDocument doc) throws AnalysisException;

    
    protected void testFiles(Collection<String> files, double percentage)
            throws IOException, ParserConfigurationException, SAXException, AnalysisException, TransformationException {
        List<Reader> testReaders = new ArrayList<Reader>();
        List<Reader> expectedReaders = new ArrayList<Reader>();

        for (String file : files) {
            testReaders.add(new InputStreamReader(AbstractDocumentProcessorTest.class.getResourceAsStream(file)));
            expectedReaders.add(new InputStreamReader(AbstractDocumentProcessorTest.class.getResourceAsStream(file)));
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
            ZipFile zipFile = new ZipFile(new File(AbstractDocumentProcessorTest.class.getResource(file).toURI()));
            Enumeration entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry zipEntry = (ZipEntry) entries.nextElement();
                if (zipEntry.getName().endsWith(XML_FILENAME_EX)) {
                    String parent = new File(zipEntry.getName()).getParent();
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
        
        return compareDocuments(testDoc, expectedDoc);
    }
    
}
