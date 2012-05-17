package pl.edu.icm.yadda.analysis.relations.general2sesame.auxil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.icm.yadda.analysis.relations.Clusterizer;
import pl.edu.icm.yadda.analysis.relations.DisambiguationInterpreter;
import pl.edu.icm.yadda.analysis.relations.Disambiguator;
import pl.edu.icm.yadda.analysis.relations.DummyInterpreter;
import pl.edu.icm.yadda.analysis.relations.PersonDirectoryBackend;
import pl.edu.icm.yadda.analysis.relations.PersonDirectoryCreator;
import pl.edu.icm.yadda.analysis.relations.WeighedDisambiguator;
import pl.edu.icm.yadda.analysis.relations.bigdataClues.BigdataDisambiguator;
import pl.edu.icm.yadda.analysis.relations.constants.RelConstants;

/**
 * Creates a person directory using a number of so-called {@link Disambiguator}
 * s.
 * 
 * @author Lukasz Bolikowski (bolo@icm.edu.pl)
 * 
 */
public class FeatureOccurenceCounter_OnSameSurnames extends PersonDirectoryCreator{
		
	private static final Logger log = LoggerFactory.getLogger(FeatureOccurenceCounter_OnSameSurnames.class);

    FileWriter fw;

    /**
     * Creates a person directory. Processes contributions group-by-group.
     * Grouping is provided by the person directory backend. For each group,
     * calls the configured {@link Disambiguator}s in order to assess similarity
     * of contributions. Next, calls the configured {@link Clusterizer} to
     * identify persons. Finally, persons are stored in the directory via the
     * configured {@link PersonDirectoryBackend}.
     * @throws Exception 
     */
    public void createOccurenceCountsCSVFile() throws Exception {
    	createPersonDirectory((Object[])null);
    }
    
