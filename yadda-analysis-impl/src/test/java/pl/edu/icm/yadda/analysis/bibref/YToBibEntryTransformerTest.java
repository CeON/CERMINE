//package pl.edu.icm.yadda.analysis.bibref;
//
//import java.io.InputStream;
//import java.util.List;
//import org.apache.commons.lang.StringUtils;
//import pl.edu.icm.yadda.bwmeta.serialization.BwmetaReader120;
//import pl.edu.icm.yadda.bwmeta.serialization.BwmetaReader;
//import org.junit.After;
//import org.junit.AfterClass;
//import org.junit.Before;
//import org.junit.BeforeClass;
//import org.junit.Test;
//import static org.junit.Assert.*;
//import pl.edu.icm.yadda.bwmeta.model.YElement;
//import pl.edu.icm.yadda.common.YaddaException;
//
///**
// *
// * @author estocka
// */
//public class YToBibEntryTransformerTest {
//
//    protected static final String testResourceArticle = "pl/edu/icm/yadda/analysis/bibref/BWMetaSampleSpringer.xml";
//    protected static final String testResourceBook = "pl/edu/icm/yadda/analysis/bibref/BWMetaSampleDMLBook.xml";
//    YElement yElementArticle;
//    YElement yElementBook;
//    BibEntry bibEntryArticle;
//    BibEntry bibEntryBook;
//    YToBibEntryTransformer ytbt;
//
//    public YToBibEntryTransformerTest() {
//    }
//
//    @BeforeClass
//    public static void setUpClass() throws Exception {
//    }
//
//    @AfterClass
//    public static void tearDownClass() throws Exception {
//    }
//
//    @Before
//    public void setUp() throws YaddaException {
//        BwmetaReader r = new BwmetaReader120();
//        InputStream is = this.getClass().getClassLoader().getResourceAsStream(testResourceArticle);
//        yElementArticle = ((List<YElement>) r.read(is, null)).get(0);
//
//        is = this.getClass().getClassLoader().getResourceAsStream(testResourceBook);
//        yElementBook = ((List<YElement>) r.read(is, null)).get(0);
//
//        bibEntryArticle = new BibEntry();
//        bibEntryBook = new BibEntry();
//
//        ytbt = new YToBibEntryTransformer();
//    }
//
//    @After
//    public void tearDown() {
//    }
//
//    @Test
//    public void convertTest() throws YaddaException {
//
//        bibEntryArticle = ytbt.convert(yElementArticle);
//        bibEntryBook = ytbt.convert(yElementBook);
//
//        //article specific
//        System.out.println(bibEntryArticle.getType());
//        assertEquals("article", bibEntryArticle.getType());
//        System.out.println(bibEntryArticle.getFirstFieldValue(BibEntry.FIELD_PAGES));
//        assertEquals("1-27", bibEntryArticle.getFirstFieldValue(BibEntry.FIELD_PAGES));
//        System.out.println(bibEntryArticle.getFirstFieldValue(BibEntry.FIELD_PUBLISHER));
//        assertEquals("Kluwer Academic Publishers", bibEntryArticle.getFirstFieldValue(BibEntry.FIELD_PUBLISHER));
//        System.out.println(bibEntryArticle.getFirstFieldValue(BibEntry.FIELD_JOURNAL));
//        assertEquals("Advances in Computational Mathematics", bibEntryArticle.getFirstFieldValue(BibEntry.FIELD_JOURNAL));
//        System.out.println(bibEntryArticle.getFirstFieldValue(BibEntry.FIELD_VOLUME));
//        assertEquals("10", bibEntryArticle.getFirstFieldValue(BibEntry.FIELD_VOLUME));
//        System.out.println(bibEntryArticle.getFirstFieldValue(BibEntry.FIELD_NUMBER));
//        assertEquals("1", bibEntryArticle.getFirstFieldValue(BibEntry.FIELD_NUMBER));
//        System.out.println(bibEntryArticle.getFirstFieldValue(BibEntry.FIELD_YEAR));
//        assertEquals("1999", bibEntryArticle.getFirstFieldValue(BibEntry.FIELD_YEAR));
//
//        //book specific
//
//        System.out.println(bibEntryBook.getType());
//        assertEquals("book", bibEntryBook.getType());
//
//        System.out.println(bibEntryBook.getFirstFieldValue(BibEntry.FIELD_PUBLISHER));
//        assertEquals("Instytut Matematyczny Polskiej Akademii Nauk",
//                bibEntryBook.getFirstFieldValue(BibEntry.FIELD_PUBLISHER));
//
//        System.out.println(bibEntryBook.getFirstFieldValue(BibEntry.FIELD_SERIES));
//        assertEquals("Monografie Matematyczne", bibEntryBook.getFirstFieldValue(BibEntry.FIELD_SERIES));
//
//
//        //title
//
//        System.out.println(bibEntryArticle.getFirstFieldValue(BibEntry.FIELD_TITLE));
//        assertEquals("Multistep approximation algorithms: Improved convergence rates through "
//                + "postconditioning with smoothing kernels", bibEntryArticle.getFirstFieldValue(BibEntry.FIELD_TITLE));
//
//        //language
//        System.out.println(bibEntryArticle.getFirstFieldValue(BibEntry.FIELD_LANGUAGE));
//        assertEquals("eng", bibEntryArticle.getFirstFieldValue(BibEntry.FIELD_LANGUAGE));
//
//        //copyrights
//        System.out.println(bibEntryArticle.getFirstFieldValue(BibEntry.FIELD_COPYRIGHT));
//        assertEquals("Kluwer Academic Publishers", bibEntryArticle.getFirstFieldValue(BibEntry.FIELD_COPYRIGHT));
//
//    }
//
//    @Test
//    public void convertContributorsTest() {
//        //authors
//        ytbt.convertContributors(yElementArticle, bibEntryArticle);
//        System.out.println(StringUtils.join(bibEntryArticle.getAllFieldValues(BibEntry.FIELD_AUTHOR), " and "));
//        assertEquals("Fasshauer, Gregory E. and Jerome, Joseph W., Jr. and Walaszek, J. K.", StringUtils.join(bibEntryArticle.getAllFieldValues(BibEntry.FIELD_AUTHOR), " and "));
//
//        //publisher with address
//        System.out.println(bibEntryArticle.getFirstFieldValue(BibEntry.FIELD_ADDRESS));
//        assertEquals("Dordrecht", bibEntryArticle.getFirstFieldValue(BibEntry.FIELD_ADDRESS));
//
//        //editors
//        ytbt.convertContributors(yElementBook, bibEntryBook);
//        System.out.println(bibEntryBook.getFirstFieldValue(BibEntry.FIELD_EDITOR));
//        assertEquals("Otto, Edward", bibEntryBook.getFirstFieldValue(BibEntry.FIELD_EDITOR));
//
//
//
//
//    }
//
//    @Test
//    public void convertDescriptionTest() {
//        //abstract
//        ytbt.convertDescription(yElementArticle, bibEntryArticle);
//        System.out.println(bibEntryArticle.getFirstFieldValue(BibEntry.FIELD_ABSTRACT));
//        assertEquals("Abstract We show how certain widely used multistep approximation"
//                + " algorithms can be interpreted as instances of an approximate"
//                + " Newton method. It was shown in an earlier paper by the second"
//                + " author that the convergence rates of approximate Newton methods"
//                + " (in the context of the numerical solution of PDEs) suffer from"
//                + " a “loss of derivatives”, and that the subsequent linear rate"
//                + " of convergence can be improved to be superlinear using an "
//                + "adaptation of Nash–Moser iteration for numerical analysis"
//                + " purposes; the essence of the adaptation being a splitting"
//                + " of the inversion and the smoothing into two separate steps."
//                + " We show how these ideas apply to scattered data approximation "
//                + "as well as the numerical solution of partial differential"
//                + " equations. We investigate the use of several radial kernels"
//                + " for the smoothing operation. In our numerical examples we use"
//                + " radial basis functions also in the inversion"
//                + " step.", bibEntryArticle.getFirstFieldValue(BibEntry.FIELD_ABSTRACT));
//
//        //note
//        ytbt.convertDescription(yElementBook, bibEntryBook);
//        System.out.println(bibEntryBook.getFirstFieldValue(BibEntry.FIELD_NOTE));
//        assertEquals("description", bibEntryBook.getFirstFieldValue(BibEntry.FIELD_NOTE));
//    }
//
//    @Test
//    public void convertKeywordsTest() {
//        ytbt.convertKeywords(yElementArticle, bibEntryArticle);
//        System.out.println(bibEntryArticle.getFirstFieldValue(BibEntry.FIELD_KEYWORDS));
//        assertEquals("capacitated location; Lagrangean heuristic; mixed integer"
//                + " linear programming", bibEntryArticle.getFirstFieldValue(BibEntry.FIELD_KEYWORDS));
//    }
//
//    @Test
//    public void convertIds() {
//        //doi
//        ytbt.convertIds(yElementArticle, bibEntryArticle);
//        System.out.println(bibEntryArticle.getFirstFieldValue(BibEntry.FIELD_DOI));
//        assertEquals("10.1023/A:1018962112170", bibEntryArticle.getFirstFieldValue(BibEntry.FIELD_DOI));
//        //issn, isbn
//    }
//
//    @Test
//    public void convertDate() {
//        //year, month
//        ytbt.convertDate(yElementBook, bibEntryBook);
//        System.out.println(bibEntryBook.getFirstFieldValue(BibEntry.FIELD_YEAR));
//        assertEquals("1950", bibEntryBook.getFirstFieldValue(BibEntry.FIELD_YEAR));
//
//        System.out.println(bibEntryBook.getFirstFieldValue(BibEntry.FIELD_MONTH));
//        assertEquals("10", bibEntryBook.getFirstFieldValue(BibEntry.FIELD_MONTH));
//    }
//
//    @Test
//    public void convertAffiliationsTest() {
//        ytbt.convertAffiliations(yElementArticle, bibEntryArticle);
//        System.out.println(bibEntryArticle.getFirstFieldValue(BibEntry.FIELD_AFFILIATION));
//        assertEquals("Department of Computer Science and Applied Mathematics, Illinois "
//                + "Institute of Technology; Department of Mathematics, Northwestern "
//                + "University", bibEntryArticle.getFirstFieldValue(BibEntry.FIELD_AFFILIATION));
//    }
//}
