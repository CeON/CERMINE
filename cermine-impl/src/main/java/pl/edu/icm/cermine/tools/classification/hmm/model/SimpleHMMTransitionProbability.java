package pl.edu.icm.cermine.tools.classification.hmm.model;

import pl.edu.icm.cermine.structure.tools.ProbabilityDistribution;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simple Hidden Markov Model transition probability implementation.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class SimpleHMMTransitionProbability<S> implements HMMTransitionProbability<S> {

    private Map<S, ProbabilityDistribution<S>> probability;

    private double zeroProbabilityValue;

    public SimpleHMMTransitionProbability(List<HMMTrainingSample<S>> trainingElements) {
        this(trainingElements, 0.0);
    }

    public SimpleHMMTransitionProbability(List<HMMTrainingSample<S>> trainingElements, double zeroProbabilityValue) {
        this.zeroProbabilityValue = zeroProbabilityValue;
        probability = new HashMap<S, ProbabilityDistribution<S>>();
        for (HMMTrainingSample<S> element : trainingElements) {
            if (element.getNextLabel() != null) {
                if (!probability.containsKey(element.getLabel())) {
                    probability.put(element.getLabel(), new ProbabilityDistribution<S>());
                }
                probability.get(element.getLabel()).addEvent(element.getNextLabel());
            }
        }
    }

    @Override
    public double getProbability(S startLabel, S endLabel) {
        double prob = zeroProbabilityValue;
        if (probability.containsKey(startLabel)) {
            prob = probability.get(startLabel).getProbability(endLabel);
            if (prob == 0) {
                prob = zeroProbabilityValue;
            }
        }
        return prob;
    }

}
