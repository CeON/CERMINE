package pl.edu.icm.coansys.metaextr.tools.classification.hmm.probability;

import pl.edu.icm.coansys.metaextr.tools.classification.hmm.probability.HMMTransitionProbability;
import pl.edu.icm.coansys.metaextr.tools.classification.hmm.probability.HMMProbabilityInfo;
import pl.edu.icm.coansys.metaextr.tools.classification.hmm.probability.HMMInitialProbability;
import pl.edu.icm.coansys.metaextr.tools.classification.hmm.probability.HMMEmissionProbability;
import pl.edu.icm.coansys.metaextr.tools.classification.features.FeatureVector;

/**
 * Simple Hidden Markov Model probability information container class implementation.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class SimpleHMMProbabilityInfo<S> implements HMMProbabilityInfo<S> {

    private HMMInitialProbability<S> initialProbability;
    private HMMTransitionProbability<S> transitionProbability;
    private HMMEmissionProbability<S> emissionProbability;

    public SimpleHMMProbabilityInfo() {
    }

    public SimpleHMMProbabilityInfo(HMMInitialProbability<S> initialProbability,
            HMMTransitionProbability<S> transitionProbability, HMMEmissionProbability<S> decisionTree) {
        this.initialProbability = initialProbability;
        this.transitionProbability = transitionProbability;
        this.emissionProbability = decisionTree;
    }

    @Override
    public void setInitialProbability(HMMInitialProbability<S> initialProbability) {
        this.initialProbability = initialProbability;
    }

    @Override
    public void setTransitionProbability(HMMTransitionProbability<S> transitionProbability) {
        this.transitionProbability = transitionProbability;
    }

    @Override
    public void setEmissionProbability(HMMEmissionProbability<S> emissionProbability) {
        this.emissionProbability = emissionProbability;
    }

    @Override
    public double getInitialProbability(S label) {
        return initialProbability.getProbability(label);
    }

    @Override
    public double getTransitionProbability(S startLabel, S endLabel) {
        return transitionProbability.getProbability(startLabel, endLabel);
    }

    @Override
    public double getEmissionProbability(S label, FeatureVector observation) {
        return emissionProbability.getProbability(label, observation);
    }

}
