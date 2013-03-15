package pl.edu.icm.cermine.bibref.transformers;

import java.io.StringWriter;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import pl.edu.icm.cermine.StandardDataExamples;
import pl.edu.icm.cermine.bibref.model.BibEntry;
import pl.edu.icm.cermine.exception.TransformationException;
import pl.edu.icm.cermine.tools.transformers.ModelToFormatWriter;

/**
 *
 * @author Dominika Tkaczyk
 */
public class BibEntryToBibTeXWriterTest {

    private ModelToFormatWriter<BibEntry> writer;
    
    private List<BibEntry> bibEntries;
    private List<String> bibtexEntries;


    @Before
    public void setUp() {
        writer = new BibEntryToBibTeXWriter();
        bibEntries = StandardDataExamples.getReferencesAsBibEntry();
        bibtexEntries = StandardDataExamples.getReferencesAsBibTeX();
    }

    @Test
    public void testWrite() throws TransformationException {
        assertEquals(bibtexEntries.get(0), writer.write(bibEntries.get(0)));
        
        StringWriter sw = new StringWriter();
        writer.write(sw, bibEntries.get(0));
        assertEquals(bibtexEntries.get(0), sw.toString());
    }
    
    @Test
    public void testMultiple() throws TransformationException {
        assertEquals(StringUtils.join(bibtexEntries, "\n\n"), writer.writeAll(bibEntries));
        
        StringWriter sw = new StringWriter();
        writer.writeAll(sw, bibEntries);
        assertEquals(StringUtils.join(bibtexEntries, "\n\n"), sw.toString());
    }
    
}
