/**
 * This file is part of CERMINE project.
 * Copyright (c) 2011-2016 ICM-UW
 *
 * CERMINE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CERMINE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with CERMINE. If not, see <http://www.gnu.org/licenses/>.
 */

package pl.edu.icm.cermine.service;

import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.*;

/**
 * @author Aleksander Nowinski (a.nowinski@icm.edu.pl)
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
        assertEquals("Association des Annales de l’institut Fourier", result.getPublisher());
        assertEquals("Annales de l’institut Fourier",result.getJournalTitle());
        assertEquals("Analytic inversion of adjunction: blabla extension theorems with gain", result.getTitle());
        assertEquals("10.1016/j.lisr.2011.06.002", result.getDoi());
        assertEquals("Some abstract text...", result.getAbstractText());
        assertEquals("703", result.getFpage());
        assertEquals("718", result.getLpage());
        assertEquals("57", result.getVolume());
        assertEquals("3", result.getIssue());
        assertEquals("2007", result.getPubDate());
        assertEquals("2005-07-25", result.getReceivedDate());
        assertEquals("2006-02-20", result.getAcceptedDate());
        assertEquals(2, result.getAuthors().size());
        
        assertTrue(result.getAuthors().get(0).getName().contains("McNeal"));
        assertTrue(result.getAuthors().get(0).getName().contains("Jeffery D."));
        assertTrue(result.getKeywords().contains("32Q99"));
        assertTrue(result.getKeywords().contains("denominators"));
        assertEquals(8, result.getKeywords().size());
    }
}
