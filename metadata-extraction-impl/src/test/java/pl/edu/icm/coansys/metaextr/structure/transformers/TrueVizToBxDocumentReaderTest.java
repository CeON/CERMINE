package pl.edu.icm.coansys.metaextr.structure.transformers;

import java.io.File;
import pl.edu.icm.coansys.metaextr.structure.transformers.MargToTextrImporter;
import pl.edu.icm.coansys.metaextr.structure.transformers.TrueVizToBxDocumentReader;
import pl.edu.icm.coansys.metaextr.structure.model.BxZoneLabel;
import pl.edu.icm.coansys.metaextr.structure.model.Indexable;
import pl.edu.icm.coansys.metaextr.structure.model.BxChunk;
import pl.edu.icm.coansys.metaextr.structure.model.BxWord;
import pl.edu.icm.coansys.metaextr.structure.model.BxPage;
import pl.edu.icm.coansys.metaextr.structure.model.BxZone;
import pl.edu.icm.coansys.metaextr.structure.model.BxDocument;
import pl.edu.icm.coansys.metaextr.structure.model.BxLine;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipFile;
import javax.xml.parsers.ParserConfigurationException;
import static org.junit.Assert.*;
import org.junit.Test;
import org.xml.sax.SAXException;
import pl.edu.icm.coansys.metaextr.exception.TransformationException;


/**
 *
 * @author krusek
 * @author kura
 * @author Pawel Szostek (p.szostek@icm.edu.pl)
 */
public class TrueVizToBxDocumentReaderTest {
	static String PATH = "/pl/edu/icm/coansys/metaextr/structure/";

    public TrueVizToBxDocumentReaderTest() {
    }

    @Test
    public void testImporter() throws IOException,  ParserConfigurationException, SAXException,
            TransformationException {
       BxPage page=new TrueVizToBxDocumentReader().read(new InputStreamReader(this.getClass().getResourceAsStream("/pl/edu/icm/coansys/metaextr/structure/imports/MargImporterTest1.xml"))).get(0);
       boolean contains=false;
       boolean rightText=false;
       boolean rightSize=false;
       for (BxZone zone:page.getZones()) {
          if (zone.getLabel()!=null) {
           if (zone.getLabel().equals(BxZoneLabel.MET_AUTHOR)) {
               contains=true;
               System.out.println(zone.toText());
               // takie cos na toplevelu                 Howard M. Schachter,* Ba' Pham,* Jim King,tt  Stephanie Langford,* David Moher*$
              if (zone.toText().trim().equalsIgnoreCase("Howard M  Schachter   Ba  Pham   Jim King tt\nStephanie Langford   David Moher".trim())) {
                  rightText=true;
              }
              if (zone.getBounds().getX()==72 && zone.getBounds().getY()==778 && zone.getBounds().getWidth()==989 && zone.getBounds().getHeight()==122) {
                    rightSize=true;
                  } else {
                   System.out.println(zone.getBounds().getX()+ " " + zone.getBounds().getY() +" "+zone.getBounds().getWidth()+ " "+zone.getBounds().getHeight());
                  }
           }
         } else {
              System.out.println("Zone with no label: "+zone.toText());
         }
       }
       assertTrue(contains);
       assertTrue(rightText);
       assertTrue(rightSize);
    }

	private BxDocument getDocumentFromZipFile(String zipFilename, String filename) throws TransformationException, IOException, URISyntaxException {
        URL url = this.getClass().getResource(PATH + zipFilename);
        ZipFile zipFile = new ZipFile(new File(url.toURI()));
        InputStream is = zipFile.getInputStream(zipFile.getEntry(filename));
        InputStreamReader isr = new InputStreamReader(is);

		TrueVizToBxDocumentReader reader = new TrueVizToBxDocumentReader();
		BxDocument doc = new BxDocument().setPages(reader.read(isr));
		isr.close();
		return doc;
	}

    @Test
    public void testAllNextsAreSet1() throws TransformationException, IOException, URISyntaxException {
    	BxDocument orderedDoc = getDocumentFromZipFile("roa_test.zip", "1748717X.xml.out");
    	//walk through document's structure
    	Integer nextNulls = 0;
    	for(BxPage page: orderedDoc.asPages()) {
    		if(page.getNext() == null)
    			++nextNulls;
    	}
    	assertEquals(nextNulls, new Integer(1));

    	nextNulls = 0;
    	for(BxZone zone: orderedDoc.asZones()) {
    		if(zone.getNext() == null)
    			++nextNulls;
    	}
    	assertEquals(nextNulls, new Integer(1));

    	nextNulls = 0;
    	for(BxLine line: orderedDoc.asLines()) {
    		if(line.getNext() == null)
    			++nextNulls;
    	}
    	assertEquals(nextNulls, new Integer(1));

    	nextNulls = 0;
    	for(BxWord word: orderedDoc.asWords()) {
    		if(word.getNext() == null)
    			++nextNulls;
    	}
    	assertEquals(nextNulls, new Integer(1));

    	nextNulls = 0;
    	for(BxChunk chunk: orderedDoc.asChunks()) {
    		if(chunk.getNext() == null)
    			++nextNulls;
    	}
    	assertEquals(nextNulls, new Integer(1));
    	
    }
    
    public  <A extends Indexable> Integer countChainedElements(List<A> list) throws TransformationException, IOException {
    	Set<A> nextSet = new HashSet<A>();
    	for(A elem: list) {
    		A next = (A)elem.getNext();
    		if(next != null && list.contains(next))
    			nextSet.add(next);
    	}
    	return nextSet.size();
    }

    @Test
    public void testChainedElementsEven() throws TransformationException, IOException, URISyntaxException {
    	BxDocument doc = getDocumentFromZipFile("roa_test.zip", "1748717X.xml.out");
    	assertEquals(countChainedElements(doc.asPages()),  new Integer(doc.asPages().size()-1));
    	assertEquals(countChainedElements(doc.asZones()),  new Integer(doc.asZones().size()-1));
    	assertEquals(countChainedElements(doc.asLines()),  new Integer(doc.asLines().size()-1));
    	assertEquals(countChainedElements(doc.asWords()),  new Integer(doc.asWords().size()-1));
    	assertEquals(countChainedElements(doc.asChunks()), new Integer(doc.asChunks().size()-1));
    }
    
    @Test
    public void testHeight() throws IOException,  ParserConfigurationException, SAXException, TransformationException {
       BxPage page=new MargToTextrImporter().read(new InputStreamReader(this.getClass().getResourceAsStream("/pl/edu/icm/coansys/metaextr/structure/006.xml"))).get(0);

       for (BxZone zone:page.getZones()) {
           assertTrue("Zero heigh zone: "+zone.toText()+" : "+zone.getLabel().name(),zone.getBounds().getHeight()>0);

       }
    
    }

}