package pl.edu.icm.yadda.analysis.classification.hmm.process.nodes;

import pl.edu.icm.yadda.analysis.classification.hmm.probability.HMMEmissionProbability;
import pl.edu.icm.yadda.analysis.classification.hmm.probability.SimpleHMMEmissionProbability;
import pl.edu.icm.yadda.analysis.classification.hmm.training.TrainingElement;
import pl.edu.icm.yadda.process.ctx.ProcessContext;
import pl.edu.icm.yadda.process.node.IProcessingNode;

/**
 * Simple Hidden Markov Model emission probability calculator node.
 * 
 * @param <S> a labal type
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class SimpleEmissionProbabilityCalculatorNode<S>
        implements IProcessingNode<TrainingElement<S>[], HMMEmissionProbability<S>> {

    private double zeroProbabilityValue = 0.0;

    @Override
    public HMMEmissionProbability<S> process(TrainingElement<S>[] input, ProcessContext ctx)
            throws Exception {
        return new SimpleHMMEmissionProbability<S>(input, zeroProbabilityValue);
    }

    public void setZeroProbabilityValue(double zeroProbabilityValue) {
        this.zeroProbabilityValue = zeroProbabilityValue;
    }

}
