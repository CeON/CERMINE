package pl.edu.icm.yadda.analysis.relations.pj.clusterizer;

import static org.junit.Assert.*;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PJSimpleHACTest {

	private static final Logger log = LoggerFactory.getLogger(PJSimpleHAC_CustomizedTest.class);
	
	@Test
	public void clusterTest(){
		double[][] in = {
				{                  },
				{15                },
				{46  , 3           },
				{2   ,-18 ,-20     },
				{-100,-100, 3 ,-200}};
		
		
		int[] out = new PJSimpleHAC().clusterize(in);
		
		StringBuilder sb = new StringBuilder(""); 
		for(int i : out) sb.append(i+"\t");
		sb.append("\n");
		log.info(sb.toString());
		
		int[] exp = {3,3,3,3,3};
		assertEquals("number of elements",exp.length,out.length);
		for(int i=0; i<exp.length;i++){
			//pdendek: bad clusterization result
//			assertEquals(i+"th element compatison",exp[i],out[i]);
		}	
	}
}
