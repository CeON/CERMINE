package pl.edu.icm.yadda.analysis.relations;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openrdf.model.Value;
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

import pl.edu.icm.yadda.analysis.AnalysisException;
import pl.edu.icm.yadda.analysis.relations.constants.RelConstants;
import pl.edu.icm.yadda.bwmeta.model.YConstants;

/**
 * Sesame-powered implementation of {@link PersonDirectory}.
 */
public class SesamePersonDirectory implements PersonDirectory, PersonDirectoryBackend {
    // TODO Reference to relevant constants.
    private static final String OTHER = YConstants.CR_OTHER;
    
    private Repository repository;
    
	private static final Logger log = LoggerFactory.getLogger(SesamePersonDirectory.class);
	
    /* (non-Javadoc)
     * @see pl.edu.icm.yadda.analysis.relations.PersonDirectory#getPerson(java.lang.String)
     */
    @Override
    public String getPerson(String contributionId) {
    	String retVal=null;
    	
		String zeroQueryString = "" +
		  "Select distinct person  \n" +
		  "from \n" +
		  "{"+contributionId+"} <"+RelConstants.RL_IS_PERSON+"> {person} \n" +
		  "";
		
		TupleQuery query = null;
		TupleQueryResult res = null;
		
		try {
		    RepositoryConnection conn = repository.getConnection();
			query = conn.prepareTupleQuery(QueryLanguage.SERQL, zeroQueryString);
			res = query.evaluate();
		    if(res.hasNext()){
		    	retVal = res.next().getValue("person").toString();
		    }
		    if(res.hasNext()){
		    	int more = 0;
		    	while(res.hasNext()){
			    	res.next();
			    	more++;
			    }
		    	log.warn("Contributor "+contributionId+" has "+(more+1)+"person ids!");
		    }
		    conn.close();
			res.close();
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
		}
        return retVal;
    }

    //TODO czym to się różni od 
    //{@link pl.edu.icm.yadda.analysis.relations.SesamePersonDirectory#members(String)}
    /* (non-Javadoc)
     * @see pl.edu.icm.yadda.analysis.relations.PersonDirectory#getContributions(java.lang.String, java.lang.String)
     */
    @Override
    public List<String> getContributions(String personId, String role) {
        if (role != null || !OTHER.equals(role)) {
            return Collections.emptyList();
        }
        
    	LinkedList<String> retList = new LinkedList<String>();
    	
		String zeroQueryString = "" +
		  "Select distinct contrib  \n" +
		  "from \n" +
		  "{contrib} <"+RelConstants.RL_IS_PERSON+"> {"+personId+"} \n" +
		  "";
		
		TupleQuery query = null;
		TupleQueryResult res = null;
		
		try {
			
            RepositoryConnection conn = repository.getConnection();
			query = conn.prepareTupleQuery(QueryLanguage.SERQL, zeroQueryString);
			res = query.evaluate();
			
			while(res.hasNext())
		    	retList.add(res.next().getValue("contrib").toString());
			
			res.close();
			conn.close();
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
		}
        return retList;
    }

    /* (non-Javadoc)
     * @see pl.edu.icm.yadda.analysis.relations.PersonDirectoryBackend#groupIds()
     */
    @Override
    public Iterable<String> groupIds() throws AnalysisException {
    	
    	LinkedList<String> retList = new LinkedList<String>();
    	
//		String zeroQueryString = "" +
//		  "Select distinct y  \n" + /*w razie problemow z pamięcią wyłącz distinc i dodawaj do zbioru (Set)*/
//		  "from \n" +
//		  "{} <"+RelConstants.RL_HAS_TRACE+"> {y} \n" +
//		  "";
		
		String zeroQueryString = "" +
		" 			SELECT distinct sur " +
	    " 			FROM " +
	    "				{} <"+RelConstants.RL_SURNAME+"> {sur}" +
	    "";
		
		TupleQuery query = null;
		TupleQueryResult res = null;
		
		try {
            RepositoryConnection conn = repository.getConnection();
            
            //dodanie jeszcze nie istniejących indeksów
//            KuraForenameSurnameTrace trace = new KuraForenameSurnameTrace();
//    		trace.setRepositoryConnection(conn);
//    		trace.analyze(conn);
			
			query = conn.prepareTupleQuery(QueryLanguage.SERQL, zeroQueryString);
			res = query.evaluate();
			
			while(res.hasNext()){
//				String x = res.next().getValue("x").toString();
				String y = res.next().getValue("sur").stringValue();
//				System.out.println(x);
				retList.add(y);
			}
		    	
			
			res.close();
			conn.close();
		} catch (RepositoryException e) {
			e.toString();
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
		}
        return retList;
    }

    
    
