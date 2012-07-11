package pl.edu.icm.yadda.analysis.bibref.parsing.nodes;

import java.util.ArrayList;
import java.util.List;
import pl.edu.icm.yadda.analysis.bibref.parsing.model.Citation;
import pl.edu.icm.yadda.analysis.bibref.parsing.model.CitationToken;
import pl.edu.icm.yadda.analysis.bibref.parsing.model.CitationTokenLabel;
import pl.edu.icm.yadda.analysis.bibref.parsing.tools.CitationUtils;
import pl.edu.icm.yadda.analysis.classification.features.FeatureVector;
import pl.edu.icm.yadda.analysis.classification.features.FeatureVectorBuilder;
import pl.edu.icm.yadda.analysis.classification.hmm.training.HMMTrainingElement;
import pl.edu.icm.yadda.analysis.classification.hmm.training.SimpleHMMTrainingElement;
import pl.edu.icm.yadda.process.ctx.ProcessContext;
import pl.edu.icm.yadda.process.node.IProcessingNode;

/**
 * Citations to HMM training elements converter node. The observations emitted
 * by resulting training elements are vectors of features.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class CitationsToFVHMMTrainingElementsConverterNode
        implements IProcessingNode<Citation[], HMMTrainingElement<CitationTokenLabel>[]> {

    private FeatureVectorBuilder<CitationToken, Citation> featureVectorBuilder;

    @Override
    public HMMTrainingElement<CitationTokenLabel>[] process(Citation[] input, ProcessContext ctx)
            throws Exception {
        List<HMMTrainingElement<CitationTokenLabel>> trainingList =
                new ArrayList<HMMTrainingElement<CitationTokenLabel>>();
        for (Citation citation : input) {
            CitationUtils.addHMMLabels(citation);
            SimpleHMMTrainingElement<CitationTokenLabel> prevToken = null;
            for (CitationToken token : citation.getTokens()) {
                FeatureVector featureVector = featureVectorBuilder.getFeatureVector(token, citation);
                SimpleHMMTrainingElement<CitationTokenLabel> element =
                        new SimpleHMMTrainingElement<CitationTokenLabel>(featureVector, token.getLabel(), prevToken == null);
                trainingList.add(element);
                if (prevToken != null) {
                    prevToken.setNextLabel(token.getLabel());
                }
                prevToken = element;
            }
        }
        return trainingList.toArray(new HMMTrainingElement[]{});
    }

    public void setFeatureVectorBuilder(FeatureVectorBuilder<CitationToken, Citation> featureVectorBuilder) {
        this.featureVectorBuilder = featureVectorBuilder;
    }
}