    /**
     * Creates a person directory. Processes contributions group-by-group.
     * Grouping is provided by the person directory backend. For each group,
     * calls the configured {@link Disambiguator}s in order to assess similarity
     * of contributions. Next, calls the configured {@link Clusterizer} to
     * identify persons. Finally, persons are stored in the directory via the
     * configured {@link PersonDirectoryBackend}.
     * @throws Exception 
     */
    public void createPersonDirectory(Object[] objects) throws Exception {

    	/*
    	 * list containing:
    	 * - size of group
    	 * - no of occurrence 
    	 * - total proceeding time for all this size groups
    	 */
    	Map<Integer,Entry<Integer,Long>> map = new HashMap<Integer,Entry<Integer,Long>>();  
    	
    	long creationTime = System.nanoTime();
    	long howManyTraceGroups = 0;
    	long howManyPersonGroups = 0;
    	long howManyContribs = 0;
    	
    	long traceTime = System.nanoTime();
    	Iterable<String> its = backend.groupIds();
    	traceTime = System.nanoTime()-traceTime;
    	
    	Integer its_size = getItsSize(its);
        DisambiguationInterpreter di = setDisambiguatorInterpreter(objects);
        setOnlyCounter(objects);
    	int maxSize = setMaxSize(objects);
    	int minSize = setMinSize(objects);
    	@SuppressWarnings("unchecked")
		Set<Integer> onlyGivenNumber = (Set<Integer>) objects[4];
    	@SuppressWarnings("unchecked")
		Set<Integer> onlyGivenSize = (Set<Integer>) objects[6];
    	boolean oneSizeOnce = (Boolean) objects[7];
    	
    	String time = DateFormat.getDateTimeInstance()
		.format(new Date()).replaceAll(" ", "_")
		.replaceAll(":", "-"); 
        File f = new File("/home/pdendek/dane_icm/2012-04-20-CEDRAM_N3_NEWPREDICATES/",time+".csv");
        fw = new FileWriter(f);
        
    	LinkedList<Integer>  sizeAlreadyProceeded = new LinkedList<Integer>();
    	Integer curr_num=0;
        for (String groupId : its) {
        	if(groupId.length()==0) continue;
        	curr_num++;
        	if(onlyGivenNumber!=null && !onlyGivenNumber.contains(curr_num)) continue;
        	List<String> contributionIds = backend.members(groupId);
            int size = contributionIds.size();
            if(oneSizeOnce && sizeAlreadyProceeded.contains(size)) continue;
            sizeAlreadyProceeded.add(size);
            if(onlyGivenSize!=null && !onlyGivenSize.contains(size)) continue;
            howManyTraceGroups++;
            howManyContribs+=size;
            
            if(objects[2]!=null){
            	log.debug("group {} of size {}", new Object[]{groupId, size});
            	continue;
            }else{
            	log.debug("Processing group {} of size {} [Progress level: {}/{}, {}%]", new Object[]{groupId, size, curr_num,its_size,curr_num*100d/(double)its_size});
            }
            
            if(size>maxSize){log.debug("The group {} is bigger then MaxSize threshold {} -- skipping the shard", new Object[]{groupId, maxSize});
            	continue;}
            if(size<minSize){log.debug("The group {} is smaller then MinSize threshold {} -- skipping the shard", new Object[]{groupId, minSize});
            	continue;}
            
            //////////////////////////////
            log.debug("group {} of size {}", new Object[]{groupId, size});
            ArrayList<String> list = occurenceCounter(di, contributionIds, size); 
            	
//            	calculateOccurences(di, contributionIds, size);
            persistOccurences(f, list);
            
            System.nanoTime();
        }
        fw.close();
        creationTime = System.nanoTime()-creationTime;
        creationTime/=1000000;
        traceTime/=1000000;
        log.debug("Total proceeding time(ms): {}", creationTime);
        log.debug("Trace group extraction proceeding time(ms): {}", traceTime);
        log.debug("Trace person extraction proceeding time(ms): {}", creationTime - traceTime);
        
        log.debug("Number of contributors: {}", howManyContribs);
        log.debug("Number of trace groups: {}", howManyTraceGroups);
        log.debug("Number of person groups: {}", howManyPersonGroups);
        
        Object[] key = map.keySet().toArray();
        Arrays.sort(key);
        
        log.debug("Proceeding time for each trace group is as following:");
        log.debug("Group size\tOccurence number\tAvg proceeding time");
        
        
        for (int i = 0; i < key.length; i++) {
        	log.debug(""+key[i]
        	            +"\t"+(map.get(key[i]).getKey())
        	            +"\t"+(map.get(key[i]).getValue()/(map.get(key[i]).getKey())));
        }
    }

	private void persistOccurences(File f, ArrayList<String> list)
			throws IOException {
		for(String l : list){fw.write(l);}
		fw.flush();
	}

	private int setMinSize(Object[] objects) {
		int minSize = -1;
    	try{
    		minSize = Integer.parseInt((String) objects[5]);
    	}catch(Exception e){
    		//be silent like a ninja
    	}
		return minSize;
	}

	private int setMaxSize(Object[] objects) {
		int maxSize = Integer.MAX_VALUE;
    	try{
    		maxSize = Integer.parseInt((String) objects[3]);
    	}catch(Exception e){
    		//be silent like a ninja
    	}
		return maxSize;
	}

	private boolean setOnlyCounter(Object[] objects) {
		boolean onlyCounter = false;
        if(objects[2]!=null) onlyCounter = true;
        return onlyCounter;
	}

	private DisambiguationInterpreter setDisambiguatorInterpreter(
			Object[] objects) {
		DisambiguationInterpreter di = null;        
        try{
        	di = (DisambiguationInterpreter) objects[1];
        }catch(Exception ex){
        	di = new DummyInterpreter();
        }
		return di;
	}

	@SuppressWarnings("unused")
	private Integer getItsSize(Iterable<String> its) {
		Integer its_size = 0;
    	for (String groupId : its) {
    		its_size++;
    	}
		return its_size;
	}


	
	
	protected ArrayList<String> occurenceCounter(DisambiguationInterpreter di,
			List<String> contributionIds, int size) {
		try{
			if(size<4)
				return regularOccurenceCounter(di,contributionIds,size);
			else{
				return cachedOccurenceCounter(di,contributionIds,size);
			}
		}catch(Exception e){
			System.out.println(e);
			throw new Error(e);
		}
	}
	
	
	
