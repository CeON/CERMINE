package pl.edu.icm.yadda.analysis.bibref;

import java.util.ArrayList;
import java.util.List;
import pl.edu.icm.yadda.bwmeta.model.YAffiliation;
import pl.edu.icm.yadda.bwmeta.model.YDate;
import pl.edu.icm.yadda.bwmeta.model.YElement;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import pl.edu.icm.yadda.bwmeta.model.YConstants;
import pl.edu.icm.yadda.bwmeta.model.YContributor;
import pl.edu.icm.yadda.bwmeta.model.YName;
import pl.edu.icm.yadda.bwmeta.model.YStructure;
import pl.edu.icm.yadda.bwmeta.model.YTagList;
import pl.edu.icm.yadda.metadata.transformers.TransformationException;

/**
 *
 * @author estocka
 */
public class BibEntryToYTransformerTest {

    BibEntry bibEntryArticle;
    BibEntry bibEntryBook;
    YElement yElementArticle;
    YElement yElementBook;
    BibEntryToYTransformer bety;

    public BibEntryToYTransformerTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        bety = new BibEntryToYTransformer();
        bibEntryArticle = new BibEntry();
        bibEntryArticle.setType(BibEntry.TYPE_ARTICLE);
        bibEntryArticle.setField(BibEntry.FIELD_PUBLISHER, "Kluwer Academic Publishers");
        bibEntryArticle.setField(BibEntry.FIELD_ADDRESS, "Dordrecht");
        bibEntryArticle.setField(BibEntry.FIELD_JOURNAL, "Advances in Computational Mathematics");
        bibEntryArticle.setField(BibEntry.FIELD_VOLUME, "10");
        bibEntryArticle.setField(BibEntry.FIELD_NUMBER, "1");
        bibEntryArticle.setField(BibEntry.FIELD_YEAR, "1999");
        bibEntryArticle.setField(BibEntry.FIELD_PAGES, "1-27");
        bibEntryArticle.setField(BibEntry.FIELD_TITLE, "Multistep approximation algorithms: Improved convergence rates through "
                + "postconditioning with smoothing kernels");
        bibEntryArticle.setField(BibEntry.FIELD_LANGUAGE, "eng");
        bibEntryArticle.setField(BibEntry.FIELD_COPYRIGHT, "Kluwer Academic Publishers");
        bibEntryArticle.addField(BibEntry.FIELD_AUTHOR, "Fasshauer, Gregory E.");
        bibEntryArticle.addField(BibEntry.FIELD_AUTHOR, "Jerome, Joseph W., Jr.");
        bibEntryArticle.addField(BibEntry.FIELD_AUTHOR, "Walaszek, J. K.");
        bibEntryArticle.setField(BibEntry.FIELD_AFFILIATION, "Department of Computer Science and Applied Mathematics, Illinois"
                + " Institute of Technology; Department of Mathematics, Northwestern University");
        bibEntryArticle.setField(BibEntry.FIELD_ABSTRACT, "Abstract We show how certain widely used multistep"
                + " approximation algorithms can be interpreted as instances"
                + " of an approximate Newton method. It was shown in an earlier"
                + " paper by the second author that the convergence rates of"
                + " approximate Newton methods (in the context of the numerical"
                + " solution of PDEs) suffer from a â€śloss of derivativesâ€ť,"
                + " and that the subsequent linear rate of convergence can be"
                + " improved to be superlinear using an adaptation of Nashâ€“Moser"
                + " iteration for numerical analysis purposes; the essence of "
                + "the adaptation being a splitting of the inversion and the"
                + " smoothing into two separate steps. We show how these ideas"
                + " apply to scattered data approximation as well as the numerical"
                + " solution of partial differential equations. We investigate the"
                + " use of several radial kernels for the smoothing operation. "
                + "In our numerical examples we use radial basis functions also"
                + " in the inversion step.");
        bibEntryArticle.setField(BibEntry.FIELD_KEYWORDS, "capacitated location; Lagrangean heuristic; mixed integer"
                + " linear programming");
        bibEntryArticle.setField(BibEntry.FIELD_DOI, "10.1023/A:1018962112170");


        bibEntryBook = new BibEntry();
        bibEntryBook.setType(BibEntry.TYPE_BOOK);
        bibEntryBook.setField(BibEntry.FIELD_SERIES, "Monografie Matematyczne");
        bibEntryBook.setField(BibEntry.FIELD_PUBLISHER, "Instytut Matematyczny Polskiej Akademii Nauk");
        bibEntryBook.setField(BibEntry.FIELD_EDITOR, "Otto, Edward");
        bibEntryBook.setField(BibEntry.FIELD_NOTE, "description");
        bibEntryBook.setField(BibEntry.FIELD_YEAR, "1950");
        bibEntryBook.setField(BibEntry.FIELD_MONTH, "10");

