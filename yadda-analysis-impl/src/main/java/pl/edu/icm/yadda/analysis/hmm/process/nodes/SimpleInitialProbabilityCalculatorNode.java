package pl.edu.icm.yadda.analysis.hmm.process.nodes;

import pl.edu.icm.yadda.analysis.hmm.probability.HMMInitialProbability;
import pl.edu.icm.yadda.analysis.hmm.probability.SimpleHMMInitialProbability;
import pl.edu.icm.yadda.analysis.hmm.training.HMMTrainingElement;
import pl.edu.icm.yadda.process.ctx.ProcessContext;
import pl.edu.icm.yadda.process.node.IProcessingNode;

/**
 * Simple Hidden Markov Model initial probability calculator node.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class SimpleInitialProbabilityCalculatorNode<S,T>
        implements IProcessingNode<HMMTrainingElement<S,T>[], HMMInitialProbability<S>> {

    private double zeroProbabilityValue = 0.0;

    @Override
    public HMMInitialProbability<S> process(HMMTrainingElement<S,T>[] input, ProcessContext ctx) throws Exception {
        return new SimpleHMMInitialProbability<S>(input, zeroProbabilityValue);
    }

    public void setZeroProbabilityValue(double zeroProbabilityValue) {
        this.zeroProbabilityValue = zeroProbabilityValue;
    }

}
