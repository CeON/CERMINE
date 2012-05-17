package pl.edu.icm.yadda.analysis.mscsimilarity;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import pl.edu.icm.yadda.analysis.datastructures.TreeMapMatrix;
import pl.edu.icm.yadda.bwmeta.model.YCategoryRef;
import pl.edu.icm.yadda.bwmeta.model.YConstants;
import pl.edu.icm.yadda.bwmeta.model.YElement;

public class MscClassesCooccurrenceStatsTest {
    
    
    @Test
    public void nodataCoocurrence() throws Exception {
        MscClassesCooccurrenceStatsWritingNode generator = new MscClassesCooccurrenceStatsWritingNode();
        List<YElement> yelements = new ArrayList<YElement>();        
        generator.store(yelements, null);
        
        TreeMapMatrix<String, String, Integer> m = generator.getCooccurrence();
        Assert.assertEquals( new Integer(0), m.get("21", "22", 0) );
        Assert.assertEquals( new Integer(0), m.get("21", "21", 0) );        
        Assert.assertEquals( new Integer(0), m.get("22", "22", 0) );
    }

    @Test
    public void pairSingleCoocurrence() throws Exception {
        MscClassesCooccurrenceStatsWritingNode generator = new MscClassesCooccurrenceStatsWritingNode();
        List<YElement> yelements = new ArrayList<YElement>();        
        YElement yelement = new YElement();
        addMscCode(yelement, "21");
        addMscCode(yelement, "22");         
        yelements.add(yelement);
        generator.store(yelements, null);
        
        TreeMapMatrix<String, String, Integer> m = generator.getCooccurrence();
        Assert.assertEquals( new Integer(1), m.get("21", "21") );
        Assert.assertEquals( new Integer(1), m.get("22", "22") );
        Assert.assertEquals( new Integer(1), m.get("21", "22") );        
    }
    
    @Test
    public void pairDoubleCoocurrence() throws Exception {
        MscClassesCooccurrenceStatsWritingNode generator = new MscClassesCooccurrenceStatsWritingNode();
        List<YElement> yelements = new ArrayList<YElement>();        
        YElement yelement = new YElement("1");
        addMscCode(yelement, "21");
        addMscCode(yelement, "22");         
        yelements.add(yelement);        
        
        YElement yelement2 = new YElement("2");
        addMscCode(yelement2, "21");
        addMscCode(yelement2, "22");         
        yelements.add(yelement2);
        generator.store(yelements, null);
        
        TreeMapMatrix<String, String, Integer> m = generator.getCooccurrence();
        Assert.assertEquals( new Integer(2), m.get("21", "21") );        
        Assert.assertEquals( new Integer(2), m.get("22", "22") );
        Assert.assertEquals( new Integer(2), m.get("21", "22") );
    }
    
    @Test
    public void twoPairsCoocurrence() throws Exception {
        MscClassesCooccurrenceStatsWritingNode generator = new MscClassesCooccurrenceStatsWritingNode();
        List<YElement> yelements = new ArrayList<YElement>();        
        YElement yelement = new YElement();
        addMscCode(yelement, "21");
        addMscCode(yelement, "22");         
        yelements.add(yelement);        
        
        YElement yelement2 = new YElement();
        addMscCode(yelement2, "23");
        addMscCode(yelement2, "24");         
        yelements.add(yelement2);
        generator.store(yelements, null);
        
        TreeMapMatrix<String, String, Integer> m = generator.getCooccurrence();
        Assert.assertEquals( new Integer(1), m.get("21", "21") );
        Assert.assertEquals( new Integer(1), m.get("22", "22") );
        Assert.assertEquals( new Integer(1), m.get("23", "23") );
        Assert.assertEquals( new Integer(1), m.get("24", "24") );
        Assert.assertEquals( new Integer(1), m.get("21", "22") );
        Assert.assertEquals( new Integer(1), m.get("23", "24") );    
    }
    
    @Test
    public void threeGroupsCoocurrence() throws Exception {
        MscClassesCooccurrenceStatsWritingNode generator = new MscClassesCooccurrenceStatsWritingNode();
        List<YElement> yelements = new ArrayList<YElement>();        
        YElement yelement = new YElement();
        addMscCode(yelement, "21");
        addMscCode(yelement, "22");         
        yelements.add(yelement);        
        
        YElement yelement2 = new YElement();
        addMscCode(yelement2, "21");
        addMscCode(yelement2, "23");         
        yelements.add(yelement2);
        generator.store(yelements, null);
        
        TreeMapMatrix<String, String, Integer> m = generator.getCooccurrence();
        Assert.assertEquals( new Integer(2), m.get("21", "21") );
        Assert.assertEquals( new Integer(1), m.get("22", "22") );
        Assert.assertEquals( new Integer(1), m.get("23", "23") );
        Assert.assertEquals( new Integer(1), m.get("21", "22", 0) );
        Assert.assertEquals( new Integer(1), m.get("21", "23", 0) );    
        Assert.assertEquals( new Integer(0), m.get("22", "23", 0) );
    }
 
    
    public YElement addMscCode(YElement yelement, String code) {
        return yelement.addCategoryRef(new YCategoryRef(YConstants.EXT_CLASSIFICATION_MSC, code));
    }
}