	protected ArrayList<String> regularOccurenceCounter(DisambiguationInterpreter di,
			List<String> contributionIds, int size) throws RepositoryException, QueryEvaluationException, MalformedQueryException {

		Repository repo = (Repository) this.getBackend().getRepository();
        RepositoryConnection conn = repo.getConnection();
		
		ArrayList<String> list = new ArrayList<String>(); 
		for (int i = 1; i < size; i++) {
		    String contributionIdA = contributionIds.get(i);
		    for (int j = 0; j < i; j++) {
		        String contributionIdB = contributionIds.get(j);       
		        StringBuilder sb = new StringBuilder();
		        for (WeighedDisambiguator wd : regularWeighedDisambiguators) {
		            Disambiguator disambiguator = wd.getDisambiguator();
		            sb.append(disambiguator.analyze(contributionIdA, contributionIdB)+"\t");
		        }
		        evaluatePersonality(conn, contributionIdA, contributionIdB, sb);
		        sb.append("\tregular\n");
		        list.add(sb.toString());
		    }
		}
		conn.close();
		return list;
	}

	private void evaluatePersonality(RepositoryConnection conn,
			String contributionIdA, String contributionIdB, StringBuilder sb)
			throws QueryEvaluationException, RepositoryException,
			MalformedQueryException {
		if(conn.prepareBooleanQuery(QueryLanguage.SPARQL, 
				"ask \n" +
		        "{ \n" +
		        "<"+contributionIdA+"> <http://is-database-person.pl> ?zblp .\n" +
		        "<"+contributionIdB+"> <http://is-database-person.pl> ?zblp .\n" +
		        "FILTER(?zblp != <http://person.pl/zbl/->) \n" +
		        "}").evaluate()) sb.append(1);
		else if(conn.prepareBooleanQuery(QueryLanguage.SPARQL, 
				"ask \n" +
		        "{ \n" +
		        "<"+contributionIdA+"> <http://is-database-person.pl> ?zblp1 .\n" +
		        "<"+contributionIdB+"> <http://is-database-person.pl> ?zblp2 .\n" +
		        "FILTER(" +
		        		"?zblp1!=?zblp2 " +
		        		" && ?zblp1 != <http://person.pl/zbl/->" +
		        		" && ?zblp2 != <http://person.pl/zbl/->" +
		        		")" +
		        "}").evaluate()) sb.append(-1);
		else{
			sb.append(0);
		}
	}

	
	protected ArrayList<String> cachedOccurenceCounter(DisambiguationInterpreter di,
			List<String> contributionIds, int size) throws RepositoryException, MalformedQueryException, QueryEvaluationException {
	
		
		long init_start = ManagementFactory.getThreadMXBean().getThreadCpuTime(Thread.currentThread().getId());
		
		//initializeCache
		Repository cache = null;
		cache = new SailRepository(new MemoryStore());
		cache.initialize();
		//pass cache to auxiliar disambiguators 
		WeighedDisambiguator[] auxiliarWeighedDisambiguators = new WeighedDisambiguator[regularWeighedDisambiguators.size()];
		
		int ite=0;
		for(WeighedDisambiguator wd : regularWeighedDisambiguators) {
			BigdataDisambiguator d = (BigdataDisambiguator)wd.getDisambiguator();
			d.copyTo(cache, contributionIds);
			
			BigdataDisambiguator prim = (BigdataDisambiguator)d.clone();
			prim.setRepository(cache);
			auxiliarWeighedDisambiguators[ite]=new WeighedDisambiguator(wd.getWeight(), prim);
			ite++;
		}
		
		long affinity_start = ManagementFactory.getThreadMXBean().getThreadCpuTime(Thread.currentThread().getId());
		
		
		ManagementFactory.getThreadMXBean().getThreadCpuTime(Thread.currentThread().getId());
		
        RepositoryConnection conn = cache.getConnection();
		
		ArrayList<String> list = new ArrayList<String>(); 
		for (int i = 1; i < size; i++) {
		    String contributionIdA = contributionIds.get(i);
		    for (int j = 0; j < i; j++) {
		        String contributionIdB = contributionIds.get(j);       
		        StringBuilder sb = new StringBuilder();
		        for (WeighedDisambiguator wd : auxiliarWeighedDisambiguators) {
		            Disambiguator disambiguator = wd.getDisambiguator();
		            sb.append(disambiguator.analyze(contributionIdA, contributionIdB)+"\t");
		        }
		        evaluatePersonality(conn, contributionIdA, contributionIdB, sb);
		        sb.append("\taux\n");
		        list.add(sb.toString());
		    }
		}
		conn.close();
		
		long affinity_end = ManagementFactory.getThreadMXBean().getThreadCpuTime(Thread.currentThread().getId());
		
		auxiliarWeighedDisambiguators=null;
		cache.shutDown();
		log.debug("");
		long shutdown_end = ManagementFactory.getThreadMXBean().getThreadCpuTime(Thread.currentThread().getId());
		
		log.debug("CACHE: group of size {}, cache init & shutdown took {} miliseconds, affinity calculation took {} miliseconds", 
				new Object[]{size, 
				(affinity_start-init_start+shutdown_end-affinity_end)/1000000, 
				(affinity_end-affinity_start)/1000000});
		
		return list;
	}
		
