/**
 * This file is part of CERMINE project.
 * Copyright (c) 2011-2013 ICM-UW
 *
 * CERMINE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CERMINE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with CERMINE. If not, see <http://www.gnu.org/licenses/>.
 */

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
