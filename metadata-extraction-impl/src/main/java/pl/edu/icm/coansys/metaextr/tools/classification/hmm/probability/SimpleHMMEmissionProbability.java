package pl.edu.icm.coansys.metaextr.tools.classification.hmm.probability;

import pl.edu.icm.coansys.metaextr.tools.classification.hmm.probability.HMMEmissionProbability;
import java.util.HashMap;
import java.util.Map;

import pl.edu.icm.coansys.metaextr.tools.classification.features.FeatureVector;
import pl.edu.icm.coansys.metaextr.tools.classification.hmm.training.TrainingElement;
import pl.edu.icm.coansys.metaextr.structure.tools.ProbabilityDistribution;

/**
 * Simple Hidden Markov Model emission probability implementation.
 *
 * @param <S> a label type
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class SimpleHMMEmissionProbability<S> implements HMMEmissionProbability<S> {

    private Map<S, ProbabilityDistribution<FeatureVector>> probability;

    private double zeroProbabilityValue;

    public SimpleHMMEmissionProbability(TrainingElement<S>[] trainingElements) {
        this(trainingElements, 0.0);
    }

    public SimpleHMMEmissionProbability(TrainingElement<S>[] trainingElements, double zeroProbabilityValue) {
        this.zeroProbabilityValue = zeroProbabilityValue;
        probability = new HashMap<S, ProbabilityDistribution<FeatureVector>>();
        for (TrainingElement<S> element : trainingElements) {

            if (!probability.containsKey(element.getLabel())) {
                probability.put(element.getLabel(), new ProbabilityDistribution<FeatureVector>());
            }
            probability.get(element.getLabel()).addEvent(element.getObservation());

        }
    }

    @Override
    public double getProbability(S label, FeatureVector observation) {
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
