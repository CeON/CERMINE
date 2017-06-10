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
import pl.edu.icm.cermine.bibref.model.BibEntryFieldType;
import pl.edu.icm.cermine.bibref.model.BibEntryType;
import pl.edu.icm.cermine.exception.AnalysisException;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public abstract class AbstractBibReferenceParserTest {
    
    private final BibEntry[] entries = {
        new BibEntry().setText("[6]  W. Hoeffding, Probability inequalities for sums of bounded random variables, J. Amer. Statist. Assoc, 58 (1963) 13-30.")
            .addField(BibEntryFieldType.AUTHOR, "Hoeffding, W.")
            .addField(BibEntryFieldType.TITLE, "Probability inequalities for sums of bounded random variables")
            .addField(BibEntryFieldType.JOURNAL, "J. Amer. Statist. Assoc")
            .addField(BibEntryFieldType.VOLUME, "58")
            .addField(BibEntryFieldType.YEAR, "1963")
            .addField(BibEntryFieldType.PAGES, "13--30"),
        new BibEntry().setText(" [3]  Agranovitch (M.S.) and  Vishisk (M.I.). — Elliptic problems with a parameter and parabolic problems of general type, Russian Math. Surveys, 19, 1964, 53-157.")
            .addField(BibEntryFieldType.AUTHOR, "Agranovitch, M.S.")
            .addField(BibEntryFieldType.AUTHOR, "Vishisk, M.I.")
            .addField(BibEntryFieldType.TITLE, "Elliptic problems with a parameter and parabolic problems of general type")
            .addField(BibEntryFieldType.JOURNAL, "Russian Math. Surveys")
            .addField(BibEntryFieldType.VOLUME, "19")
            .addField(BibEntryFieldType.YEAR, "1964")
            .addField(BibEntryFieldType.PAGES, "53--157"),
        new BibEntry().setText("[27]  M-Y. Wang,  X. Wang and  D. Guo, A level-set method for structural topology optimization. Comput. Methods Appl. Mech. Engrg, 192 (2003) 227–246.")
            .addField(BibEntryFieldType.AUTHOR, "Wang, M-Y.")
            .addField(BibEntryFieldType.AUTHOR, "Wang, X.")
            .addField(BibEntryFieldType.AUTHOR, "Guo, D.")
            .addField(BibEntryFieldType.TITLE, "A level-set method for structural topology optimization")
            .addField(BibEntryFieldType.JOURNAL, "Comput. Methods Appl. Mech. Engrg")
            .addField(BibEntryFieldType.VOLUME, "192")
            .addField(BibEntryFieldType.YEAR, "2003")
            .addField(BibEntryFieldType.PAGES, "227--246"),
        new BibEntry().setText("  [8] R. Kobayashi, Einstein-Kähler V metrics on open Satake V -surfaces with isolated quotient singularities, Math. Ann. 272 (1985), 385-398.")
            .addField(BibEntryFieldType.AUTHOR, "Kobayashi, R.")
            .addField(BibEntryFieldType.TITLE, "Einstein-Kähler V metrics on open Satake V -surfaces with isolated quotient singularities")
            .addField(BibEntryFieldType.JOURNAL, "Math. Ann.")
            .addField(BibEntryFieldType.VOLUME, "272")
            .addField(BibEntryFieldType.YEAR, "1985")
            .addField(BibEntryFieldType.PAGES, "385--398"),
        new BibEntry(BibEntryType.ARTICLE)
            .setText("[4] W. C. Lee, Y. E. Chavez, T. Baker, and B. R. Luce, “Economic burden of heart failure: a summary of recent literature,” Heart and Lung, vol. 33, no. 6, pp. 362–371, 2004.")
            .addField(BibEntryFieldType.AUTHOR, "Lee, W. C.")
            .addField(BibEntryFieldType.AUTHOR, "Chavez, Y. E.")
            .addField(BibEntryFieldType.AUTHOR, "Baker, T.")
            .addField(BibEntryFieldType.AUTHOR, "Luce, B. R.")
            .addField(BibEntryFieldType.TITLE, "Economic burden of heart failure: a summary of recent literature")
            .addField(BibEntryFieldType.JOURNAL, "Heart and Lung")
            .addField(BibEntryFieldType.VOLUME, "33")
            .addField(BibEntryFieldType.NUMBER, "6")
            .addField(BibEntryFieldType.YEAR, "2004")
            .addField(BibEntryFieldType.PAGES, "362--371"),
        new BibEntry(BibEntryType.ARTICLE)
            .setText("[6] C. Chan, D. Tang, and A. Jones, “Clinical outcomes of a cardiac rehabilitation and maintenance program for Chinese patients with congestive heart failure,” Disability and Rehabilitation, vol. 30, no. 17, pp. 1245–1253, 2008.")
            .addField(BibEntryFieldType.AUTHOR, "Chan, C.")
            .addField(BibEntryFieldType.AUTHOR, "Tang, D.")
            .addField(BibEntryFieldType.AUTHOR, "Jones, A.")
            .addField(BibEntryFieldType.TITLE, "Clinical outcomes of a cardiac rehabilitation and maintenance program for Chinese patients with congestive heart failure")
            .addField(BibEntryFieldType.JOURNAL, "Disability and Rehabilitation")
            .addField(BibEntryFieldType.VOLUME, "30")
            .addField(BibEntryFieldType.NUMBER, "17")
            .addField(BibEntryFieldType.YEAR, "2008")
            .addField(BibEntryFieldType.PAGES, "1245--1253"),
        new BibEntry(BibEntryType.ARTICLE)
            .setText("[11] E. Rideout and M. Montemuro, “ Hope, morale and adapta- tion in patients with chronic heart failure,” Journal of Advanced Nursing, vol. 11, no. 4, pp. 429–438, 1986.")
            .addField(BibEntryFieldType.AUTHOR, "Rideout, E.")
            .addField(BibEntryFieldType.AUTHOR, "Montemuro, M.")
            .addField(BibEntryFieldType.TITLE, "Hope, morale and adapta- tion in patients with chronic heart failure")
            .addField(BibEntryFieldType.JOURNAL, "Journal of Advanced Nursing")
            .addField(BibEntryFieldType.VOLUME, "11")
            .addField(BibEntryFieldType.NUMBER, "4")
            .addField(BibEntryFieldType.YEAR, "1986")
            .addField(BibEntryFieldType.PAGES, "429--438"),
        new BibEntry(BibEntryType.ARTICLE)
            .setText("S.E. Fahlman and C. Lebiere. The cascade-correlation learn-ing architecture. In D.S.   Touretzky, editor, Advances in Neural Information Processing Systems, volume 2, pages 524-532, San Mateo, 1990. Morgan Kaufmann.")
            .addField(BibEntryFieldType.AUTHOR, "Fahlman, S.E.")
            .addField(BibEntryFieldType.AUTHOR, "Lebiere, C.")
            .addField(BibEntryFieldType.TITLE, "The cascade-correlation learn-ing architecture")
            .addField(BibEntryFieldType.JOURNAL, "Advances in Neural Information Processing Systems")
            .addField(BibEntryFieldType.VOLUME, "2")
            .addField(BibEntryFieldType.YEAR, "1990")
            .addField(BibEntryFieldType.PAGES, "524--532"),
        new BibEntry(BibEntryType.ARTICLE)
            .setText("Sridhar Mahadevan and Jonathan Connell. >Scaling reinforcement learning to robotics by exploiting the subsumption architecture. In Proceedings of the Eighth International Workshop on Machine Learning, 1991.")
            .addField(BibEntryFieldType.AUTHOR, "Mahadevan, Sridhar")
            .addField(BibEntryFieldType.AUTHOR, "Connell, Jonathan")
            .addField(BibEntryFieldType.TITLE, "Scaling reinforcement learning to robotics by exploiting the subsumption architecture")
            .addField(BibEntryFieldType.JOURNAL, "In Proceedings of the Eighth International Workshop on Machine Learning")
            .addField(BibEntryFieldType.YEAR, "1991"),
        new BibEntry(BibEntryType.ARTICLE)
            .setText("S.D. Whitehead and D. H. Ballard. Active perception and reinforcement learning. Neural Computation, 2 (4): 409-419, 1990.")
            .addField(BibEntryFieldType.AUTHOR, "Whitehead, S.D.")
            .addField(BibEntryFieldType.AUTHOR, "Ballard, D. H.")
            .addField(BibEntryFieldType.TITLE, "Active perception and reinforcement learning")
            .addField(BibEntryFieldType.JOURNAL, "Neural Computation")
            .addField(BibEntryFieldType.VOLUME, "2")
            .addField(BibEntryFieldType.NUMBER, "4")
            .addField(BibEntryFieldType.YEAR, "1990")
            .addField(BibEntryFieldType.PAGES, "409--419"),
        new BibEntry(BibEntryType.ARTICLE)
            .setText("Garijo, D., & Gil, Y. (2011). A new approach for publishing workflows: abstractions, standards, and linked data. In Proceedings of the 6th workshop on Workflows in support of large-scale science: 47–56. DOI: 10.1145/2110497.2110504")
            .addField(BibEntryFieldType.AUTHOR, "Garijo, D.")
            .addField(BibEntryFieldType.AUTHOR, "Gil, Y.")
            .addField(BibEntryFieldType.DOI, "10.1145/2110497.2110504")
            .addField(BibEntryFieldType.JOURNAL, "In Proceedings of the 6th")
            .addField(BibEntryFieldType.PAGES, "47--56")
            .addField(BibEntryFieldType.TITLE, "A new approach for publishing workflows: abstractions, standards, and linked data")
            .addField(BibEntryFieldType.YEAR, "2011"),
        new BibEntry(BibEntryType.ARTICLE)
            .setText("Van Heuven WJB, Dijkstra T. Language comprehension in the bilingual brain: fMRI and ERP support for psycholinguistic models. Brain Res Rev. 2010; 64(1):104 – 22. doi: 10.1016/j.brainresrev.2010.03.002 PMID: 20227440")
            .addField(BibEntryFieldType.AUTHOR, "Van Heuven, WJB")
            .addField(BibEntryFieldType.AUTHOR, "Dijkstra, T.")
            .addField(BibEntryFieldType.DOI, "10.1016/j.brainresrev.2010.03")
            .addField(BibEntryFieldType.JOURNAL, "Brain Res Rev")
            .addField(BibEntryFieldType.NUMBER, "1")
            .addField(BibEntryFieldType.PAGES, "104--22")
            .addField(BibEntryFieldType.TITLE, "Language comprehension in the bilingual brain: fMRI and ERP support for psycholinguistic models")
            .addField(BibEntryFieldType.VOLUME, "64")
            .addField(BibEntryFieldType.YEAR, "2010"),
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
            for (BibEntryFieldType key : entry.getFieldKeys()) {
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
