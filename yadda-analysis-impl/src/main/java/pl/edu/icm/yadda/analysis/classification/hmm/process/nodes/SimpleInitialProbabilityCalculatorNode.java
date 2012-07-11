package pl.edu.icm.yadda.analysis.classification.hmm.process.nodes;

import java.util.List;

import pl.edu.icm.yadda.analysis.classification.hmm.probability.HMMInitialProbability;
import pl.edu.icm.yadda.analysis.classification.hmm.probability.SimpleHMMInitialProbability;
import pl.edu.icm.yadda.analysis.classification.hmm.training.HMMTrainingElement;
import pl.edu.icm.yadda.process.ctx.ProcessContext;
import pl.edu.icm.yadda.process.node.IProcessingNode;

/**
 * Simple Hidden Markov Model initial probability calculator node.
 *
 * @param <S> a label type
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class SimpleInitialProbabilityCalculatorNode<S>
        implements IProcessingNode<List<HMMTrainingElement<S>>, HMMInitialProbability<S>> {

    private double zeroProbabilityValue = 0.0;

    @Override
    public HMMInitialProbability<S> process(List<HMMTrainingElement<S>> input, ProcessContext ctx) throws Exception {
        return new SimpleHMMInitialProbability<S>(input, zeroProbabilityValue);
    }

    public void setZeroProbabilityValue(double zeroProbabilityValue) {
        this.zeroProbabilityValue = zeroProbabilityValue;
    }

}
