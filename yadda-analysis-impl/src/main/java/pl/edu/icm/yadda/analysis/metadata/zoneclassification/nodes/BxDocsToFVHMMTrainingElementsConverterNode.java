package pl.edu.icm.yadda.analysis.metadata.zoneclassification.nodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import pl.edu.icm.yadda.analysis.classification.features.FeatureVector;
import pl.edu.icm.yadda.analysis.classification.features.FeatureVectorBuilder;
import pl.edu.icm.yadda.analysis.classification.hmm.training.HMMTrainingElement;
import pl.edu.icm.yadda.analysis.classification.hmm.training.SimpleHMMTrainingElement;
import pl.edu.icm.yadda.analysis.textr.model.BxDocument;
import pl.edu.icm.yadda.analysis.textr.model.BxPage;
import pl.edu.icm.yadda.analysis.textr.model.BxZone;
import pl.edu.icm.yadda.analysis.textr.model.BxZoneLabel;
import pl.edu.icm.yadda.analysis.metadata.zoneclassification.tools.ZoneClassificationUtils;
import pl.edu.icm.yadda.process.ctx.ProcessContext;
import pl.edu.icm.yadda.process.node.IProcessingNode;

/**
 * BxDocument objects to HMM training elements converter node. The observations
 * emitted by resulting training elements are vectors of features.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class BxDocsToFVHMMTrainingElementsConverterNode
        implements IProcessingNode<List<BxDocument>, List<HMMTrainingElement<BxZoneLabel>>> {

    private FeatureVectorBuilder<BxZone, BxPage> featureVectorBuilder;

    private Map<BxZoneLabel, BxZoneLabel> labelMap;

    private double zoneSortTolerance = 5.0;

    @Override
    public List<HMMTrainingElement<BxZoneLabel>> process(List<BxDocument> input, ProcessContext ctx)
            throws Exception {
        List<HMMTrainingElement<BxZoneLabel>> trainingList =
                new ArrayList<HMMTrainingElement<BxZoneLabel>>(input.size());
        for (BxDocument doc : input) {
            ZoneClassificationUtils.correctPagesBounds(doc);
            ZoneClassificationUtils.sortZones(doc, zoneSortTolerance);
            if (labelMap != null) {
                ZoneClassificationUtils.mapZoneLabels(doc, labelMap);
            }

            SimpleHMMTrainingElement<BxZoneLabel> prev = null;
            for (BxPage page : doc.getPages()) {
                for (BxZone zone : page.getZones()) {
                    FeatureVector featureVector = featureVectorBuilder.getFeatureVector(zone, page);
                    SimpleHMMTrainingElement<BxZoneLabel> element =
                            new SimpleHMMTrainingElement<BxZoneLabel>(featureVector, zone.getLabel(), prev == null);
                    trainingList.add(element);

                    if (prev != null) {
                        prev.setNextLabel(zone.getLabel());
                    }
                    prev = element;
                }
            }
        }
        return trainingList;
    }

    public void setFeatureVectorBuilder(FeatureVectorBuilder<BxZone, BxPage> featureVectorBuilder) {
        this.featureVectorBuilder = featureVectorBuilder;
    }

    public void setLabelMap(Map<BxZoneLabel, BxZoneLabel> labelMap) {
        this.labelMap = labelMap;
    }
    
}
