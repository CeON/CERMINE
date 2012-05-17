package pl.edu.icm.yadda.analysis.hmm.process.nodes;

import pl.edu.icm.yadda.analysis.hmm.probability.HMMEmissionProbability;
import pl.edu.icm.yadda.analysis.hmm.probability.SimpleHMMEmissionProbability;
import pl.edu.icm.yadda.analysis.hmm.training.HMMTrainingElement;
import pl.edu.icm.yadda.process.ctx.ProcessContext;
import pl.edu.icm.yadda.process.node.IProcessingNode;

/**
 * Simple Hidden Markov Model emission probability calculator node.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class SimpleEmissionProbabilityCalculatorNode<S,T>
        implements IProcessingNode<HMMTrainingElement<S,T>[], HMMEmissionProbability<S,T>> {

    private double zeroProbabilityValue = 0.0;

    @Override
    public HMMEmissionProbability<S,T> process(HMMTrainingElement<S,T>[] input, ProcessContext ctx)
            throws Exception {
        return new SimpleHMMEmissionProbability(input, zeroProbabilityValue);
    }

    public void setZeroProbabilityValue(double zeroProbabilityValue) {
        this.zeroProbabilityValue = zeroProbabilityValue;
    }

}
