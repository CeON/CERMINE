package pl.edu.icm.cermine;

import java.io.IOException;
import java.io.InputStream;
import org.jdom.JDOMException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;
import pl.edu.icm.cermine.bibref.model.BibEntry;
import pl.edu.icm.cermine.exception.AnalysisException;

/**
 *
 * @author Dominika Tkaczyk
 */
public class PdfBibEntryReferencesExtractorTest {
    static final private String INPUT_FILE = "/pl/edu/icm/cermine/test2.pdf";
    
    private DocumentReferencesExtractor<BibEntry> extractor;
    
    private double minPercentage = 0.8;

    private BibEntry[] expRefs = {
        new BibEntry()
            .addField(BibEntry.FIELD_AUTHOR, "Braunwald, E.")
            .addField(BibEntry.FIELD_TITLE, "Shattuck lecture: cardiovascular medicine at the turn of the millennium: triumphs, concerns, and opportunities")
            .addField(BibEntry.FIELD_JOURNAL, "New England Journal of Medicine")
            .addField(BibEntry.FIELD_VOLUME, "337")
            .addField(BibEntry.FIELD_NUMBER, "19")
            .addField(BibEntry.FIELD_YEAR, "1997")
            .addField(BibEntry.FIELD_PAGES, "1360--1369"),
        new BibEntry()
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
        new BibEntry()
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
        new BibEntry()
            .addField(BibEntry.FIELD_AUTHOR, "Lee, W. C.")
            .addField(BibEntry.FIELD_AUTHOR, "Chavez, Y. E.")
            .addField(BibEntry.FIELD_AUTHOR, "Baker, T.")
            .addField(BibEntry.FIELD_TITLE, "Economic burden of heart failure: a summary of recent literature")
            .addField(BibEntry.FIELD_JOURNAL, "Heart and Lung")
            .addField(BibEntry.FIELD_VOLUME, "33")
            .addField(BibEntry.FIELD_NUMBER, "6")
            .addField(BibEntry.FIELD_YEAR, "2004")
            .addField(BibEntry.FIELD_PAGES, "362--371"),
        new BibEntry()
            .addField(BibEntry.FIELD_AUTHOR, "Masoudi, F. A.")
            .addField(BibEntry.FIELD_AUTHOR, "Rumsfeld, J. S.")
            .addField(BibEntry.FIELD_AUTHOR, "Havranek E. P.")
            .addField(BibEntry.FIELD_TITLE, "Age, functional capacity, and health-related quality of life in patients with heart failure")
            .addField(BibEntry.FIELD_JOURNAL, "Journal of Cardiac Failure")
            .addField(BibEntry.FIELD_VOLUME, "10")
            .addField(BibEntry.FIELD_NUMBER, "5")
            .addField(BibEntry.FIELD_YEAR, "2004")
            .addField(BibEntry.FIELD_PAGES, "368--373"),
        new BibEntry()
            .addField(BibEntry.FIELD_AUTHOR, "Chan, C.")
            .addField(BibEntry.FIELD_AUTHOR, "Tang, D.")
            .addField(BibEntry.FIELD_AUTHOR, "Jones A.")
            .addField(BibEntry.FIELD_TITLE, "Clinical outcomes of a cardiac rehabilitation and maintenance program for Chinese patients with congestive heart failure")
            .addField(BibEntry.FIELD_JOURNAL, "Disability and Rehabilitation")
            .addField(BibEntry.FIELD_VOLUME, "30")
            .addField(BibEntry.FIELD_NUMBER, "17")
            .addField(BibEntry.FIELD_YEAR, "2008")
            .addField(BibEntry.FIELD_PAGES, "1245--1253"),
        new BibEntry()
            .addField(BibEntry.FIELD_AUTHOR, "Colonna, P.")
            .addField(BibEntry.FIELD_AUTHOR, "Sorino, M.")
            .addField(BibEntry.FIELD_AUTHOR, "D’Agostino, C.")
            .addField(BibEntry.FIELD_TITLE, "Nonpharmacologic care of heart failure: counseling, dietary restriction, rehabilitation, treatment of sleep apnea, and ultrafiltration")
            .addField(BibEntry.FIELD_JOURNAL, "American Journal of Cardiology")
            .addField(BibEntry.FIELD_VOLUME, "91")
            .addField(BibEntry.FIELD_NUMBER, "9")
            .addField(BibEntry.FIELD_YEAR, "2003")
            .addField(BibEntry.FIELD_PAGES, "41--50"),
        new BibEntry()
            .addField(BibEntry.FIELD_TITLE, "Focus Groups: A Guide to Learning the Needs of Those We Serve")
            .addField(BibEntry.FIELD_PUBLISHER, "University of Wisconsin- Madison")
            .addField(BibEntry.FIELD_YEAR, "2007"),
        new BibEntry()
            .addField(BibEntry.FIELD_AUTHOR, "Stemler, S.")
            .addField(BibEntry.FIELD_TITLE, "An overview of content analysis")
            .addField(BibEntry.FIELD_JOURNAL, "Practical Assessment, Research & Evaluation")
            .addField(BibEntry.FIELD_VOLUME, "7")
            .addField(BibEntry.FIELD_NUMBER, "17")
            .addField(BibEntry.FIELD_YEAR, "2001"),
        new BibEntry()
            .addField(BibEntry.FIELD_AUTHOR, "Johansson, P.")
            .addField(BibEntry.FIELD_AUTHOR, "Dahlstr ̈ m, U.")
            .addField(BibEntry.FIELD_AUTHOR, "Brostr ̈ m, A.")
            .addField(BibEntry.FIELD_TITLE, "The measure- ment and prevalence of depression in patients with chronic heart failure")
            .addField(BibEntry.FIELD_JOURNAL, "Progress in Cardiovascular Nursing")
            .addField(BibEntry.FIELD_VOLUME, "21")
            .addField(BibEntry.FIELD_NUMBER, "1")
            .addField(BibEntry.FIELD_YEAR, "2006")
            .addField(BibEntry.FIELD_PAGES, "28--36"),
        new BibEntry()
            .addField(BibEntry.FIELD_AUTHOR, "Rideout, E.")
            .addField(BibEntry.FIELD_AUTHOR, "Montemuro, M.")
            .addField(BibEntry.FIELD_TITLE, "“Hope, morale and adapta- tion in patients with chronic heart failure")
            .addField(BibEntry.FIELD_JOURNAL, "Journal of Advanced Nursing")
            .addField(BibEntry.FIELD_VOLUME, "11")
            .addField(BibEntry.FIELD_NUMBER, "4")
            .addField(BibEntry.FIELD_YEAR, "1986")
            .addField(BibEntry.FIELD_PAGES, "429--438"),
        new BibEntry()
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
        new BibEntry()
            .addField(BibEntry.FIELD_AUTHOR, "Dickstein, K.")
            .addField(BibEntry.FIELD_AUTHOR, "Cohen-Solal, A.")
            .addField(BibEntry.FIELD_AUTHOR, "Filippatos, G.")
            .addField(BibEntry.FIELD_TITLE, "ESC Guidelines for the diagnosis and treatment of acute and chronic heart failure 2008: the Task Force for the diagnosis and treatment of acute and chronic heart failure 2008 of the European Society of Cardiology")
            .addField(BibEntry.FIELD_JOURNAL, "European Heart Journal")
            .addField(BibEntry.FIELD_VOLUME, "29")
            .addField(BibEntry.FIELD_NUMBER, "19")
            .addField(BibEntry.FIELD_YEAR, "2008")
            .addField(BibEntry.FIELD_PAGES, "2388--2442"),
        new BibEntry()
            .addField(BibEntry.FIELD_AUTHOR, "Piepoli, M. F.")
            .addField(BibEntry.FIELD_AUTHOR, "Flather, M.")
            .addField(BibEntry.FIELD_AUTHOR, "Coats, A. J. S.")
            .addField(BibEntry.FIELD_TITLE, "Overview of studies of exercise training in chronic heart failure: the need for a prospective randomized multicentre European trial")
            .addField(BibEntry.FIELD_JOURNAL, "European Heart Journal")
            .addField(BibEntry.FIELD_VOLUME, "19")
            .addField(BibEntry.FIELD_NUMBER, "6")
            .addField(BibEntry.FIELD_YEAR, "1998")
            .addField(BibEntry.FIELD_PAGES, "830--841"),
        new BibEntry()
            .addField(BibEntry.FIELD_AUTHOR, "Corvera-Tindel, T.")
            .addField(BibEntry.FIELD_AUTHOR, "Doering, L. V.")
            .addField(BibEntry.FIELD_AUTHOR, "Gomez, T.")
            .addField(BibEntry.FIELD_AUTHOR, "Dracup, K.")
            .addField(BibEntry.FIELD_TITLE, "Predictors of noncompliance to exercise training in heart failure")
            .addField(BibEntry.FIELD_JOURNAL, "The Journal of Cardiovascular Nursing")
            .addField(BibEntry.FIELD_VOLUME, "19")
            .addField(BibEntry.FIELD_NUMBER, "4")
            .addField(BibEntry.FIELD_YEAR, "2004")
            .addField(BibEntry.FIELD_PAGES, "269--279"),
        new BibEntry()
            .addField(BibEntry.FIELD_AUTHOR, "Evangelista, L. S.")
            .addField(BibEntry.FIELD_AUTHOR, "Dracup, K.")
            .addField(BibEntry.FIELD_AUTHOR, "Erickson, V.")
            .addField(BibEntry.FIELD_AUTHOR, "Mccarthy, W. J.")
            .addField(BibEntry.FIELD_AUTHOR, "Hamilton, M. A.")
            .addField(BibEntry.FIELD_AUTHOR, "Fonarow, G. C.")
            .addField(BibEntry.FIELD_TITLE, "Validity of pedometers for measuring exercise adherence in heart failure patients")
            .addField(BibEntry.FIELD_JOURNAL, "Journal of Cardiac Failure")
            .addField(BibEntry.FIELD_VOLUME, "11")
            .addField(BibEntry.FIELD_NUMBER, "5")
            .addField(BibEntry.FIELD_YEAR, "2005")
            .addField(BibEntry.FIELD_PAGES, "366--371"),
        new BibEntry()
            .addField(BibEntry.FIELD_AUTHOR, "Evangelista, L. S.")
            .addField(BibEntry.FIELD_AUTHOR, "Hamilton, M. A.")
            .addField(BibEntry.FIELD_AUTHOR, "Fonarow, G. C.")
            .addField(BibEntry.FIELD_AUTHOR, "Dracup, K.")
            .addField(BibEntry.FIELD_TITLE, "Is exercise adherence associated with clinical outcomes in patients with advanced heart failure?")
            .addField(BibEntry.FIELD_JOURNAL, "Physician and Sportsmedicine")
            .addField(BibEntry.FIELD_VOLUME, "38")
            .addField(BibEntry.FIELD_NUMBER, "1")
            .addField(BibEntry.FIELD_YEAR, "2010")
            .addField(BibEntry.FIELD_PAGES, "28--36"),
    };
    