    /* (non-Javadoc)
     * @see pl.edu.icm.yadda.analysis.relations.PersonDirectoryBackend#members(java.lang.String)
     */
    //TODO czym to się różni od 
    //{@link pl.edu.icm.yadda.analysis.relations.SesamePersonDirectory#getContributors(String)}
    @Override
    public List<String> members(String groupId) throws AnalysisException {
    	LinkedList<String> retList = new LinkedList<String>();
    	
		String zeroQueryString = "" +
		  "Select distinct contrib  \n" +
		  "from \n" +
		  "{contrib} <"+RelConstants.RL_SURNAME+"> {\""+groupId+"\"}" +
		  "";
		
		TupleQuery query = null;
		TupleQueryResult res = null;
		
		try {
            RepositoryConnection conn = repository.getConnection();
			query = conn.prepareTupleQuery(QueryLanguage.SERQL, zeroQueryString);
			res = query.evaluate();
			
			while(res.hasNext())
		    	retList.add(res.next().getValue("contrib").toString());
			
			res.close();
			conn.close();
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
		}
        return retList;
    }

    /* (non-Javadoc)
     * @see pl.edu.icm.yadda.analysis.relations.PersonDirectoryBackend#storePerson(java.lang.String, java.lang.Iterable)
     */
    @Override
    public void storePerson(String personId, Iterable<String> contributionId) throws AnalysisException {
//    	log.debug("czy usunac trojki?");
//        if("kill repo".equals(personId)){
//        	log.debug("tak");
//        	try{
//        		RepositoryConnection conn = repository.getConnection();
//            	conn.remove((Resource)null, conn.getValueFactory().createURI(RelConstants.RL_IS_PERSON), (Value)null,(Resource)null);
//            	log.debug("Usuwam trójki z predykatem "+RelConstants.RL_IS_PERSON);
//            	conn.remove((Resource)null, conn.getValueFactory().createURI(RelConstants.RL_HAS_TRACE_NAME), (Value)null,(Resource)null);
//            	log.debug("Usuwam trójki z predykatem "+RelConstants.RL_HAS_TRACE_NAME);
//            	log.debug("udało się!");
//        	}catch(Exception e){
//        		log.error(e.toString());
//    			for(StackTraceElement s: e.getStackTrace())
//    				log.error(s.toString());
//        	}
//        	return;
//        }
    	
    	try {
            RepositoryConnection conn = repository.getConnection();
            conn.setAutoCommit(false);
            
            ValueFactory vf = conn.getValueFactory();
            for (String contrib : contributionId) {
                conn.add(vf.createURI(contrib), vf.createURI(RelConstants.RL_IS_PERSON), vf.createURI(personId));
            }
            conn.commit();
            conn.close();
        } catch (RepositoryException e) {
			log.error(e.toString());
			for(StackTraceElement s: e.getStackTrace())
				log.error(s.toString());
        }
    }

    @Override
    public List<String> getRoles(String personId) {
		String zeroQueryString = "" +
		  "Select distinct contrib role \n" +
		  "from \n" +
		  "{contrib} <"+RelConstants.RL_IS_PERSON+"> {<"+personId+">}, \n" +
		  "{contrib} <"+RelConstants.RL_HAS_ROLE+"> {role} \n" +
		  "";
		
		TupleQuery query = null;
		TupleQueryResult res = null;
		
		List<String> roles = new LinkedList<String>();
		
		try {
        RepositoryConnection conn = repository.getConnection();
			query = conn.prepareTupleQuery(QueryLanguage.SERQL, zeroQueryString);
			res = query.evaluate();
			
			while(res.hasNext())
				roles.add(res.next().getValue("role").toString());
			res.close();
			conn.close();
			return roles;
			
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
		}
		return new LinkedList<String>();
    }

