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
import pl.edu.icm.yadda.analysis.textr.tools.BxModelUtils;
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
        implements IProcessingNode<BxDocument[], HMMTrainingElement<BxZoneLabel, FeatureVector>[]> {

    private FeatureVectorBuilder featureVectorBuilder;

    private Map<BxZoneLabel, BxZoneLabel> labelMap;

    private double zoneSortTolerance = 5.0;

    @Override
    public HMMTrainingElement<BxZoneLabel, FeatureVector>[] process(BxDocument[] input, ProcessContext ctx)
            throws Exception {
        List<HMMTrainingElement<BxZoneLabel, FeatureVector>> trainingList =
                new ArrayList<HMMTrainingElement<BxZoneLabel, FeatureVector>>();
        for (BxDocument doc : input) {
            ZoneClassificationUtils.correctPagesBounds(doc);
            ZoneClassificationUtils.sortZones(doc, zoneSortTolerance);
            if (labelMap != null) {
                ZoneClassificationUtils.mapZoneLabels(doc, labelMap);
            }

            SimpleHMMTrainingElement<BxZoneLabel, FeatureVector> prev = null;
            for (BxPage page : doc.getPages()) {
                for (BxZone zone : page.getZones()) {
                    FeatureVector featureVector = featureVectorBuilder.getFeatureVector(zone, page);
                    SimpleHMMTrainingElement<BxZoneLabel, FeatureVector> element =
                            new SimpleHMMTrainingElement(featureVector, zone.getLabel(), prev == null);
                    trainingList.add(element);

                    if (prev != null) {
                        prev.setNextLabel(zone.getLabel());
                    }
                    prev = element;
                }
            }
        }
        return trainingList.toArray(new SimpleHMMTrainingElement[]{});
    }

    public void setFeatureVectorBuilder(FeatureVectorBuilder featureVectorBuilder) {
        this.featureVectorBuilder = featureVectorBuilder;
    }

    public void setLabelMap(Map<BxZoneLabel, BxZoneLabel> labelMap) {
        this.labelMap = labelMap;
    }
    
}
