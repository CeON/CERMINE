package pl.edu.icm.yadda.analysis.hmm.probability;

import java.util.HashMap;
import java.util.Map;
import pl.edu.icm.yadda.analysis.hmm.training.HMMTrainingElement;
import pl.edu.icm.yadda.analysis.textr.tools.ProbabilityDistribution;

/**
 * Simple Hidden Markov Model emission probability implementation.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class SimpleHMMEmissionProbability<S,T> implements HMMEmissionProbability<S,T> {

    private Map<S, ProbabilityDistribution<T>> probability;

    private double zeroProbabilityValue;

    public SimpleHMMEmissionProbability(HMMTrainingElement<S,T>[] trainingElements) {
        this(trainingElements, 0.0);
    }

    public SimpleHMMEmissionProbability(HMMTrainingElement<S,T>[] trainingElements, double zeroProbabilityValue) {
        this.zeroProbabilityValue = zeroProbabilityValue;
        probability = new HashMap<S, ProbabilityDistribution<T>>();
        for (HMMTrainingElement<S,T> element : trainingElements) {

            if (!probability.containsKey(element.getLabel())) {
                probability.put(element.getLabel(), new ProbabilityDistribution<T>());
            }
            probability.get(element.getLabel()).addEvent(element.getObservation());

        }
    }

    @Override
    public double getProbability(S label, T observation) {
        double prob = zeroProbabilityValue;
        if (probability.containsKey(label)) {
            prob = probability.get(label).getProbability(observation);
            if (prob == 0) {
                prob = zeroProbabilityValue;
            }
        }
        return prob;
    }

}
