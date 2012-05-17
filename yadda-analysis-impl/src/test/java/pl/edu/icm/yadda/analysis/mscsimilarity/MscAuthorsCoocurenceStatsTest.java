package pl.edu.icm.yadda.analysis.mscsimilarity;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import pl.edu.icm.yadda.analysis.datastructures.SymmetricTreeMapMatrix;
import pl.edu.icm.yadda.bwmeta.model.YAttribute;
import pl.edu.icm.yadda.bwmeta.model.YCategoryRef;
import pl.edu.icm.yadda.bwmeta.model.YConstants;
import pl.edu.icm.yadda.bwmeta.model.YContributor;
import pl.edu.icm.yadda.bwmeta.model.YElement;

/**
 * Test if statistics matrices are built properly.
 * 
 * @author tkusm
 */
public class MscAuthorsCoocurenceStatsTest {


    @Test
    public void singleClassSingleAuthorTest() throws Exception {
        MscAuthorsCoocurenceStatsWritingNode generator = new MscAuthorsCoocurenceStatsWritingNode();
        List<YElement> yelements = new ArrayList<YElement>();        
        YElement yelement = new YElement();
        addMscCode(yelement, "21");               
        addAuthor(yelement, "piotr.dendek");
        yelements.add(yelement);
        
        generator.store(yelements, null);
        SymmetricTreeMapMatrix<String, Integer> m = generator.buildCoocurrenceMatrix();
        Assert.assertEquals(new Integer(1), m.get("21", "21"));
    }
    
    @Test
    public void twoClassesTwoAuthorsTest() throws Exception {
        MscAuthorsCoocurenceStatsWritingNode generator = new MscAuthorsCoocurenceStatsWritingNode();
        List<YElement> yelements = new ArrayList<YElement>();        
        YElement yelement = new YElement();
        addMscCode(yelement, "21");
        addMscCode(yelement, "22");    
        addAuthor(yelement, "piotr.dendek");
        addAuthor(yelement, "tomasz.kusmierczyk");
        yelements.add(yelement);
        
        generator.store(yelements, null);
        SymmetricTreeMapMatrix<String, Integer> m = generator.buildCoocurrenceMatrix();        
        Assert.assertEquals(new Integer(2), m.get("21", "21"));
        Assert.assertEquals(new Integer(2), m.get("22", "22"));
        Assert.assertEquals(new Integer(2), m.get("21", "22"));
        Assert.assertEquals(new Integer(2), m.get("22", "21"));
    }
    
    @Test
    public void twoClassesTwoAuthorsTestDifferentYElement() throws Exception {
        MscAuthorsCoocurenceStatsWritingNode generator = new MscAuthorsCoocurenceStatsWritingNode();
        List<YElement> yelements = new ArrayList<YElement>();        
        YElement yelement = new YElement();
        addMscCode(yelement, "21");
        addMscCode(yelement, "22");    
        addAuthor(yelement, "piotr.dendek");        
        yelements.add(yelement);
        
        YElement yelement2 = new YElement();
        addMscCode(yelement2, "21");
        addMscCode(yelement2, "22");            
        addAuthor(yelement2, "tomasz.kusmierczyk");
        yelements.add(yelement2);
        
        generator.store(yelements, null);
        SymmetricTreeMapMatrix<String, Integer> m = generator.buildCoocurrenceMatrix();        
        Assert.assertEquals(new Integer(2), m.get("21", "21"));
        Assert.assertEquals(new Integer(2), m.get("22", "22"));
        Assert.assertEquals(new Integer(2), m.get("21", "22"));
        Assert.assertEquals(new Integer(2), m.get("22", "21"));
    }
    
    @Test
    public void twoClassesTwoAuthorsTestDifferentYElement2() throws Exception {
        MscAuthorsCoocurenceStatsWritingNode generator = new MscAuthorsCoocurenceStatsWritingNode();
        List<YElement> yelements = new ArrayList<YElement>();   
        
        YElement yelement = new YElement();
        addMscCode(yelement, "21");
        addMscCode(yelement, "22");    
        addAuthor(yelement, "piotr.dendek");        
        yelements.add(yelement);
        
        YElement yelement2 = new YElement();
        addMscCode(yelement2, "21");
        addAuthor(yelement2, "tomasz.kusmierczyk");
        yelements.add(yelement2);
        
        generator.store(yelements, null);
        SymmetricTreeMapMatrix<String, Integer> m = generator.buildCoocurrenceMatrix();        
        Assert.assertEquals(new Integer(2), m.get("21", "21"));
        Assert.assertEquals(new Integer(1), m.get("22", "22"));
        Assert.assertEquals(new Integer(1), m.get("21", "22"));
        Assert.assertEquals(new Integer(1), m.get("22", "21"));
    }
    
