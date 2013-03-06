package pl.edu.icm.cermine;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.filter.Filter;
import org.jdom.input.SAXBuilder;
import pl.edu.icm.cermine.bibref.model.BibEntry;

/**
 *
 * @author Dominika Tkaczyk
 */
public class StandardDataExamples {

    public static List<Element> getReferencesAsNLMElement() throws IOException, JDOMException {
        String file = "/pl/edu/icm/cermine/references.xml";
        
        InputStream is = StandardDataExamples.class.getResourceAsStream(file);
        InputStreamReader isr = new InputStreamReader(is);
        SAXBuilder saxBuilder = new SAXBuilder("org.apache.xerces.parsers.SAXParser");
        Document dom;
        try {
            dom = saxBuilder.build(isr);
        } finally {
            is.close();
            isr.close();
        }
        Element nlm = dom.getRootElement();
        Iterator<Element> it = nlm.getDescendants(new Filter() {

            @Override
            public boolean matches(Object obj) {
                return obj instanceof Element && ((Element) obj).getName().equals("mixed-citation");
            }
        });
        List<Element> elements = new ArrayList<Element>();
        while (it.hasNext()) {
            elements.add(it.next());
        }

        return elements;
    }
    
    public static List<BibEntry> getReferencesAsBibEntry() {
        BibEntry[] entries = {
            new BibEntry()
                .setText("[6]  W. Hoeffding, Probability inequalities for sums of bounded random variables, J. Amer. Statist. Assoc. 58 (1963) 13-30.")
                .addField(BibEntry.FIELD_AUTHOR, "Hoeffding, W.", 5, 17)
                .addField(BibEntry.FIELD_TITLE, "Probability inequalities for sums of bounded random variables", 19, 80)
                .addField(BibEntry.FIELD_JOURNAL, "J. Amer. Statist. Assoc.", 82, 106)
                .addField(BibEntry.FIELD_VOLUME, "58", 107, 109)
                .addField(BibEntry.FIELD_YEAR, "1963",111, 115)
                .addField(BibEntry.FIELD_PAGES, "13--30", 117, 122),
            new BibEntry().setText("S.J. Bean et  C.P. Tsakas (1980). - Developments in non-parametric density estimation. Inter. Stat. Review, 48, p. 267-287")
                .addField(BibEntry.FIELD_AUTHOR, "Bean, S.J.", 0, 9)
                .addField(BibEntry.FIELD_AUTHOR, "Tsakas, C.P.", 14, 25)
                .addField(BibEntry.FIELD_TITLE, "Developments in non-parametric density estimation", 36, 85)
                .addField(BibEntry.FIELD_JOURNAL, "Inter. Stat. Review", 87, 106)
                .addField(BibEntry.FIELD_VOLUME, "48", 108, 110)
                .addField(BibEntry.FIELD_YEAR, "1980", 27, 31)
                .addField(BibEntry.FIELD_PAGES, "267--287", 115, 122),
            new BibEntry().setText("[27]  M-Y. Wang,  X. Wang and  D. Guo, A level-set method for structural topology optimization. Comput. Methods Appl. Mech. Engrg. 192 (2003) 227–246.")
                .addField(BibEntry.FIELD_AUTHOR, "Wang, M-Y.", 6, 15)
                .addField(BibEntry.FIELD_AUTHOR, "Wang, X.", 18, 25)
                .addField(BibEntry.FIELD_AUTHOR, "Guo, D.", 31, 37)
                .addField(BibEntry.FIELD_TITLE, "A level-set method for structural topology optimization", 39, 94)
                .addField(BibEntry.FIELD_JOURNAL, "Comput. Methods Appl. Mech. Engrg.", 96, 130)
                .addField(BibEntry.FIELD_VOLUME, "192", 131, 134)
                .addField(BibEntry.FIELD_YEAR, "2003", 136, 140)
                .addField(BibEntry.FIELD_PAGES, "227--246", 142, 149),
            new BibEntry().setText("[8] R. Kobayashi, Einstein-Kähler V metrics on open Satake V -surfaces with isolated quotient singularities, Math. Ann. 272 (1985), 385-398.")
                .addField(BibEntry.FIELD_AUTHOR, "Kobayashi, R.", 4, 16)
                .addField(BibEntry.FIELD_TITLE, "Einstein-Kähler V metrics on open Satake V -surfaces with isolated quotient singularities", 18, 107)
                .addField(BibEntry.FIELD_JOURNAL, "Math. Ann.", 109, 119)
                .addField(BibEntry.FIELD_VOLUME, "272", 120, 123)
                .addField(BibEntry.FIELD_YEAR, "1985", 125, 129)
                .addField(BibEntry.FIELD_PAGES, "385--398", 132, 139),
            new BibEntry().setText("[15] T. Corvera-Tindel, L. V. Doering, T. Gomez, and K. Dracup, \"Predictors of noncompliance to exercise training in heart failure,\" The Journal of Cardiovascular Nursing, vol. 19, no. 4, pp. 269–279, 2004.")
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
    
    
}
