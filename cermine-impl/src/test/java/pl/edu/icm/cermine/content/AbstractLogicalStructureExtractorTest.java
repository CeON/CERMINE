/**
 * This file is part of CERMINE project.
 * Copyright (c) 2011-2013 ICM-UW
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

package pl.edu.icm.cermine.content;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import org.jdom.JDOMException;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import pl.edu.icm.cermine.content.model.DocumentContentStructure;
import pl.edu.icm.cermine.content.transformers.HTMLToDocContentStructReader;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.structure.HierarchicalReadingOrderResolver;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.transformers.TrueVizToBxDocumentReader;

/**
 *
 * @author Dominika Tkaczyk
 */
public abstract class AbstractLogicalStructureExtractorTest {
    
    String dir = "/pl/edu/icm/cermine/content/";

    String testZip = "test-small.zip";
    String sourceDir = "source/";
    String structureDir = "structure/";

    double minHeaderPrecission = 90.0;
    double minHeaderRecall = 90.0;
    
    List<BxDocument> testDocuments = new ArrayList<BxDocument>();
    List<DocumentContentStructure> testHeaderStructures = new ArrayList<DocumentContentStructure>();
       
    LogicalStructureExtractor extractor = null;
    

    protected void setUp() throws IOException, TransformationException, AnalysisException, URISyntaxException, JDOMException {
        fillLists(dir+testZip, testDocuments, testHeaderStructures);
    }

    @Test
    public void test() throws IOException, TransformationException, AnalysisException, URISyntaxException {
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
       
        //assertTrue(hPrecission >= minHeaderPrecission);
        //assertTrue(hRecall >= minHeaderRecall);
    }
    
    protected List<ZipEntry> getEntries(ZipFile zipFile) throws URISyntaxException, ZipException, IOException {
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
  
    private void fillLists(String zipFileName, List<BxDocument> documents, List<DocumentContentStructure> headerStructures) 
            throws IOException, TransformationException, JDOMException, URISyntaxException {
        ZipFile zipFile = new ZipFile(new File(this.getClass().getResource(zipFileName).toURI()));
        List<ZipEntry> entries = getEntries(zipFile);
        
        HierarchicalReadingOrderResolver roa = new HierarchicalReadingOrderResolver();
        TrueVizToBxDocumentReader bxReader = new TrueVizToBxDocumentReader();
        HTMLToDocContentStructReader dcsReader = new HTMLToDocContentStructReader();
        
        for (ZipEntry ze : entries) {
            if (ze.getName().matches("^.*/"+sourceDir+".*$")) {
                InputStream xis = zipFile.getInputStream(ze);
                InputStreamReader xisr = new InputStreamReader(xis);
                
                System.out.println(ze.getName());
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

}