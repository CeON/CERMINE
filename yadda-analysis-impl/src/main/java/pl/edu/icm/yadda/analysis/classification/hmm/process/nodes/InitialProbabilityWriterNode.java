package pl.edu.icm.yadda.analysis.classification.hmm.process.nodes;

import pl.edu.icm.yadda.analysis.classification.hmm.HMMStorage;
import pl.edu.icm.yadda.analysis.classification.hmm.probability.HMMInitialProbability;
import pl.edu.icm.yadda.process.ctx.ProcessContext;
import pl.edu.icm.yadda.process.node.IWriterNode;

/**
 * Hidden Markov Model initial probability writer node.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class InitialProbabilityWriterNode<T> implements IWriterNode<HMMInitialProbability<T>> {

    private HMMStorage hmmStorage;
    private String hmmId;

    @Override
    public void store(HMMInitialProbability<T> data, ProcessContext ctx) throws Exception {
        hmmStorage.storeInitialProbability(hmmId, data);
    }

    public void setHmmStorage(HMMStorage hmmStorage) {
        this.hmmStorage = hmmStorage;
    }

    public void setHmmId(String hmmId) {
        this.hmmId = hmmId;
    }

}