	protected ArrayList<String> calculateOccurences(DisambiguationInterpreter di,
	List<String> contributionIds, int size) throws RepositoryException, MalformedQueryException, QueryEvaluationException {

		Repository repo = (Repository) this.getBackend().getRepository();
        RepositoryConnection conn = repo.getConnection();
		
		ArrayList<String> list = new ArrayList<String>(); 
		for (int i = 1; i < size; i++) {
		    String contributionIdA = contributionIds.get(i);
		    for (int j = 0; j < i; j++) {
		        String contributionIdB = contributionIds.get(j);       
		        StringBuilder sb = new StringBuilder();
		        for (WeighedDisambiguator wd : regularWeighedDisambiguators) {
		            Disambiguator disambiguator = wd.getDisambiguator();
		            sb.append(disambiguator.analyze(contributionIdA, contributionIdB)+"\t");
		        }
		        
		        if(conn.prepareBooleanQuery(QueryLanguage.SPARQL, 
		        		"ask \n" +
				        "{ \n" +
				        "<"+contributionIdA+"> <http://is-database-person.pl> ?zblp .\n" +
				        "<"+contributionIdB+"> <http://is-database-person.pl> ?zblp \n" +
				        "}").evaluate()) sb.append(1+"\n");
		        else sb.append(-1+"\n");
		        list.add(sb.toString());
		    }
		}
		conn.close();
		return list;
	}
	
	protected void persistResults(Map<Integer, List<String>> clusters,
			Object[] objects) throws Exception {
		for(Entry<Integer,List<String>> e : clusters.entrySet()){
			String personId = generatePersonId(e.getValue());
          backend.storePerson(personId, e.getValue());
		}
		
	}

	/**
     * Generates a person identifier based on a given list of contributions.
     * 
     * @param contributionIds
     *            List of contributions by a given person.
     * @return Person identifier.
     */
    public static String generatePersonId(List<String> contributionIds) {
        Collections.sort(contributionIds);
        StringBuilder builder = new StringBuilder();
        for (String contributionId : contributionIds) {
            builder.append(contributionId).append('\n');
        }
        try {
        	String ret = RelConstants.NS_PERSON + UUID.nameUUIDFromBytes(builder.toString().getBytes("UTF-8")).toString();
        	if(ret.equals(RelConstants.NS_PERSON))
        		throw new IllegalStateException("Person number not generated");
            return  ret;
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Cannot happen", e);
        }
    }

    public static String generateAffinityId(List<String> strings) {
        Collections.sort(strings);
        StringBuilder builder = new StringBuilder();
        for (String contributionId : strings) {
            builder.append(contributionId).append('\n');
        }
        try {
        	String ret = RelConstants.NS_AFFINITY + UUID.nameUUIDFromBytes(builder.toString().getBytes("UTF-8")).toString();
        	if(ret.equals(RelConstants.NS_AFFINITY))
        		throw new IllegalStateException("Person number not generated");
            return  ret;
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Cannot happen", e);
        }
    }
}
