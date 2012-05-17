package pl.edu.icm.yadda.analysis.bibref;


import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import pl.edu.icm.yadda.analysis.bibref.BibReferenceGenerator.Person;

/**
 *
 * @author estocka
 */
public class BibReferenceGeneratorTest {

    BibReferenceGenerator brg;

    BibEntry bibEntryArticle;
    BibEntry bibEntryBook;
    BibEntry entryJournal1;
    BibEntry entryJournal2;
    BibEntry entryJournal3;
    BibEntry entryJournalWithOneAuthor;
    BibEntry entryJournalWithMultipleAuthor;
    BibEntry entryJournalWithoutPages;
    BibEntry entryJournalWithoutPagesAndVolume;
    BibEntry entryJournalWithoutVolumeAndYear;
    BibEntry entryJournalWithoutNumber;
    BibEntry entryJournalWithoutVolume;
    BibEntry entryJournalWithoutYear;
    BibEntry entryJournalWithoutJournal;
    BibEntry entryJournalWithoutTitle;
    BibEntry entryJournalWithoutAuthors;
    BibEntry entryJournalWithoutPVJY;
    BibEntry entryBook1;
    BibEntry entryBook2;
    BibEntry entryBookWithoutAddress;
    BibEntry entryBookWithoutPublisher;
    BibEntry entryBookWithoutYear;
    BibEntry entryBookWithoutPY;
    BibEntry entryBookWithEditor;
    BibEntry entryBookWithEdition;
    BibEntry entryInProceedings;
    BibEntry entryInProceedings2;
    BibEntry entryInProceedingsWithoutPPA;
    BibEntry entryInProceedingsWithoutPA;
    BibEntry entryInProceedingsWithoutP;

