package pl.edu.icm.yadda.analysis.relations;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.util.AbstractMap;
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

import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.sail.nativerdf.NativeStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import pl.edu.icm.yadda.analysis.relations.constants.RelConstants;
import pl.edu.icm.yadda.analysis.relations.pj.clues.PJSameJournalClue;
import pl.edu.icm.yadda.analysis.relations.pj.clues.PJTagWordsClue;
import pl.edu.icm.yadda.analysis.relations.pj.clusterizer.PJSingleLinkHAC_Customized;
import pl.edu.icm.yadda.analysis.relations.pj.proofs.PJEmailProof;
import pl.edu.icm.yadda.analysis.relations.pj.proofs.PJReferenceToSameNameProof;

/**
 * Creates a person directory using a number of so-called {@link Disambiguator}
 * s.
 * 
 * @author Lukasz Bolikowski (bolo@icm.edu.pl)
 * 
 */
public class PersonDirectoryCreator {
	
    public static void main(String[] arg) throws /*Analysis*/Exception, RepositoryException, IOException, RDFHandlerException{
//    	String hopla = YConstants.AT_CONTACT_EMAIL; 
    	
    	Repository rep = null;
    	
    	if(arg.length==0)
    		rep = new SailRepository(new NativeStore(new File(
    			"/home/pdendek/.aduna/openrdf-sesame/repositories/aaa (13. kopia)/")));
    	else
    		rep = new SailRepository(new NativeStore(new File(arg[0])));
    	
    	rep.shutDown();
    	rep.initialize();
    	SesamePersonDirectory backend = new SesamePersonDirectory();
    	backend.setRepository(rep);
    	
    	
    	
    	
//    	rep.getConnection().close();
//    	log.debug("koniec!");
//    	if(new Date()!=null) return;
    	
    	PersonDirectoryCreator pc = new PersonDirectoryCreator();
    	pc.setBackend(backend);
    	pc.setClusterizer(new PJSingleLinkHAC_Customized());
    	pc.setThreshold(0);
    	
    	List<WeighedDisambiguator> weighedDisambiguators = new LinkedList<WeighedDisambiguator>();
    	
    	PJEmailProof q = new PJEmailProof(); q.setRepository(rep);
    		weighedDisambiguators.add(new WeighedDisambiguator(1.0,q));
    	PJReferenceToSameNameProof r = new PJReferenceToSameNameProof(); r.setRepository(rep);
    		weighedDisambiguators.add(new WeighedDisambiguator(1.0,r));
    	PJSameJournalClue t = new PJSameJournalClue(); t.setRepository(rep);
    		weighedDisambiguators.add(new WeighedDisambiguator(1.0,t));
    	PJTagWordsClue y = new PJTagWordsClue(); y.setRepository(rep);
    		weighedDisambiguators.add(new WeighedDisambiguator(1.0,y));

    	pc.setWeighedDisambiguators(weighedDisambiguators);
    	log.info("Starting transfer");
        pc.createPersonDirectory();
        log.info("End transfer");
        
		rep.shutDown();
        
//        File f = new File(("/tmp/Test+"+new Date()).replaceAll(" ", "_").replaceAll(":", "_"));
//		if(f.exists())f.delete();
//		f.createNewFile();
//		FileOutputStream fos = new FileOutputStream(f);
//		RDFWriter qw = Rio.createWriter(RDFFormat.N3, fos);
//		rep.getConnection().export(qw);
        
        
    }
	
	private static final Logger log = LoggerFactory.getLogger(PersonDirectoryCreator.class);

    protected double threshold = 0.0;

    protected PersonDirectoryBackend backend;

    protected List<WeighedDisambiguator> regularWeighedDisambiguators;

    private Clusterizer clusterizer;