        yElementArticle = new YElement();
        yElementBook = new YElement();
    }

    @Test
    public void testConvert() throws TransformationException {
        yElementArticle = (YElement) bety.convert(bibEntryArticle);
        YStructure yStructure = yElementArticle.getStructure(YConstants.EXT_HIERARCHY_JOURNAL);

        System.out.println(yStructure.getCurrent().getPosition());
        assertEquals("1-27", yStructure.getCurrent().getPosition());

        System.out.println(yStructure.getCurrent().getLevel());
        assertEquals(YConstants.EXT_LEVEL_JOURNAL_ARTICLE, yStructure.getCurrent().getLevel());

        System.out.println(yStructure.getAncestor(YConstants.EXT_LEVEL_JOURNAL_PUBLISHER).getOneName().getText());
        assertEquals("Kluwer Academic Publishers", yStructure.getAncestor(YConstants.EXT_LEVEL_JOURNAL_PUBLISHER).getOneName().getText());

        System.out.println(yStructure.getAncestor(YConstants.EXT_LEVEL_JOURNAL_JOURNAL).getOneName().getText());
        assertEquals("Advances in Computational Mathematics", yStructure.getAncestor(YConstants.EXT_LEVEL_JOURNAL_JOURNAL).getOneName().getText());

        System.out.println(yStructure.getAncestor(YConstants.EXT_LEVEL_JOURNAL_VOLUME).getOneName().getText());
        assertEquals("10", yStructure.getAncestor(YConstants.EXT_LEVEL_JOURNAL_VOLUME).getOneName().getText());

        System.out.println(yStructure.getAncestor(YConstants.EXT_LEVEL_JOURNAL_ISSUE).getOneName().getText());
        assertEquals("1", yStructure.getAncestor(YConstants.EXT_LEVEL_JOURNAL_ISSUE).getOneName().getText());

        System.out.println(yStructure.getAncestor(YConstants.EXT_LEVEL_JOURNAL_YEAR).getOneName().getText());
        assertEquals("1999", yStructure.getAncestor(YConstants.EXT_LEVEL_JOURNAL_YEAR).getOneName().getText());



        yElementBook = (YElement) bety.convert(bibEntryBook);

        yStructure = yElementBook.getStructure(YConstants.EXT_HIERARCHY_BOOK);

        System.out.println(yStructure.getCurrent().getLevel());
        assertEquals(YConstants.EXT_LEVEL_BOOK_BOOK, yStructure.getCurrent().getLevel());

        System.out.println(yStructure.getAncestor(YConstants.EXT_LEVEL_BOOK_PUBLISHER).getOneName().getText());
        assertEquals("Instytut Matematyczny Polskiej Akademii Nauk", yStructure.getAncestor(YConstants.EXT_LEVEL_BOOK_PUBLISHER).getOneName().getText());

        System.out.println(yStructure.getAncestor(YConstants.EXT_LEVEL_BOOK_SERIES).getOneName().getText());
        assertEquals("Monografie Matematyczne", yStructure.getAncestor(YConstants.EXT_LEVEL_BOOK_SERIES).getOneName().getText());

        System.out.println(yElementArticle.getOneName().getText());
        assertEquals("Multistep approximation algorithms: Improved convergence rates through"
                + " postconditioning with smoothing kernels", yElementArticle.getOneName().getText());

        System.out.println(yElementArticle.getOneLanguage().getBibliographicCode());
        assertEquals("eng", yElementArticle.getOneLanguage().getBibliographicCode());

        System.out.println(yElementArticle.getOneAttributeSimpleValue(YConstants.AT_COPYRIGHT_HOLDER));
        assertEquals("Kluwer Academic Publishers", yElementArticle.getOneAttributeSimpleValue(YConstants.AT_COPYRIGHT_HOLDER));

    }

    @Test
    public void convertContributorsTest() throws TransformationException {

        bety.convertContributors(bibEntryArticle, yElementArticle);



        List<YContributor> yContributors = yElementArticle.getContributors();
        //publisher and address

        String address = null;
        for (YContributor yContributor : yContributors) {

            if (yContributor.getRole().equals(YConstants.CR_PUBLISHER)) {

                address = yContributor.getOneAttribute(YConstants.AT_ADDRESS_CITY).getValue();

            }

        }
        assertEquals("Dordrecht", address);
        //authors
        List<YContributor> yAuthorList = new ArrayList<YContributor>();

        for (YContributor yContributor : yContributors) {
            if (yContributor.getRole().equals(YConstants.CR_AUTHOR)) {
                yAuthorList.add(yContributor);
            }
        }

        String nameList = "";
        for (YContributor yContributor : yAuthorList) {

            List<YName> names = yContributor.getNames();
            for (YName name : names) {
                nameList = nameList + " " + name.getType() + ": " + name.getText();
            }

        }
        System.out.println(nameList);
        assertEquals(" canonical: Fasshauer, Gregory E. surname: Fasshauer"
                + " forenames: Gregory E. canonical: Jerome, Joseph W., Jr."
                + " surname: Jerome forenames: Joseph W. suffix: Jr. canonical: "
                + "Walaszek, J. K. surname: Walaszek forenames: J. K.", nameList);

        bety.convertContributors(bibEntryBook, yElementBook);

        //editors

        List<YContributor> yEditorList = yElementBook.getContributors();
        nameList = "";
        for (YContributor yContributor : yEditorList) {

            List<YName> names = yContributor.getNames();
            for (YName name : names) {
                nameList = nameList + " " + name.getType() + ": " + name.getText();
            }
        }
        System.out.println(nameList);
        assertEquals(" canonical: Otto, Edward surname: Otto forenames: Edward", nameList);
    }

    @Test
    public void convertDescriptionTest() {
        bety.convertDescription(bibEntryArticle, yElementArticle);
        System.out.println(yElementArticle.getOneDescription(YConstants.DS_ABSTRACT).getText());
        assertEquals("Abstract We show how certain widely used multistep"
                + " approximation algorithms can be interpreted as instances"
                + " of an approximate Newton method. It was shown in an earlier"
                + " paper by the second author that the convergence rates of"
                + " approximate Newton methods (in the context of the numerical"
                + " solution of PDEs) suffer from a â€śloss of derivativesâ€ť,"
                + " and that the subsequent linear rate of convergence can be"
                + " improved to be superlinear using an adaptation of Nashâ€“Moser"
                + " iteration for numerical analysis purposes; the essence of "
                + "the adaptation being a splitting of the inversion and the"
                + " smoothing into two separate steps. We show how these ideas"
                + " apply to scattered data approximation as well as the numerical"
                + " solution of partial differential equations. We investigate the"
                + " use of several radial kernels for the smoothing operation. "
                + "In our numerical examples we use radial basis functions also"
                + " in the inversion step.", yElementArticle.getOneDescription(YConstants.DS_ABSTRACT).getText());

        bety.convertDescription(bibEntryBook, yElementBook);
        System.out.println(yElementBook.getOneDescription(YConstants.DS_NOTE).getText());
        assertEquals("description", yElementBook.getOneDescription(YConstants.DS_NOTE).getText());
    }

    @Test
    public void convertKeywordsTest() {
        bety.convertKeywords(bibEntryArticle, yElementArticle);
        YTagList tagList = yElementArticle.getTagList(YConstants.TG_KEYWORD);
        List<String> values = tagList.getValues();
        String keywordList = "";
        for (String value : values) {
            keywordList = keywordList + " " + value;
        }
        assertEquals(" capacitated location Lagrangean "
                + "heuristic mixed integer linear programming", keywordList);
    }

    @Test
    public void convertIdsTest() {
        bety.convertIds(bibEntryArticle, yElementArticle);
        String doi = yElementArticle.getId(YConstants.EXT_SCHEME_DOI);
        System.out.println(doi);
        assertEquals("10.1023/A:1018962112170", doi);
    }

    @Test
    public void convertDateTest() {
        bety.convertDate(bibEntryBook, yElementBook);
        YDate date = yElementBook.getDate(YConstants.DT_PUBLISHED);
        assertEquals("1950 10", date.getYear() + " " + date.getMonth());
    }

    @Test
    public void convertAffiliations() {
        bety.convertAffiliations(bibEntryArticle, yElementArticle);
        List<YAffiliation> affiliations = yElementArticle.getAffiliations();
        StringBuilder sb = new StringBuilder();
        for (YAffiliation affiliation : affiliations) {
            sb.append("\n");
            sb.append(affiliation.getSimpleText());
        }
        assertEquals("\nDepartment of Computer Science and Applied Mathematics,"
                + " Illinois Institute of Technology\nDepartment of Mathematics,"
                + " Northwestern University", sb.toString());
    }
}
