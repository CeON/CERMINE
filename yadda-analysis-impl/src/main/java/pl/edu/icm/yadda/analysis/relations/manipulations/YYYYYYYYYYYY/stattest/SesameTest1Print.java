package pl.edu.icm.yadda.analysis.relations.manipulations.YYYYYYYYYYYY.stattest;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SesameTest1Print extends SesameBasicOperations{

	public static void main(String[] args) throws Exception {
		new SesameTest1Print().execute();
	}
		
	public void test() {
		try {
			System.out.println("======================================================");
			System.out.println("SHARD SURNAMES");
			System.out.println("======================================================");
			
			HashMap<String,Set<String>> hm = getShards();
			for(Map.Entry<String, Set<String>> e : hm.entrySet()){
				System.out.println(e.getKey());
			}
			return;
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
