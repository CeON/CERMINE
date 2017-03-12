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

import java.io.StringReader;
import java.util.List;
import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import pl.edu.icm.cermine.StandardDataExamples;
import pl.edu.icm.cermine.bibref.model.BibEntry;
import pl.edu.icm.cermine.bibref.model.BibEntryField;
import pl.edu.icm.cermine.bibref.model.BibEntryFieldType;
import pl.edu.icm.cermine.exception.TransformationException;

/**
 * @author Ewa Stocka
 */
public class BibTeXToBibEntryReaderTest {
    
    private final BibTeXToBibEntryReader reader = new BibTeXToBibEntryReader();
    
    private final List<String> bibtex = StandardDataExamples.getReferencesAsBibTeX();
    private final List<BibEntry> entries = StandardDataExamples.getReferencesAsBibEntry();

    @Before
    public void setUp() {
        CollectionUtils.forAllDo(entries, new Closure() {

            @Override
            public void execute(Object input) {
                removeTextAndIndexes((BibEntry) input);
            }
        });
    }

    @Test
    public void testRead() throws TransformationException {
        assertEquals(entries.get(0), reader.read(bibtex.get(0)));
        
        StringReader sr = new StringReader(bibtex.get(0));
        assertEquals(entries.get(0), reader.read(sr));
    }
    
    @Test
    public void testReadAll() throws TransformationException {
        String allBibTex = StringUtils.join(bibtex, "\n\n");
    
        assertEquals(entries, reader.readAll(allBibTex));
        
        StringReader sr = new StringReader(allBibTex);
        assertEquals(entries, reader.readAll(sr));
    }
    
    private void removeTextAndIndexes(BibEntry entry) {
        entry.setText(null);
        for (BibEntryFieldType key : entry.getFieldKeys()) {
            for (BibEntryField field : entry.getAllFields(key)) {
                field.setIndexes(-1, -1);
            }
        }
    }

}