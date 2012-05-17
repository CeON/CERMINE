//package pl.edu.icm.yadda.analysis.relations.manipulations.operations;
//
//import java.util.LinkedList;
//import java.util.Map;
//
//import org.openrdf.model.Resource;
//import org.openrdf.model.ValueFactory;
//import org.openrdf.query.BindingSet;
//import org.openrdf.query.QueryLanguage;
//import org.openrdf.query.TupleQuery;
//import org.openrdf.query.TupleQueryResult;
//import org.openrdf.repository.Repository;
//import org.openrdf.repository.RepositoryConnection;
//
//import pl.edu.icm.yadda.analysis.relations.constants.RelConstants;
//
//public abstract class WeightRecalculatorBase implements Operation{
//
//	@Override
//	public Object execute(Object repository, Map<String, Object> operationParam) {
//		try {
//			Repository repo = (Repository) repository;
//			RepositoryConnection con = repo.getConnection();
//			ValueFactory vf = con.getValueFactory();
//			String testedIsPersonRelation = (String) operationParam.get("testedIsPersonRelation");
//			
//
//			String contribQuery = "" +
//			  " Select distinct i,observ, ifSame, weight, featurePrediction \n " +
//			  " from \n " +
//			  " {observ} <"+RelConstants.RL_OBSERVATION_ID+"> {i}, \n" +
//			  " {observ} <"+RelConstants.RL_OBSERVATION_CONTAINS_SAME_PERSON+"> {ifSame}, \n " +
//			  " {observ} <"+RelConstants.RL_OBSERVATION_HAS_WEIGHT+"> {weight}, \n " +
//			  " {observ} <"+testedIsPersonRelation+"> {featurePrediction} \n " +
//			  "";
//			
//			TupleQuery query = con.prepareTupleQuery(QueryLanguage.SERQL, contribQuery);
//			TupleQueryResult res = query.evaluate();
//			
//			LinkedList<String> repe = new LinkedList<String>();
//			while(res.hasNext()){
//				BindingSet bs = res.next();
//				if(repe.contains(bs.getBinding("i").getValue().stringValue())) continue;
//				else repe.add(bs.getBinding("i").getValue().stringValue());				
//				repo.getConnection().remove(
//						vf.createURI(bs.getBinding("observ").getValue().stringValue()),
//						vf.createURI(RelConstants.RL_OBSERVATION_HAS_WEIGHT), 
//						(Resource)null,(Resource)null);
//				repo.getConnection().add(
//						vf.createURI(bs.getBinding("observ").getValue().stringValue()),
//						vf.createURI(RelConstants.RL_OBSERVATION_HAS_WEIGHT), 
//						vf.createLiteral(recalculate(bs)) ,
//						(Resource)null);
//			}
//			res.close();
//			return null;	
//		} catch (Exception e) {
//			return e;
//		}
//	}
//
//	
//	
//	
//	protected String recalculate(BindingSet bs){
//		if("TRUE".equals(bs.getValue("ifSame").stringValue())){
//			if("TRUE".equals(bs.getValue("featurePrediction").stringValue())){
//				return recalculateCorrect(bs);
//			}
//			else{
//				return recalculateWrong(bs);
//			}
//		}
//		else{
//			if("TRUE".equals(bs.getValue("featurePrediction").stringValue())){
//				return recalculateWrong(bs);
//			}
//			else{
//				return recalculateCorrect(bs);
//			}
//		}
//	}
//	
//	protected String recalculateCorrect(BindingSet bs){
//		//correctSituation
//		return recalculateCorrectWeight(Double.parseDouble(bs.getValue("weight").stringValue()))+"";
//	}
//
//	protected String recalculateWrong(BindingSet bs){
//		//incorrectSituation
//		
//		return recalculateIncorrectWeight(Double.parseDouble(bs.getValue("weight").stringValue()))+"";
//	}
//	
//	protected abstract double recalculateIncorrectWeight(double i);
//	
//	protected abstract double recalculateCorrectWeight(double i);
//}
