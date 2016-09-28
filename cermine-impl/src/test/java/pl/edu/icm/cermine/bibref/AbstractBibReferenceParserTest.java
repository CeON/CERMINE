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

package pl.edu.icm.cermine.bibref;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import pl.edu.icm.cermine.bibref.model.BibEntry;
import pl.edu.icm.cermine.exception.AnalysisException;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public abstract class AbstractBibReferenceParserTest {
    
    private final BibEntry[] entries = {
        new BibEntry().setText("[6]  W. Hoeffding, Probability inequalities for sums of bounded random variables, J. Amer. Statist. Assoc, 58 (1963) 13-30.")
            .addField(BibEntry.FIELD_AUTHOR, "Hoeffding, W.")
            .addField(BibEntry.FIELD_TITLE, "Probability inequalities for sums of bounded random variables")
            .addField(BibEntry.FIELD_JOURNAL, "J. Amer. Statist. Assoc")
            .addField(BibEntry.FIELD_VOLUME, "58")
            .addField(BibEntry.FIELD_YEAR, "1963")
            .addField(BibEntry.FIELD_PAGES, "13--30"),
        new BibEntry().setText(" [3]  Agranovitch (M.S.) and  Vishisk (M.I.). — Elliptic problems with a parameter and parabolic problems of general type, Russian Math. Surveys, 19, 1964, 53-157.")
            .addField(BibEntry.FIELD_AUTHOR, "Agranovitch, M.S.")
            .addField(BibEntry.FIELD_AUTHOR, "Vishisk, M.I.")
            .addField(BibEntry.FIELD_TITLE, "Elliptic problems with a parameter and parabolic problems of general type")
            .addField(BibEntry.FIELD_JOURNAL, "Russian Math. Surveys")
            .addField(BibEntry.FIELD_VOLUME, "19")
            .addField(BibEntry.FIELD_YEAR, "1964")
            .addField(BibEntry.FIELD_PAGES, "53--157"),
        new BibEntry().setText("[27]  M-Y. Wang,  X. Wang and  D. Guo, A level-set method for structural topology optimization. Comput. Methods Appl. Mech. Engrg, 192 (2003) 227–246.")
            .addField(BibEntry.FIELD_AUTHOR, "Wang, M-Y.")
            .addField(BibEntry.FIELD_AUTHOR, "Wang, X.")
            .addField(BibEntry.FIELD_AUTHOR, "Guo, D.")
            .addField(BibEntry.FIELD_TITLE, "A level-set method for structural topology optimization")
            .addField(BibEntry.FIELD_JOURNAL, "Comput. Methods Appl. Mech. Engrg")
            .addField(BibEntry.FIELD_VOLUME, "192")
            .addField(BibEntry.FIELD_YEAR, "2003")
            .addField(BibEntry.FIELD_PAGES, "227--246"),
        new BibEntry().setText("  [8] R. Kobayashi, Einstein-Kähler V metrics on open Satake V -surfaces with isolated quotient singularities, Math. Ann. 272 (1985), 385-398.")
            .addField(BibEntry.FIELD_AUTHOR, "Kobayashi, R.")
            .addField(BibEntry.FIELD_TITLE, "Einstein-Kähler V metrics on open Satake V -surfaces with isolated quotient singularities")
            .addField(BibEntry.FIELD_JOURNAL, "Math. Ann.")
            .addField(BibEntry.FIELD_VOLUME, "272")
            .addField(BibEntry.FIELD_YEAR, "1985")
            .addField(BibEntry.FIELD_PAGES, "385--398"),
        new BibEntry(BibEntry.TYPE_ARTICLE)
            .setText("[4] W. C. Lee, Y. E. Chavez, T. Baker, and B. R. Luce, “Economic burden of heart failure: a summary of recent literature,” Heart and Lung, vol. 33, no. 6, pp. 362–371, 2004.")
            .addField(BibEntry.FIELD_AUTHOR, "Lee, W. C.")
            .addField(BibEntry.FIELD_AUTHOR, "Chavez, Y. E.")
            .addField(BibEntry.FIELD_AUTHOR, "Baker, T.")
            .addField(BibEntry.FIELD_AUTHOR, "Luce, B. R.")
            .addField(BibEntry.FIELD_TITLE, "Economic burden of heart failure: a summary of recent literature")
            .addField(BibEntry.FIELD_JOURNAL, "Heart and Lung")
            .addField(BibEntry.FIELD_VOLUME, "33")
            .addField(BibEntry.FIELD_NUMBER, "6")
            .addField(BibEntry.FIELD_YEAR, "2004")
            .addField(BibEntry.FIELD_PAGES, "362--371"),
        new BibEntry(BibEntry.TYPE_ARTICLE)
            .setText("[6] C. Chan, D. Tang, and A. Jones, “Clinical outcomes of a cardiac rehabilitation and maintenance program for Chinese patients with congestive heart failure,” Disability and Rehabilitation, vol. 30, no. 17, pp. 1245–1253, 2008.")
            .addField(BibEntry.FIELD_AUTHOR, "Chan, C.")
            .addField(BibEntry.FIELD_AUTHOR, "Tang, D.")
            .addField(BibEntry.FIELD_AUTHOR, "Jones, A.")
            .addField(BibEntry.FIELD_TITLE, "Clinical outcomes of a cardiac rehabilitation and maintenance program for Chinese patients with congestive heart failure")
            .addField(BibEntry.FIELD_JOURNAL, "Disability and Rehabilitation")
            .addField(BibEntry.FIELD_VOLUME, "30")
            .addField(BibEntry.FIELD_NUMBER, "17")
            .addField(BibEntry.FIELD_YEAR, "2008")
            .addField(BibEntry.FIELD_PAGES, "1245--1253"),
        new BibEntry(BibEntry.TYPE_ARTICLE)
            .setText("[11] E. Rideout and M. Montemuro, “ Hope, morale and adapta- tion in patients with chronic heart failure,” Journal of Advanced Nursing, vol. 11, no. 4, pp. 429–438, 1986.")
            .addField(BibEntry.FIELD_AUTHOR, "Rideout, E.")
            .addField(BibEntry.FIELD_AUTHOR, "Montemuro, M.")
            .addField(BibEntry.FIELD_TITLE, "Hope, morale and adapta- tion in patients with chronic heart failure")
            .addField(BibEntry.FIELD_JOURNAL, "Journal of Advanced Nursing")
            .addField(BibEntry.FIELD_VOLUME, "11")
            .addField(BibEntry.FIELD_NUMBER, "4")
            .addField(BibEntry.FIELD_YEAR, "1986")
            .addField(BibEntry.FIELD_PAGES, "429--438"),
        new BibEntry(BibEntry.TYPE_ARTICLE)
            .setText("S.E. Fahlman and C. Lebiere. The cascade-correlation learn-ing architecture. In D.S.   Touretzky, editor, Advances in Neural Information Processing Systems, volume 2, pages 524-532, San Mateo, 1990. Morgan Kaufmann.")
            .addField(BibEntry.FIELD_AUTHOR, "Fahlman, S.E.")
            .addField(BibEntry.FIELD_AUTHOR, "Lebiere, C.")
            .addField(BibEntry.FIELD_TITLE, "The cascade-correlation learn-ing architecture")
            .addField(BibEntry.FIELD_JOURNAL, "Advances in Neural Information Processing Systems")
            .addField(BibEntry.FIELD_VOLUME, "2")
            .addField(BibEntry.FIELD_YEAR, "1990")
            .addField(BibEntry.FIELD_PAGES, "524--532"),
        new BibEntry(BibEntry.TYPE_ARTICLE)
            .setText("Sridhar Mahadevan and Jonathan Connell. >Scaling reinforcement learning to robotics by exploiting the subsumption architecture. In Proceedings of the Eighth International Workshop on Machine Learning, 1991.")
            .addField(BibEntry.FIELD_AUTHOR, "Mahadevan, Sridhar")
            .addField(BibEntry.FIELD_AUTHOR, "Connell, Jonathan")
            .addField(BibEntry.FIELD_TITLE, "Scaling reinforcement learning to robotics by exploiting the subsumption architecture")
            .addField(BibEntry.FIELD_JOURNAL, "In Proceedings of the Eighth International Workshop on Machine Learning")
            .addField(BibEntry.FIELD_YEAR, "1991"),
        new BibEntry(BibEntry.TYPE_ARTICLE)
            .setText("S.D. Whitehead and D. H. Ballard. Active perception and reinforcement learning. Neural Computation, 2 (4): 409-419, 1990.")
            .addField(BibEntry.FIELD_AUTHOR, "Whitehead, S.D.")
            .addField(BibEntry.FIELD_AUTHOR, "Ballard, D. H.")
            .addField(BibEntry.FIELD_TITLE, "Active perception and reinforcement learning")
            .addField(BibEntry.FIELD_JOURNAL, "Neural Computation")
            .addField(BibEntry.FIELD_VOLUME, "2")
            .addField(BibEntry.FIELD_NUMBER, "4")
            .addField(BibEntry.FIELD_YEAR, "1990")
            .addField(BibEntry.FIELD_PAGES, "409--419"),
        new BibEntry(BibEntry.TYPE_ARTICLE)
            .setText("Garijo, D., & Gil, Y. (2011). A new approach for publishing workflows: abstractions, standards, and linked data. In Proceedings of the 6th workshop on Workflows in support of large-scale science: 47–56. DOI: 10.1145/2110497.2110504")
            .addField(BibEntry.FIELD_AUTHOR, "Garijo, D.")
            .addField(BibEntry.FIELD_AUTHOR, "Gil, Y.")
            .addField(BibEntry.FIELD_DOI, "10.1145/2110497.2110504")
            .addField(BibEntry.FIELD_JOURNAL, "In Proceedings of the 6th")
            .addField(BibEntry.FIELD_PAGES, "47--56")
            .addField(BibEntry.FIELD_TITLE, "A new approach for publishing workflows: abstractions, standards, and linked data")
            .addField(BibEntry.FIELD_YEAR, "2011"),
    };

    @Test
    public void bibReferenceEmptyParserTest() throws AnalysisException {
        BibEntry be = getParser().parseBibReference("");
        assertEquals("", be.getText());
        assertTrue(be.getFieldKeys().isEmpty());
    }
    
    @Test
    public void bibReferenceParserTest() throws AnalysisException {
        int allFields = 0;
        int parsedFields = 0;
        for (BibEntry entry : entries) {
            BibEntry testEntry = getParser().parseBibReference(entry.getText());
            for (String key : entry.getFieldKeys()) {
                allFields++;
                if (entry.getAllFieldValues(key).equals(testEntry.getAllFieldValues(key))) {
                    parsedFields++;
                }
            }
        }
        
        assertTrue((double) parsedFields / (double) allFields >= getMinPercentage());
    }
    
    protected abstract BibReferenceParser<BibEntry> getParser();
    
    protected abstract double getMinPercentage();    
}
