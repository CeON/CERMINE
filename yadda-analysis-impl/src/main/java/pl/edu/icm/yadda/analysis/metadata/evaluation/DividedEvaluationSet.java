package pl.edu.icm.yadda.analysis.metadata.evaluation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import pl.edu.icm.yadda.analysis.textr.model.BxDocument;

class DividedEvaluationSet
{
	List<BxDocument> trainingSet;
	List<BxDocument> testSet;

	public DividedEvaluationSet(List<BxDocument> trainingSet, List<BxDocument> testSet) {
		this.trainingSet = trainingSet;
		this.testSet = testSet;
	}
	public List<BxDocument> getTrainingSet() {
		return trainingSet;
	}
	public List<BxDocument> getTestSet() {
		return testSet;
	}
	
	public static DividedEvaluationSet build(List<BxDocument> documents, Double ratio)
	{
		Integer numberOfTrainingDocs = (int)Math.ceil((documents.size()*ratio)/(1+ratio));
		// based on the equations:
		// size(training) / size(test) = ratio
		// size(training) + size(test) = size(documents)
		
		List<Integer> trainingIndices = new ArrayList<Integer>(numberOfTrainingDocs);
		Random randomGenerator = new Random();
		
		while(trainingIndices.size() < (documents.size()*ratio)/(1+ratio)) {
			Integer randomInt = randomGenerator.nextInt(documents.size());
			if(!trainingIndices.contains(randomInt)) {
				trainingIndices.add(randomInt);
			}
		}
		
		List<BxDocument> trainingDocs = new ArrayList<BxDocument>(numberOfTrainingDocs);
		List<BxDocument> testDocs = new ArrayList<BxDocument>(documents.size()-numberOfTrainingDocs);
		
		for(Integer index=0; index<documents.size(); ++index) {
			if(trainingIndices.contains(index)) {
				trainingDocs.add(documents.get(index));
			} else {
				testDocs.add(documents.get(index));
			}
		}
		return new DividedEvaluationSet(trainingDocs, testDocs);
	}
}