    @Before
    public void setUp() throws AnalysisException, IOException {
        extractor = new PdfBibEntryReferencesExtractor();
    }
    
    @Test
    public void metadataExtractionTest() throws AnalysisException, JDOMException, IOException, SAXException {
        InputStream testStream = this.getClass().getResourceAsStream(INPUT_FILE);
        BibEntry[] testRefs;
        try {
            testRefs = extractor.extractReferences(testStream);
        } finally {
            testStream.close();
        }
        
        assertEquals("Expected " + expRefs.length  + " got " + testRefs.length ,testRefs.length, expRefs.length);
        
        int allFields = 0;
        int parsedFields = 0;
        for (int i = 0; i < testRefs.length; i++) {
            BibEntry testRef = testRefs[i];
            BibEntry expRef = expRefs[i];
            for (String key : expRef.getFieldKeys()) {
                for (String value : expRef.getAllFieldValues(key)) {
                    allFields++;
                    if (testRef.getAllFieldValues(key) != null && testRef.getAllFieldValues(key).contains(value)) {
                        parsedFields++;
                    } else {
                        System.out.println("");
                        System.out.println(expRef.getAllFieldValues(key));
                        System.out.println(testRef.getAllFieldValues(key));
                    }
                }
            }
        }

        assertTrue((double) parsedFields / (double) allFields >= minPercentage);
    }
}