    public BibReferenceGeneratorTest() {
    }
    @Before
    public void setUp() {
    brg= new BibReferenceGenerator();
     bibEntryArticle = new BibEntry();
        bibEntryArticle.setType(BibEntry.TYPE_ARTICLE);
        bibEntryArticle.setField(BibEntry.FIELD_PUBLISHER, "Kluwer Academic Publishers");
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
        bibEntryArticle.addField(BibEntry.FIELD_AUTHOR, "Jerome, Joseph W.");
        bibEntryArticle.addField(BibEntry.FIELD_AUTHOR, "Walaszek, J. K.");
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
        bibEntryArticle.setField(BibEntry.FIELD_DOI,"10.1023/A:1018962112170");

         bibEntryBook = new BibEntry();
        bibEntryBook.setType(BibEntry.TYPE_BOOK);
        bibEntryBook.setField(BibEntry.FIELD_SERIES, "Monografie Matematyczne");
        bibEntryBook.setField(BibEntry.FIELD_PUBLISHER, "Instytut Matematyczny Polskiej Akademii Nauk");
        bibEntryBook.setField(BibEntry.FIELD_EDITOR, "Otto, Edward");
        bibEntryBook.setField(BibEntry.FIELD_NOTE, "description");
        bibEntryBook.setField(BibEntry.FIELD_YEAR, "1950");
        bibEntryBook.setField(BibEntry.FIELD_MONTH, "10");

    entryJournal1 = new BibEntry(BibEntry.TYPE_ARTICLE)
                .setField(BibEntry.FIELD_YEAR, "1990")
                .setField(BibEntry.FIELD_JOURNAL, "Software Practice and Experience")
                .setField(BibEntry.FIELD_AUTHOR, "Gettys, Jim")
                .setField(BibEntry.FIELD_TITLE, "The X Window System, Version 11")
                .setField(BibEntry.FIELD_VOLUME, "20")
                .setField(BibEntry.FIELD_NUMBER, "S2")
                .setField(BibEntry.FIELD_PAGES, "156-160");

   entryJournal2 = new BibEntry(BibEntry.TYPE_ARTICLE).setField(BibEntry.FIELD_YEAR, "2004")
                .setField(BibEntry.FIELD_JOURNAL, "Child and Family Behavior Therapy")
                .addField(BibEntry.FIELD_AUTHOR, "Hughes, Jane C.")
                .addField(BibEntry.FIELD_AUTHOR, "Brestan, Elizabeth V.")
                .addField(BibEntry.FIELD_AUTHOR, "Valle, Linda Anne")
                .setField(BibEntry.FIELD_TITLE, "Problem-Solving Interactions between Mothers and Children")
                .setField(BibEntry.FIELD_VOLUME, "26")
                .setField(BibEntry.FIELD_NUMBER, "1")
                .setField(BibEntry.FIELD_PAGES, "1-16");

    entryJournal3 = new BibEntry(BibEntry.TYPE_ARTICLE).setField(BibEntry.FIELD_YEAR, "1983")
                .setField(BibEntry.FIELD_JOURNAL, "Journal of Comparative and Physiological Psychology")
                .setField(BibEntry.FIELD_AUTHOR, "Harlow, H. F.")
                .setField(BibEntry.FIELD_TITLE, "Fundamentals for preparing psychology journal articles")
                .setField(BibEntry.FIELD_VOLUME, "55")
                .setField(BibEntry.FIELD_PAGES, "893-896");

    entryJournalWithOneAuthor = new BibEntry(BibEntry.TYPE_ARTICLE)
                .setField(BibEntry.FIELD_YEAR, "2009")
                .setField(BibEntry.FIELD_JOURNAL, "Classical Philology")
                .setField(BibEntry.FIELD_AUTHOR, "Weinstein, Joshua I")
                .setField(BibEntry.FIELD_TITLE, "The Market in Plato’s Republic")
                .setField(BibEntry.FIELD_VOLUME, "104")
                .setField(BibEntry.FIELD_NUMBER, "S2")
                .setField(BibEntry.FIELD_PAGES, "439–58");
    
    entryJournalWithMultipleAuthor = new BibEntry(BibEntry.TYPE_ARTICLE)
                .setField(BibEntry.FIELD_YEAR, "1990")
                .setField(BibEntry.FIELD_JOURNAL, "Software Practice and Experience")
                .addField(BibEntry.FIELD_AUTHOR, "Smith, Jr., Michael")
                .addField(BibEntry.FIELD_AUTHOR, "Gettys, Jim")
                .addField(BibEntry.FIELD_AUTHOR, "Gates, III, William")
                .setField(BibEntry.FIELD_TITLE, "The X Window System, Version 11")
                .setField(BibEntry.FIELD_VOLUME, "20")
                .setField(BibEntry.FIELD_NUMBER, "S2")
                .setField(BibEntry.FIELD_PAGES, "156-160");

    entryJournalWithoutPages = new BibEntry(BibEntry.TYPE_ARTICLE)
                .setField(BibEntry.FIELD_YEAR, "1990")
                .setField(BibEntry.FIELD_JOURNAL, "Software Practice and Experience")
                .addField(BibEntry.FIELD_AUTHOR, "Smith, Jr., Michael")
                .addField(BibEntry.FIELD_AUTHOR, "Gettys, Jim")
                .addField(BibEntry.FIELD_AUTHOR, "Gates, III, William")
                .setField(BibEntry.FIELD_TITLE, "The X Window System, Version 11")
                .setField(BibEntry.FIELD_VOLUME, "20")
                .setField(BibEntry.FIELD_NUMBER, "S2");

    entryJournalWithoutNumber =   new BibEntry(BibEntry.TYPE_ARTICLE)
                .setField(BibEntry.FIELD_YEAR, "1990")
                .setField(BibEntry.FIELD_JOURNAL, "Software Practice and Experience")
                .addField(BibEntry.FIELD_AUTHOR, "Smith, Jr., Michael")
                .addField(BibEntry.FIELD_AUTHOR, "Gettys, Jim")
                .addField(BibEntry.FIELD_AUTHOR, "Gates, III, William")
                .setField(BibEntry.FIELD_TITLE, "The X Window System, Version 11")
                .setField(BibEntry.FIELD_VOLUME, "20")
                .setField(BibEntry.FIELD_PAGES, "156-160");

    entryJournalWithoutVolume = new BibEntry(BibEntry.TYPE_ARTICLE)
                .setField(BibEntry.FIELD_YEAR, "1990")
                .setField(BibEntry.FIELD_JOURNAL, "Software Practice and Experience")
                .addField(BibEntry.FIELD_AUTHOR, "Smith, Jr., Michael")
                .addField(BibEntry.FIELD_AUTHOR, "Gettys, Jim")
                .addField(BibEntry.FIELD_AUTHOR, "Gates, III, William")
                .setField(BibEntry.FIELD_TITLE, "The X Window System, Version 11")
                .setField(BibEntry.FIELD_PAGES, "156-160")
                .setField(BibEntry.FIELD_NUMBER, "S2");

    entryJournalWithoutPagesAndVolume = new BibEntry(BibEntry.TYPE_ARTICLE)
                .setField(BibEntry.FIELD_YEAR, "1990")
                .setField(BibEntry.FIELD_JOURNAL, "Software Practice and Experience")
                .addField(BibEntry.FIELD_AUTHOR, "Smith, Jr., Michael")
                .addField(BibEntry.FIELD_AUTHOR, "Gettys, Jim")
                .addField(BibEntry.FIELD_AUTHOR, "Gates, III, William")
                .setField(BibEntry.FIELD_TITLE, "The X Window System, Version 11")
                .setField(BibEntry.FIELD_NUMBER, "S2");

    entryJournalWithoutVolumeAndYear = new BibEntry(BibEntry.TYPE_ARTICLE)
                .setField(BibEntry.FIELD_JOURNAL, "Software Practice and Experience")
                .addField(BibEntry.FIELD_AUTHOR, "Smith, Jr., Michael")
                .addField(BibEntry.FIELD_AUTHOR, "Gettys, Jim")
                .addField(BibEntry.FIELD_AUTHOR, "Gates, III, William")
                .setField(BibEntry.FIELD_TITLE, "The X Window System, Version 11")
                .setField(BibEntry.FIELD_NUMBER, "S2")
                .setField(BibEntry.FIELD_PAGES, "156-160");

    entryJournalWithoutYear = new BibEntry(BibEntry.TYPE_ARTICLE)
                .setField(BibEntry.FIELD_JOURNAL, "Software Practice and Experience")
                .addField(BibEntry.FIELD_AUTHOR, "Smith, Jr., Michael")
                .addField(BibEntry.FIELD_AUTHOR, "Gettys, Jim")
                .addField(BibEntry.FIELD_AUTHOR, "Gates, III, William")
                .setField(BibEntry.FIELD_TITLE, "The X Window System, Version 11")
                .setField(BibEntry.FIELD_VOLUME, "20")
                .setField(BibEntry.FIELD_NUMBER, "S2")
                .setField(BibEntry.FIELD_PAGES, "156-160");

    entryJournalWithoutJournal = new BibEntry(BibEntry.TYPE_ARTICLE)
                .setField(BibEntry.FIELD_YEAR, "1990")
                .addField(BibEntry.FIELD_AUTHOR, "Smith, Jr., Michael")
                .addField(BibEntry.FIELD_AUTHOR, "Gettys, Jim")
                .addField(BibEntry.FIELD_AUTHOR, "Gates, III, William")
                .setField(BibEntry.FIELD_TITLE, "The X Window System, Version 11")
                .setField(BibEntry.FIELD_VOLUME, "20")
                .setField(BibEntry.FIELD_NUMBER, "S2")
                .setField(BibEntry.FIELD_PAGES, "156-160");

    entryJournalWithoutTitle = new BibEntry(BibEntry.TYPE_ARTICLE)
                .setField(BibEntry.FIELD_YEAR, "1990")
                .setField(BibEntry.FIELD_JOURNAL, "Software Practice and Experience")
                .addField(BibEntry.FIELD_AUTHOR, "Smith, Jr., Michael")
                .addField(BibEntry.FIELD_AUTHOR, "Gettys, Jim")
                .addField(BibEntry.FIELD_AUTHOR, "Gates, III, William")
                .setField(BibEntry.FIELD_VOLUME, "20")
                .setField(BibEntry.FIELD_NUMBER, "S2")
                .setField(BibEntry.FIELD_PAGES, "156-160");

   entryJournalWithoutAuthors = new BibEntry(BibEntry.TYPE_ARTICLE)
                .setField(BibEntry.FIELD_YEAR, "1990")
                .setField(BibEntry.FIELD_JOURNAL, "Software Practice and Experience")
                .setField(BibEntry.FIELD_TITLE, "The X Window System, Version 11")
                .setField(BibEntry.FIELD_VOLUME, "20")
                .setField(BibEntry.FIELD_NUMBER, "S2")
                .setField(BibEntry.FIELD_PAGES, "156-160");

   entryJournalWithoutPVJY = new BibEntry(BibEntry.TYPE_ARTICLE)
                .addField(BibEntry.FIELD_AUTHOR, "Smith, Jr., Michael")
                .addField(BibEntry.FIELD_AUTHOR, "Gettys, Jim")
                .addField(BibEntry.FIELD_AUTHOR, "Gates, III, William")
                .setField(BibEntry.FIELD_TITLE, "The X Window System, Version 11");

   entryBook1 = new BibEntry(BibEntry.TYPE_BOOK)
                .setField(BibEntry.FIELD_YEAR, "2006")
                .setField(BibEntry.FIELD_PUBLISHER, "Penguin")
                .setField(BibEntry.FIELD_ADDRESS, "New York")
                .setField(BibEntry.FIELD_AUTHOR, "Pollan, Michael")
                .setField(BibEntry.FIELD_TITLE, "The Omnivore’s Dilemma: A Natural History of Four Meals");

  entryBook2 = new BibEntry(BibEntry.TYPE_BOOK)
                .setField(BibEntry.FIELD_YEAR, "1993")
                .setField(BibEntry.FIELD_PUBLISHER, "Alaska Northwest Books")
                .setField(BibEntry.FIELD_AUTHOR, "Jans, Nick")
                .setField(BibEntry.FIELD_ADDRESS, "Anchorage")
                .setField(BibEntry.FIELD_TITLE, "The Last Light Breaking: Life among Alaska's Inupiat Eskimos");

   entryBookWithoutAddress = new BibEntry(BibEntry.TYPE_BOOK)
                .setField(BibEntry.FIELD_YEAR, "2006")
                .setField(BibEntry.FIELD_PUBLISHER, "Penguin")
                .setField(BibEntry.FIELD_AUTHOR, "Pollan, Michael")
                .setField(BibEntry.FIELD_TITLE, "The Omnivore’s Dilemma: A Natural History of Four Meals");
   entryBookWithoutPublisher = new BibEntry(BibEntry.TYPE_BOOK)
                .setField(BibEntry.FIELD_YEAR, "2006")
                .setField(BibEntry.FIELD_ADDRESS, "New York")
                .setField(BibEntry.FIELD_AUTHOR, "Pollan, Michael")
                .setField(BibEntry.FIELD_TITLE, "The Omnivore’s Dilemma: A Natural History of Four Meals");

   entryBookWithoutYear = new BibEntry(BibEntry.TYPE_BOOK)
                .setField(BibEntry.FIELD_ADDRESS, "New York")
                .setField(BibEntry.FIELD_PUBLISHER, "Penguin")
                .setField(BibEntry.FIELD_AUTHOR, "Pollan, Michael")
                .setField(BibEntry.FIELD_TITLE, "The Omnivore’s Dilemma: A Natural History of Four Meals");

   entryBookWithoutPY = new BibEntry(BibEntry.TYPE_BOOK)
                .setField(BibEntry.FIELD_ADDRESS, "New York")
                .setField(BibEntry.FIELD_AUTHOR, "Pollan, Michael")
                .setField(BibEntry.FIELD_TITLE, "The Omnivore’s Dilemma: A Natural History of Four Meals");

   entryBookWithEditor = new BibEntry(BibEntry.TYPE_BOOK)
                .addField(BibEntry.FIELD_EDITOR,"Hughes, Jane C.")
                .addField(BibEntry.FIELD_EDITOR,"Brestan, Elizabeth V.")
                .addField(BibEntry.FIELD_EDITOR,"Valle, Linda Anne")
                .setField(BibEntry.FIELD_YEAR, "2006")
                .setField(BibEntry.FIELD_PUBLISHER, "Penguin")
                .setField(BibEntry.FIELD_ADDRESS, "New York")
                .setField(BibEntry.FIELD_AUTHOR, "Pollan, Michael")
                .setField(BibEntry.FIELD_TITLE, "The Omnivore’s Dilemma: A Natural History of Four Meals");

   entryBookWithEdition = entryBookWithEditor
                .setField(BibEntry.FIELD_EDITION, "Third");
   
   entryInProceedings = new BibEntry(BibEntry.TYPE_INPROCEEDINGS)
                .addField(BibEntry.FIELD_EDITOR,"Kelly, John D.")
                .addField(BibEntry.FIELD_EDITOR,"Jauregui, Beatrice")
                .addField(BibEntry.FIELD_EDITOR,"Mitchell, Sean T.")
                .addField(BibEntry.FIELD_EDITOR,"Walton, Jeremy")
                .setField(BibEntry.FIELD_YEAR, "2010")
                .setField(BibEntry.FIELD_PUBLISHER, "University of Chicago Press")
                .setField(BibEntry.FIELD_ADDRESS, "Chicago")
                .setField(BibEntry.FIELD_PAGES, "67–83")
                .setField(BibEntry.FIELD_AUTHOR, "Kelly, John D.")
                .setField(BibEntry.FIELD_BOOKTITLE, "Anthropology and Global Counterinsurgency")
                .setField(BibEntry.FIELD_TITLE, "Seeing Red: Mao Fetishism, Pax Americana, and the Moral Economy of War");
	
   entryInProceedings2 = new BibEntry(BibEntry.TYPE_INPROCEEDINGS)
                .setField(BibEntry.FIELD_EDITOR,"Kooper, Erik")
                .setField(BibEntry.FIELD_YEAR, "1991")
                .setField(BibEntry.FIELD_PUBLISHER, "Rodopi")
                .setField(BibEntry.FIELD_ADDRESS, "Amsterdam")
                .setField(BibEntry.FIELD_PAGES, "173-88")
                .setField(BibEntry.FIELD_AUTHOR, "Mann, Jill")
                .setField(BibEntry.FIELD_BOOKTITLE, "This Noble Craft: Proceedings of the Tenth Research "
                +"Symposium of the Dutch and Belgian University Teachers "
                +"of Old and Middle English and Historical Linguistics, Utrecht, "
                +"19-20 January 1989")
                .setField(BibEntry.FIELD_TITLE, "Chaucer and the 'Woman Question'");
        
     entryInProceedingsWithoutPPA = new BibEntry(BibEntry.TYPE_INPROCEEDINGS)

                .setField(BibEntry.FIELD_YEAR, "2010")
                .setField(BibEntry.FIELD_AUTHOR, "Kelly, John D.")
                .setField(BibEntry.FIELD_BOOKTITLE, "Anthropology and Global Counterinsurgency")
                .setField(BibEntry.FIELD_TITLE, "Seeing Red: Mao Fetishism, Pax Americana, and the Moral Economy of War");

    entryInProceedingsWithoutPA = new BibEntry(BibEntry.TYPE_INPROCEEDINGS)
                .setField(BibEntry.FIELD_YEAR, "2010")
                .setField(BibEntry.FIELD_AUTHOR, "Kelly, John D.")
                .setField(BibEntry.FIELD_PAGES, "67–83")
                .setField(BibEntry.FIELD_BOOKTITLE, "Anthropology and Global Counterinsurgency")
                .setField(BibEntry.FIELD_TITLE, "Seeing Red: Mao Fetishism, Pax Americana, and the Moral Economy of War");

    entryInProceedingsWithoutP = new BibEntry(BibEntry.TYPE_INPROCEEDINGS)

                .setField(BibEntry.FIELD_YEAR, "2010")
                .setField(BibEntry.FIELD_AUTHOR, "Kelly, John D.")
                .setField(BibEntry.FIELD_PAGES, "67–83")
                .setField(BibEntry.FIELD_ADDRESS, "Chicago")
                .setField(BibEntry.FIELD_BOOKTITLE, "Anthropology and Global Counterinsurgency")
                .setField(BibEntry.FIELD_TITLE, "Seeing Red: Mao Fetishism, Pax Americana, and the Moral Economy of War");
    }
    @Test
    public void sepatateForemanesTest() {


        String[] names = brg.seperateForenames("John Andrew Tom");

        assertEquals("John", names[0]);
        assertEquals("Andrew", names[1]);
        assertEquals("Tom", names[2]);

        names = brg.seperateForenames("John");

        assertEquals("John", names[0]);

    }

