package pl.edu.icm.yadda.analysis.bibref.parsing.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import pl.edu.icm.yadda.analysis.bibref.parsing.model.Citation;
import pl.edu.icm.yadda.analysis.bibref.parsing.model.CitationToken;
import pl.edu.icm.yadda.analysis.bibref.parsing.model.CitationTokenLabel;
import pl.edu.icm.yadda.analysis.classification.features.FeatureVector;
import pl.edu.icm.yadda.analysis.classification.features.FeatureVectorBuilder;
import pl.edu.icm.yadda.analysis.classification.hmm.training.SimpleTrainingElement;
import pl.edu.icm.yadda.analysis.classification.hmm.training.TrainingElement;

/**
 * Citations to HMM training elements converter node. The observations emitted
 * by resulting training elements are vectors of features.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class CitationsToHMMConverter {

    private FeatureVectorBuilder<CitationToken, Citation> featureVectorBuilder;
    
    public TrainingElement<CitationTokenLabel>[] process(Set<Citation> citations) {
        List<TrainingElement<CitationTokenLabel>> trainingList =
                new ArrayList<TrainingElement<CitationTokenLabel>>();
        for (Citation citation : citations) {
            CitationUtils.addHMMLabels(citation);
            SimpleTrainingElement<CitationTokenLabel> prevToken = null;
            for (CitationToken token : citation.getTokens()) {
                FeatureVector featureVector = featureVectorBuilder.getFeatureVector(token, citation);
                SimpleTrainingElement<CitationTokenLabel> element =
                        new SimpleTrainingElement<CitationTokenLabel>(featureVector, token.getLabel(), prevToken == null);
                trainingList.add(element);
                if (prevToken != null) {
                    prevToken.setNextLabel(token.getLabel());
                }
                prevToken = element;
            }
        }
        return trainingList.toArray(new TrainingElement[]{});
    }

    public void setFeatureVectorBuilder(FeatureVectorBuilder<CitationToken, Citation> featureVectorBuilder) {
        this.featureVectorBuilder = featureVectorBuilder;
    }
}
