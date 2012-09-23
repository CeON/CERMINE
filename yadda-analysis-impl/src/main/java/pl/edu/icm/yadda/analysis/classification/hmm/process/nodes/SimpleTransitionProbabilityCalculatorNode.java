package pl.edu.icm.yadda.analysis.classification.hmm.process.nodes;

import java.util.List;

import pl.edu.icm.yadda.analysis.classification.hmm.probability.HMMTransitionProbability;
import pl.edu.icm.yadda.analysis.classification.hmm.probability.SimpleHMMTransitionProbability;
import pl.edu.icm.yadda.analysis.classification.hmm.training.TrainingElement;
import pl.edu.icm.yadda.process.ctx.ProcessContext;
import pl.edu.icm.yadda.process.node.IProcessingNode;

/**
 * Simple Hidden Markov Model transition probability calculator node.
 *
 * @param <A> a label type
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class SimpleTransitionProbabilityCalculatorNode<S>
        implements IProcessingNode<List<TrainingElement<S>>, HMMTransitionProbability<S>> {

    private double zeroProbabilityValue = 0.0;

    @Override
    public HMMTransitionProbability<S> process(List<TrainingElement<S>> input, ProcessContext ctx) throws Exception {
        return new SimpleHMMTransitionProbability<S>(input, zeroProbabilityValue);
    }

    public void setZeroProbabilityValue(double zeroProbabilityValue) {
        this.zeroProbabilityValue = zeroProbabilityValue;
    }

}
