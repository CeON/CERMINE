package pl.edu.icm.yadda.analysis.relations.manipulations.YYYYYYYYYYYY.stattest;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;

import pl.edu.icm.yadda.analysis.relations.constants.RelConstants;

public class SesameTest5aCoKeywords extends SesameBasicOperations{

	public static void main(String[] args) throws Exception {
		new SesameTest5aCoKeywords().execute();
	}
	
	public void test() {
		try {
			System.out.println("======================================================");
			System.out.println("CO-KEYWORDS ");
			System.out.println("======================================================");
			
			HashMap<String,Set<String>> hm = getShards();
			
			int totalNumbr = 0;
			
			HashMap<Integer,Integer> zlicz = new HashMap<Integer,Integer>();
			HashMap<Integer,Integer> innerzlicz = new HashMap<Integer,Integer>();
			
			int enumb = 0;
			for(Map.Entry<String, Set<String>> e : hm.entrySet()){
				enumb++;
				System.out.println("Surname: "+enumb+"/"+hm.entrySet().size()+"\tcurrentTotal:"+totalNumbr+"\t"+new Date());
				
				Set<String> set = e.getValue(); 
				
				Object[] arr = set.toArray();
				
				for(int i=0;i<arr.length;i++){
					String c1 = arr[i].toString();
					for(int j=i+1;j<arr.length;j++){
						String c2 = arr[j].toString();
						
						
						int innerA = 0;
						int innerB = 0;
						int splitinnerA = 0;
						int splitinnerB = 0;
						
						String contribQuery = "" +
						" 			SELECT k1,k2 " +
					    " 			FROM " +
					    "				{<"+c1+">} <"+RelConstants.RL_IS_PERSON+"> {per}," +					    
					    "				{doc1} <"+RelConstants.RL_HAS_CONTRIBUTOR+"> {<"+c1+">}," +
					    "				{doc1} <"+RelConstants.RL_TAG+"> {t1}," +
					    "				{t1} <"+RelConstants.RL_KEYWORDS+"> {k1}," +
					    "				{<"+c2+">} <"+RelConstants.RL_IS_PERSON+"> {per}," +
					    "				{doc2} <"+RelConstants.RL_HAS_CONTRIBUTOR+"> {<"+c2+">}," +
					    "				{doc2} <"+RelConstants.RL_TAG+"> {t2}," +
					    "				{t2} <"+RelConstants.RL_KEYWORDS+"> {k2}" +  
						"";
						
						
						TupleQuery query = repository.getConnection().prepareTupleQuery(QueryLanguage.SERQL, contribQuery);
						TupleQueryResult res = query.evaluate();
						
						Set<String> setA = new HashSet<String>();
						Set<String> setB = new HashSet<String>();
						Set<String> splitsetA = new HashSet<String>();
						Set<String> splitsetB = new HashSet<String>();
						
						while(res.hasNext()){
							BindingSet bs = res.next();
							
							String k1 = bs.getValue("k1").stringValue();
							setA.add(k1);
							splitsetA.addAll(Arrays.asList(k1.split(" ")));
							innerA++;
							
							String k2 = bs.getValue("k2").stringValue();
							setB.add(k2);
							splitsetB.addAll(Arrays.asList(k2.split(" ")));
							innerB++;
						}
						
						if(zlicz.containsKey(innerA)){
							zlicz.put(innerA, zlicz.get(innerA)+1);
						}else{
							zlicz.put(innerA, 1);
						}
						
						if(innerzlicz.containsKey(splitinnerA)){
							innerzlicz.put(splitinnerA, innerzlicz.get(splitinnerA)+1);
						}else{
							innerzlicz.put(splitinnerA, 1);
						}
						
					}
				}
			}
				
			System.out.println("All contribs with same co-contribs: "+totalNumbr);
			
			for(Map.Entry<Integer, Integer> e : zlicz.entrySet()){
				System.out.println("co-keyword of size "+e.getKey()+" occured "+e.getValue());
			}
			
			for(Map.Entry<Integer, Integer> e : innerzlicz.entrySet()){
				System.out.println("[split] co-keyword of size "+e.getKey()+" occured "+e.getValue());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
}
