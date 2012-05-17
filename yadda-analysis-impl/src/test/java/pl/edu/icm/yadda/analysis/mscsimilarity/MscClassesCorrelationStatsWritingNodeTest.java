package pl.edu.icm.yadda.analysis.mscsimilarity;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import pl.edu.icm.yadda.analysis.datastructures.SymmetricTreeMapMatrix;
import pl.edu.icm.yadda.analysis.datastructures.SymmetricTreeMapMatrixTest;
import pl.edu.icm.yadda.analysis.datastructures.TreeMapMatrix;
import pl.edu.icm.yadda.bwmeta.model.YCategoryRef;
import pl.edu.icm.yadda.bwmeta.model.YConstants;
import pl.edu.icm.yadda.bwmeta.model.YElement;

/**
 * 
 * @author tkusm
 *
 */
public class MscClassesCorrelationStatsWritingNodeTest {

	@Test
	public void testCorrelationMatrix() throws Exception {
		MscClassesCorrelationStatsWritingNode generator = new MscClassesCorrelationStatsWritingNode();
        List<YElement> yelements = new ArrayList<YElement>();                
        
        addNextYElement(yelements, new String[]{"1", "2",  	   "4"});        
        addNextYElement(yelements, new String[]{ 	  	   	   "4"});        
        addNextYElement(yelements, new String[]{"1",  	   	   "4"});        
        addNextYElement(yelements, new String[]{ 	  	  "3", "4"});        
        addNextYElement(yelements, new String[]{"1",  	   	   "4"});        
        addNextYElement(yelements, new String[]{ 	 "2", "3", "4"});        
        addNextYElement(yelements, new String[]{"1",  	   	   "4"});        
        addNextYElement(yelements, new String[]{ 	  	   	   "4"});        
        addNextYElement(yelements, new String[]{"1",  	       "4"});        
        addNextYElement(yelements, new String[]{ 	  	   	   "5"});        
        addNextYElement(yelements, new String[]{ 	  	  "3", "4"});        
        addNextYElement(yelements, new String[]{ 	  	   	   "4"});        
        
        generator.store(yelements, null);
        SymmetricTreeMapMatrix<String, Double> m = generator.getCorrelationMatrix();
        
        Assert.assertEquals(0.075593, m.get("1", "2"), 0.00001);
        Assert.assertEquals(-0.48795, m.get("1", "3"), 0.00001);
        Assert.assertEquals(0.25482, m.get("1", "4"), 0.00001);
        Assert.assertEquals(-0.25482, m.get("1", "5"), 0.00001);
        Assert.assertEquals(0.25820, m.get("2", "3"), 0.00001);
        Assert.assertEquals(0.13484, m.get("2", "4"), 0.00001);
        Assert.assertEquals(-0.13484, m.get("2", "5"), 0.00001);
        Assert.assertEquals(0.17408, m.get("3", "4"), 0.00001);
        Assert.assertEquals(-0.17408, m.get("3", "5"), 0.00001);
        Assert.assertEquals(-1.0, m.get("4", "5"), 0.00001);
        
        Assert.assertEquals(1.0, m.get("1", "1"), 0.000001);
        Assert.assertEquals(1.0, m.get("2", "2"), 0.000001);
        Assert.assertEquals(1.0, m.get("3", "3"), 0.000001);
        Assert.assertEquals(1.0, m.get("4", "4"), 0.000001);
        Assert.assertEquals(1.0, m.get("5", "5"), 0.000001);
	}

	private void addNextYElement(List<YElement> yelements, String[] codes) {
		YElement yelement = new YElement();
        for (String code: codes) {
        	addMscCode(yelement, code);
        }
        yelements.add(yelement);
	}
	
    public YElement addMscCode(YElement yelement, String code) {
        return yelement.addCategoryRef(new YCategoryRef(YConstants.EXT_CLASSIFICATION_MSC, code));
    }
}
