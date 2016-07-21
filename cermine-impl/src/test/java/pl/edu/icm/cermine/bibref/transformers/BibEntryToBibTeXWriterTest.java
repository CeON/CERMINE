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
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
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
