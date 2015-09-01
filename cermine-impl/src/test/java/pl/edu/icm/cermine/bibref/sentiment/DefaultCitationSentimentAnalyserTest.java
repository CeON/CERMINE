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

package pl.edu.icm.cermine.bibref.sentiment;

import com.google.common.collect.Lists;
import java.util.EnumSet;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import pl.edu.icm.cermine.bibref.model.BibEntry;
import pl.edu.icm.cermine.bibref.sentiment.model.CiTOProperty;
import pl.edu.icm.cermine.bibref.sentiment.model.CitationContext;
import pl.edu.icm.cermine.bibref.sentiment.model.CitationSentiment;

/**
 *
 * @author Dominika Tkaczyk
 */
public class DefaultCitationSentimentAnalyserTest {
    
    private static BibEntry citation = new BibEntry().setText(" [12]  W. Hoeffding, Probability inequalities for sums of bounded random variables, J. Amer. Statist. Assoc, 58 (1963) 13-30.")
            .addField(BibEntry.FIELD_AUTHOR, "Hoeffding, W.")
            .addField(BibEntry.FIELD_TITLE, "Probability inequalities for sums of bounded random variables")
            .addField(BibEntry.FIELD_JOURNAL, "J. Amer. Statist. Assoc")
            .addField(BibEntry.FIELD_VOLUME, "58")
            .addField(BibEntry.FIELD_YEAR, "1963")
            .addField(BibEntry.FIELD_PAGES, "13--30");
    
    
    @Test
    public void testContextFinder() {
        CitationSentimentAnalyser analyser = new DefaultCitationSentimentAnalyser();
        CitationContext context = new CitationContext();
        context.setContext("context");
        
        CitationSentiment sentiment = analyser.analyzeSentiment(citation, Lists.newArrayList(context));
        assertEquals(EnumSet.of(CiTOProperty.CITES), sentiment.getProperties());
    }
    
}
