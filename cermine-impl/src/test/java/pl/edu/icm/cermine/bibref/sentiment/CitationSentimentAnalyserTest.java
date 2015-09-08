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
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import pl.edu.icm.cermine.ComponentFactory;
import pl.edu.icm.cermine.ContentExtractor;
import pl.edu.icm.cermine.bibref.model.BibEntry;
import pl.edu.icm.cermine.bibref.sentiment.model.CiTOProperty;
import pl.edu.icm.cermine.bibref.sentiment.model.CitationPosition;
import pl.edu.icm.cermine.bibref.sentiment.model.CitationSentiment;
import pl.edu.icm.cermine.exception.AnalysisException;

/**
 *
 * @author Dominika Tkaczyk
 */
public class CitationSentimentAnalyserTest {
    
    private static final String documentText =
            "This is a typical state of the art fragment. We can reference a single " +
            "document like this [2] or [ 12]. Sometimes we use [3,2, 4, 12 ] to " +
            "reference multiple documents in one place. To save space, the number can " +
            "also be given as ranges: [2-4] or [1-5, 7]. Random spaces are used to " +
            "make sure the regexps work well.";
    
    private static BibEntry[] citations = {
        new BibEntry().setText(" [12]  W. Hoeffding, Probability inequalities for sums of bounded random variables, J. Amer. Statist. Assoc, 58 (1963) 13-30.")
            .addField(BibEntry.FIELD_AUTHOR, "Hoeffding, W.")
            .addField(BibEntry.FIELD_TITLE, "Probability inequalities for sums of bounded random variables")
            .addField(BibEntry.FIELD_JOURNAL, "J. Amer. Statist. Assoc")
            .addField(BibEntry.FIELD_VOLUME, "58")
            .addField(BibEntry.FIELD_YEAR, "1963")
            .addField(BibEntry.FIELD_PAGES, "13--30"),
        new BibEntry().setText("5.  M-Y. Wang,  X. Wang and  D. Guo, A level-set method for structural topology optimization. Comput. Methods Appl. Mech. Engrg, 192 (2003) 227–246.")
            .addField(BibEntry.FIELD_AUTHOR, "Wang, M-Y.")
            .addField(BibEntry.FIELD_AUTHOR, "Wang, X.")
            .addField(BibEntry.FIELD_AUTHOR, "Guo, D.")
            .addField(BibEntry.FIELD_TITLE, "A level-set method for structural topology optimization")
            .addField(BibEntry.FIELD_JOURNAL, "Comput. Methods Appl. Mech. Engrg")
            .addField(BibEntry.FIELD_VOLUME, "192")
            .addField(BibEntry.FIELD_YEAR, "2003")
            .addField(BibEntry.FIELD_PAGES, "227--246"),
        new BibEntry().setText("  [47] R. Kobayashi, Einstein-Kähler V metrics on open Satake V -surfaces with isolated quotient singularities, Math. Ann. 272 (1985), 385-398.")
            .addField(BibEntry.FIELD_AUTHOR, "Kobayashi, R.")
            .addField(BibEntry.FIELD_TITLE, "Einstein-Kähler V metrics on open Satake V -surfaces with isolated quotient singularities")
            .addField(BibEntry.FIELD_JOURNAL, "Math. Ann.")
            .addField(BibEntry.FIELD_VOLUME, "272")
            .addField(BibEntry.FIELD_YEAR, "1985")
            .addField(BibEntry.FIELD_PAGES, "385--398"),
    };

    @Test
    public void testCitationPositions() throws AnalysisException {
        ContentExtractor extractor = new ContentExtractor();
        extractor.setRawFullText(documentText);
        extractor.setReferences(Lists.newArrayList(citations));
        List<List<CitationPosition>> locations = extractor.getCitationPositions();
        
        assertEquals(3, locations.size());
        
        assertEquals(2, locations.get(0).size());
        assertEquals(98, locations.get(0).get(0).getStartRefPosition());
        assertEquals(101, locations.get(0).get(0).getEndRefPosition());
        assertEquals(122, locations.get(0).get(1).getStartRefPosition());
        assertEquals(133, locations.get(0).get(1).getEndRefPosition());
        
        assertEquals(1, locations.get(1).size());
        assertEquals(246, locations.get(1).get(0).getStartRefPosition());
        assertEquals(252, locations.get(1).get(0).getEndRefPosition());

        assertEquals(0, locations.get(2).size());
    }
    
    @Test
    public void testCitationSentiment() throws AnalysisException {
        ContentExtractor extractor = new ContentExtractor();
        extractor.setRawFullText(documentText);
        extractor.setReferences(Lists.newArrayList(citations));
        List<CitationSentiment> sentiments = extractor.getCitationSentiments();
        
        assertEquals(3, sentiments.size());
        assertEquals(EnumSet.of(CiTOProperty.CITES), sentiments.get(0).getProperties());
        assertEquals(EnumSet.of(CiTOProperty.CITES), sentiments.get(1).getProperties());
        assertEquals(0, sentiments.get(2).getProperties().size());
    }
    
    @Test
    public void testRandomCitationSentiment() throws AnalysisException {
        ContentExtractor extractor = new ContentExtractor();
        extractor.setRawFullText(documentText);
        extractor.setReferences(Lists.newArrayList(citations));
        extractor.getConf().setCitationSentimentAnalyser(ComponentFactory.getRandomCitationSentimentAnalyser());
        List<CitationSentiment> sentiments = extractor.getCitationSentiments();
        
        assertEquals(3, sentiments.size()); 
        assertEquals(EnumSet.of(CiTOProperty.CITES, CiTOProperty.REFUTES), sentiments.get(0).getProperties());
        assertEquals(EnumSet.of(CiTOProperty.CITES, CiTOProperty.UPDATES), sentiments.get(1).getProperties());
        assertEquals(EnumSet.of(CiTOProperty.CITES, CiTOProperty.EXTENDS), sentiments.get(2).getProperties());
    }
    
    @Test
    public void testCitationLocationSentiment() throws AnalysisException {
        ContentExtractor extractor = new ContentExtractor();
        extractor.setRawFullText(documentText);
        
        CitationPosition position1 = new CitationPosition();
        position1.setStartRefPosition(98);
        position1.setEndRefPosition(101);
        
        CitationPosition position2 = new CitationPosition();
        position2.setStartRefPosition(122);
        position2.setEndRefPosition(133);
        
        CitationPosition position3 = new CitationPosition();
        position3.setStartRefPosition(246);
        position3.setEndRefPosition(252);
        
        List<CitationPosition> list1 = Lists.newArrayList(position1, position2);
        List<CitationPosition> list2 = Lists.newArrayList(position3);
        List<CitationPosition> list3 = new ArrayList<CitationPosition>();
        List<List<CitationPosition>> locations = Lists.newArrayList(list1, list2, list3);
        
        extractor.setReferences(Lists.newArrayList(citations));
        extractor.setCitationPositions(locations);
        List<CitationSentiment> sentiments = extractor.getCitationSentiments();
        
        assertEquals(3, sentiments.size());
        assertEquals(EnumSet.of(CiTOProperty.CITES), sentiments.get(0).getProperties());
        assertEquals(EnumSet.of(CiTOProperty.CITES), sentiments.get(1).getProperties());
        assertEquals(0, sentiments.get(2).getProperties().size());
    }
    
}
