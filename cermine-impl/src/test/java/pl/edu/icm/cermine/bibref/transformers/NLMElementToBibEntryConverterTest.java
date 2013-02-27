package pl.edu.icm.cermine.bibref.transformers;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import pl.edu.icm.cermine.bibref.model.BibEntry;
import pl.edu.icm.cermine.exception.TransformationException;

/**
 *
 * @author Dominika Tkaczyk
 */
public class NLMElementToBibEntryConverterTest {
    
    static final private String TEST_FILE = "/pl/edu/icm/cermine/test2-cont.xml";
    
    private Element[] testRefs;
    
    private BibEntry[] expRefs = {
        new BibEntry(BibEntry.TYPE_ARTICLE)
            .addField(BibEntry.FIELD_AUTHOR, "Braunwald, E.")
            .addField(BibEntry.FIELD_TITLE, "Shattuck lecture: cardiovascular medicine at the turn of the millennium: triumphs")
            .addField(BibEntry.FIELD_JOURNAL, "New England Journal of Medicine")
            .addField(BibEntry.FIELD_VOLUME, "337")
            .addField(BibEntry.FIELD_NUMBER, "19")
            .addField(BibEntry.FIELD_YEAR, "1997")
            .addField(BibEntry.FIELD_PAGES, "1360--1369"),
        new BibEntry(BibEntry.TYPE_ARTICLE)
            .addField(BibEntry.FIELD_AUTHOR, "Campbell, R. L.")
            .addField(BibEntry.FIELD_AUTHOR, "Banner, R.")
            .addField(BibEntry.FIELD_AUTHOR, "Konick-McMahan, J.")
            .addField(BibEntry.FIELD_AUTHOR, "Naylor, M. D.")
            .addField(BibEntry.FIELD_TITLE, "Discharge planning and home follow-up of the elderly patient with heart failure")
            .addField(BibEntry.FIELD_JOURNAL, "The Nursing Clinics of North America")
            .addField(BibEntry.FIELD_VOLUME, "33")
            .addField(BibEntry.FIELD_NUMBER, "3")
            .addField(BibEntry.FIELD_YEAR, "1998")
            .addField(BibEntry.FIELD_PAGES, "497--513"),
        new BibEntry(BibEntry.TYPE_ARTICLE)
            .addField(BibEntry.FIELD_AUTHOR, "Murray, S. A.")
            .addField(BibEntry.FIELD_AUTHOR, "Boyd, K.")
            .addField(BibEntry.FIELD_AUTHOR, "Kendall, M.")
            .addField(BibEntry.FIELD_AUTHOR, "Worth, A.")
            .addField(BibEntry.FIELD_AUTHOR, "Benton, T. F.")
            .addField(BibEntry.FIELD_TITLE, "Dying of lung cancer or cardiac failure: prospective qualitative interview study of patients and their carers in the community")
            .addField(BibEntry.FIELD_JOURNAL, "British Medical Journal")
            .addField(BibEntry.FIELD_VOLUME, "325")
            .addField(BibEntry.FIELD_NUMBER, "7370")
            .addField(BibEntry.FIELD_YEAR, "2002")
            .addField(BibEntry.FIELD_PAGES, "929--932"),
        new BibEntry(BibEntry.TYPE_ARTICLE)
            .addField(BibEntry.FIELD_AUTHOR, "Lee, W. C.")
            .addField(BibEntry.FIELD_AUTHOR, "Chavez, Y. E.")
            .addField(BibEntry.FIELD_AUTHOR, "Baker, T.")
            .addField(BibEntry.FIELD_TITLE, "Economic burden of heart failure: a summary of recent literature")
            .addField(BibEntry.FIELD_PUBLISHER, "Heart and Lung")
            .addField(BibEntry.FIELD_VOLUME, "vol. 33")
            .addField(BibEntry.FIELD_NUMBER, "6")
            .addField(BibEntry.FIELD_YEAR, "2004")
            .addField(BibEntry.FIELD_PAGES, "362--371"),
        new BibEntry(BibEntry.TYPE_ARTICLE)
            .addField(BibEntry.FIELD_AUTHOR, "Masoudi, F. A.")
            .addField(BibEntry.FIELD_AUTHOR, "Rumsfeld, J. S.")
            .addField(BibEntry.FIELD_TITLE, "Age, functional capacity, and health-related quality of life in patients with heart failure")
            .addField(BibEntry.FIELD_JOURNAL, "E. P. Havranek et al.")
            .addField(BibEntry.FIELD_JOURNAL, "Journal of Cardiac Failure")
            .addField(BibEntry.FIELD_VOLUME, "10")
            .addField(BibEntry.FIELD_NUMBER, "5")
            .addField(BibEntry.FIELD_YEAR, "2004")
            .addField(BibEntry.FIELD_PAGES, "368--373"),
        new BibEntry(BibEntry.TYPE_ARTICLE)
            .addField(BibEntry.FIELD_AUTHOR, "Chan, C.")
            .addField(BibEntry.FIELD_AUTHOR, "Tang, D.")
            .addField(BibEntry.FIELD_TITLE, "Clinical outcomes of a cardiac rehabilitation and maintenance program for Chinese patients with congestive heart failure")
            .addField(BibEntry.FIELD_PUBLISHER, "Disability and Rehabilitation")
            .addField(BibEntry.FIELD_VOLUME, "vol. 30")
            .addField(BibEntry.FIELD_NUMBER, "17")
            .addField(BibEntry.FIELD_YEAR, "2008")
            .addField(BibEntry.FIELD_PAGES, "1245--1253"),
        new BibEntry(BibEntry.TYPE_ARTICLE)
            .addField(BibEntry.FIELD_AUTHOR, "Colonna, P.")
            .addField(BibEntry.FIELD_AUTHOR, "Sorino, M.")
            .addField(BibEntry.FIELD_TITLE, "C. D’Agostino et al")
            .addField(BibEntry.FIELD_TITLE, "Nonpharma- cologic care of heart failure: counseling, dietary restriction, rehabilitation, treatment of sleep apnea")
            .addField(BibEntry.FIELD_JOURNAL, "American Journal of Cardiology")
            .addField(BibEntry.FIELD_VOLUME, "91")
            .addField(BibEntry.FIELD_NUMBER, "9")
            .addField(BibEntry.FIELD_NUMBER, "1")
            .addField(BibEntry.FIELD_YEAR, "2003")
            .addField(BibEntry.FIELD_PAGES, "41--50"),
        new BibEntry(BibEntry.TYPE_ARTICLE)
            .addField(BibEntry.FIELD_AUTHOR, "Groups, Focus")
            .addField(BibEntry.FIELD_TITLE, "Oﬃce of Quality Improvement")
            .addField(BibEntry.FIELD_TITLE, "A Guide to Learning the Needs of Those We Serve")
            .addField(BibEntry.FIELD_PUBLISHER, "University of Wisconsin- Madison")
            .addField(BibEntry.FIELD_YEAR, "2007"),
        new BibEntry(BibEntry.TYPE_ARTICLE)
            .addField(BibEntry.FIELD_AUTHOR, "Stemler, S.")
            .addField(BibEntry.FIELD_TITLE, "An overview of content analysis")
            .addField(BibEntry.FIELD_JOURNAL, "Practical Assess- ment, Research & Evaluation")
            .addField(BibEntry.FIELD_VOLUME, "7")
            .addField(BibEntry.FIELD_NUMBER, "17")
            .addField(BibEntry.FIELD_YEAR, "2001"),
        new BibEntry(BibEntry.TYPE_ARTICLE)
            .addField(BibEntry.FIELD_AUTHOR, "Johansson, P.")
            .addField(BibEntry.FIELD_AUTHOR, "Dahlstro, U.")
            .addField(BibEntry.FIELD_TITLE, "The measure- ment and prevalence of depression in patients with chronic heart failure")
            .addField(BibEntry.FIELD_JOURNAL, "Progress in Cardiovascular Nursing")
            .addField(BibEntry.FIELD_VOLUME, "21")
            .addField(BibEntry.FIELD_NUMBER, "1")
            .addField(BibEntry.FIELD_YEAR, "2006")
            .addField(BibEntry.FIELD_PAGES, "28--36"),
        new BibEntry(BibEntry.TYPE_ARTICLE)
            .addField(BibEntry.FIELD_AUTHOR, "Rideout, E.")
            .addField(BibEntry.FIELD_AUTHOR, "Montemuro, M.")
            .addField(BibEntry.FIELD_TITLE, "“Hope, morale and adapta- tion in patients with chronic heart failure")
            .addField(BibEntry.FIELD_JOURNAL, "Journal of Advanced Nursing")
            .addField(BibEntry.FIELD_VOLUME, "11")
            .addField(BibEntry.FIELD_NUMBER, "4")
            .addField(BibEntry.FIELD_YEAR, "1986")
            .addField(BibEntry.FIELD_PAGES, "429--438"),
        new BibEntry(BibEntry.TYPE_ARTICLE)
            .addField(BibEntry.FIELD_AUTHOR, "Leventhal, M. J. E.")
            .addField(BibEntry.FIELD_AUTHOR, "Riegel, B.")
            .addField(BibEntry.FIELD_AUTHOR, "Carlson, B.")
            .addField(BibEntry.FIELD_AUTHOR, "De Geest, S.")
            .addField(BibEntry.FIELD_TITLE, "Negotiating compliance in heart failure: remaining issues and questions")
            .addField(BibEntry.FIELD_JOURNAL, "European Journal of Cardiovascular Nursing")
            .addField(BibEntry.FIELD_VOLUME, "4")
            .addField(BibEntry.FIELD_NUMBER, "4")
            .addField(BibEntry.FIELD_YEAR, "2005")
            .addField(BibEntry.FIELD_PAGES, "298--307"),
        new BibEntry(BibEntry.TYPE_ARTICLE)
            .addField(BibEntry.FIELD_AUTHOR, "Dickstein, K.")
            .addField(BibEntry.FIELD_AUTHOR, "Cohen-Solal, A.")
            .addField(BibEntry.FIELD_JOURNAL, "G. Filippatos et al.")
            .addField(BibEntry.FIELD_TITLE, "ESC Guidelines for the diagnosis and treatment of acute and chronic heart failure 2008: the Task Force for the diagnosis and treatment of acute and chronic heart failure 2008 of the European Society of Cardiology")
            .addField(BibEntry.FIELD_JOURNAL, "European Heart Journal")
            .addField(BibEntry.FIELD_VOLUME, "29")
            .addField(BibEntry.FIELD_NUMBER, "19")
            .addField(BibEntry.FIELD_YEAR, "2008")
            .addField(BibEntry.FIELD_PAGES, "2388--2442"),
        new BibEntry(BibEntry.TYPE_ARTICLE)
            .addField(BibEntry.FIELD_AUTHOR, "Piepoli, M. F.")
            .addField(BibEntry.FIELD_JOURNAL, "M. Flather, and A. J. S. Coats")
            .addField(BibEntry.FIELD_TITLE, "Overview of studies of exercise training in chronic heart failure: the need for a prospective randomized multicentre European trial")
            .addField(BibEntry.FIELD_JOURNAL, "European Heart Journal")
            .addField(BibEntry.FIELD_VOLUME, "19")
            .addField(BibEntry.FIELD_NUMBER, "6")
            .addField(BibEntry.FIELD_YEAR, "1998")
            .addField(BibEntry.FIELD_PAGES, "830--841"),
        new BibEntry(BibEntry.TYPE_ARTICLE)
            .addField(BibEntry.FIELD_AUTHOR, "Corvera-Tindel, T.")
            .addField(BibEntry.FIELD_AUTHOR, "Doering, L. V.")
            .addField(BibEntry.FIELD_AUTHOR, "Gomez, T.")
            .addField(BibEntry.FIELD_TITLE, "Predictors of noncompliance to exercise training in heart failure")
            .addField(BibEntry.FIELD_JOURNAL, "The Journal of Cardiovascular Nursing")
            .addField(BibEntry.FIELD_VOLUME, "19")
            .addField(BibEntry.FIELD_NUMBER, "4")
            .addField(BibEntry.FIELD_YEAR, "2004")
            .addField(BibEntry.FIELD_PAGES, "269--279"),
        new BibEntry(BibEntry.TYPE_ARTICLE)
            .addField(BibEntry.FIELD_AUTHOR, "Evangelista, L. S.")
            .addField(BibEntry.FIELD_AUTHOR, "Dracup, K.")
            .addField(BibEntry.FIELD_AUTHOR, "Erickson, V.")
            .addField(BibEntry.FIELD_AUTHOR, "Mccarthy, W. J.")
            .addField(BibEntry.FIELD_JOURNAL, "M. A. Hamilton, and G. C. Fonarow")
            .addField(BibEntry.FIELD_TITLE, "Validity of pedometers for measuring exercise adherence in heart failure patients")
            .addField(BibEntry.FIELD_JOURNAL, "Journal of Cardiac Failure")
            .addField(BibEntry.FIELD_VOLUME, "11")
            .addField(BibEntry.FIELD_NUMBER, "5")
            .addField(BibEntry.FIELD_YEAR, "2005")
            .addField(BibEntry.FIELD_PAGES, "366--371"),
        new BibEntry(BibEntry.TYPE_ARTICLE)
            .addField(BibEntry.FIELD_AUTHOR, "Evangelista, L. S.")
            .addField(BibEntry.FIELD_AUTHOR, "Hamilton, M. A.")
            .addField(BibEntry.FIELD_AUTHOR, "Fonarow, G. C.")
            .addField(BibEntry.FIELD_TITLE, "Is exercise adherence associated with clinical outcomes in patients with advanced heart failure?” Physician and Sportsmedicine")
            .addField(BibEntry.FIELD_VOLUME, "vol. 38")
            .addField(BibEntry.FIELD_NUMBER, "1")
            .addField(BibEntry.FIELD_YEAR, "2010")
            .addField(BibEntry.FIELD_PAGES, "28--36"),
    };
    
