package pl.edu.icm.cermine.tools.classification.sampleselection;

import java.util.ArrayList;
import java.util.List;
import pl.edu.icm.cermine.tools.classification.general.TrainingSample;

public class NormalSelector<S> implements SampleSelector<S> {

	@Override
	public List<TrainingSample<S>> pickElements(List<TrainingSample<S>> inputElements) {
		return new ArrayList<TrainingSample<S>>(inputElements);
	}

}
