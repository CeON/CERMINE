package pl.edu.icm.cermine.tools.classification.sampleselection;

import java.util.List;
import pl.edu.icm.cermine.tools.classification.general.TrainingSample;

/**
 * Interface for picking samples according to a certain strategy from the
 * input set.
 * 
 * @author Pawel Szostek (p.szostek@icm.edu.pl)
 *
 * @param <S> label type (BxZoneLabel by default)
 */
public interface SampleSelector<S> {
	public List<TrainingSample<S>> pickElements(List<TrainingSample<S>> inputElements);
}
