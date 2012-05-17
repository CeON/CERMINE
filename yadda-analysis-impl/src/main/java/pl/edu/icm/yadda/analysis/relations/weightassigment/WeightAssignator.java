package pl.edu.icm.yadda.analysis.relations.weightassigment;

import java.util.LinkedList;

import org.openrdf.repository.Repository;

import pl.edu.icm.yadda.analysis.AnalysisException;
import pl.edu.icm.yadda.analysis.relations.Disambiguator;

/**
 * 
 * @author pdendek
 *
 */
public abstract class WeightAssignator {
	LinkedList<Disambiguator> disambiguatorList;
	Repository modelRepository;
	Integer basicWeight; 
	String truePersonalityName;
	String testingPersonalityName;
	
	public LinkedList<Disambiguator> getDisambiguatorList() {
		return disambiguatorList;
	}

	public void setDisambiguatorList(LinkedList<Disambiguator> disambiguatorList) {
		this.disambiguatorList = disambiguatorList;
	}

	public Repository getModelRepository() {
		return modelRepository;
	}

	public void setModelRepository(Repository modelRepository) {
		this.modelRepository = modelRepository;
	}

	public Integer getBasicWeight() {
		return basicWeight;
	}

	public void setBasicWeight(Integer basicWeight) {
		this.basicWeight = basicWeight;
	}

	public String getTruePersonalityName() {
		return truePersonalityName;
	}

	public void setTruePersonalityName(String traininingPersonalityName) {
		this.truePersonalityName = traininingPersonalityName;
	}

	public String getTestingPersonalityName() {
		return testingPersonalityName;
	}

	public void setTestingPersonalityName(String testingPersonalityName) {
		this.testingPersonalityName = testingPersonalityName;
	}
	
	public abstract void  assignWeights() throws AnalysisException;
}
