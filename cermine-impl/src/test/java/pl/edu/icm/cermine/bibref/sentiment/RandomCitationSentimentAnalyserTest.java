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
import pl.edu.icm.cermine.bibref.sentiment.model.CiTOProperty;
import pl.edu.icm.cermine.bibref.sentiment.model.CitationSentiment;

/**
 *
 * @author Dominika Tkaczyk
 */
public class RandomCitationSentimentAnalyserTest {
    
    @Test
    public void testPositionFinder() {
        CitationSentimentAnalyser analyser = new RandomCitationSentimentAnalyser();

        CitationSentiment sentiment = analyser.analyzeSentiment(Lists.newArrayList("context1"));
        assertEquals(EnumSet.of(CiTOProperty.CITES, CiTOProperty.CREDITS), sentiment.getProperties());
        
        sentiment = analyser.analyzeSentiment(Lists.newArrayList("context2", "context3"));
        assertEquals(EnumSet.of(CiTOProperty.CITES, CiTOProperty.PLAGIARIZES), sentiment.getProperties());
        
        sentiment = analyser.analyzeSentiment(Lists.newArrayList("context3", "context4"));
        assertEquals(EnumSet.of(CiTOProperty.CITES, CiTOProperty.UPDATES), sentiment.getProperties());
    }
    
}
