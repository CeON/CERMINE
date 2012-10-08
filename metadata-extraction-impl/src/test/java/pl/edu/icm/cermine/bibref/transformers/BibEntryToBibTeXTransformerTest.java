package pl.edu.icm.cermine.bibref.transformers;

import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.*;
import pl.edu.icm.cermine.bibref.model.BibEntry;
import pl.edu.icm.cermine.exception.TransformationException;

/**
 *
 * @author estocka
 */
public class BibEntryToBibTeXTransformerTest {

    List<BibEntry> bibEntryList;
    BibEntryToBibTeXTransformer btbt;

    public BibEntryToBibTeXTransformerTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        btbt = new BibEntryToBibTeXTransformer();
        BibEntry entry = new BibEntry(BibEntry.TYPE_BOOK).setField(BibEntry.FIELD_YEAR, "1876").setField(BibEntry.FIELD_AUTHOR, "Twain, Mark").setField(BibEntry.FIELD_TITLE, "The Adventures of Tom Sawyer");
        BibEntry entry1 = new BibEntry(BibEntry.TYPE_BOOK).setField(BibEntry.FIELD_YEAR, "1876").setField(BibEntry.FIELD_AUTHOR, "Smith, John").setField(BibEntry.FIELD_TITLE, "Title_including { curly_braces} ");
        bibEntryList = new ArrayList<BibEntry>();
        bibEntryList.add(entry);
        bibEntryList.add(entry1);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void writeTest() throws TransformationException {
        System.out.println(btbt.write(bibEntryList));

        assertEquals("@book{Twain1876,\n"
                + "\tauthor = {Twain, Mark},\n"
                + "\ttitle = {The Adventures of Tom Sawyer},\n"
                + "\tyear = {1876},\n"
                + "}\n\n"
                + "@book{Smith1876,\n"
                + "\tauthor = {Smith, John},\n"
                + "\ttitle = {Title\\_including \\{ curly\\_braces\\} },\n"
                + "\tyear = {1876},\n"
                + "}\n\n", btbt.write(bibEntryList));
    }
}