    @Test
    public void parseAuthorTest() {
        brg = new BibReferenceGenerator();
        Person parsedAuthor = brg.parsePerson("Gates, III, William Adam");


        assertEquals("William Adam", brg.concatenateForenames(parsedAuthor.forenames));
        assertEquals("Gates", parsedAuthor.surname);
        assertEquals("III", parsedAuthor.pedigree);
    }

    @Test
    public void toChicagoNotesAndBibliographyJournalTest() {
        brg = new BibReferenceGenerator();
        

        System.out.println(brg.toChicagoNotesAndBibliography(entryJournal1));

        assertEquals("Gettys, Jim. \"The X Window System, Version 11.\" Software Practice"
                + " and Experience 20 (1990): 156-160.", brg.toChicagoNotesAndBibliography(entryJournal1));

        assertEquals("Gettys, Jim. \"The X Window System, Version 11.\" Software Practice"
                + " and Experience 20 (1990): "
                + "156-160.", brg.toBibReference(entryJournal1, BibReferenceFormatConstants.ChicagoNotesAndBibliography));

        System.out.println(brg.toChicagoNotesAndBibliography(entryJournalWithMultipleAuthor));

        assertEquals("Smith, Michael, Jr., Jim Gettys, and William Gates III. \"The"
                + " X Window System, Version 11.\" Software Practice and Experience"
                + " 20 (1990): 156-160.", brg.toChicagoNotesAndBibliography(entryJournalWithMultipleAuthor));

        //without pages and volume

        System.out.println(brg.toChicagoNotesAndBibliography(entryJournalWithoutPagesAndVolume));
        assertEquals("Smith, Michael, Jr., Jim Gettys, and William Gates III. \"The X Window System, Version"
                + " 11.\" Software Practice and Experience (1990).", brg.toChicagoNotesAndBibliography(entryJournalWithoutPagesAndVolume));

        //without year

        System.out.println(brg.toChicagoNotesAndBibliography(entryJournalWithoutYear));
        assertEquals("Smith, Michael, Jr., Jim Gettys, and William Gates III. \"The X Window System, Version 11.\" "
                + "Software Practice and Experience 20: 156-160.", brg.toChicagoNotesAndBibliography(entryJournalWithoutYear));

        //without journal

        System.out.println(brg.toChicagoNotesAndBibliography(entryJournalWithoutJournal));
        assertEquals("Smith, Michael, Jr., Jim Gettys, and William Gates III. \"The X Window System, Version 11.\" 20"
                + " (1990): 156-160.", brg.toChicagoNotesAndBibliography(entryJournalWithoutJournal));
        //without title

        System.out.println(brg.toChicagoNotesAndBibliography(entryJournalWithoutTitle));
        assertEquals("Smith, Michael, Jr., Jim Gettys, and William Gates III.  Software Practice and Experience 20 "
                + "(1990): 156-160.", brg.toChicagoNotesAndBibliography(entryJournalWithoutTitle));
        //without authors

        System.out.println(brg.toChicagoNotesAndBibliography(entryJournalWithoutAuthors));
        assertEquals("\"The X Window System, Version 11.\" Software Practice and Experience 20 (1990): "
                + "156-160.", brg.toChicagoNotesAndBibliography(entryJournalWithoutAuthors));

        //without volume
        
        System.out.println(brg.toChicagoNotesAndBibliography(entryJournalWithoutVolume));
        assertEquals("Smith, Michael, Jr., Jim Gettys, and William Gates III. \"The X Window"
                + " System, Version 11.\" Software Practice and Experience (1990):"
                + " 156-160.",brg.toChicagoNotesAndBibliography(entryJournalWithoutVolume));

        //without volume and year
        
        System.out.println(brg.toChicagoNotesAndBibliography(entryJournalWithoutVolumeAndYear));
        assertEquals("Smith, Michael, Jr., Jim Gettys, and William Gates III."
                + " \"The X Window System, Version 11.\" Software Practice"
                + " and Experience 156-160.",brg.toChicagoNotesAndBibliography(entryJournalWithoutVolumeAndYear));

        //without pages, volume, journal, year

        System.out.println(brg.toChicagoNotesAndBibliography(entryJournalWithoutPVJY));
        assertEquals("Smith, Michael, Jr., Jim Gettys, and William Gates III. \"The X Window System, Version"
                + " 11.\"", brg.toChicagoNotesAndBibliography(entryJournalWithoutPVJY));
    }
    @Test
    public void toChicagoNotesAndBibliographyBookTest() {
      
        System.out.println(brg.toChicagoNotesAndBibliography(entryBook1));

        assertEquals("Pollan, Michael. The Omnivore’s Dilemma: A Natural History of Four Meals. New York: Penguin,"
                + " 2006.", brg.toChicagoNotesAndBibliography(entryBook1));

        //without address

        System.out.println(brg.toChicagoNotesAndBibliography(entryBookWithoutAddress));
        assertEquals("Pollan, Michael. The Omnivore’s Dilemma: A Natural History of Four Meals. Penguin, 2006.", brg.toChicagoNotesAndBibliography(entryBookWithoutAddress));

        //without publisher

        System.out.println(brg.toChicagoNotesAndBibliography(entryBookWithoutPublisher));
        assertEquals("Pollan, Michael. The Omnivore’s Dilemma: A Natural History of Four Meals. New York, 2006.", brg.toChicagoNotesAndBibliography(entryBookWithoutPublisher));

        //without year

        System.out.println(brg.toChicagoNotesAndBibliography(entryBookWithoutYear));
        assertEquals("Pollan, Michael. The Omnivore’s Dilemma: A Natural History of Four Meals. New York: Penguin.", brg.toChicagoNotesAndBibliography(entryBookWithoutYear));

        //without publisher and year

        System.out.println(brg.toChicagoNotesAndBibliography(entryBookWithoutPY));
        assertEquals("Pollan, Michael. The Omnivore’s Dilemma: A Natural History of Four Meals. New York.", brg.toChicagoNotesAndBibliography(entryBookWithoutPY));

        //with editor

        System.out.println(brg.toChicagoNotesAndBibliography(entryBookWithEditor));
        assertEquals("Pollan, Michael. The Omnivore’s Dilemma: A Natural History of Four Meals. Edited by Jane C. Hughes, Elizabeth "
                + "V. Brestan, and Linda Anne Valle. New York: Penguin, 2006.", brg.toChicagoNotesAndBibliography(entryBookWithEditor));

    }
    @Test
    public void toChicagoNotesAndBibliographyInProceedingsTest() {
        System.out.println(brg.toChicagoNotesAndBibliography(entryInProceedings));
        assertEquals("Kelly, John D. \"Seeing Red: Mao Fetishism, Pax Americana,"
                + " and the Moral Economy of War.\" In Anthropology and Global "
                + "Counterinsurgency, 67–83. Chicago: University"
                + " of Chicago Press, 2010.", brg.toChicagoNotesAndBibliography(entryInProceedings));

        //without pages, address, publisher

        System.out.println(brg.toChicagoNotesAndBibliography(entryInProceedingsWithoutPPA));
        assertEquals("Kelly, John D. \"Seeing Red: Mao Fetishism, Pax Americana, and the Moral Economy of War.\" "
                + "In Anthropology and Global Counterinsurgency. 2010.", brg.toChicagoNotesAndBibliography(entryInProceedingsWithoutPPA));

        //without address, publisher

        System.out.println(brg.toChicagoNotesAndBibliography(entryInProceedingsWithoutPA));
        assertEquals("Kelly, John D. \"Seeing Red: Mao Fetishism, Pax Americana, and the Moral Economy of War.\" In "
                + "Anthropology and Global Counterinsurgency, 67–83. 2010.", brg.toChicagoNotesAndBibliography(entryInProceedingsWithoutPA));
        //without publisher

        System.out.println(brg.toChicagoNotesAndBibliography(entryInProceedingsWithoutP));
        assertEquals("Kelly, John D. \"Seeing Red: Mao Fetishism, Pax Americana, and the Moral Economy of War.\" In"
                + " Anthropology and Global Counterinsurgency, 67–83. Chicago,"
                + " 2010.", brg.toChicagoNotesAndBibliography(entryInProceedingsWithoutP));
    }


