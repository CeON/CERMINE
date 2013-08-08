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

package pl.edu.icm.cermine.bibref;

import static org.junit.Assert.assertTrue;
import org.junit.Test;
import pl.edu.icm.cermine.bibref.model.BibEntry;
import pl.edu.icm.cermine.exception.AnalysisException;

/**
 *
 * @author Dominika Tkaczyk
 */
public abstract class AbstractBibReferenceParserTest {
    
    private BibEntry[] entries = {
        new BibEntry().setText("[6]  W. Hoeffding, Probability inequalities for sums of bounded random variables, J. Amer. Statist. Assoc. 58 (1963) 13-30.")
            .addField(BibEntry.FIELD_AUTHOR, "Hoeffding, W.")
            .addField(BibEntry.FIELD_TITLE, "Probability inequalities for sums of bounded random variables")
            .addField(BibEntry.FIELD_JOURNAL, "J. Amer. Statist. Assoc.")
            .addField(BibEntry.FIELD_VOLUME, "58")
            .addField(BibEntry.FIELD_YEAR, "1963")
            .addField(BibEntry.FIELD_PAGES, "13--30"),
        new BibEntry().setText("S.J. Bean et  C.P. Tsakas (1980). - Developments in non-parametric density estimation. Inter. Stat. Review, 48, p. 267-287")
            .addField(BibEntry.FIELD_AUTHOR, "Bean, S.J.")
            .addField(BibEntry.FIELD_AUTHOR, "Tsakas, C.P.")
            .addField(BibEntry.FIELD_TITLE, "Developments in non-parametric density estimation")
            .addField(BibEntry.FIELD_JOURNAL, "Inter. Stat. Review")
            .addField(BibEntry.FIELD_VOLUME, "48")
            .addField(BibEntry.FIELD_YEAR, "1980")
            .addField(BibEntry.FIELD_PAGES, "267--287"),
        new BibEntry().setText(" [3]  Agranovitch (M.S.) and  Vishisk (M.I.). — Elliptic problems with a parameter and parabolic problems of general type, Russian Math. Surveys, 19, 1964, 53-157.")
            .addField(BibEntry.FIELD_AUTHOR, "Agranovitch, M.S.")
            .addField(BibEntry.FIELD_AUTHOR, "Vishisk, M.I.")
            .addField(BibEntry.FIELD_TITLE, "Elliptic problems with a parameter and parabolic problems of general type")
            .addField(BibEntry.FIELD_JOURNAL, "Russian Math. Surveys")
            .addField(BibEntry.FIELD_VOLUME, "19")
            .addField(BibEntry.FIELD_YEAR, "1964")
            .addField(BibEntry.FIELD_PAGES, "53--157"),
        new BibEntry().setText("  [27]  M-Y. Wang,  X. Wang and  D. Guo, A level-set method for structural topology optimization. Comput. Methods Appl. Mech. Engrg. 192 (2003) 227–246.")
            .addField(BibEntry.FIELD_AUTHOR, "Wang, M-Y.")
            .addField(BibEntry.FIELD_AUTHOR, "Wang, X.")
            .addField(BibEntry.FIELD_AUTHOR, "Guo, D.")
            .addField(BibEntry.FIELD_TITLE, "A level-set method for structural topology optimization")
            .addField(BibEntry.FIELD_JOURNAL, "Comput. Methods Appl. Mech. Engrg.")
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
    };

    @Test
    public void hmmBibReferenceParserTest() throws AnalysisException {
        int allFields = 0;
        int parsedFields = 0;
        for (BibEntry entry : entries) {
            BibEntry testEntry = getParser().parseBibReference(entry.getText());
            for (String key : entry.getFieldKeys()) {
                allFields++;
                if (entry.getAllFieldValues(key).equals(testEntry.getAllFieldValues(key))) {
                    parsedFields++;
                } else {
                    System.out.println(entry.getAllFieldValues(key));
                    System.out.println(testEntry.getAllFieldValues(key));
                }
            }
        }
        
        assertTrue((double) parsedFields / (double) allFields >= getMinPercentage());
    }
    
    protected abstract BibReferenceParser<BibEntry> getParser();
    
    protected abstract double getMinPercentage();    
}
