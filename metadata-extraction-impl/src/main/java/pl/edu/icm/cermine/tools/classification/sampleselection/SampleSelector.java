package pl.edu.icm.cermine.tools.classification.sampleselection;

import java.util.List;
import pl.edu.icm.cermine.tools.classification.hmm.training.TrainingElement;

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
