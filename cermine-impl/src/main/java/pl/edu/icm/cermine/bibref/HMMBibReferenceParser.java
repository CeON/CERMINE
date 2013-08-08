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

package pl.edu.icm.cermine.bibref;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import pl.edu.icm.cermine.bibref.model.BibEntry;
import pl.edu.icm.cermine.bibref.parsing.model.Citation;
import pl.edu.icm.cermine.bibref.parsing.model.CitationToken;
import pl.edu.icm.cermine.bibref.parsing.model.CitationTokenLabel;
import pl.edu.icm.cermine.bibref.parsing.tools.CitationUtils;
import pl.edu.icm.cermine.tools.classification.features.FeatureVector;
import pl.edu.icm.cermine.tools.classification.features.FeatureVectorBuilder;
import pl.edu.icm.cermine.tools.classification.hmm.HMMService;
import pl.edu.icm.cermine.tools.classification.hmm.HMMStorage;
import pl.edu.icm.cermine.tools.classification.hmm.model.HMMProbabilityInfo;

/**
 * Hidden Markov Models-based citation parser.
 * 
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public class HMMBibReferenceParser implements BibReferenceParser<BibEntry> {

	private HMMService hmmService;
	private HMMProbabilityInfo<CitationTokenLabel> labelProbabilities;
	private FeatureVectorBuilder<CitationToken, Citation> featureVectorBuilder;

	public HMMBibReferenceParser(HMMService hmmService, HMMStorage hmmStorage,
			String hmmId,
			FeatureVectorBuilder<CitationToken, Citation> featureVectorBuilder)
			throws IOException {
		this.hmmService = hmmService;
		this.featureVectorBuilder = featureVectorBuilder;
		this.labelProbabilities = hmmStorage.getProbabilityInfo(hmmId);
	}

	public HMMBibReferenceParser(HMMService hmmService,
			HMMProbabilityInfo<CitationTokenLabel> labelProbabilities,
			FeatureVectorBuilder<CitationToken, Citation> featureVectorBuilder) {
		this.hmmService = hmmService;
		this.labelProbabilities = labelProbabilities;
		this.featureVectorBuilder = featureVectorBuilder;
	}

	@Override
	public BibEntry parseBibReference(String text) {
		Citation citation = CitationUtils.stringToCitation(text);
        List<CitationToken> tokens = citation.getTokens();

		List<FeatureVector> featureVectors = new ArrayList<FeatureVector>();
		for (CitationToken token : tokens) {
			featureVectors.add(featureVectorBuilder.getFeatureVector(token,
					citation));
		}

		List<CitationTokenLabel> labels = hmmService.viterbiMostProbableStates(
				labelProbabilities, Arrays.asList(CitationTokenLabel.values()),
				featureVectors);
		for (int i = 0; i < tokens.size(); i++) {
			tokens.get(i).setLabel(labels.get(i));
		}

		CitationUtils.removeHMMLabels(citation);
		return CitationUtils.citationToBibref(citation);
	}

	public void setFeatureVectorBuilder(
			FeatureVectorBuilder<CitationToken, Citation> featureVectorBuilder) {
		this.featureVectorBuilder = featureVectorBuilder;
	}

	public void setHmmService(HMMService hmmService) {
		this.hmmService = hmmService;
	}

	public void setLabelProbabilities(
			HMMProbabilityInfo<CitationTokenLabel> labelProbabilities) {
		this.labelProbabilities = labelProbabilities;
	}

}
