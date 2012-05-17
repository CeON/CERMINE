package pl.edu.icm.yadda.analysis.hmm.process.nodes;

import pl.edu.icm.yadda.analysis.hmm.HMMStorage;
import pl.edu.icm.yadda.analysis.hmm.probability.HMMTransitionProbability;
import pl.edu.icm.yadda.process.ctx.ProcessContext;
import pl.edu.icm.yadda.process.node.IWriterNode;

/**
 * Hidden Markov Model transition probability writer node.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class TransitionProbabilityWriterNode<T> implements IWriterNode<HMMTransitionProbability<T>> {

    private HMMStorage hmmStorage;
    private String hmmId;

    @Override
    public void store(HMMTransitionProbability<T> data, ProcessContext ctx) throws Exception {
        hmmStorage.storeTransitionProbability(hmmId, data);
    }

    public void setHmmId(String hmmId) {
        this.hmmId = hmmId;
    }

    public void setHmmStorage(HMMStorage hmmStorage) {
        this.hmmStorage = hmmStorage;
    }

}
