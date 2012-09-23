package pl.edu.icm.yadda.analysis.metadata.sampleselection;

import java.util.List;

import pl.edu.icm.yadda.analysis.classification.hmm.training.TrainingElement;

/**
 * Interface for picking samples according to a certain strategy from the
 * input set.
 * 
 * @author Pawel Szostek (p.szostek@icm.edu.pl)
 *
 * @param <S> label type (BxZoneLabel by default)
 */
public interface SampleSelector<S> {
	public List<TrainingElement<S>> pickElements(List<TrainingElement<S>> inputElements);
}
