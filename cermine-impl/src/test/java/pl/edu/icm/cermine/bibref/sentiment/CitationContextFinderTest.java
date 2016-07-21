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

package pl.edu.icm.cermine.bibref.sentiment;

import com.google.common.collect.Lists;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import pl.edu.icm.cermine.bibref.sentiment.model.CitationPosition;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class CitationContextFinderTest {
    
    private static final String DOCUMENT_TEXT1 = 
        "We can reference a single document like this [2] or [ 12].";

    private static final String DOCUMENT_TEXT2 = 
        "Sometimes we use [3,2, 4, 12 ] to reference multiple documents in one place.";

    private static final String DOCUMENT_TEXT3 = 
        "To save space, the number can also be given as ranges: [2-4] or [1-5, 7].";

    private static final String DOCUMENT_TEXT4 = 
        "This is a typical state of the art fragment. " +
        DOCUMENT_TEXT1 + " " +
        DOCUMENT_TEXT2 + " " +
        DOCUMENT_TEXT3 + " Random spaces are used to make sure the regexps work well.";

    @Test
    public void testContextFinder() {
        CitationContextFinder finder = new CitationContextFinder();
        
        CitationPosition context1 = new CitationPosition();
        context1.setStartRefPosition(91);
        context1.setEndRefPosition(92);
        
        CitationPosition context2 = new CitationPosition();
        context2.setStartRefPosition(98);
        context2.setEndRefPosition(101);
        
        CitationPosition context3 = new CitationPosition();
        context3.setStartRefPosition(122);
        context3.setEndRefPosition(133);
        
        CitationPosition context4 = new CitationPosition();
        context4.setStartRefPosition(237);
        context4.setEndRefPosition(240);
        
        CitationPosition context5 = new CitationPosition();
        context5.setStartRefPosition(246);
        context5.setEndRefPosition(252);
        
        List<CitationPosition> list1 = Lists.newArrayList(context1);
        List<CitationPosition> list2 = Lists.newArrayList(context2, context3, context4);
        List<CitationPosition> list3 = Lists.newArrayList(context5);
        
        List<List<String>> contexts = finder.findContext(DOCUMENT_TEXT4, Lists.newArrayList(list1, list2, list3));

        assertEquals(3, contexts.size());

        assertEquals(1, contexts.get(0).size());
        assertEquals(DOCUMENT_TEXT1, contexts.get(0).get(0));
        
        assertEquals(3, contexts.get(1).size());
        assertEquals(DOCUMENT_TEXT1, contexts.get(1).get(0));
        assertEquals(DOCUMENT_TEXT2, contexts.get(1).get(1));
        assertEquals(DOCUMENT_TEXT3, contexts.get(1).get(2));
        
        assertEquals(1, contexts.get(2).size());
        assertEquals(DOCUMENT_TEXT3, contexts.get(2).get(0));
    }
    
}
