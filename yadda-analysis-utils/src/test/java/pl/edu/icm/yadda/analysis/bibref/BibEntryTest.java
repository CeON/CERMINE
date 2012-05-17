package pl.edu.icm.yadda.analysis.bibref;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Unit tests of {@link BibEntry}.
 * 
 * @author Lukasz Bolikowski (bolo@icm.edu.pl)
 *
 */
public class BibEntryTest {

    @Test
    public void testKeyGeneration() {
        BibEntry entry1 = new BibEntry(BibEntry.TYPE_ARTICLE)
                .addField(BibEntry.FIELD_AUTHOR, "Smith, John")
                .addField(BibEntry.FIELD_AUTHOR, "Black, William")
                .setField(BibEntry.FIELD_YEAR, "1990");
        assertEquals("Smith1990", entry1.generateKey());

        BibEntry entry2 = new BibEntry(BibEntry.TYPE_ARTICLE)
                .setField(BibEntry.FIELD_AUTHOR, "van der Saar, Edwin")
                .setField(BibEntry.FIELD_YEAR, "1888");
        assertEquals("vanderSaar1888", entry2.generateKey());

        BibEntry entry3 = new BibEntry(BibEntry.TYPE_ARTICLE)
                .setField(BibEntry.FIELD_AUTHOR, "'t Hooft, Gerard")
                .setField(BibEntry.FIELD_YEAR, "2010");
        assertEquals("tHooft2010", entry3.generateKey());

        BibEntry entry4 = new BibEntry(BibEntry.TYPE_ARTICLE)
                .setField(BibEntry.FIELD_YEAR, "2010");
        assertEquals("Unknown2010", entry4.generateKey());

        BibEntry entry5 = new BibEntry(BibEntry.TYPE_ARTICLE);
        assertEquals("Unknown", entry5.generateKey());

        BibEntry entry6 = new BibEntry(BibEntry.TYPE_ARTICLE)
                .setField(BibEntry.FIELD_AUTHOR, "Twain, Mark");
        assertEquals("Twain", entry6.generateKey());
    }

    @Test
    public void testBibTeXGeneration() {
        BibEntry entry = new BibEntry(BibEntry.TYPE_BOOK).setField(BibEntry.FIELD_YEAR, "1876").setField(BibEntry.FIELD_AUTHOR, "Twain, Mark").setField(BibEntry.FIELD_TITLE, "The Adventures of Tom Sawyer");
        assertEquals("@book{Twain1876,\n"
                + "\tauthor = {Twain, Mark},\n"
                + "\ttitle = {The Adventures of Tom Sawyer},\n"
                + "\tyear = {1876},\n"
                + "}", entry.toBibTeX());
    }

    @Test
    public void testEscape() {
         BibEntry entry = new BibEntry(BibEntry.TYPE_BOOK).setField(BibEntry.FIELD_YEAR, "1876").setField(BibEntry.FIELD_AUTHOR, "Smith, John").setField(BibEntry.FIELD_TITLE, "Title_including { curly_braces} ");
         assertEquals("@book{Smith1876,\n"
                 + "\tauthor = {Smith, John},\n"
                 + "\ttitle = {Title\\_including \\{ curly\\_braces\\} },\n"
                 + "\tyear = {1876},\n"
                 + "}",entry.toBibTeX());
    }
}