    @Test
    public void toChicagoAuthorDateJournalTest(){

    System.out.println(brg.toChicagoAuthorDate(entryJournalWithOneAuthor));
    assertEquals("Weinstein, Joshua I. 2009. \"The Market in Plato’s Republic.\" Classical Philology"
            + " 104:439–58.", brg.toChicagoAuthorDate(entryJournalWithOneAuthor));

    System.out.println(brg.toBibReference(entryJournalWithMultipleAuthor, BibReferenceFormatConstants.ChicagoAuthorDate));
    assertEquals("Smith, Michael, Jr., Jim Gettys, and William Gates III. 1990. \"The X Window System, Version 11.\" "
            + "Software Practice and Experience "
            + "20:156-160.",brg.toBibReference(entryJournalWithMultipleAuthor, BibReferenceFormatConstants.ChicagoAuthorDate));

    //without volume

    System.out.println(brg.toBibReference(entryJournalWithoutVolume, BibReferenceFormatConstants.ChicagoAuthorDate));
    assertEquals("Smith, Michael, Jr., Jim Gettys, and William Gates III. 1990. \"The X Window System, Version 11.\""
            + " Software Practice and Experience"
            + " 156-160.",brg.toBibReference(entryJournalWithoutVolume, BibReferenceFormatConstants.ChicagoAuthorDate));

    //without year

    System.out.println(brg.toChicagoAuthorDate(entryJournalWithoutYear));
    assertEquals("Smith, Michael, Jr., Jim Gettys, and William Gates III. \"The X Window System, Version 11.\" Software"
            + " Practice and Experience 20:156-160.",brg.toChicagoAuthorDate(entryJournalWithoutYear));

    //without authors

    System.out.println(brg.toChicagoAuthorDate(entryJournalWithoutAuthors));
    assertEquals("1990. \"The X Window System, Version 11.\" Software Practice and Experience 20:156-160.",
             brg.toChicagoAuthorDate(entryJournalWithoutAuthors));

    //without journal
    
    System.out.println(brg.toChicagoAuthorDate(entryJournalWithoutJournal));
    assertEquals("Smith, Michael, Jr., Jim Gettys, and William Gates III. 1990. \"The X Window System, Version 11.\""
            + " 20:156-160.",brg.toChicagoAuthorDate(entryJournalWithoutJournal));

    //without pages and volume

    System.out.println(brg.toChicagoAuthorDate(entryJournalWithoutPagesAndVolume));
    assertEquals("Smith, Michael, Jr., Jim Gettys, and William Gates III. 1990. \"The X Window System, Version 11.\""
            + " Software Practice and Experience.",brg.toChicagoAuthorDate(entryJournalWithoutPagesAndVolume));

    }
    @Test
    public void toChicagoAuthorDateBookTest(){
    System.out.println(brg.toChicagoAuthorDate(entryBook1));
    assertEquals("Pollan, Michael. 2006. The Omnivore’s Dilemma: A Natural History of Four Meals. New York:"
            + " Penguin.",brg.toChicagoAuthorDate(entryBook1));
    
    //with editor
    
    System.out.println(brg.toChicagoAuthorDate(entryBookWithEditor));
    assertEquals("Pollan, Michael. 2006. The Omnivore’s Dilemma: A Natural History of Four Meals. Edited by Jane C. "
            + "Hughes, Elizabeth V. Brestan, and Linda Anne Valle. New York:"
            + " Penguin.",brg.toChicagoAuthorDate(entryBookWithEditor));
    }
    @Test
    public void toChicagoAuthorDateInProceedingsTest(){
    System.out.println(brg.toChicagoAuthorDate(entryInProceedings));
    assertEquals("Kelly, John D. 2010. \"Seeing Red: Mao Fetishism, Pax Americana, and the Moral Economy of War.\" In "
            + "Anthropology and Global Counterinsurgency, 67–83. Chicago: University of Chicago"
            + " Press.",brg.toChicagoAuthorDate(entryInProceedings));
    
    //without publisher and author

    System.out.println(brg.toChicagoAuthorDate(entryInProceedingsWithoutPA));
    assertEquals("Kelly, John D. 2010. \"Seeing Red: Mao Fetishism, Pax Americana, and the Moral Economy of War.\" In "
            + "Anthropology and Global Counterinsurgency,"
            + " 67–83.",brg.toChicagoAuthorDate(entryInProceedingsWithoutPA));
    }




