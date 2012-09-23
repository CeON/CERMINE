package pl.edu.icm.yadda.analysis.classification.hmm.process.nodes;

import java.util.List;

import pl.edu.icm.yadda.analysis.classification.features.FeatureVectorBuilder;
import pl.edu.icm.yadda.analysis.classification.hmm.probability.DecisionTreeHMMEmissionProbability;
import pl.edu.icm.yadda.analysis.classification.hmm.probability.HMMEmissionProbability;
import pl.edu.icm.yadda.analysis.classification.hmm.training.TrainingElement;
import pl.edu.icm.yadda.process.ctx.ProcessContext;
import pl.edu.icm.yadda.process.node.IProcessingNode;

/**
 * Hidden Markov Model emission probability calculator node. The resulting
 * emission probability is based on a decision tree.
 *
 * @param <S> a label type
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class DecisionTreeEmissionProbabilityCalculatorNode<S>
        implements IProcessingNode<List<TrainingElement<S>>, HMMEmissionProbability<S>> {

    private FeatureVectorBuilder featureVectorBuilder;

    private int decisionTreeExpand = -1;

    private double zeroProbabilityValue = 0.0;

    @Override
    public HMMEmissionProbability<S> process(List<TrainingElement<S>> input, 
            ProcessContext ctx) throws Exception {
        return new DecisionTreeHMMEmissionProbability(input, featureVectorBuilder.getFeatureNames(), 
                                                      decisionTreeExpand, zeroProbabilityValue);
    }

    public void setFeatureVectorBuilder(FeatureVectorBuilder featureVectorBuilder) {
        this.featureVectorBuilder = featureVectorBuilder;
    }

    public void setDecisionTreeExpand(int decisionTreeExpand) {
        this.decisionTreeExpand = decisionTreeExpand;
    }

    public void setZeroProbabilityValue(double zeroProbabilityValue) {
        this.zeroProbabilityValue = zeroProbabilityValue;
    }

}
