package pl.edu.icm.yadda.analysis.relations;

import java.util.LinkedList;
import java.util.List;

import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import pl.edu.icm.yadda.analysis.relations.auxil.ResultEntity;
import pl.edu.icm.yadda.analysis.relations.auxil.XY;

/**
 * Class designed for performing basic manipulations 
 * (like massive deletion or massive predicate name change)
 * performed on Sesame based repository (tested for Sesame v.2.3.2)
 *  
 * @author pdendek
 * @deprecated
 *
 */
public class SesameRepositoryManipulator implements RepositoryManipulator,AccuracyRepositoryChecker{
	private static final Logger log = LoggerFactory.getLogger(SesameRepositoryManipulator.class);
    private Repository repository;
    
    private String oldRelationName = null;
    private String newRelationName = null;
    private String trueRelationName = null;
    private String testedRelationName = null;
    private String deleteRelationName = null;
    
    private List<ResultEntity> relist = null;  
    public String getOldRelationName() {
		return oldRelationName;
	}

	public void setOldRelationName(String oldRelationName) {
		this.oldRelationName = oldRelationName;
	}

	public String getNewRelationName() {
		return newRelationName;
	}

	public void setNewRelationName(String newRelationName) {
		this.newRelationName = newRelationName;
	}

	public String getTrueRelationName() {
		return trueRelationName;
	}

	public void setTrueRelationName(String trueRelationName) {
		this.trueRelationName = trueRelationName;
	}

	public String getTestedRelationName() {
		return testedRelationName;
	}

	public void setTestedRelationName(String testedRelationName) {
		this.testedRelationName = testedRelationName;
	}

	public String getDeleteRelationName() {
		return deleteRelationName;
	}

	public void setDeleteRelationName(String deleteRelationName) {
		this.deleteRelationName = deleteRelationName;
	}    
    
    public Repository getRepository() {
        return repository;
    }

    @Required
    public void setRepository(Repository repository) {
        this.repository = repository;
    }
    
    /**
     * Method calling {@link SesameRepositoryManipulator}.deleteRelation(String relationName),
     * where input argument is SesameRepositoryManipulator.deleteRelationName.
     * 
     * If SesameRepositoryManipulator.deleteRelationName is not set (or is equal to null), 
     * the "delete-relation-name not given" communicate will be given.
     * 
     *  For further information on method performance look at description of
     *  {@link SesameRepositoryManipulator}.deleteRelation(String relationName)
     */
    public void deleteRelationName(){
    	if(deleteRelationName != null){
    		deleteRelation(deleteRelationName);
    	}else{
    		log.debug("delete-relation-name not given");
    	}
    }
    
    /**
     * Method deleting from repository given predicate.
     * It is reflected by erasion of edge.
     * If we have two triples {{A-pred1-B},{A-pred2-C}}
     * after deletion of "pred1" result repo consist of
     * {{A-pred2-C}}.
     * 
     * Any caught exception will be printed to logger 
     * with no further consequences.
     * In case of exception there is no guarantee that database manipulation was successful 
     */
    public void deleteRelation(String relationName){
    	try {
    		repository.getConnection().remove((Resource)null, repository.getConnection().getValueFactory().createURI(relationName), (Resource)null,(Resource)null);
    	} catch (Exception e) {
			log.error(e.toString());
			for(StackTraceElement s: e.getStackTrace())
				log.error(s.toString());
    	}
    }
    
    /**
     * Method calling {@link SesameRepositoryManipulator}.changeRelationName(String oldRelationName, String newRelationName)
     * where input argument are SesameRepositoryManipulator fields (with same names)
     * 
     * If either of input argument is not set (or equal to null), 
     * the "old-relation-name or new-relation-name not given" communicate will be given.
     * 
     *  For further information on method performance look at description of
     *  {@link SesameRepositoryManipulator}.changeRelationName(String oldRelationName, String newRelationName)
     */
    public void changeRelationName(){
    	if(oldRelationName != null && 
    			newRelationName != null){
    		changeRelationName(oldRelationName,newRelationName);
    	}else{
    		log.debug("old-relation-name or new-relation-name not given");
    	}
    }    
    