    @Test
    public void toMLAJournalTest() {
        brg = new BibReferenceGenerator();
        

        System.out.println(brg.toMLA(entryJournal2));


        assertEquals("Hughes, Jane C., Elizabeth V. Brestan, and Linda Anne Valle."
                + " \"Problem-Solving Interactions between Mothers and Children.\" "
                + "Child and Family Behavior Therapy 26.1 (2004): 1-16. Print.", brg.toMLA(entryJournal2));

        assertEquals("Hughes, Jane C., Elizabeth V. Brestan, and Linda Anne Valle."
                + " \"Problem-Solving Interactions between Mothers and Children.\" "
                + "Child and Family Behavior Therapy 26.1 (2004): 1-16."
                + " Print.", brg.toBibReference(entryJournal2, BibReferenceFormatConstants.MLA));

        //without pages and volume

        System.out.println(brg.toMLA(entryJournalWithoutPagesAndVolume));
        assertEquals("Smith, Michael, Jr., Jim Gettys, and William Gates III. \"The X Window System, "
                + "Version 11.\" Software Practice and Experience (1990)."
                + " Print.", brg.toMLA(entryJournalWithoutPagesAndVolume));

        System.out.println(brg.toMLA(entryJournalWithoutNumber));
        assertEquals("Smith, Michael, Jr., Jim Gettys, and William Gates III. \"The X Window System, "
                + "Version 11.\" Software Practice and Experience 20 (1990):"
                + " 156-160. Print.", brg.toMLA(entryJournalWithoutNumber));

        //without year and volume
        
        System.out.println(brg.toMLA(entryJournalWithoutVolumeAndYear));
        assertEquals("Smith, Michael, Jr., Jim Gettys, and William Gates III. \"The X Window System,"
                + " Version 11.\" Software Practice and Experience 156-160. "
                + "Print.",brg.toMLA(entryJournalWithoutVolumeAndYear));

    }
    @Test
    public void toMLABookTest(){
        System.out.println(brg.toMLA(entryBook2));
        assertEquals("Jans, Nick. The Last Light Breaking: Life among Alaska's Inupiat"
                + " Eskimos. Anchorage: Alaska Northwest Books, 1993. Print.", brg.toMLA(entryBook2));

        //with edition

        System.out.println(brg.toMLA(entryBookWithEdition));
        assertEquals("Pollan, Michael. The Omnivore’s Dilemma: A Natural History of Four Meals. "
                + "Third ed. New York: Penguin, 2006. Print.",brg.toMLA(entryBookWithEdition));
    }
    @Test
    public void toMLAInProceedingsTest(){
        System.out.println(brg.toMLA(entryInProceedings2));
        assertEquals("Mann, Jill. \"Chaucer and the 'Woman Question'.\" This Noble Craft: Proceedings"
                + " of the Tenth Research Symposium of the Dutch and Belgian"
                + " University Teachers of Old and Middle English and Historical"
                + " Linguistics, Utrecht, 19-20 January 1989. Ed. Erik Kooper. Amsterdam: Rodopi,"
                + " 1991. 173-88. Print.",brg.toMLA(entryInProceedings2));

        //without pages, address, publisher

        System.out.println(brg.toMLA(entryInProceedingsWithoutPPA));
        assertEquals("Kelly, John D. \"Seeing Red: Mao Fetishism, Pax Americana, and the Moral Economy"
                + " of War.\" Anthropology and Global Counterinsurgency. 2010. "
                + "Print.",brg.toMLA(entryInProceedingsWithoutPPA));

        //without address, publisher

        System.out.println(brg.toMLA(entryInProceedingsWithoutPA));
        assertEquals("Kelly, John D. \"Seeing Red: Mao Fetishism, Pax Americana, and the Moral Economy"
                + " of War.\" Anthropology and Global Counterinsurgency. 2010. 67–83."
                + " Print.",brg.toMLA(entryInProceedingsWithoutPA));

        //without publisher

        System.out.println(brg.toMLA(entryInProceedingsWithoutP));
        assertEquals("Kelly, John D. \"Seeing Red: Mao Fetishism, Pax Americana, and the Moral Economy"
                + " of War.\" Anthropology and Global Counterinsurgency."
                + " Chicago, 2010. 67–83. Print.", brg.toMLA(entryInProceedingsWithoutP));
    }

