/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.edu.icm.cermine.service;

import java.io.InputStream;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Aleksander Nowinski <a.nowinski@icm.edu.pl>
 */
public class CermineExtractorServiceImplTest {
    
    Logger log = LoggerFactory.getLogger(CermineExtractorServiceImplTest.class);
    public CermineExtractorServiceImplTest() {
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
     * Test of extractNLM method, of class CermineExtractorServiceImpl.
     */
    @Test
    @Ignore
    public void testExtractNLM() throws Exception {
        


        System.out.println("extractNLM");
        InputStream is = this.getClass().getResourceAsStream("/pdf/test1.pdf");
        log.debug("Input stream is: {}", is);
        CermineExtractorServiceImpl instance = new CermineExtractorServiceImpl();
        String expResult = "";
        String result = instance.extractNLM(is);
        assertTrue(result.trim().startsWith("<"));
    }
}