    /**
     * Method gets all pairs subject-object connected by given predicate(oldRelationName),
     * deletes in one step tripples with this relation
     * and puts new tripples (containing predicate constructed from newRelationName) to store.  
     * 
     * It is assumed that oldRelationName and newRelationName can create {@link org.openrdf.model.URI}
     * 
     * Any caught exception will be printed to logger 
     * with no further consequences.
     * In case of exception there is no guarantee that database manipulation was successful
     */
    public void changeRelationName(String oldRelationName, String newRelationName){
    	LinkedList<XY> list = new LinkedList<XY>();
    	
		String uriQueryString = "" +
		  "Select distinct x,y  \n" +
		  "from \n" +
		  "{x} <"+oldRelationName+"> {y} \n" +
		  "where isURI(y) \n" +
		  "";
		
//		String literalQueryString = "" +
//		  "Select distinct x,y  \n" +
//		  "from \n" +
//		  "{x} <"+oldRelationName+"> {y} \n" +
//		  "where isLiteral(y) \n" +
//		  "";
		
		TupleQuery query = null;
		TupleQueryResult res = null;
		
		try {
			ValueFactory vf = repository.getConnection().getValueFactory();
	    	
	    	vf.createURI(oldRelationName);
	    	URI pn = vf.createURI(newRelationName);
			
			
		    RepositoryConnection conn = repository.getConnection();
			query = conn.prepareTupleQuery(QueryLanguage.SERQL, uriQueryString);
			res = query.evaluate();
		    while(res.hasNext()){
		    	BindingSet bs = res.next();
		    	list.add(
		    			new XY().
		    			setX(bs.getValue("x").toString()).
		    			setY(bs.getValue("y").toString())
		    			);
		    }
		    res.close();
		    
		    for(XY xy : list){
		    	URI x = vf.createURI(xy.x);
		    	URI y = vf.createURI(xy.y);
		    	repository.getConnection().add(x, pn, y,(Resource)null);
		    }
		    list.clear();
		    
		    //pdendek: predicate is never Literal!
//			query = conn.prepareTupleQuery(QueryLanguage.SERQL, literalQueryString);
//			res = query.evaluate();
//		    while(res.hasNext()){
//		    	BindingSet bs = res.next();
//		    	list.add(
//	    			new XY().
//	    			setX(bs.getValue("x").toString()).
//	    			setY(bs.getValue("y").toString())
//	    			);
//
//		    }
//		    res.close();
		    
		    for(XY xy : list){
		    	URI x = vf.createURI(xy.x);
		    	Literal y = vf.createLiteral(xy.y);
		    	repository.getConnection().add(x, pn, y,(Resource)null);
		    }
		    list.clear();
		    
		} catch (RepositoryException e) {
			log.error(e.toString());
			for(StackTraceElement s: e.getStackTrace())
				log.error(s.toString());
		} catch (MalformedQueryException e) {
			log.error(e.toString());
			for(StackTraceElement s: e.getStackTrace())
				log.error(s.toString());
		} catch (QueryEvaluationException e) {
			log.error(e.toString());
			for(StackTraceElement s: e.getStackTrace())
				log.error(s.toString());
		} catch (Exception e) {
			log.error(e.toString());
			for(StackTraceElement s: e.getStackTrace())
				log.error(s.toString());
		}
    }

    
    /**
     * Any caught exception will be printed to logger 
     * with no further consequences.
     * In case of exception there is no guarantee that database manipulation was successful
     * 
     * @return List of {@link SesameRepositoryManipulator.ResultEntity} containing name of checked results 
     * ("TP"-TruePositive,"FP"-FalsePositive, etc.) connected with its occurence number.
     * ATTENTION 1: FalseNegative is divided into "FatFN" and "ThisFN".
     * ThinFalseNegative (ThinFN) means that contributors {c1,c2) are person p but then point to another persons.
	 * FatFalseNegative (FatFN) means that contributors {c1,c2) are person p but then point to another persons or at least one of them is empty.
	 * ATTENTION 2: Result of sameOccurenceCheck(String trueRelationName,String testedRelationName) method is put into {@link SesameRepositoryManipulator}.relist;
	 * ATTENTION 3: One must proceed with sameOccurenceCheck(String trueRelationName,String testedRelationName) or provide values in fields trueRelationName, testedRelationName
	 *  to use printAccuracyStats() method.   
     */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<Object> sameOccurenceCheck(String trueRelationName,
			String testedRelationName) {
    	
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
			
		    RepositoryConnection conn = repository.getConnection();

			List relist = new LinkedList();
		    
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
			    	result.next();
			    	temp++;
			    }
			    result.close();
			    re.setOccurence(temp);
			    re.setQuerry(null);
		    }
		    
		    this.relist=relist;
		    return relist;
		    
		} catch (RepositoryException e) {
			log.error(e.toString());
			for(StackTraceElement s: e.getStackTrace())
				log.error(s.toString());
		} catch (MalformedQueryException e) {
			log.error(e.toString());
			for(StackTraceElement s: e.getStackTrace())
				log.error(s.toString());
		} catch (QueryEvaluationException e) {
			log.error(e.toString());
			for(StackTraceElement s: e.getStackTrace())
				log.error(s.toString());
		} catch (Exception e) {
			log.error(e.toString());
			for(StackTraceElement s: e.getStackTrace())
				log.error(s.toString());
		}
		return new LinkedList();
    }
	
	
    /**
     * Method digesting results of sameOccurenceCheck(String trueRelationName,String testedRelationName) stored in {@link SesameRepositoryManipulator}.relist
     * or (in case of relist set to null) calling  sameOccurenceCheck(String trueRelationName,String testedRelationName).
     * 
     * Result is message summarizing tests done in sameOccurenceCheck(String trueRelationName,String testedRelationName).
     * for given repository.
     * 
     * If either  sameOccurenceCheck(String trueRelationName,String testedRelationName) where not used 
     * or fields trueRelationName, testedRelationName are set to null
     * the "true-relation-name or tested-relation-name not given" communicate will be given.
     */
    public void printAccuracyStats(){
    	
    	if(relist==null){
    		if(this.trueRelationName==null || this.testedRelationName==null){
    			log.debug("true-relation-name or tested-relation-name not given");
    			return;
    		}
    		else{
    			sameOccurenceCheck(this.trueRelationName, this.testedRelationName);
    		}
    	}
    	
    	int tp = 0;
    	int tn = 0;
    	int fp = 0;
    	int fn = 0;
    	int ffn = 0;
    	int tfn = 0;
    	int t = 0;
    	int f = 0;
    	int p = 0;
    	int n = 0;
    	
		for(ResultEntity re : relist){
			if(re.name.equals("TP")){
				tp=re.occurence;
				t+=re.occurence;
				p+=re.occurence;
			}else if(re.name.equals("FP")){
				fp=re.occurence;
				p+=re.occurence;
				f+=re.occurence;
			}else if(re.name.equals("TN")){
				tn=re.occurence;
				n+=re.occurence;
				t+=re.occurence;
				//already counted in FatFN
//			}else if(re.name.equals("ThinFN")){
//				tfn+=re.occurence;
//				fn+=re.occurence;
//				n+=re.occurence;
//				f+=re.occurence;
			}else if(re.name.equals("FatFN")){
				ffn+=re.occurence;
				fn+=re.occurence;
				n+=re.occurence;
				f+=re.occurence;
			}
		}
    	log.debug("-------------------- Description --------------------------------");
		log.debug("TP: correct association of contributor with person");
		log.debug("TN: correct lack of association of contributor with person");
		log.debug("FP: incorrect association of contributor with person");
		log.debug("FN: incorrect lack of association of contributor with person");
		log.debug("ThinFalseNegative (ThinFN) means that contributors {c1,c2) are person p but then point to another persons");
		log.debug("FatFalseNegative (FatFN) means that contributors {c1,c2) are person p but then point to another persons or at least one of them is empty");
		log.debug("---------------------- Results ----------------------------------");		
		log.debug("T: "+t);
		log.debug("F: "+f);
		log.debug("TP: "+tp);
    	log.debug("TN: "+tn);
    	log.debug("FP: "+fp);
    	log.debug("FN: "+fn);
    	log.debug("FatFN: "+ffn);
    	log.debug("ThinFN: "+tfn);
    	log.debug("-----------------------------------------------------------------------");
    	log.debug("");
    	log.debug("");
    	log.debug("");
    	log.debug("-------------------- Description --------------------------------");
		log.debug("True Positive Rate (Sensitivity,TPR): TP/P");
		log.debug("False Positive Rate (FPR): FP/N");
		log.debug("Accuracy: (TP+TN)/(P+N)");
		log.debug("Precision is the probability that a (randomly selected) retrieved document is relevant");
		log.debug("Precision: (TP)/(P)");
		log.debug("Recall is the probability that a (randomly selected) relevant document is retrieved in a search");
		log.debug("Recall: (TP)/(TP+FN)");
		log.debug("F1 Score: 2*(Precision*Recall)/(Precision+Recall)   ");
		log.debug("---------------------- Results ----------------------------------");
		log.debug("True Positive Rate (Sensitivity,TPR): "+(tp*100/(double)p)+" [%]");
		log.debug("False Positive Rate (FPR): "+(fp*100/(double)n)+" [%]");
		log.debug("Accuracy: "+((tp+tn)*100/(double)(p+n))+" [%]");
		double precision = (tp/(double)p);
		log.debug("Precision: "+precision+"[%]");
		double recall = (tp/(double)(tp+fn));
		log.debug("Recall: "+recall+"[%]");
		log.debug("F1 Score: "+(2*precision*recall/(precision+recall))+" [%]");
    }
}
