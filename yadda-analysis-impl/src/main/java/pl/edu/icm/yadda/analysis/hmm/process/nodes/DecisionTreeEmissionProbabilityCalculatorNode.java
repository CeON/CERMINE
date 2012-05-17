package pl.edu.icm.yadda.analysis.hmm.process.nodes;

import pl.edu.icm.yadda.analysis.hmm.features.FeatureVector;
import pl.edu.icm.yadda.analysis.hmm.features.FeatureVectorBuilder;
import pl.edu.icm.yadda.analysis.hmm.probability.DecisionTreeHMMEmissionProbability;
import pl.edu.icm.yadda.analysis.hmm.probability.HMMEmissionProbability;
import pl.edu.icm.yadda.analysis.hmm.training.HMMTrainingElement;
import pl.edu.icm.yadda.process.ctx.ProcessContext;
import pl.edu.icm.yadda.process.node.IProcessingNode;

/**
 * Hidden Markov Model emission probability calculator node. The resulting
 * emission probability is based on a decision tree.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class DecisionTreeEmissionProbabilityCalculatorNode<T>
        implements IProcessingNode<HMMTrainingElement<T,FeatureVector>[], HMMEmissionProbability<T,FeatureVector>> {

    private FeatureVectorBuilder featureVectorBuilder;

    private int decisionTreeExpand = -1;

    private double zeroProbabilityValue = 0.0;

    @Override
    public HMMEmissionProbability<T,FeatureVector> process(HMMTrainingElement<T,FeatureVector>[] input, 
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
