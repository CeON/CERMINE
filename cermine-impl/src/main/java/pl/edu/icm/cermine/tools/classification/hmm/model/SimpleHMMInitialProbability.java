package pl.edu.icm.cermine.tools.classification.hmm.model;

import pl.edu.icm.cermine.structure.tools.ProbabilityDistribution;
import java.util.List;
import pl.edu.icm.cermine.tools.classification.hmm.training.HMMTrainingSample;

/**
 * Simple Hidden Markov Model initial probability implementation.
 *
 * @param <S> A label type 
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class SimpleHMMInitialProbability<S> implements HMMInitialProbability<S> {

    private ProbabilityDistribution<S> probability;

    private double zeroProbabilityValue;

    public SimpleHMMInitialProbability(List<HMMTrainingSample<S>> trainingElements) {
        this(trainingElements, 0.0);
    }

    public SimpleHMMInitialProbability(List<HMMTrainingSample<S>> trainingElements, double zeroProbabilityValue) {
        this.zeroProbabilityValue = zeroProbabilityValue;
        probability = new ProbabilityDistribution<S>();
        for (HMMTrainingSample<S> element : trainingElements) {
            if (element.isFirst()) {
                probability.addEvent(element.getLabel());
            }
        }
    }

    @Override
    public double getProbability(S label) {
        return (probability.getProbability(label) == 0) ? zeroProbabilityValue : probability.getProbability(label);
    }

}
