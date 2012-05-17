package pl.edu.icm.yadda.analysis.relations.weightassigment;

import java.lang.reflect.InvocationTargetException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.icm.yadda.analysis.AnalysisException;
import pl.edu.icm.yadda.analysis.relations.Clusterizer;
import pl.edu.icm.yadda.analysis.relations.PersonDirectoryBackend;
/**
 * 
 * 
 * @author pdendek
 *
 */
public class AdaBoostWeightAssignator extends WeightAssignator {
	private static final Logger log = LoggerFactory.getLogger(AdaBoostWeightAssignator.class);
	
	private Integer maxIterationNumber;
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

	private Double minDifferenceNumber;

	
	
	@Override
	public void assignWeights() throws AnalysisException{
		System.out.println("hello world");
		System.out.println(maxIterationNumber);
		
		log.debug("hello world");
		log.debug("{}",maxIterationNumber);
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