    @Test
    public void toApaJournalTest(){
        
        System.out.println(brg.toAPA(entryJournal3));
        assertEquals("Harlow, H. F. (1983). Fundamentals for preparing psychology "
            +"journal articles. Journal of Comparative and Physiological "
            +"Psychology, 55, 893-896.", brg.toBibReference(entryJournal3, BibReferenceFormatConstants.APA));

        //without volume

        System.out.println(brg.toAPA(entryJournalWithoutVolume));
        assertEquals("Smith, M., Jr., Gettys, J., & Gates, W., III. (1990). The X Window System, Version 11."
                + " Software Practice and Experience, 156-160.",brg.toAPA(entryJournalWithoutVolume));

        //without pages and volume

        System.out.println(brg.toAPA(entryJournalWithoutPagesAndVolume));
        assertEquals("Smith, M., Jr., Gettys, J., & Gates, W., III. (1990). The X Window System, Version 11."
                + " Software Practice and Experience.",brg.toAPA(entryJournalWithoutPagesAndVolume));

        //without pages
        
        System.out.println(brg.toAPA(entryJournalWithoutPages));
        assertEquals("Smith, M., Jr., Gettys, J., & Gates, W., III. (1990). The X Window System, Version 11."
                + " Software Practice and Experience, 20(S2).",brg.toAPA(entryJournalWithoutPages));

        System.out.println(brg.toAPA(entryJournalWithMultipleAuthor));
        assertEquals("Smith, M., Jr., Gettys, J., & Gates, W., III. (1990). "
            + "The X Window System, Version 11. Software Practice and "
            + "Experience, 20(S2), 156-160.", brg.toAPA(entryJournalWithMultipleAuthor));
    }
    @Test
    public void toApaBookTest(){
        System.out.println(brg.toAPA(entryBook1));
        assertEquals("Pollan, M. (2006). The Omnivore’s Dilemma: A Natural History of Four Meals. New York:"
                + " Penguin.", brg.toAPA(entryBook1));
        
        System.out.println(brg.toAPA(entryBookWithEdition));
        assertEquals("Pollan, M. (2006). The Omnivore’s Dilemma: A Natural History of Four Meals (Third ed.)"
                + ". New York: Penguin.", brg.toAPA(entryBookWithEdition));
    }
    @Test
    public void toApaInProceedingsTest(){
        System.out.println(brg.toAPA(entryInProceedings2));
        assertEquals("Mann, J. (1991). Chaucer and the 'Woman Question'. In This Noble Craft: Proceedings of"
                + " the Tenth Research Symposium of the Dutch and Belgian University Teachers of Old and"
                + " Middle English and Historical Linguistics, Utrecht, 19-20 January 1989 (p. 173-88)."
                + " Amsterdam: Rodopi.", brg.toAPA(entryInProceedings2));

    }
    @Test
    public void toRISArticleTest(){
    String bibEntryToRISArticle = brg.toRIS(bibEntryArticle);
        System.out.println(bibEntryToRISArticle);
        assertEquals("TY  - JOUR\n"
+"T1  - Multistep approximation algorithms: Improved convergence rates through postconditioning with smoothing kernels\n"
+"AU  - Fasshauer, Gregory E.\n"
+"AU  - Jerome, Joseph W.\n"
+"AU  - Walaszek, J. K.\n"
+"Y1  - 1999\n"
+"KW  - capacitated location\n"
+"KW  - Lagrangean heuristic\n"
+"KW  - mixed integer linear programming\n"
+"JF  - Advances in Computational Mathematics\n"
+"VL  - 10\n"
+"IS  - 1\n"
+"PB  - Kluwer Academic Publishers\n"
+"N2  - Abstract We show how certain widely used multistep approximation algorithms can be interpreted as instances of an approximate Newton method. It was shown in an earlier paper by the second author that the convergence rates of approximate Newton methods (in the context of the numerical solution of PDEs) suffer from a â€śloss of derivativesâ€ť, and that the subsequent linear rate of convergence can be improved to be superlinear using an adaptation of Nashâ€“Moser iteration for numerical analysis purposes; the essence of the adaptation being a splitting of the inversion and the smoothing into two separate steps. We show how these ideas apply to scattered data approximation as well as the numerical solution of partial differential equations. We investigate the use of several radial kernels for the smoothing operation. In our numerical examples we use radial basis functions also in the inversion step.\n"
+"ER  -",bibEntryToRISArticle);
    }
    @Test
    public void toRISBookTest(){
    String bibEntryToRISBook= brg.toBibReference(bibEntryBook, BibReferenceFormatConstants.RIS);
        System.out.println(bibEntryToRISBook);
        assertEquals("TY  - BOOK\n"
+"Y1  - 1950/10\n"
+"N1  - description\n"
+"PB  - Instytut Matematyczny Polskiej Akademii Nauk\n"
+"SN  - Monografie Matematyczne\n"
+"ER  -",bibEntryToRISBook);
    }
}
