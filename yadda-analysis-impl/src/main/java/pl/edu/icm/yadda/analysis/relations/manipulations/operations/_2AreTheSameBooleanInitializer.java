//package pl.edu.icm.yadda.analysis.relations.manipulations.operations;
//
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
//public class _2AreTheSameBooleanInitializer implements Operation {
//
//	@Override
//	public Object execute(Object repository, Map<String, Object> operationParam) {
//		try {
//			Repository repo = (Repository) repository;
//			RepositoryConnection con = repo.getConnection();
//			con.setAutoCommit(false);
//			
//			ValueFactory vf = con.getValueFactory();
//			String isPersonPredicate = RelConstants.RL_IS_PERSON;
//			
//			/*
//			 * This query is supposed to find observation#X---contrib#Y---Person#A
//			 * 											   +--contrib#Z---Person#B
//			 */
//			String contribQuery = "" +
//			  "Select observ, xPerson, yPerson  \n" +
//			  "from \n" +
//			  "{observ} <"+RelConstants.RL_OBSERVATION_ID+"> {},\n " +
//			  "{observ} <"+RelConstants.RL_OBSERVATION_CONTRIBUTOR+"> {x}, \n" +
//			  "{observ} <"+RelConstants.RL_OBSERVATION_CONTRIBUTOR+"> {y}, \n" +
//			  "{x} <"+isPersonPredicate+"> {xPerson}, \n" +
//			  "{y} <"+isPersonPredicate+"> {yPerson} " +
//			  " WHERE \n" +
//			  " x!=y \n" +
//			  "";
//			
//			TupleQuery query = con.prepareTupleQuery(QueryLanguage.SERQL, contribQuery);
//			TupleQueryResult res = query.evaluate();
//			int i=0;
//			while(res.hasNext()){
//				i++;
//				BindingSet bs = res.next();				
//				String xPerson = bs.getValue("xPerson").toString();
//				String yPerson = bs.getValue("yPerson").toString();
//				if(xPerson.equals(yPerson)){
//					con.add(vf.createURI(bs.getValue("observ").toString()),
//							vf.createURI(RelConstants.RL_OBSERVATION_CONTAINS_SAME_PERSON), 
//							vf.createLiteral("TRUE"),(Resource)null);
//				}
//				else{
//					con.add(vf.createURI(bs.getValue("observ").toString()),
//							vf.createURI(RelConstants.RL_OBSERVATION_CONTAINS_SAME_PERSON), 
//							vf.createLiteral("FALSE"),(Resource)null);
//				}
//			}
//			res.close();
//			con.commit();
//			con.close();
//			con=null;
//			return null;	
//		} catch (Exception e) {
//			return e;
//		}
//	}
//}