    /**
     * Creates a person directory. Processes contributions group-by-group.
     * Grouping is provided by the person directory backend. For each group,
     * calls the configured {@link Disambiguator}s in order to assess similarity
     * of contributions. Next, calls the configured {@link Clusterizer} to
     * identify persons. Finally, persons are stored in the directory via the
     * configured {@link PersonDirectoryBackend}.
     * @throws Exception 
     */
    public void createPersonDirectory() throws Exception {
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
        boolean onlyCounter = setOnlyCounter(objects);
    	int maxSize = setMaxSize(objects);
    	int minSize = setMinSize(objects);
    	Set<Integer> onlyGivenNumber = (Set<Integer>) objects[4];
    	Set<Integer> onlyGivenSize = (Set<Integer>) objects[6];
    	boolean oneSizeOnce = (Boolean) objects[7];
    	
    	LinkedList<Integer>  sizeAlreadyProceeded = new LinkedList<Integer>();
    	Integer curr_num=0;
        for (String groupId : its) {
        	if(groupId.length()==0) continue;
        	curr_num++;
        	if(onlyGivenNumber!=null && !onlyGivenNumber.contains(curr_num)) continue;
        	long ts = System.nanoTime();
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
            
            if(size==1){
            	log.debug("group {} of size 1 will not be proceeded", groupId);
            	continue;
            }
            //////////////////////////////
            log.debug("group {} of size {}", new Object[]{groupId, size});
            double[][] affinity = new double[size][];
            calculateAffinity(di, contributionIds, size, affinity);
            long big_start = System.nanoTime();
            long big_end = System.nanoTime() - big_start;
            
            
            
            Repository repo = (Repository) this.getBackend().getRepository();
	        RepositoryConnection conn = repo.getConnection();
	        conn.setAutoCommit(false);
            ValueFactory vf = repo.getValueFactory();
            
            String time = DateFormat.getDateTimeInstance()
    		.format(new Date()).replaceAll(" ", "_")
    		.replaceAll(":", "-"); 
            URI examinationNS = vf.createURI(RelConstants.NS_EXAMINATION+time);
            
        	describeExamination(di, contributionIds, size, affinity, conn, vf,time, examinationNS);
            conn.commit();
            
            int[] clusterization = clusterizer.clusterize(affinity);
            raportClusterization(groupId, clusterization);
            
            Map<Integer, List<String>> clusters = new HashMap<Integer, List<String>>();
            for (int i = 0; i < clusterization.length; i++) {
                addToMap(clusters, clusterization[i], contributionIds.get(i));
            }
            
            addPersonDataToDatabase(conn, vf, time, examinationNS, clusters);
            
            howManyPersonGroups+=clusters.size();            
            persistResults(clusters,objects);

            conn.close();
            conn=null;
            repo=null;
            
            Entry e = map.get(size);
            Entry<Integer,Long> nowee = null;
            if(e!=null)  nowee = new AbstractMap.SimpleEntry<Integer,Long>((Integer)e.getKey()+1,(Long)e.getValue()+System.nanoTime()-ts);
            else nowee = new AbstractMap.SimpleEntry<Integer,Long>(1,System.nanoTime()-ts);
            map.put(size, nowee);
//            retDataList.add(retData);
        }
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

	private int[] raportClusterization(String groupId, int[] clusterization) {
		int[] cluCopy = Arrays.copyOf(clusterization, clusterization.length);
		Arrays.sort(cluCopy);
		if(cluCopy[0]==cluCopy[cluCopy.length-1]){
			System.out.println("Group "+groupId+" has one cluster!");
		}else{
			System.out.println("Group "+groupId+" has many clusters");
			for(int z=0; z<clusterization.length;z++) System.out.print(clusterization[z]);
			System.out.println("");
		}
		return cluCopy;
	}

	private void addPersonDataToDatabase(RepositoryConnection conn,
			ValueFactory vf, String time, URI examinationNS,
			Map<Integer, List<String>> clusters) throws RepositoryException {
		
		for(Map.Entry<Integer, List<String>> e : clusters.entrySet()){
			 int i = 0;
			 String personId = generatePersonId(e.getValue());
			 for(String c1 : e.getValue()){
				 conn.add(vf.createURI(c1), 
						 vf.createURI(RelConstants.RL_IS_PERSON),
						 vf.createURI(personId),
						 (Resource)null);
				 conn.add(vf.createURI(personId),
						 vf.createURI(RelConstants.RL_HAS_EXAMINATION_TIME),
						 vf.createLiteral(time),
						 (Resource)null);
				 conn.add(vf.createURI(personId),
						 vf.createURI(RelConstants.RL_WAS_EXAMINATED),
						 examinationNS,
						 (Resource)null);
				 for(int j=0;j<i;j++){
					 String c2 = e.getValue().get(j);
					 conn.add(vf.createURI(c2), 
							 vf.createURI(RelConstants.RL_IS_PERSON),
							 vf.createURI(personId),
							 (Resource)null);
				 }
				 i++;
			 }
		 }
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

	private Integer getItsSize(Iterable<String> its) {
		Integer its_size = 0;
    	for (String groupId : its) {
    		its_size++;
    	}
		return its_size;
	}

	private void describeExamination(DisambiguationInterpreter di,
			List<String> contributionIds, int size, double[][] affinity,
			RepositoryConnection conn, ValueFactory vf, String time,
			org.openrdf.model.URI examinationNS) throws RepositoryException {
		conn.add(examinationNS, 
				vf.createURI(RelConstants.RL_HAS_EXAMINATION_TIME), 
				vf.createLiteral(time),
				(Resource)null);
		conn.add(examinationNS, 
				vf.createURI(RelConstants.RL_HAS_FEATURE_INTERPRETER), 
				vf.createURI(di.id()),
				(Resource)null);
		conn.add(examinationNS, 
				vf.createURI(RelConstants.RL_HAS_CLUSTERING_METHOD), 
				vf.createURI(RelConstants.NS_CLUSTERING+this.getClusterizer().getClass().toString()),
				(Resource)null);
		
		
		generateFeatureLink(conn, vf, time,examinationNS);
		generateAffinityLinks(contributionIds, size, affinity, conn, vf,examinationNS);
	}

	private int generateFeatureLink(RepositoryConnection conn, ValueFactory vf,
			String time, org.openrdf.model.URI examinationNS)
			throws RepositoryException {
		int whichFeature = 0;
		for(WeighedDisambiguator wd : this.getWeighedDisambiguators()){
			conn.add(examinationNS, 
		    		vf.createURI(RelConstants.RL_HAS_FEATURE), 
		    		vf.createURI(RelConstants.NS_FEATURE+time+"/"+whichFeature),
		    		(Resource)null);
			
			conn.add(vf.createURI(RelConstants.NS_FEATURE+time+"/"+whichFeature),
					vf.createURI(RelConstants.RL_HAS_FEATURE_ID),
					vf.createLiteral(wd.getDisambiguator().id()),
		    		(Resource)null);
			
			conn.add(vf.createURI(RelConstants.NS_FEATURE+time+"/"+whichFeature),
					vf.createURI(RelConstants.RL_HAS_FEATURE_WEIGHT),
					vf.createLiteral(wd.getWeight()),
		    		(Resource)null);
			whichFeature++;
		}
		return whichFeature;
	}

	private void generateAffinityLinks(List<String> contributionIds, int size,
			double[][] affinity, RepositoryConnection conn, ValueFactory vf,
			org.openrdf.model.URI examinationNS) throws RepositoryException {
		for (int i = 1; i < size; i++) {
		    String contributionIdA = contributionIds.get(i);
		    for (int j = 0; j < i; j++) {
		        String contributionIdB = contributionIds.get(j);
		        
		        String affinityId = this.generateAffinityId(Arrays.asList(contributionIdA, contributionIdB));
		        
		        conn.add(vf.createURI(contributionIdA), 
		        		vf.createURI(RelConstants.RL_IS_AFFINE), 
		        		vf.createURI(affinityId),
		        		(Resource)null);
		        
		        conn.add(vf.createURI(contributionIdB), 
		        		vf.createURI(RelConstants.RL_IS_AFFINE), 
		        		vf.createURI(affinityId),
		        		(Resource)null);
		        
		        conn.add(vf.createURI(affinityId), 
		        		vf.createURI(RelConstants.RL_HAS_AFFINITY_VALUE), 
		        		vf.createLiteral(affinity[i][j]),
		        		(Resource)null);
		        
		        conn.add(vf.createURI(affinityId), 
		        		vf.createURI(RelConstants.RL_WAS_EXAMINATED), 
		        		examinationNS,
		        		(Resource)null);
		    }
		}
	}

	protected void calculateAffinity(DisambiguationInterpreter di,
			List<String> contributionIds, int size, double[][] workspace) {
		//matrix 'workspace' is distance matrix 
		//therefore it is triangular, shifted down by 1
		//
		//e.g.
		//0 1 2         {}
		//1 0 4    is   {1}
		//2 4 0         {2,4}

		
		
		for (int i = 1; i < size; i++) {
		    String contributionIdA = contributionIds.get(i);
		    workspace[i] = new double[i];
		    for (int j = 0; j < i; j++) {
		        String contributionIdB = contributionIds.get(j);
		        double sum = -threshold;
		        for (WeighedDisambiguator wd : regularWeighedDisambiguators) {
		            double weight = wd.getWeight();
		            Disambiguator disambiguator = wd.getDisambiguator();
		            sum += weight * di.interpretResult(disambiguator,disambiguator.analyze(contributionIdA, contributionIdB)); 
		        }
		        
		        workspace[i][j] = sum;
		    }
		}
	}
	
	
    private double di(Class<? extends Disambiguator> class1, double analyze) {
		// TODO Auto-generated method stub
		return analyze;
	}

	protected void persistResults(Map<Integer, List<String>> clusters,
			Object[] objects) throws Exception {
		for(Entry<Integer,List<String>> e : clusters.entrySet()){
			String personId = generatePersonId(e.getValue());
          backend.storePerson(personId, e.getValue());
		}
		
	}

	private static <K, V> void addToMap(Map<Integer, List<String>> clusters, Integer clusterization, String string) {
        List<String> values = clusters.get(clusterization);
        if (values == null) {
            values = new ArrayList<String>();
            values.add(string);
            clusters.put(clusterization, values);
        }else{
        	values.add(string);
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
    
    public PersonDirectoryBackend getBackend() {
        return backend;
    }

    @Required
    public void setBackend(PersonDirectoryBackend backend) {
        this.backend = backend;
    }

    public List<WeighedDisambiguator> getWeighedDisambiguators() {
        return regularWeighedDisambiguators;
    }

    @Required
    public void setWeighedDisambiguators(List<WeighedDisambiguator> weighedDisambiguators) {
        this.regularWeighedDisambiguators = weighedDisambiguators;
    }

    public Clusterizer getClusterizer() {
        return clusterizer;
    }

    @Required
    public void setClusterizer(Clusterizer clusterizer) {
        this.clusterizer = clusterizer;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }
}
