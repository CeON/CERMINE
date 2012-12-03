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
        
    }
}
