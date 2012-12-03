/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.edu.icm.cermine.service;

import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Aleksander Nowinski <a.nowinski@icm.edu.pl>
 */
public class ArticleMetaTest {

    public ArticleMetaTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }


    /**
     * Test of extractNLM method, of class ArticleMeta.
     */
    @Test
    public void testExtractNLM() throws Exception {
        System.out.println("extractNLM");

        SAXBuilder builder = new SAXBuilder();
        Document nlm = builder.build(this.getClass().getResourceAsStream("/sampleNlm.xml"));
        ArticleMeta result = ArticleMeta.extractNLM(nlm);
        assertEquals("Annales de l’institut Fourier",result.getJournalTitle());
        assertEquals("Analytic inversion of adjunction: blabla extension theorems with gain", result.getTitle());
        assertEquals("10.1016/j.lisr.2011.06.002", result.getDoi());
        assertEquals("Some abstract text...", result.getAbstractText());
        assertEquals("703", result.getFpage());
        assertEquals("718", result.getLpage());
        assertEquals("57", result.getVolume());
        assertEquals("3", result.getIssue());
        assertEquals(2, result.getAuthors().size());
        
        assertTrue(result.getAuthors().get(0).contains("McNeal"));
        assertTrue(result.getAuthors().get(0).contains("Jeffery D."));
        assertTrue(result.getKeywords().contains("32Q99"));
        assertTrue(result.getKeywords().contains("denominators"));
        assertEquals(8, result.getKeywords().size());
    }
}
