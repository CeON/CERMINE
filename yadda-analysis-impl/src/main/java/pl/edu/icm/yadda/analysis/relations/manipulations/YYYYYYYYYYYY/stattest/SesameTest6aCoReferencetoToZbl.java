package pl.edu.icm.yadda.analysis.relations.manipulations.YYYYYYYYYYYY.stattest;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;

import pl.edu.icm.yadda.analysis.relations.constants.RelConstants;

public class SesameTest6aCoReferencetoToZbl extends SesameBasicOperations{
	
	public static void main(String[] args) throws Exception {
		new SesameTest6aCoReferencetoToZbl().execute();
	}
	
	public void test() {
		try {
			System.out.println("======================================================");
			System.out.println("CO-REFERENCE-TO-ZBL ");
			System.out.println("======================================================");
			
			HashMap<String,Set<String>> hm = getShards();
			
			int totalNumbr = 0;
			
			HashMap<Integer,Integer> zlicz = new HashMap<Integer,Integer>(); 
			
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
						
						
						int inner = 0;
						
						String contribQuery = "" +
						" 			SELECT doc_r_id " +
					    " 			FROM " +
					    "				{<"+c1+">} <"+RelConstants.RL_IS_PERSON+"> {per}," +					    
					    "				{doc1} <"+RelConstants.RL_HAS_CONTRIBUTOR+"> {<"+c1+">}," +
					    "				{doc1} <"+RelConstants.RL_REFERENCES+"> {doc1r}," +
					    "				{doc1r} <"+RelConstants.RL_IS_DOCUMENT+"> {doc_r_id}," +
					    "				{<"+c2+">} <"+RelConstants.RL_IS_PERSON+"> {per}," +
					    "				{doc2} <"+RelConstants.RL_HAS_CONTRIBUTOR+"> {<"+c2+">}," +
					    "				{doc2} <"+RelConstants.RL_REFERENCES+"> {doc2r}," +
					    "				{doc2r} <"+RelConstants.RL_IS_DOCUMENT+"> {doc_r_id}" +	    
					    "           WHERE " +
					    "			doc1 != doc2	" +
						"";
						
						
						TupleQuery query = repository.getConnection().prepareTupleQuery(QueryLanguage.SERQL, contribQuery);
						TupleQueryResult res = query.evaluate();
						
						while(res.hasNext()){
							totalNumbr++;
							inner++;
							res.next();
						}			
						
						if(zlicz.containsKey(inner)){
							zlicz.put(inner, zlicz.get(inner)+1);
						}else{
							zlicz.put(inner, 1);
						}
						
					}
				}
			}
				
			System.out.println("All contribs with same co-reference: "+totalNumbr);
			
			for(Map.Entry<Integer, Integer> e : zlicz.entrySet()){
				System.out.println("co-contribution of size "+e.getKey()+" occured "+e.getValue());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
}
