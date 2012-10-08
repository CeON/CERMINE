package pl.edu.icm.cermine.evaluation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import pl.edu.icm.cermine.structure.model.BxDocument;

class DividedEvaluationSet
{
	public DividedEvaluationSet(List<BxDocument> trainingDocuments, List<BxDocument> testDocuments) {
		this.trainingDocuments = trainingDocuments;
		this.testDocuments = testDocuments;
	}

	List<BxDocument> trainingDocuments;
	List<BxDocument> testDocuments;

	public List<BxDocument> getTrainingDocuments() {
		return trainingDocuments;
	}

	public List<BxDocument> getTestDocuments() {
		return testDocuments;
	}

	public static List<DividedEvaluationSet> build(List<BxDocument> documents, Integer foldness)
	{
		List<BxDocument> shuffledDocs = new ArrayList<BxDocument>(documents.size());
		shuffledDocs.addAll(documents);
		Collections.shuffle(shuffledDocs);
		List<List<BxDocument>> dividedDocs = new ArrayList<List<BxDocument>>(foldness);

		for(Integer fold=0; fold < foldness; ++fold) {
			Integer docsPerSet = shuffledDocs.size() / (foldness-fold);
			dividedDocs.add(new ArrayList<BxDocument>());
			for(Integer idx=0; idx < docsPerSet; ++idx) {
				dividedDocs.get(dividedDocs.size()-1).add(shuffledDocs.get(0));
				shuffledDocs.remove(0);
			}
		}

		List<DividedEvaluationSet> ret = new ArrayList<DividedEvaluationSet>(foldness);

		for(Integer fold=0; fold<foldness; ++fold) {
			List<BxDocument> trainingDocuments = new ArrayList<BxDocument>();
			List<BxDocument> testDocuments = new ArrayList<BxDocument>();
			for(Integer setIdx=0; setIdx<foldness; ++setIdx) {
				if(setIdx == fold)
					testDocuments.addAll(dividedDocs.get(setIdx));
				else
					trainingDocuments.addAll(dividedDocs.get(setIdx));
			}
			ret.add(new DividedEvaluationSet(trainingDocuments, testDocuments));
		}
		return ret;
	}
}