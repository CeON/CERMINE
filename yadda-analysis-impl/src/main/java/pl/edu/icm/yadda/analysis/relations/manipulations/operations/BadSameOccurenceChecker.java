package pl.edu.icm.yadda.analysis.relations.manipulations.operations;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;

import pl.edu.icm.yadda.analysis.relations.auxil.ResultEntity;

public class BadSameOccurenceChecker implements Operation {

	@SuppressWarnings("unchecked")
	@Override
	public Object execute(Object repository, Map<String, Object> operationParam) {
			Repository repo = (Repository) repository;
			String trueRelationName = (String) operationParam.get("trueRelationName");
			String testedRelationName = (String) operationParam.get("testedRelationName");
	    	
	    	String truePositiveQueryString= "" +
			  "Select distinct c1,c2,pdb,p  \n" +
			  "from \n" +
			  "{c1} <"+trueRelationName+"> {pdb} \n" +
			  ",{c2} <"+trueRelationName+"> {pdb} \n" +
			  ",{c1} <"+testedRelationName+"> {p} \n" +
			  ",{c2} <"+testedRelationName+"> {p} \n" +
			  "";
	    	
	    	String trueNegativeQueryString= "" +
			  "Select distinct c1,c2,pdb1,pdb2,p1,p2  \n" +
			  "from \n" +
			  "{c1} <"+trueRelationName+"> {pdb1} \n" +
			  ",{c2} <"+trueRelationName+"> {pdb2} \n" +
			  ",{c1} <"+testedRelationName+"> {p1} \n" +
			  ",{c2} <"+testedRelationName+"> {p2} \n" +
			  "where \n" +
			  "pdb1!=pdb2 \n" +
			  "AND p1!=p2 \n" +
			  "";
	    	
	    	String falsePositiveQueryString= "" +
			  "Select distinct c1,c2,pdb1,pdb2,p  \n" +
			  "from \n" +
			  "{c1} <"+trueRelationName+"> {pdb1} \n" +
			  ",{c2} <"+trueRelationName+"> {pdb2} \n" +
			  ",{c1} <"+testedRelationName+"> {p} \n" +
			  ",{c2} <"+testedRelationName+"> {p} \n" +
			  "where \n" +
			  "pdb1!=pdb2 \n" +
			  "";
	    	
	    	
			String fatFalseNegativeQueryString= "" +
			  "Select distinct c1,c2,pdb,p1,p2  \n" +
			  "from \n" +
			  "{c1} <"+trueRelationName+"> {pdb} \n" +
			  ",{c2} <"+trueRelationName+"> {pdb} \n" +
			  ",[{c1} <"+testedRelationName+"> {p1}] \n" +
			  ",[{c2} <"+testedRelationName+"> {p2}] \n" +
			  "where \n" +
			  "p1!=p2  \n" +
			  "OR p1=NULL AND p2!=NULL \n" +
			  "OR p1!=NULL AND p2=NULL \n" +
			  "";
			
			String thinFalseNegativeQueryString= "" +
			  "Select distinct c1,c2,pdb,p1,p2  \n" +
			  "from \n" +
			  "{c1} <"+trueRelationName+"> {pdb} \n" +
			  ",{c2} <"+trueRelationName+"> {pdb} \n" +
			  ",[{c1} <"+testedRelationName+"> {p1}] \n" +
			  ",[{c2} <"+testedRelationName+"> {p2}] \n" +
			  "where \n" +
			  "p1!=p2  \n" +
			  "";
			
			TupleQueryResult result = null;
			
			try {
				
			    RepositoryConnection conn = repo.getConnection();

				List<ResultEntity> relist = new LinkedList<ResultEntity>();
			    relist.add(new ResultEntity().setName("TP").setOccurence(0)
					    .setQuerry(conn.prepareTupleQuery(QueryLanguage.SERQL, truePositiveQueryString)));
			    relist.add(new ResultEntity().setName("FP").setOccurence(0)
					    .setQuerry(conn.prepareTupleQuery(QueryLanguage.SERQL, falsePositiveQueryString)));
			    relist.add(new ResultEntity().setName("TN").setOccurence(0)
					    .setQuerry(conn.prepareTupleQuery(QueryLanguage.SERQL, trueNegativeQueryString)));
			    relist.add(new ResultEntity().setName("FatFN").setOccurence(0)
					    .setQuerry(conn.prepareTupleQuery(QueryLanguage.SERQL, fatFalseNegativeQueryString)));
			    relist.add(new ResultEntity().setName("ThisFN").setOccurence(0)
					    .setQuerry(conn.prepareTupleQuery(QueryLanguage.SERQL, thinFalseNegativeQueryString)));
			    
			    for(Object re1 : relist){
			    	ResultEntity re = (ResultEntity)re1;
			    	result = re.getQuerry().evaluate();
			    	int temp = 0;
				    while(result.hasNext()){
				    	BindingSet bs = result.next();
				    	temp++;
				    }
				    result.close();
				    re.setOccurence(temp);
				    re.setQuerry(null);
			    }			    
			    return relist;
			    
			} catch (Exception e) {
				return e;
			}
	    }
}
