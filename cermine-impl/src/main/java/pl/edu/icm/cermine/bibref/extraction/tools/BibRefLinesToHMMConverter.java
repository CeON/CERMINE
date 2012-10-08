package pl.edu.icm.cermine.bibref.extraction.tools;

import java.util.ArrayList;
import java.util.List;
import pl.edu.icm.cermine.bibref.extraction.model.BibReferenceLineLabel;
import pl.edu.icm.cermine.bibref.extraction.model.BxDocumentBibReferences;
import pl.edu.icm.cermine.structure.model.BxLine;
import pl.edu.icm.cermine.tools.classification.features.FeatureVector;
import pl.edu.icm.cermine.tools.classification.features.FeatureVectorBuilder;
import pl.edu.icm.cermine.tools.classification.hmm.training.SimpleTrainingElement;
import pl.edu.icm.cermine.tools.classification.hmm.training.TrainingElement;

/**
 * Bibliographic references' lines to HMM training elements converter node.
 * The observations emitted by resulting training elements are vectors of features.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class BibRefLinesToHMMConverter {

    private FeatureVectorBuilder<BxLine, BxDocumentBibReferences> featureVectorBuilder;

    public TrainingElement<BibReferenceLineLabel>[] process(BxDocumentBibReferences[] input) {

        List<TrainingElement<BibReferenceLineLabel>> trainingList =
                new ArrayList<TrainingElement<BibReferenceLineLabel>>();

        for (BxDocumentBibReferences refs : input) {
            SimpleTrainingElement<BibReferenceLineLabel> prevToken = null;
            for (BxLine line : refs.getLines()) {
                FeatureVector featureVector = featureVectorBuilder.getFeatureVector(line, refs);
                SimpleTrainingElement<BibReferenceLineLabel> element =
                        new SimpleTrainingElement<BibReferenceLineLabel>(featureVector, refs.getLabel(line), prevToken == null);
                trainingList.add(element);
                if (prevToken != null) {
                    prevToken.setNextLabel(refs.getLabel(line));
                }
                prevToken = element;
            }
        }

        return trainingList.toArray(new TrainingElement[]{});
    }

    public void setFeatureVectorBuilder(FeatureVectorBuilder<BxLine, BxDocumentBibReferences> featureVectorBuilder) {
        this.featureVectorBuilder = featureVectorBuilder;
    }

}
