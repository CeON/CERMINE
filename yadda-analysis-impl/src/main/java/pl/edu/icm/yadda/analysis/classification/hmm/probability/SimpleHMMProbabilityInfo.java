package pl.edu.icm.yadda.analysis.classification.hmm.probability;

/**
 * Simple Hidden Markov Model probability information implementation.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class SimpleHMMProbabilityInfo<S,T> implements HMMProbabilityInfo<S,T> {

    private HMMInitialProbability<S> initialProbability;
    private HMMTransitionProbability<S> transitionProbability;
    private HMMEmissionProbability<S,T> emissionProbability;

    public SimpleHMMProbabilityInfo() {
    }

    public SimpleHMMProbabilityInfo(HMMInitialProbability<S> initialProbability,
            HMMTransitionProbability<S> transitionProbability, HMMEmissionProbability<S,T> decisionTree) {
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
    public void setEmissionProbability(HMMEmissionProbability<S,T> emissionProbability) {
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
    public double getEmissionProbability(S label, T observation) {
        return emissionProbability.getProbability(label, observation);
    }

}
