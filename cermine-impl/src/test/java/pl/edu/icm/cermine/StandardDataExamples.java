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

package pl.edu.icm.cermine;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import pl.edu.icm.cermine.bibref.model.BibEntry;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class StandardDataExamples {

    public static List<Element> getReferencesAsNLMElement() throws IOException, JDOMException {
        String[] references = {
            "<mixed-citation>[6] <string-name><given-names>W.</given-names> <surname>Hoeffding</surname></string-name>, <article-title>Probability inequalities for sums of bounded random variables</article-title>, <source>J. Amer. Statist. Assoc.</source> <volume>58</volume> (<year>1963</year>) <fpage>13</fpage>-<lpage>30</lpage>.</mixed-citation>",
            "<mixed-citation><string-name><given-names>S.J.</given-names> <surname>Bean</surname></string-name> et <string-name><given-names>C.P.</given-names> <surname>Tsakas</surname></string-name> (<year>1980</year>). - <article-title>Developments in non-parametric density estimation</article-title>. <source>Inter. Stat. Review</source>, <volume>48</volume>, p. <fpage>267</fpage>-<lpage>287</lpage></mixed-citation>",
            "<mixed-citation>[27] <string-name><given-names>M-Y.</given-names> <surname>Wang</surname></string-name>, <string-name><given-names>X.</given-names> <surname>Wang</surname></string-name> and <string-name><given-names>D.</given-names> <surname>Guo</surname></string-name>, <article-title>A level-set method for structural topology optimization</article-title>. <source>Comput. Methods Appl. Mech. Engrg.</source> <volume>192</volume> (<year>2003</year>) <fpage>227</fpage>–<lpage>246</lpage>.</mixed-citation>",
            "<mixed-citation>[8] <string-name><given-names>R.</given-names> <surname>Kobayashi</surname></string-name>, <article-title>Einstein-Kähler V metrics on open Satake V -surfaces with isolated quotient singularities</article-title>, <source>Math. Ann.</source> <volume>272</volume> (<year>1985</year>), <fpage>385</fpage>-<lpage>398</lpage>.</mixed-citation>",
            "<mixed-citation>[15] <string-name><given-names>T.</given-names> <surname>Corvera-Tindel</surname></string-name>, <string-name><given-names>L. V.</given-names> <surname>Doering</surname></string-name>, <string-name><given-names>T.</given-names> <surname>Gomez</surname></string-name>, and <string-name><given-names>K.</given-names> <surname>Dracup</surname></string-name>, \"<article-title>Predictors of noncompliance to exercise training in heart failure</article-title>,\" <source>The Journal of Cardiovascular Nursing</source>, vol. <volume>19</volume>, no. <issue>4</issue>, pp. <fpage>269</fpage>–<lpage>279</lpage>, <year>2004</year>.</mixed-citation>"
        };
        
        SAXBuilder saxBuilder = new SAXBuilder("org.apache.xerces.parsers.SAXParser");
        List<Element> elements = new ArrayList<Element>();
        for (String reference : references) {
            StringReader sr = new StringReader(reference);
            Document dom = saxBuilder.build(sr);
            Element nlm = dom.getRootElement();
            elements.add(nlm);
        }

        return elements;
    }
    
    public static List<BibEntry> getReferencesAsBibEntry() {
        BibEntry[] entries = {
            new BibEntry(BibEntry.TYPE_ARTICLE)
                .setText("[6] W. Hoeffding, Probability inequalities for sums of bounded random variables, J. Amer. Statist. Assoc. 58 (1963) 13-30.")
                .addField(BibEntry.FIELD_AUTHOR, "Hoeffding, W.", 4, 16)
                .addField(BibEntry.FIELD_TITLE, "Probability inequalities for sums of bounded random variables", 18, 79)
                .addField(BibEntry.FIELD_JOURNAL, "J. Amer. Statist. Assoc.", 81, 105)
                .addField(BibEntry.FIELD_VOLUME, "58", 106, 108)
                .addField(BibEntry.FIELD_YEAR, "1963", 110, 114)
                .addField(BibEntry.FIELD_PAGES, "13--30", 116, 121),
            new BibEntry(BibEntry.TYPE_ARTICLE)
                .setText("S.J. Bean et C.P. Tsakas (1980). - Developments in non-parametric density estimation. Inter. Stat. Review, 48, p. 267-287")
                .addField(BibEntry.FIELD_AUTHOR, "Bean, S.J.", 0, 9)
                .addField(BibEntry.FIELD_AUTHOR, "Tsakas, C.P.", 13, 24)
                .addField(BibEntry.FIELD_TITLE, "Developments in non-parametric density estimation", 35, 84)
                .addField(BibEntry.FIELD_JOURNAL, "Inter. Stat. Review", 86, 105)
                .addField(BibEntry.FIELD_VOLUME, "48", 107, 109)
                .addField(BibEntry.FIELD_YEAR, "1980", 26, 30)
                .addField(BibEntry.FIELD_PAGES, "267--287", 114, 121),
            new BibEntry(BibEntry.TYPE_ARTICLE)
                .setText("[27] M-Y. Wang, X. Wang and D. Guo, A level-set method for structural topology optimization. Comput. Methods Appl. Mech. Engrg. 192 (2003) 227–246.")
                .addField(BibEntry.FIELD_AUTHOR, "Wang, M-Y.", 5, 14)
                .addField(BibEntry.FIELD_AUTHOR, "Wang, X.", 16, 23)
                .addField(BibEntry.FIELD_AUTHOR, "Guo, D.", 28, 34)
                .addField(BibEntry.FIELD_TITLE, "A level-set method for structural topology optimization", 36, 91)
                .addField(BibEntry.FIELD_JOURNAL, "Comput. Methods Appl. Mech. Engrg.", 93, 127)
                .addField(BibEntry.FIELD_VOLUME, "192", 128, 131)
                .addField(BibEntry.FIELD_YEAR, "2003", 133, 137)
                .addField(BibEntry.FIELD_PAGES, "227--246", 139, 146),
            new BibEntry(BibEntry.TYPE_ARTICLE)
                .setText("[8] R. Kobayashi, Einstein-Kähler V metrics on open Satake V -surfaces with isolated quotient singularities, Math. Ann. 272 (1985), 385-398.")
                .addField(BibEntry.FIELD_AUTHOR, "Kobayashi, R.", 4, 16)
                .addField(BibEntry.FIELD_TITLE, "Einstein-Kähler V metrics on open Satake V -surfaces with isolated quotient singularities", 18, 107)
                .addField(BibEntry.FIELD_JOURNAL, "Math. Ann.", 109, 119)
                .addField(BibEntry.FIELD_VOLUME, "272", 120, 123)
                .addField(BibEntry.FIELD_YEAR, "1985", 125, 129)
                .addField(BibEntry.FIELD_PAGES, "385--398", 132, 139),
            new BibEntry(BibEntry.TYPE_ARTICLE)
                .setText("[15] T. Corvera-Tindel, L. V. Doering, T. Gomez, and K. Dracup, \"Predictors of noncompliance to exercise training in heart failure,\" The Journal of Cardiovascular Nursing, vol. 19, no. 4, pp. 269–279, 2004.")
                .addField(BibEntry.FIELD_AUTHOR, "Corvera-Tindel, T.", 5, 22)
                .addField(BibEntry.FIELD_AUTHOR, "Doering, L. V.", 24, 37)
                .addField(BibEntry.FIELD_AUTHOR, "Gomez, T.", 39, 47)
                .addField(BibEntry.FIELD_AUTHOR, "Dracup, K.", 53, 62)
                .addField(BibEntry.FIELD_TITLE, "Predictors of noncompliance to exercise training in heart failure", 65, 130)
                .addField(BibEntry.FIELD_JOURNAL, "The Journal of Cardiovascular Nursing", 133, 170)
                .addField(BibEntry.FIELD_VOLUME, "19", 177, 179)
                .addField(BibEntry.FIELD_NUMBER, "4", 185, 186)
                .addField(BibEntry.FIELD_PAGES, "269--279", 192, 199)
                .addField(BibEntry.FIELD_YEAR, "2004", 201, 205)
        };
        return Arrays.asList(entries);
    }
    
    public static List<String> getReferencesAsBibTeX() {
        String[] entries = {
            "@article{Hoeffding1963,\n"
                + "\tauthor = {Hoeffding, W.},\n"
                + "\tjournal = {J. Amer. Statist. Assoc.},\n"
                + "\tpages = {13--30},\n"
                + "\ttitle = {Probability inequalities for sums of bounded random variables},\n"
                + "\tvolume = {58},\n"
                + "\tyear = {1963},\n"
                + "}",
            "@article{Bean1980,\n"
                + "\tauthor = {Bean, S.J., Tsakas, C.P.},\n"
                + "\tjournal = {Inter. Stat. Review},\n"
                + "\tpages = {267--287},\n"
                + "\ttitle = {Developments in non-parametric density estimation},\n"
                + "\tvolume = {48},\n"
                + "\tyear = {1980},\n"
                + "}",
            "@article{Wang2003,\n"
                + "\tauthor = {Wang, M-Y., Wang, X., Guo, D.},\n"
                + "\tjournal = {Comput. Methods Appl. Mech. Engrg.},\n"
                + "\tpages = {227--246},\n"
                + "\ttitle = {A level-set method for structural topology optimization},\n"
                + "\tvolume = {192},\n"
                + "\tyear = {2003},\n"
                + "}",
            "@article{Kobayashi1985,\n"
                + "\tauthor = {Kobayashi, R.},\n"
                + "\tjournal = {Math. Ann.},\n"
                + "\tpages = {385--398},\n"
                + "\ttitle = {Einstein-Kähler V metrics on open Satake V -surfaces with isolated quotient singularities},\n"
                + "\tvolume = {272},\n"
                + "\tyear = {1985},\n"
                + "}",
            "@article{Corvera2004,\n"
                + "\tauthor = {Corvera-Tindel, T., Doering, L. V., Gomez, T., Dracup, K.},\n"
                + "\tjournal = {The Journal of Cardiovascular Nursing},\n"
                + "\tnumber = {4},\n"
                + "\tpages = {269--279},\n"
                + "\ttitle = {Predictors of noncompliance to exercise training in heart failure},\n"
                + "\tvolume = {19},\n"
                + "\tyear = {2004},\n"
                +"}"
        };
        return Arrays.asList(entries);
    }
    
}
