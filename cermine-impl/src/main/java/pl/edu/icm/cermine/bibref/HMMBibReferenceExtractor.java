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
import pl.edu.icm.cermine.bibref.extraction.model.BibReferenceLineLabel;
import pl.edu.icm.cermine.bibref.extraction.model.BxDocumentBibReferences;
import pl.edu.icm.cermine.bibref.extraction.tools.BibRefExtractionUtils;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.model.BxLine;
import pl.edu.icm.cermine.tools.classification.features.FeatureVector;
import pl.edu.icm.cermine.tools.classification.features.FeatureVectorBuilder;
import pl.edu.icm.cermine.tools.classification.hmm.HMMService;
import pl.edu.icm.cermine.tools.classification.hmm.HMMStorage;
import pl.edu.icm.cermine.tools.classification.hmm.model.HMMProbabilityInfo;

/**
 * HMM-based bibliographic reference extractor.
 * 
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class HMMBibReferenceExtractor implements BibReferenceExtractor {

    private HMMService hmmService;
    private HMMProbabilityInfo<BibReferenceLineLabel> labelProbabilities;
    private FeatureVectorBuilder<BxLine, BxDocumentBibReferences> featureVectorBuilder;

    public HMMBibReferenceExtractor(HMMService hmmService, HMMStorage hmmStorage, String hmmId,
            FeatureVectorBuilder<BxLine, BxDocumentBibReferences> featureVectorBuilder) throws IOException {
        this.hmmService = hmmService;
        this.featureVectorBuilder = featureVectorBuilder;
        this.labelProbabilities = hmmStorage.getProbabilityInfo(hmmId);
    }

    public HMMBibReferenceExtractor(HMMService hmmService, HMMProbabilityInfo<BibReferenceLineLabel> labelProbabilities,
            FeatureVectorBuilder<BxLine, BxDocumentBibReferences> featureVectorBuilder) {
        this.hmmService = hmmService;
        this.labelProbabilities = labelProbabilities;
        this.featureVectorBuilder = featureVectorBuilder;
    }

    /**
     * Extracts individual bibliographic references from the document. References lines are 
     * classified using Viterbi algorithm as FIRST, INNER or LAST line of the reference, which 
     * allows for splitting references blocks into individual references.
     * 
     * @param document
     * @return an array of extracted references
     * @throws AnalysisException 
     */
    @Override
    public String[] extractBibReferences(BxDocument document) throws AnalysisException {
        BxDocumentBibReferences documentReferences = BibRefExtractionUtils.extractBibRefLines(document);

        List<FeatureVector> featureVectors = new ArrayList<FeatureVector>();
        for (BxLine line : documentReferences.getLines()) {
            featureVectors.add(featureVectorBuilder.getFeatureVector(line,documentReferences));
        }

        List<BibReferenceLineLabel> labels = hmmService.viterbiMostProbableStates(labelProbabilities,
                Arrays.asList(BibReferenceLineLabel.values()), featureVectors);

        return BibRefExtractionUtils.groupLinesIntoBibRefs(documentReferences, labels);
    }

    public void setFeatureVectorBuilder(FeatureVectorBuilder<BxLine, BxDocumentBibReferences> featureVectorBuilder) {
        this.featureVectorBuilder = featureVectorBuilder;
    }

    public void setHmmService(HMMService hmmService) {
        this.hmmService = hmmService;
    }

    public void setLabelProbabilities(HMMProbabilityInfo<BibReferenceLineLabel> labelProbabilities) {
        this.labelProbabilities = labelProbabilities;
    }

}
