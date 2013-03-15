package pl.edu.icm.cermine.bibref.parsing.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import pl.edu.icm.cermine.bibref.parsing.model.Citation;
import pl.edu.icm.cermine.bibref.parsing.model.CitationToken;
import pl.edu.icm.cermine.bibref.parsing.model.CitationTokenLabel;
import pl.edu.icm.cermine.tools.classification.features.FeatureVector;
import pl.edu.icm.cermine.tools.classification.features.FeatureVectorBuilder;
import pl.edu.icm.cermine.tools.classification.hmm.model.HMMTrainingSample;

/**
 * Citations to HMM training elements converter node. The observations emitted
 * by resulting training elements are vectors of features.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public final class CitationsToHMMConverter {

    private CitationsToHMMConverter() {}
    
    public static HMMTrainingSample<CitationTokenLabel>[] convertToHMM(Set<Citation> citations, FeatureVectorBuilder<CitationToken, Citation> featureVectorBuilder) {
        List<HMMTrainingSample<CitationTokenLabel>> trainingList =
                new ArrayList<HMMTrainingSample<CitationTokenLabel>>();
        for (Citation citation : citations) {
            CitationUtils.addHMMLabels(citation);
            HMMTrainingSample<CitationTokenLabel> prevToken = null;
            for (CitationToken token : citation.getTokens()) {
                FeatureVector featureVector = featureVectorBuilder.getFeatureVector(token, citation);
                HMMTrainingSample<CitationTokenLabel> element =
                        new HMMTrainingSample<CitationTokenLabel>(featureVector, token.getLabel(), prevToken == null);
                trainingList.add(element);
                if (prevToken != null) {
                    prevToken.setNextLabel(token.getLabel());
                }
                prevToken = element;
            }
        }
        return trainingList.toArray(new HMMTrainingSample[trainingList.size()]);
    }

}
