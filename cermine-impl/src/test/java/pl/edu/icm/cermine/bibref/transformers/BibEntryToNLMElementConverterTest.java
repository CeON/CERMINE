package pl.edu.icm.cermine.bibref.transformers;

import java.io.IOException;
import java.util.List;
import org.custommonkey.xmlunit.Diff;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.XMLOutputter;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;
import pl.edu.icm.cermine.StandardDataExamples;
import pl.edu.icm.cermine.bibref.model.BibEntry;
import pl.edu.icm.cermine.exception.TransformationException;

/**
 *
 * @author Dominika Tkaczyk
 */
public class BibEntryToNLMElementConverterTest {
   
    private BibEntryToNLMElementConverter converter;
    
    List<BibEntry> entries;
    List<Element> elements;

    @Before
    public void setUp() throws JDOMException, IOException {
        converter = new BibEntryToNLMElementConverter();
        entries = StandardDataExamples.getReferencesAsBibEntry();
        elements = StandardDataExamples.getReferencesAsNLMElement();
    }
    
    @Test
    public void test() throws TransformationException, SAXException, IOException {
        assertEquals(entries.size(), elements.size());
        XMLOutputter xmlOut = new XMLOutputter();
        int i = 0;
        for (BibEntry entry : entries) {
            Element testElement = converter.convert(entry);
            Diff diff = new Diff(xmlOut.outputString(elements.get(i)), xmlOut.outputString(testElement));
            assertTrue(diff.similar());
            i++;
        }
    }
    
}