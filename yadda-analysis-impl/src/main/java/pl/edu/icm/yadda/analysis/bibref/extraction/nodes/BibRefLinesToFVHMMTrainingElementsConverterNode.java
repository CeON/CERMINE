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
        implements IProcessingNode<BxDocumentBibReferences[], HMMTrainingElement<BibReferenceLineLabel, FeatureVector>[]> {

    private FeatureVectorBuilder<BxLine, BxDocumentBibReferences> featureVectorBuilder;

    @Override
    public HMMTrainingElement<BibReferenceLineLabel, FeatureVector>[] process(BxDocumentBibReferences[] input, ProcessContext ctx)
            throws Exception {

        List<HMMTrainingElement<BibReferenceLineLabel, FeatureVector>> trainingList =
                new ArrayList<HMMTrainingElement<BibReferenceLineLabel, FeatureVector>>();

        for (BxDocumentBibReferences refs : input) {
            SimpleHMMTrainingElement prevToken = null;
            for (BxLine line : refs.getLines()) {
                FeatureVector featureVector = featureVectorBuilder.getFeatureVector(line, refs);
                SimpleHMMTrainingElement<BibReferenceLineLabel, FeatureVector> element =
                        new SimpleHMMTrainingElement(featureVector, refs.getLabel(line), prevToken == null);
                trainingList.add(element);
                if (prevToken != null) {
                    prevToken.setNextLabel(refs.getLabel(line));
                }
                prevToken = element;
            }
        }

        return trainingList.toArray(new HMMTrainingElement[]{});
    }

    public void setFeatureVectorBuilder(FeatureVectorBuilder featureVectorBuilder) {
        this.featureVectorBuilder = featureVectorBuilder;
    }

}
