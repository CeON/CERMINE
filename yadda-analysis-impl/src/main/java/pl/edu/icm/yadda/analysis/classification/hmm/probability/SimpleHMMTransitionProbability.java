package pl.edu.icm.yadda.analysis.classification.hmm.probability;

import java.util.HashMap;
import java.util.Map;
import pl.edu.icm.yadda.analysis.classification.hmm.training.HMMTrainingElement;
import pl.edu.icm.yadda.analysis.textr.tools.ProbabilityDistribution;

/**
 * Simple Hidden Markov Model transition probability implementation.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class SimpleHMMTransitionProbability<S> implements HMMTransitionProbability<S> {

    private Map<S, ProbabilityDistribution<S>> probability;

    private double zeroProbabilityValue;

    public SimpleHMMTransitionProbability(HMMTrainingElement<S,?>[] trainingElements) {
        this(trainingElements, 0.0);
    }

    public SimpleHMMTransitionProbability(HMMTrainingElement<S,?>[] trainingElements, double zeroProbabilityValue) {
        this.zeroProbabilityValue = zeroProbabilityValue;
        probability = new HashMap<S, ProbabilityDistribution<S>>();
        for (HMMTrainingElement<S,?> element : trainingElements) {
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
