package pl.edu.icm.yadda.analysis.bibref.extraction.nodes;

import java.util.ArrayList;
import java.util.List;
import pl.edu.icm.yadda.analysis.bibref.extraction.model.BxDocumentBibReferences;
import pl.edu.icm.yadda.analysis.bibref.extraction.model.BibReferenceLineLabel;
import pl.edu.icm.yadda.analysis.classification.features.FeatureVector;
import pl.edu.icm.yadda.analysis.classification.features.FeatureVectorBuilder;
import pl.edu.icm.yadda.analysis.classification.hmm.training.HMMTrainingElement;
import pl.edu.icm.yadda.analysis.classification.hmm.training.SimpleHMMTrainingElement;
import pl.edu.icm.yadda.analysis.textr.model.BxLine;
import pl.edu.icm.yadda.process.ctx.ProcessContext;
import pl.edu.icm.yadda.process.node.IProcessingNode;

/**
 * Bibliographic references' lines to HMM training elements converter node.
 * The observations emitted by resulting training elements are vectors of features.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class BibRefLinesToFVHMMTrainingElementsConverterNode
        implements IProcessingNode<BxDocumentBibReferences[], HMMTrainingElement<BibReferenceLineLabel>[]> {

    private FeatureVectorBuilder<BxLine, BxDocumentBibReferences> featureVectorBuilder;

    @Override
    public HMMTrainingElement<BibReferenceLineLabel>[] process(BxDocumentBibReferences[] input, ProcessContext ctx)
            throws Exception {

        List<HMMTrainingElement<BibReferenceLineLabel>> trainingList =
                new ArrayList<HMMTrainingElement<BibReferenceLineLabel>>();

        for (BxDocumentBibReferences refs : input) {
            SimpleHMMTrainingElement<BibReferenceLineLabel> prevToken = null;
            for (BxLine line : refs.getLines()) {
                FeatureVector featureVector = featureVectorBuilder.getFeatureVector(line, refs);
                SimpleHMMTrainingElement<BibReferenceLineLabel> element =
                        new SimpleHMMTrainingElement<BibReferenceLineLabel>(featureVector, refs.getLabel(line), prevToken == null);
                trainingList.add(element);
                if (prevToken != null) {
                    prevToken.setNextLabel(refs.getLabel(line));
                }
                prevToken = element;
            }
        }

        return trainingList.toArray(new HMMTrainingElement[]{});
    }

    public void setFeatureVectorBuilder(FeatureVectorBuilder<BxLine, BxDocumentBibReferences> featureVectorBuilder) {
        this.featureVectorBuilder = featureVectorBuilder;
    }

}
