package pl.edu.icm.cermine.bibref.transformers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jdom.Element;
import org.jdom.JDOMException;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import pl.edu.icm.cermine.StandardDataExamples;
import pl.edu.icm.cermine.bibref.model.BibEntry;
import pl.edu.icm.cermine.exception.TransformationException;

/**
 *
 * @author Dominika Tkaczyk
 */
public class NLMElementToBibEntryConverterTest {
    
    private NLMElementToBibEntryConverter converter;
    
    List<BibEntry> entries;
    List<Element> elements;

    @Before
    public void setUp() throws JDOMException, IOException {
        converter = new NLMElementToBibEntryConverter();
    
        elements = StandardDataExamples.getReferencesAsNLMElement();
        entries = StandardDataExamples.getReferencesAsBibEntry();
    }
    
    @Test
    public void test() throws TransformationException {
        assertEquals(entries.size(), elements.size());
        List<BibEntry> testEntries = new ArrayList<BibEntry>();
        for (Element element : elements) {
            testEntries.add(converter.convert(element));
        }
        assertEquals(entries, testEntries);
    }
}