package pl.edu.icm.yadda.analysis.classification.hmm.process.nodes;

import pl.edu.icm.yadda.analysis.classification.hmm.probability.HMMTransitionProbability;
import pl.edu.icm.yadda.analysis.classification.hmm.probability.SimpleHMMTransitionProbability;
import pl.edu.icm.yadda.analysis.classification.hmm.training.HMMTrainingElement;
import pl.edu.icm.yadda.process.ctx.ProcessContext;
import pl.edu.icm.yadda.process.node.IProcessingNode;

/**
 * Simple Hidden Markov Model transition probability calculator node.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class SimpleTransitionProbabilityCalculatorNode<S,T>
        implements IProcessingNode<HMMTrainingElement<S,T>[], HMMTransitionProbability<S>> {

    private double zeroProbabilityValue = 0.0;

    @Override
    public HMMTransitionProbability<S> process(HMMTrainingElement<S,T>[] input, ProcessContext ctx) throws Exception {
        return new SimpleHMMTransitionProbability<S>(input, zeroProbabilityValue);
    }

    public void setZeroProbabilityValue(double zeroProbabilityValue) {
        this.zeroProbabilityValue = zeroProbabilityValue;
    }

}
