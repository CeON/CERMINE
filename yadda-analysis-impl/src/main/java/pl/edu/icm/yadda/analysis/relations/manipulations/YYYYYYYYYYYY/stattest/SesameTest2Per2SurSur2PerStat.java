package pl.edu.icm.yadda.analysis.relations.manipulations.YYYYYYYYYYYY.stattest;

import java.util.HashMap;
import java.util.Map;

import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;

import pl.edu.icm.yadda.analysis.relations.constants.RelConstants;

public class SesameTest2Per2SurSur2PerStat  extends SesameBasicOperations{
	
	public static void main(String[] args) throws Exception {
		new SesameTest2Per2SurSur2PerStat().execute();
	}
	
	public void test() {
		try {
			System.out.println("======================================================");
			System.out.println("====== KONTRYBUTORZY Z PEROSNAMI + EMAILEM ===========");
			System.out.println("======================================================");
			

			String contribQuery = "" +
			" Select distinct c,p,s" +
			" from" +
			" {c} <"+RelConstants.RL_IS_PERSON+"> {p}" +
			" ,{c} <"+RelConstants.RL_SURNAME+"> {s}" +
			" ,{c} <"+RelConstants.RL_CONTACT_EMAIL+"> {e}" +
			" where" +
			" p!=<http://yadda.icm.edu.pl/person#zbl#->" +
			" and e!=\"\"" +
			"";
			
			TupleQuery query = repository.getConnection().prepareTupleQuery(QueryLanguage.SERQL, contribQuery);
			TupleQueryResult res = query.evaluate();
			
			HashMap<String, HashMap<String, Integer>> per2sur = new HashMap<String, HashMap<String, Integer>>();
			HashMap<String, HashMap<String, Integer>> sur2per = new HashMap<String, HashMap<String, Integer>>();
			
			int totalNumbr = 0;
			int blank = 0;
			while(res.hasNext()){
				BindingSet bs = res.next();
				String per = bs.getValue("p").stringValue();
				String sur = bs.getValue("s").stringValue();
				
				if(per2sur.containsKey(per))
					if(per2sur.get(per).containsKey(sur)){
						Integer i = per2sur.get(per).get(sur);
						i++;
						per2sur.get(per).put(sur,i);
					}else{
						per2sur.get(per).put(sur,1);
					}
				else{
					HashMap<String,Integer> hm = new HashMap<String,Integer>(); 
					hm.put(sur, 1);
					per2sur.put(per, hm);
				}
				
				if(sur2per.containsKey(sur))
					if(sur2per.get(sur).containsKey(per)){
						Integer i = sur2per.get(sur).get(per);
						i++;
						sur2per.get(sur).put(per,i);
					}else{
						sur2per.get(sur).put(per,1);
					}
				else{
					HashMap<String,Integer> hm = new HashMap<String,Integer>(); 
					hm.put(per, 1);
					sur2per.put(sur, hm);
				}
			}	
			
			System.out.println("====================================================");
			System.out.println("=================PER2SUR============================");
			System.out.println("====================================================");
			for(Map.Entry<String, HashMap<String,Integer>> outer : per2sur.entrySet()){
				if(outer.getValue().size()>1){
					System.out.println("Outer: "+outer.getKey());
					for(Map.Entry<String, Integer> inner : outer.getValue().entrySet()){
						System.out.println("\tInner: {"+inner.getKey()+", "+inner.getValue()+"}");
					}
				}
			}
			
			System.out.println("====================================================");
			System.out.println("=================SUR2PER============================");
			System.out.println("====================================================");
			
			
			HashMap<Integer,Integer> shardSizeNumber = new HashMap<Integer,Integer>(); 
			int number = 0;
			for(Map.Entry<String, HashMap<String,Integer>> outer : sur2per.entrySet()){
				if(outer.getValue().size()>1 || ((Map.Entry<String,Integer>) outer.getValue().entrySet().toArray()[0]).getValue()>1){
					int innernumber=0;
					number++;
					System.out.println("Outer: "+outer.getKey());
					for(Map.Entry<String, Integer> inner : outer.getValue().entrySet()){
						System.out.println("\tInner: {"+inner.getKey()+", "+inner.getValue()+"}");
						innernumber+=inner.getValue();
					}
					if(shardSizeNumber.containsKey(innernumber)){
						shardSizeNumber.put(innernumber, shardSizeNumber.get(innernumber)+1);
					}else{
						shardSizeNumber.put(innernumber, 1);
					}
				}else{
					
				}
			}
			System.out.println("Multiple person match with "+number+" surnames");
			
			for(Map.Entry<Integer, Integer> e : shardSizeNumber.entrySet()){
				System.out.println("shard size: "+e.getKey()+" occured "+e.getValue()+" times");
			}
			
			
		} catch (Exception e) {
		}
	}

}