    @Override
    public int getContributionsCount(String personId, String role) {
        return getContributions(personId, role).size();
    }

    public Repository getRepository() {
        return repository;
    }

    @Required
    public void setRepository(Repository repository) {
        this.repository = repository;
    }

	@Override
	//Returns data about first associated contributor. 
	//Data covers: Forenames, surname, title (if there is so) 
	public Map<String, String> getContributor(String personId) {
		String zeroQueryString = "" +
		  "Select distinct contrib, surname, forename,affil,inst  \n" +
		  "from \n" +
		  "{contrib} <"+RelConstants.RL_IS_PERSON+"> {<"+personId+">} \n" +
		  ",[{contrib} <"+RelConstants.RL_HAS_ROLE+"> {role}] \n" +
		  ",[{contrib} <"+RelConstants.RL_IS_INSTITUTION+"> {inst}] \n" +
		  ",{contrib} <"+RelConstants.RL_SURNAME+"> {surename} \n" +
		  ",[{contrib} <"+RelConstants.RL_FORENAMES+"> {forenames}] \n" +
		  ",[{contrib} <"+RelConstants.RL_IS_AFFILIATED_WITH_ID+"> {affil}] \n" +
		  "limit 1 \n" + //TODO check if this "limit" is OK!!!
		  "";
		
		TupleQuery query = null;
		TupleQueryResult res = null;
		
		Map<String,String> retMap = new HashMap<String,String>();
		
		try {
          RepositoryConnection conn = repository.getConnection();
			query = conn.prepareTupleQuery(QueryLanguage.SERQL, zeroQueryString);
			res = query.evaluate();
			
			if(!res.hasNext()) return new HashMap<String,String>();
			
			while (res.hasNext()){
				BindingSet bs = res.next();
				if(bs.getValue("surname")!=null){
					retMap.put("surname", bs.getValue("surname").toString());
				}
		    	if(bs.getValue("forenames")!=null){
					retMap.put("forenames", bs.getValue("forenames").toString());
				}
		    	if(bs.getValue("contrib")!=null){
					retMap.put("contrib", bs.getValue("contrib").toString());
				}
		    	if(bs.getValue("affil")!=null){
					retMap.put("affil", bs.getValue("affil").toString());
				}
		    	if(bs.getValue("role")!=null){
					retMap.put("role", bs.getValue("role").toString());
				}
		    	if(bs.getValue("inst")!=null){
					retMap.put("institution", bs.getValue("inst").toString());
				}
		    	
		    	
			}
			retMap.put("personId", personId);
			res.close();
			conn.close();
			return retMap;
			
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
		}
		return new HashMap<String,String>();
	}


//	@Override
	public Map<String, Map<String, String>> getContributedItems( String personId ) {
		String zeroQueryString = "" +
		  "Select distinct contrib  \n" +
		  "from \n" +
		  "{contrib} <"+RelConstants.RL_IS_PERSON+"> {<"+personId+">} \n" +
		  "";
		
		TupleQuery query = null;
		TupleQueryResult zeroRes = null;
		
		Map<String, Map<String, String>> retMap = new HashMap<String,Map<String,String>>();
		
		try {
        RepositoryConnection conn = repository.getConnection();
			query = conn.prepareTupleQuery(QueryLanguage.SERQL, zeroQueryString);
			zeroRes = query.evaluate();
			
			if(!zeroRes.hasNext()) return retMap;
			while(zeroRes.hasNext()){
				String contrib = zeroRes.next().getValue("contrib").toString();
				
				String firstQueryString = "" +
				  "Select distinct doc, title, journal, volume, year, pages, publisher, issue  \n" +
				  "from \n" +
				  "{doc} <"+RelConstants.RL_HAS_CONTRIBUTOR+"> {<"+contrib+">} \n" +
//				  ",[{doc} <"+RelConstants.RL_HAS_CONTRIBUTOR+"> {contrib}] \n" +
				  ",[{doc} <"+RelConstants.RL_TITLE+"> {title}] \n" +
				  ",[{doc} <"+RelConstants.RL_JOURNAL+"> {journal}] \n" +
				  ",[{doc} <"+RelConstants.RL_VOLUME+"> {volume}] \n" +
				  ",[{doc} <"+RelConstants.RL_YEAR+"> {year}] \n" +
				  ",[{doc} <"+RelConstants.RL_PAGES+"> {pages}] \n" +
				  ",[{doc} <"+RelConstants.RL_PUBLISHER+"> {publisher}] \n" +
				  ",[{doc} <"+RelConstants.RL_ISSUE+"> {issue}] \n" +
//				  ",[{doc} <"+RelConstants.RL_CATEGORY_CEJSH+"> {cejsh}] \n" +
//				  ",[{doc} <"+RelConstants.RL_CATEGORY_CLC+"> {clc}] \n" +
//				  ",[{doc} <"+RelConstants.RL_CATEGORY_JEL+"> {jel}] \n" +
//				  ",[{doc} <"+RelConstants.RL_CATEGORY_MSC+"> {msc}] \n" +
//				  ",[{doc} <"+RelConstants.RL_CATEGORY_PACS+"> {pacs}] \n" +
//				  ",[{doc} <"+RelConstants.RL_CATEGORY_QICS+"> {qics}] \n" +
//				  ",[{doc} <"+RelConstants.RL_CATEGORY_ZDM+"> {zdm}] \n" +
//				  "where \n" +
//				  "contrib != "+contrib+"\n" +
				  "";
				query = conn.prepareTupleQuery(QueryLanguage.SERQL, firstQueryString);
				TupleQueryResult firstRes = query.evaluate();
				
				if(firstRes.hasNext()){
					BindingSet bs = firstRes.next();
					Map<String,String> hm = new HashMap<String,String>();
					
					Value that = bs.getValue("doc");
					if(that!=null) hm.put("doc", that.toString());
					
					that = bs.getValue("title");
					if(that!=null) hm.put("title", that.toString());
					
					that = bs.getValue("journal");
					if(that!=null) hm.put("journal", that.toString());
					
					that = bs.getValue("volume");
					if(that!=null) hm.put("volume", that.toString());
					
					that = bs.getValue("year");
					if(that!=null) hm.put("year", that.toString());
					
					that = bs.getValue("pages");
					if(that!=null) hm.put("pages", that.toString());
					
					that = bs.getValue("publisher");
					if(that!=null) hm.put("publisher", that.toString());
					
					that = bs.getValue("issue");
					if(that!=null) hm.put("issue", that.toString());
					
					retMap.put(contrib, hm);
				}
				firstRes.close();
			}
			
			zeroRes.close();
			conn.close();
			return retMap;
			
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
		}
		return new HashMap<String,Map<String,String>>();
	}

