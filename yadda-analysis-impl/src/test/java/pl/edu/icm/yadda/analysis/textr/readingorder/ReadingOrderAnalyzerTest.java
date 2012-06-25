package pl.edu.icm.yadda.analysis.textr.readingorder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Test;

import pl.edu.icm.yadda.analysis.textr.model.BxChunk;
import pl.edu.icm.yadda.analysis.textr.model.BxDocument;
import pl.edu.icm.yadda.analysis.textr.model.BxPage;
import pl.edu.icm.yadda.analysis.textr.model.BxZone;
import pl.edu.icm.yadda.analysis.textr.transformers.TrueVizToBxDocumentReader;
import pl.edu.icm.yadda.metadata.transformers.TransformationException;


/**
 * 
 * @author Pawel Szostek (p.szostek@icm.edu.pl)
 *
 */

public class ReadingOrderAnalyzerTest {
	static final private String PATH = "/pl/edu/icm/yadda/analysis/textr/";
	static final private String[] TEST_FILENAMES = {"13191004.xml", "09629351.xml", "10255834.xml", "11781238.xml", "1748717X.xml"};
	
	private Boolean areDocumentsEqual(BxDocument doc1, BxDocument doc2) {
		if(doc1.getPages().size() != doc2.getPages().size())
			return false;
		for(Integer pageIdx=0; pageIdx < doc1.getPages().size(); ++pageIdx) {
			BxPage page1 = doc1.getPages().get(pageIdx);
			BxPage page2 = doc2.getPages().get(pageIdx);
			if(page1.getZones().size() != page2.getZones().size())
				return false;
			for(Integer zoneIdx=0; zoneIdx < page1.getZones().size(); ++zoneIdx) {
				BxZone zone1 = page1.getZones().get(zoneIdx);
				BxZone zone2 = page2.getZones().get(zoneIdx);
				if(zone1.getChunks().size() != zone2.getChunks().size())
					return false;
				for(Integer chunkIdx=0; chunkIdx < zone1.getChunks().size(); ++chunkIdx) {
					BxChunk chunk1 = zone1.getChunks().get(chunkIdx);
					BxChunk chunk2 = zone2.getChunks().get(chunkIdx);
					if(chunk1.getText() != chunk2.getText())
						return false;
				}
			}
		}
		return true;
	}
	
	
	private BxDocument getDocumentFromFile(String filename) throws TransformationException {
		InputStream is = ReadingOrderAnalyzerTest.class.getResourceAsStream(PATH + filename);
		InputStreamReader isr = new InputStreamReader(is);

		TrueVizToBxDocumentReader reader = new TrueVizToBxDocumentReader();
		BxDocument doc = new BxDocument().setPages(reader.read(isr));
		try {
		isr.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
		return doc;
	}
	
	@Test
	public void testSetReadingOrder() throws TransformationException {
		for(String filename: TEST_FILENAMES) {
			BxDocument doc = getDocumentFromFile(filename);
			BxDocument modelOrderedDoc = getDocumentFromFile(filename + ".out");
	
			ReadingOrderAnalyzer roa = new ReadingOrderAnalyzer();
			BxDocument orderedDoc = roa.setReadingOrder(doc);
			
			assertTrue(areDocumentsEqual(orderedDoc, modelOrderedDoc));
		}
	}
	
	@Test
	public void testConstantElementsNumber() throws TransformationException {
		for(String filename: TEST_FILENAMES) {
			BxDocument doc = getDocumentFromFile(filename);
			
			ReadingOrderAnalyzer roa = new ReadingOrderAnalyzer();
			BxDocument orderedDoc = roa.setReadingOrder(doc);
			
			assertEquals(doc.asPages().size(),  orderedDoc.asPages().size());
			assertEquals(doc.asZones().size(),  orderedDoc.asZones().size());
			assertEquals(doc.asLines().size(),  orderedDoc.asLines().size());
			assertEquals(doc.asWords().size(),  orderedDoc.asWords().size());
		    assertEquals(doc.asChunks().size(), orderedDoc.asChunks().size());	
		}
	}
}