    @Test
    public void threeClassesThreeDocumentsTest() throws Exception {
        MscAuthorsCoocurenceStatsWritingNode generator = new MscAuthorsCoocurenceStatsWritingNode();
        List<YElement> yelements = new ArrayList<YElement>();        
        YElement yelement = new YElement();
        addMscCode(yelement, "21");
        addMscCode(yelement, "22");    
        addAuthor(yelement, "piotr.dendek");
        addAuthor(yelement, "tomasz.kusmierczyk");
        yelements.add(yelement);
        
        YElement yelement2 = new YElement();
        addMscCode(yelement2, "21");
        addMscCode(yelement2, "23");    
        addAuthor(yelement2, "piotr.dendek");
        yelements.add(yelement2);
        
        YElement yelement3 = new YElement();
        addMscCode(yelement3, "22");
        addMscCode(yelement3, "23");    
        addAuthor(yelement3, "michal.lukasik");
        addAuthor(yelement3, "tomasz.kusmierczyk");
        yelements.add(yelement3);
        
        generator.store(yelements, null);
        
        SymmetricTreeMapMatrix<String, Integer> m = generator.buildCoocurrenceMatrix();               
        
        Assert.assertEquals(new Integer(2), m.get("21", "21")); //2 unique authors for class
        Assert.assertEquals(new Integer(3), m.get("22", "22")); //3 unique authors for class
        Assert.assertEquals(new Integer(3), m.get("23", "23")); //3 unique
        Assert.assertEquals(new Integer(2), m.get("21", "22"));
        Assert.assertEquals(new Integer(2), m.get("23", "21"));
        Assert.assertEquals(new Integer(2), m.get("21", "23"));
        Assert.assertEquals(new Integer(3), m.get("22", "23"));
    }
    
    @Test
    public void threeClassesThreeDocumentsTest2() throws Exception {
        MscAuthorsCoocurenceStatsWritingNode generator = new MscAuthorsCoocurenceStatsWritingNode();
        List<YElement> yelements = new ArrayList<YElement>();        
        YElement yelement = new YElement();
        addMscCode(yelement, "21");
        addMscCode(yelement, "22");    
        addAuthor(yelement, "piotr.dendek");
        addAuthor(yelement, "tomasz.kusmierczyk");
        yelements.add(yelement);
        
        YElement yelement2 = new YElement(); //no authors
        addMscCode(yelement2, "21");
        addMscCode(yelement2, "23");    
        yelements.add(yelement2);
        
        YElement yelement3 = new YElement();
        addMscCode(yelement3, "22");
        addMscCode(yelement3, "23");    
        addAuthor(yelement3, "michal.lukasik");
        addAuthor(yelement3, "tomasz.kusmierczyk");
        yelements.add(yelement3);
        
        generator.store(yelements, null);
        
        SymmetricTreeMapMatrix<String, Integer> m = generator.buildCoocurrenceMatrix();               
        
        Assert.assertEquals(new Integer(2), m.get("21", "21")); //2 unique authors for class
        Assert.assertEquals(new Integer(3), m.get("22", "22")); //3 unique authors for class
        Assert.assertEquals(new Integer(2), m.get("23", "23")); //3 unique
        
        Assert.assertEquals(new Integer(2), m.get("21", "22"));
        Assert.assertEquals(new Integer(1), m.get("21", "23"));
        Assert.assertEquals(new Integer(2), m.get("22", "23"));
    }
    
    

    @Test
    public void fourClassesFourDocumentsTest() throws Exception {
        MscAuthorsCoocurenceStatsWritingNode generator = new MscAuthorsCoocurenceStatsWritingNode();
        List<YElement> yelements = new ArrayList<YElement>();        
        YElement yelement = new YElement();
        addMscCode(yelement, "21");
        addMscCode(yelement, "22");    
        addAuthor(yelement, "piotr.dendek");
        addAuthor(yelement, "tomasz.kusmierczyk");
        yelements.add(yelement);
        
        YElement yelement2 = new YElement();
        addMscCode(yelement2, "21");
        addMscCode(yelement2, "23");    
        addAuthor(yelement2, "piotr.dendek");
        yelements.add(yelement2);
        
        YElement yelement3 = new YElement();
        addMscCode(yelement3, "22");
        addMscCode(yelement3, "23");    
        addAuthor(yelement3, "michal.lukasik");
        addAuthor(yelement3, "tomasz.kusmierczyk");
        yelements.add(yelement3);
        
        YElement yelement4 = new YElement();
        addMscCode(yelement4, "22");
        addMscCode(yelement4, "24");    
        addAuthor(yelement4, "stefan.niesiołowski");
        addAuthor(yelement4, "tomasz.kusmierczyk");
        yelements.add(yelement4);
        
        generator.store(yelements, null);
        
        SymmetricTreeMapMatrix<String, Integer> m = generator.buildCoocurrenceMatrix();               
        
        Assert.assertEquals(new Integer(2), m.get("21", "21")); //2 unique authors for class
        Assert.assertEquals(new Integer(4), m.get("22", "22")); //3 unique authors for class
        Assert.assertEquals(new Integer(3), m.get("23", "23")); //3 unique
        Assert.assertEquals(new Integer(2), m.get("24", "24")); //2 unique
        
        Assert.assertEquals(new Integer(2), m.get("21", "22"));        
        Assert.assertEquals(new Integer(2), m.get("21", "23"));
        Assert.assertEquals(new Integer(1), m.get("21", "24"));
        Assert.assertEquals(new Integer(3), m.get("22", "23"));
        Assert.assertEquals(new Integer(2), m.get("22", "24"));
        Assert.assertEquals(new Integer(1), m.get("23", "24"));
    }


    public void addAuthor(YElement yelement, String fingerprint) {
        YContributor author = new YContributor();
        author.addAttribute(new YAttribute(YConstants.AT_ZBL_AUTHOR_FINGERPRINT, fingerprint));
        yelement.addContributor(author);
    }

    public YElement addMscCode(YElement yelement, String code) {
        return yelement.addCategoryRef(new YCategoryRef(YConstants.EXT_CLASSIFICATION_MSC, code));
    }
    
    public static void main(String[] args) {
        MscAuthorsCoocurenceStatsTest test = new MscAuthorsCoocurenceStatsTest();
        try {
            test.singleClassSingleAuthorTest();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
