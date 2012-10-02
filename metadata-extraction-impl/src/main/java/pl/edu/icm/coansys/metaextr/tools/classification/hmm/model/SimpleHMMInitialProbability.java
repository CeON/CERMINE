package pl.edu.icm.coansys.metaextr.tools.classification.hmm.model;

import java.util.List;
import pl.edu.icm.coansys.metaextr.structure.tools.ProbabilityDistribution;
import pl.edu.icm.coansys.metaextr.tools.classification.hmm.training.TrainingElement;

/**
 * Simple Hidden Markov Model initial probability implementation.
 *
 * @param <S> A label type 
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class SimpleHMMInitialProbability<S> implements HMMInitialProbability<S> {

    private ProbabilityDistribution<S> probability;

    private double zeroProbabilityValue;

    public SimpleHMMInitialProbability(List<TrainingElement<S>> trainingElements) {
        this(trainingElements, 0.0);
    }

    public SimpleHMMInitialProbability(List<TrainingElement<S>> trainingElements, double zeroProbabilityValue) {
        this.zeroProbabilityValue = zeroProbabilityValue;
        probability = new ProbabilityDistribution<S>();
        for (TrainingElement<S> element : trainingElements) {
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
