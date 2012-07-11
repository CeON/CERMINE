package pl.edu.icm.yadda.analysis.classification.hmm.process.nodes;

import pl.edu.icm.yadda.analysis.classification.hmm.HMMStorage;
import pl.edu.icm.yadda.analysis.classification.hmm.probability.HMMEmissionProbability;
import pl.edu.icm.yadda.process.ctx.ProcessContext;
import pl.edu.icm.yadda.process.node.IWriterNode;

/**
 * Hidden Markov Model emission probability writer node.
 *
 * @param <S> a label type
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class EmissionProbabilityWriterNode<S> implements IWriterNode<HMMEmissionProbability<S>> {

    private HMMStorage hmmStorage;
    private String hmmId;

    @Override
    public void store(HMMEmissionProbability<S> data, ProcessContext ctx) throws Exception {
        hmmStorage.storeEmissionProbability(hmmId, data);
    }

    public void setHmmId(String hmmId) {
        this.hmmId = hmmId;
    }

    public void setHmmStorage(HMMStorage hmmStorage) {
        this.hmmStorage = hmmStorage;
    }

}