	@Override
	public HashMap<String,Map<String,String>> getContributedItems(String personalityId,
			Map<String, String[]> attributeFilter, String[] order,
			boolean[] descendingOrder, long offset, int count) {
		
		
		StringBuilder header = new StringBuilder("Select distinct contrib,doc");
		
		StringBuilder from = new StringBuilder("" +
				  "from \n" +
				  "{contrib} <"+RelConstants.RL_IS_PERSON+"> {<"+personalityId+">} \n" +
				  ",{doc} <"+RelConstants.RL_HAS_CONTRIBUTOR+"> {contrib} \n" +
				  ""); 
		
		
		StringBuilder where = new StringBuilder("");
		if(!attributeFilter.isEmpty()) where.append("WHERE \n");

		
		
		boolean first = true;

		if(attributeFilter.containsKey("ROLE")){
			String[] pdate = attributeFilter.get("ROLE");
			if(pdate.length!=0){
				if(!first){
					from.append(",");
					where.append("\nAND\n");
				}
				header.append(",role ");
				
				from.append("{contrib} <"+RelConstants.RL_HAS_ROLE+"> {role} \n");
				first=false;
				
				where.append("(\n" +
						"role = "+pdate[0]+"\n");
				for(int i=1;i<pdate.length;i++) where.append("OR role = "+pdate[i]+"\n");
				where.append(")\n");
			}
		}		
		
		if(attributeFilter.containsKey("PUBLICATION_DATE")){
			String[] pdate = attributeFilter.get("PUBLICATION_DATE");
			if(pdate.length!=0){
				if(!first){
					from.append(",");
					where.append("\nAND\n");
				}
				header.append(",year ");
				
				from.append("{doc} <"+RelConstants.RL_YEAR+"> {year} \n");
				first=false;
				
				where.append("(\n" +
						"year = "+pdate[0]+"\n");
				for(int i=1;i<pdate.length;i++) where.append("OR year = "+pdate[i]+"\n");
				where.append(")\n");
			}
		}
		
		if(attributeFilter.containsKey("TITLE")){
			String[] pdate = attributeFilter.get("TITLE");
			if(pdate.length!=0){
				if(!first){
					from.append(",");
					where.append("\nAND\n");
				}
				header.append(",title ");
				
				from.append("{doc} <"+RelConstants.RL_TITLE+"> {title} \n");
				first=false;
				
				where.append("(\n" +
						"title LIKE \""+pdate[0]+"\" IGNORE CASE \n");
				for(int i=1;i<pdate.length;i++) where.append("OR title LIKE \""+pdate[0]+"\"   IGNORE CASE  \n");
				where.append(")\n");
			}
		}
		
		
		
		StringBuilder orderby = new StringBuilder("");
		if(order.length>0){
			orderby.append("ORDER BY \n");
			if(order[0].equalsIgnoreCase("by_title")){
				if(descendingOrder[0]) orderby.append(" title DESC");
				else orderby.append(" title ASC");
			}else if(order[0].equalsIgnoreCase("by_publication_date")){
				if(descendingOrder[0]) orderby.append(" year DESC");
				else orderby.append(" year ASC"); 
			}
			for(int i=1;i<order.length;i++){
				if(order[i].equalsIgnoreCase("by_title")){
					if(descendingOrder[i]) orderby.append(", title DESC\n");
					else orderby.append(", title ASC\n");
				}else if(order[i].equalsIgnoreCase("by_publication_date")){
					if(descendingOrder[i]) orderby.append(", year DESC\n");
					else orderby.append(", year ASC\n"); 
				}	
			}
		}
		header.append(from).append(where).append(orderby);
		if(count>0) header.append("LIMIT "+count);
		header.append("OFFSET "+offset);
			
		TupleQuery query = null;
		TupleQueryResult zeroRes = null;
		
		try {
			RepositoryConnection conn = repository.getConnection();
			query = conn.prepareTupleQuery(QueryLanguage.SERQL, header.toString());
			zeroRes = query.evaluate();
			
			if(!zeroRes.hasNext()) return new HashMap<String,Map<String,String>>();
			
			HashMap<String,Map<String,String>> returnMap = new HashMap<String,Map<String,String>>();
			while(zeroRes.hasNext()){
				BindingSet bs = zeroRes.next();
				HashMap<String,String> map = new HashMap<String,String>();
				
				String vcontrib = bs.getValue("contrib").toString();
				
				map.put("DOC", bs.getValue("doc").toString());
				if(attributeFilter.containsKey("TITLE"))
					map.put("TITLE", bs.getValue("title").toString());
				if(attributeFilter.containsKey("ROLE"))
					map.put("ROLE", bs.getValue("role").toString());
				if(attributeFilter.containsKey("PUBLICATION_DATE"))
					map.put("PUBLICATION_DATE", bs.getValue("year").toString());
				returnMap.put(vcontrib, map);
			}			
			zeroRes.close();
			
			return returnMap;
			
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
		}
		return new HashMap<String,Map<String,String>>();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Map<String, Map<String, String>> searchPersonalities(
			Map<String, String[]> attributeFilter, String[] order,
			boolean[] descendingOrder, long offset, int count) {
		
		
		StringBuilder header = new StringBuilder("Select distinct person");
		
		StringBuilder from = new StringBuilder("" +
				  "from \n" +
				  "{contrib} <"+RelConstants.RL_IS_PERSON+"> {person} \n" +
				  ""); 
		StringBuilder where = new StringBuilder("");
		if(!attributeFilter.isEmpty()) where.append("WHERE \n");
		
		boolean first = true;

		if(attributeFilter.containsKey("ROLE")){
			String[] pdate = attributeFilter.get("ROLE");
			if(pdate.length!=0){
				if(!first){
					from.append(",");
					where.append("\nAND\n");
				}
				header.append(",role ");
				
				from.append("{contrib} <"+RelConstants.RL_HAS_ROLE+"> {role} \n");
				first=false;
				
				where.append("(\n" +
						"role = "+pdate[0]+"\n");
				for(int i=1;i<pdate.length;i++) where.append("OR role = "+pdate[i]+"\n");
				where.append(")\n");
			}
		}		
		
		if(attributeFilter.containsKey("TITLE")){			
			String[] pdate = attributeFilter.get("TITLE");
			if(pdate.length!=0){
				if(!first){
					from.append(",");
					where.append("\nAND\n");
				}
				header.append(",cname");
				
				from.append("{contrib} <"+RelConstants.RL_CANONICAL_NAME+"> {cname} \n");
				first=false;
				
				where.append("(\n" +
						"cname LIKE "+pdate[0]+" IGNORE CASE \n");
				for(int i=1;i<pdate.length;i++) where.append("OR cname LIKE "+pdate[i]+"  IGNORE CASE   \n");
				where.append(")\n");
			}
		}		
		
		if(attributeFilter.containsKey("FIRSTNAME")){			
			String[] pdate = attributeFilter.get("FIRSTNAME");
			if(pdate.length!=0){
				if(!first){
					from.append(",");
					where.append("\nAND\n");
				}
				header.append(",fname");
				
				from.append("{contrib} <"+RelConstants.RL_FORENAMES+"> {fname} \n");
				
				first=false;
				
				where.append("(\n" +
						"fname LIKE "+pdate[0]+" IGNORE CASE \n");
				for(int i=1;i<pdate.length;i++) where.append("OR fname LIKE "+pdate[i]+"  IGNORE CASE   \n");
				where.append(")\n");
			}
		}
		
		if(attributeFilter.containsKey("LASTNAME")){			
			String[] pdate = attributeFilter.get("LASTNAME");
			if(pdate.length!=0){
				if(!first){
					from.append(",");
					where.append("\nAND\n");
				}
				header.append(",lname");
				
				from.append("{contrib} <"+RelConstants.RL_FORENAMES+"> {lname} \n");
				
				first=false;
				
				where.append("(\n" +
						"lname LIKE "+pdate[0]+" IGNORE CASE \n");
				for(int i=1;i<pdate.length;i++) where.append("OR lname LIKE "+pdate[i]+"  IGNORE CASE   \n");
				where.append(")\n");
			}
		}
		
		
		
		StringBuilder orderby = new StringBuilder("");
		if(order.length>0){
			orderby.append("ORDER BY \n");
			if(order[0].equalsIgnoreCase("by_title")){
				if(descendingOrder[0]) orderby.append(" cname DESC");
				else orderby.append(" cname ASC");
			}else if(order[0].equalsIgnoreCase("by_firstname")){
				if(descendingOrder[0]) orderby.append(" fname DESC");
				else orderby.append(" fname ASC"); 
			}else if(order[0].equalsIgnoreCase("by_lastname")){
				if(descendingOrder[0]) orderby.append(" sname DESC");
				else orderby.append(" sname ASC"); 
			}
			
			
			for(int i=1;i<order.length;i++){
				if(order[i].equalsIgnoreCase("by_title")){
					if(descendingOrder[i]) orderby.append(", cname DESC\n");
					else orderby.append(", cname ASC\n");
				}else if(order[i].equalsIgnoreCase("by_firstname")){
					if(descendingOrder[i]) orderby.append(", fname DESC\n");
					else orderby.append(", fname ASC\n"); 
				}else if(order[i].equalsIgnoreCase("by_surname")){
					if(descendingOrder[i]) orderby.append(", sname DESC\n");
					else orderby.append(", sname ASC\n"); 
				}
			}
		}
		header.append(from).append(where).append(orderby);
		if(count>0) header.append("LIMIT "+count);
		header.append("OFFSET "+offset);
			
		TupleQuery query = null;
		TupleQueryResult zeroRes = null;
		
		try {
			RepositoryConnection conn = repository.getConnection();
			query = conn.prepareTupleQuery(QueryLanguage.SERQL, header.toString());
			zeroRes = query.evaluate();
			
			if(!zeroRes.hasNext()) return new HashMap<String,Map<String,String>>();
			
			LinkedList<Object[]> auxList = new LinkedList<Object[]>(); 
			
			int con_sort = 0; 
			//con_sort = 0: no contributions count sort
			//con_sort = 1: asc contributions count sort
			//con_sort = 2: desc contributions count sort
			for(int i = 0; i< order.length;i++){ 
				String s = order[i];
				if("BY_CONTRIBUTIONS".equals(s)){
					if(descendingOrder[i]) con_sort=2;
					else con_sort=3;
				}	
			}
			
			HashMap<String,Map<String,String>> returnMap = new HashMap<String,Map<String,String>>();
			while(zeroRes.hasNext()){
				
				BindingSet bs = zeroRes.next();
				
				TupleQueryResult tq = conn.prepareTupleQuery(QueryLanguage.SERQL, "" +
						"Select distinct contrib \n" +
						"from \n" +
						"{contrib} <"+RelConstants.RL_IS_PERSON+"> {<"+bs.getValue("person")+">} \n" +
						"").evaluate();
				
				int i = 0;
				while(tq.hasNext()){
					tq.next();
					i++;
				}
				tq.close();
				
				HashMap<String,String> map = new HashMap<String,String>();
				
				String vperson = bs.getValue("person").toString();
				if(attributeFilter.containsKey("TITLE"))
					map.put("TITLE", bs.getValue("cname").toString());
				if(attributeFilter.containsKey("FIRSTNAME"))
					map.put("FIRSTNAME", bs.getValue("fname").toString());
				if(attributeFilter.containsKey("LASTNAME"))
					map.put("LASTNAME", bs.getValue("sname").toString());
				if(attributeFilter.containsKey("ROLE"))
					map.put("ROLE", bs.getValue("role").toString());
				for(String s : order) if("BY_CONTRIBUTIONS".equals(s)) 
					map.put("CONTRIBUTIONS", i+"");

				if(con_sort>0){
					Object[] ret = new Object[3];
					ret[0] = i;
					ret[1] = vperson;
					ret[2] = map;
					auxList.add(ret);
				}else{
					returnMap.put(vperson, map);
				}				
			}			
			zeroRes.close();
			
			if(con_sort==1){
				Collections.sort(auxList,new Comparator() {
					@Override
					public int compare(Object o1, Object o2) {
						return (Integer)((Object[])o2)[0] - (Integer)((Object[])o1)[0];
					}
				});
				for(Object[] o : auxList)
					returnMap.put((String)o[1], (Map<String,String>)o[2]);
			}if(con_sort==2){
				Collections.sort(auxList,new Comparator() {
					@Override
					public int compare(Object o1, Object o2) {
						return (Integer)((Object[])o1)[0] - (Integer)((Object[])o2)[0];
					}
				});
				for(Object[] o : auxList)
					returnMap.put((String)o[1], (Map<String,String>)o[2]);
			}
			conn.close();
			return returnMap;
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
		}
		return new HashMap<String,Map<String,String>>();		
	}

	@Override
	public void setRepository(Object repo) {
		this.repository=(Repository) repo;
	}
	
	public Object clone(){
		SesamePersonDirectory spd = new SesamePersonDirectory();
		spd.setRepository(repository);
		return spd;
		
	}
}
