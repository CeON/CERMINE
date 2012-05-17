package pl.edu.icm.yadda.analysis.relations.general2sesame.auxil;

import java.lang.management.ManagementFactory;
import java.util.List;

import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.icm.yadda.analysis.relations.DisambiguationInterpreter;
import pl.edu.icm.yadda.analysis.relations.Disambiguator;
import pl.edu.icm.yadda.analysis.relations.PersonDirectoryCreator;
import pl.edu.icm.yadda.analysis.relations.WeighedDisambiguator;
import pl.edu.icm.yadda.analysis.relations.bigdataClues.BigdataDisambiguator;

public class CachedPersonDirectoryCreator extends PersonDirectoryCreator{
	

	private static final Logger log = LoggerFactory.getLogger(PersonDirectoryCreator.class);
	
	@Override
	protected void calculateAffinity(DisambiguationInterpreter di,
			List<String> contributionIds, int size, double[][] workspace) {
		try{
			if(size<4)
				regularCalculateAffinity(di,contributionIds,size,workspace);
			else{
				cachedCalculateAffinity(di,contributionIds,size,workspace);
			}
		}catch(Exception e){
			System.out.println(e);
			throw new Error(e);
		}
	}
	
	
	
	protected void regularCalculateAffinity(DisambiguationInterpreter di,
			List<String> contributionIds, int size, double[][] workspace) {

		long start = ManagementFactory.getThreadMXBean().getThreadCpuTime(Thread.currentThread().getId());
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
		long end = ManagementFactory.getThreadMXBean().getThreadCpuTime(Thread.currentThread().getId());
		
		log.debug("BIGDATA: group of size {}, affinity calculation took {} miliseconds", 
				new Object[]{size,(end-start)/1000000});
	}

	
	protected void cachedCalculateAffinity(DisambiguationInterpreter di,
			List<String> contributionIds, int size, double[][] workspace) throws RepositoryException, MalformedQueryException, QueryEvaluationException {
	
		
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
		
		for (int i = 1; i < size; i++) {
		    String contributionIdA = contributionIds.get(i);
		    workspace[i] = new double[i];
		    for (int j = 0; j < i; j++) {
		        String contributionIdB = contributionIds.get(j);
		        double sum = -threshold;
		        for (WeighedDisambiguator wd : auxiliarWeighedDisambiguators) {
		            double weight = wd.getWeight();
		            Disambiguator disambiguator = wd.getDisambiguator();
		            sum += weight * di.interpretResult(disambiguator,disambiguator.analyze(contributionIdA, contributionIdB)); 
		        }
		        workspace[i][j] = sum;
		    }
		}
		
		long affinity_end = ManagementFactory.getThreadMXBean().getThreadCpuTime(Thread.currentThread().getId());
		
		auxiliarWeighedDisambiguators=null;
		cache.shutDown();
		log.debug("");
		long shutdown_end = ManagementFactory.getThreadMXBean().getThreadCpuTime(Thread.currentThread().getId());
		
		log.debug("CACHE: group of size {}, cache init & shutdown took {} miliseconds, affinity calculation took {} miliseconds", 
				new Object[]{size, 
				(affinity_start-init_start+shutdown_end-affinity_end)/1000000, 
				(affinity_end-affinity_start)/1000000});
	}
	
}
