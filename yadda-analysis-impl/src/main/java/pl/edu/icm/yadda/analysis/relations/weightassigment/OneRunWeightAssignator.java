package pl.edu.icm.yadda.analysis.relations.weightassigment;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.openrdf.repository.Repository;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;
import org.openrdf.sail.memory.MemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.icm.yadda.analysis.AnalysisException;
import pl.edu.icm.yadda.analysis.relations.Clusterizer;
import pl.edu.icm.yadda.analysis.relations.Disambiguator;
import pl.edu.icm.yadda.analysis.relations.PersonDirectoryBackend;
import pl.edu.icm.yadda.analysis.relations.PersonDirectoryCreator;
import pl.edu.icm.yadda.analysis.relations.WeighedDisambiguator;
import pl.edu.icm.yadda.analysis.relations.constants.RelConstants;
import pl.edu.icm.yadda.analysis.relations.manipulations.manipulators.SesameManipulator;
/**
 * 
 * 
 * @author pdendek
 *
 */
public class OneRunWeightAssignator extends WeightAssignator {
	private static final Logger log = LoggerFactory.getLogger(OneRunWeightAssignator.class);
	
	private Integer maxIterationNumber;
	private Double minDifferenceNumber;

	private Double threshold;
    private PersonDirectoryBackend backend;
    private Clusterizer clusterizer;
    
    private Map<String,SesameManipulator> manipulators;

	
	public Integer getMaxIterationNumber() {
		return maxIterationNumber;
	}

	public void setMaxIterationNumber(Integer maxIterationNumber) {
		this.maxIterationNumber = maxIterationNumber;
	}

	public Double getMinDifferenceNumber() {
		return minDifferenceNumber;
	}

	public void setMinDifferenceNumber(Double minDifferenceNumber) {
		this.minDifferenceNumber = minDifferenceNumber;
	}

    public Double getThreshold() {
		return threshold;
	}

	public void setThreshold(Double threshold) {
		this.threshold = threshold;
	}

	public PersonDirectoryBackend getBackend() {
		return backend;
	}

	public void setBackend(PersonDirectoryBackend backend) {
		this.backend = backend;
	}

	public Clusterizer getClusterizer() {
		return clusterizer;
	}

	public void setClusterizer(Clusterizer clusterizer) {
		this.clusterizer = clusterizer;
	}
	
	@Override
	public void assignWeights() throws AnalysisException{
		LinkedList<LinkedList<WeighedDisambiguator>> wdl = new LinkedList<LinkedList<WeighedDisambiguator>>(); 
		double[] weight = new double[wdl.size()];
		PersonDirectoryCreator[] pdc = new PersonDirectoryCreator[wdl.size()]; 
		Repository memrepo = null;
		Arrays.fill(weight, this.basicWeight);

		
		
		try{
			//create file from model repo
			File repoFile = new File(("/tmp/sesame_repo+"+
	        		new Date()).replaceAll(" ", "_").replaceAll(":", "_"));
			if(repoFile.exists())repoFile.delete();
			repoFile.createNewFile();
			FileOutputStream fos = new FileOutputStream(repoFile);
			RDFWriter w = Rio.createWriter(RDFFormat.N3, fos);
			((Repository)backend.getRepository()).getConnection().export(w);	
			fos.flush();

			//creating memory store from model repo file
			MemoryStore store=new MemoryStore();
			SailRepository rep=new SailRepository(store);
			rep.initialize();
			rep.getConnection().add(repoFile, "", RDFFormat.N3);
			memrepo=rep;
			backend.setRepository(memrepo);
			
			//Create person directory for each weighted disambiguator
			int i = 0;
			for(Disambiguator d : this.disambiguatorList){
				pdc[i] = new PersonDirectoryCreator();
				PersonDirectoryBackend b = backend;
				pdc[i].setBackend(b);
				pdc[i].setClusterizer(clusterizer);
				pdc[i].setThreshold(threshold);
				LinkedList<WeighedDisambiguator> n = new LinkedList<WeighedDisambiguator>();
				n.add(new WeighedDisambiguator(weight[i], d));
				pdc[i].setWeighedDisambiguators(n);
				i++;
			}
			
			//Put is-person-${num} value, where ${num} is order number of person directory starting from 0
			int num = 0;
			for(PersonDirectoryCreator p : pdc){
				p.createPersonDirectory();
				SesameManipulator sm = manipulators.get("renamemanipulator");
				sm.setRepository(memrepo);
				Map<String, Object> operationParam = new HashMap<String,Object>();
				operationParam.put("oldRelationName", RelConstants.RL_IS_PERSON);
				operationParam.put("newRelationName", RelConstants.RL_IS_PERSON+"-"+num);
				sm.execute(operationParam);
				num++;
			}
			
			{
				SesameManipulator sm = manipulators.get("adaboost-weight-initializer");
				sm.setRepository(memrepo);
				Map<String, Object> operationParam = new HashMap<String,Object>();
				operationParam.put("initialweight", 1);
				sm.execute(operationParam);
			}
					
			//best feature list
			LinkedList<Integer> bestFeatureRecord = new LinkedList<Integer>();
			
					//Check which feature was best in this iteration
					num = 0;
					Double[] result = new Double[pdc.length];
					for(PersonDirectoryCreator p : pdc){
						SesameManipulator sm = manipulators.get("adaboost-accuracy-checker");
						sm.setRepository(memrepo);
						Map<String, Object> operationParam = new HashMap<String,Object>();
						operationParam.put("generatedRelationName", RelConstants.RL_IS_PERSON+"-"+num);
						operationParam.put("patternRelationName", RelConstants.RL_IS_PERSON);
						result[num] = (Double)sm.execute(operationParam);
						num++;
					}
		
					
		
			//somehow display results from result[]		
		}catch(Exception e){
			throw new AnalysisException(e);
		}
	}

	private Double calculateLowestTotalDifference() {
		// TODO Auto-generated method stub
		return null;
	}

	private PersonDirectoryBackend cloneBackend(PersonDirectoryBackend backend) throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		return (PersonDirectoryBackend) backend.getClass().getMethod("clone").invoke(backend);
	}
	
	private Clusterizer cloneClusterizer(Clusterizer clusterizer) throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		return (Clusterizer) clusterizer.getClass().getMethod("clone").invoke(clusterizer);
	}
	
//	private Repository getRepository(PersonDirectoryBackend pdb) throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
//		return (Repository) clusterizer.getClass().getMethod("getRepository").invoke(pdb);
//	}
}
