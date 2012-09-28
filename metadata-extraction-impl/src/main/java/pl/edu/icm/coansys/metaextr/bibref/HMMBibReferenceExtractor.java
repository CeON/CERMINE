package pl.edu.icm.coansys.metaextr.bibref;

import pl.edu.icm.coansys.metaextr.bibref.BibReferenceExtractor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import pl.edu.icm.coansys.metaextr.AnalysisException;
import pl.edu.icm.coansys.metaextr.bibref.extraction.model.BibReferenceLineLabel;
import pl.edu.icm.coansys.metaextr.bibref.extraction.model.BxDocumentBibReferences;
import pl.edu.icm.coansys.metaextr.bibref.extraction.tools.BibRefExtractionUtils;
import pl.edu.icm.coansys.metaextr.tools.classification.features.FeatureVector;
import pl.edu.icm.coansys.metaextr.tools.classification.features.FeatureVectorBuilder;
import pl.edu.icm.coansys.metaextr.tools.classification.hmm.HMMService;
import pl.edu.icm.coansys.metaextr.tools.classification.hmm.HMMStorage;
import pl.edu.icm.coansys.metaextr.tools.classification.hmm.probability.HMMProbabilityInfo;
import pl.edu.icm.coansys.metaextr.structure.model.BxDocument;
import pl.edu.icm.coansys.metaextr.structure.model.BxLine;

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