    private NLMElementToBibEntryConverter converter;

    @Before
    public void setUp() throws JDOMException, IOException {
        converter = new NLMElementToBibEntryConverter();
    
        InputStream expStream = this.getClass().getResourceAsStream(TEST_FILE);
        InputStreamReader expReader = new InputStreamReader(expStream);
        SAXBuilder saxBuilder = new SAXBuilder("org.apache.xerces.parsers.SAXParser");
        Document dom;
        try {
            dom = saxBuilder.build(expReader);
        } finally {
            expStream.close();
            expReader.close();
        }
        Element expNLM = dom.getRootElement();
        Element back = expNLM.getChild("back");
        Element refList = back.getChild("ref-list");
        List children = refList.getChildren("ref");
        
        testRefs = new Element[children.size()];
        int i = 0;
        for (Object ref : refList.getChildren("ref")) {
            if (ref instanceof Element) {
                Element mixedCitation = ((Element)ref).getChild("mixed-citation");
                testRefs[i++] = mixedCitation;
            }
        }
    }
    
    @Test
    public void test() throws TransformationException {
        assertEquals(testRefs.length, expRefs.length);
        for (int i = 0; i < testRefs.length; i++) {
            BibEntry converted = converter.convert(testRefs[i]);
            assertEquals(converted.toBibTeX(), expRefs[i].toBibTeX());
        }
    }
}