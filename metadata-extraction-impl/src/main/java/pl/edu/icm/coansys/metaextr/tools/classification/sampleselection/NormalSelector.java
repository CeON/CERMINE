package pl.edu.icm.coansys.metaextr.tools.classification.sampleselection;

import java.util.ArrayList;
import java.util.List;

import pl.edu.icm.coansys.metaextr.tools.classification.hmm.training.TrainingElement;

public class NormalSelector<S> implements SampleSelector<S> {

	@Override
	public List<TrainingElement<S>> pickElements(List<TrainingElement<S>> inputElements) {
		return new ArrayList<TrainingElement<S>>(inputElements);
	}

}
