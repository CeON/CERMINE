package pl.edu.icm.cermine.tools.classification.svm;

import java.util.*;
import java.util.Map.Entry;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.metadata.zoneclassification.tools.LabelPair;
import pl.edu.icm.cermine.structure.model.BxDocument;
import pl.edu.icm.cermine.structure.model.BxPage;
import pl.edu.icm.cermine.structure.model.BxZone;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;
import pl.edu.icm.cermine.tools.classification.features.FeatureVectorBuilder;
import pl.edu.icm.cermine.tools.classification.general.ClassificationUtils;
import pl.edu.icm.cermine.tools.classification.hmm.training.TrainingElement;

/*
public class SVMZoneMultiClassifier extends SVMZoneClassifier {

    private FeatureVectorBuilder<BxZone, BxPage> featureVectorBuilder;
    private List<BxZoneLabel> possibleLabels;
    private Map<LabelPair, SVMZoneClassifier> classifierMatrix;

    public SVMMultiClassifier(FeatureVectorBuilder<BxZone, BxPage> featureVectorBuilder) {
        super(featureVectorBuilder);
        this.featureVectorBuilder = featureVectorBuilder;
    }

    private BxZoneLabel findMaxLabel(Map<BxZoneLabel, Integer> votes) {
        Integer maxVote = 0;
        BxZoneLabel bestLab = null;
        for (Entry<BxZoneLabel, Integer> entry : votes.entrySet()) {
            if (entry.getValue() > maxVote) {
                maxVote = entry.getValue();
                bestLab = entry.getKey();
            }
        }
        assert bestLab != null;
        return bestLab;
    }

    @Override
    public BxDocument classifyZones(BxDocument document) throws AnalysisException {
        for (BxZone zone : document.asZones()) {
            Map<BxZoneLabel, Integer> votes = new HashMap<BxZoneLabel, Integer>();
            for (LabelPair labelPair : classifierMatrix.keySet()) {
                SVMZoneClassifier clas = classifierMatrix.get(labelPair);
                BxZoneLabel vote = clas.predictZoneLabel(zone);
                if (votes.containsKey(vote)) {
                    votes.put(vote, votes.get(vote) + 1);
                } else {
                    votes.put(vote, 1);
                }
                zone.setLabel(findMaxLabel(votes));
            }
        }
        return document;
    }

    public SVMZoneClassifier getClassifier(BxZoneLabel lab1, BxZoneLabel lab2) {
        if (lab1 == lab2) {
            throw new RuntimeException("No classifier for two same labels");
        }
        if (lab2.ordinal() > lab1.ordinal()) {
            return classifierMatrix.get(new LabelPair(lab2, lab1));
        } else {
            return classifierMatrix.get(new LabelPair(lab1, lab2));
        }
    }

    public void setPossibleLabels(Collection<BxZoneLabel> labels) {
        possibleLabels.addAll(labels);
        for (BxZoneLabel lab1 : possibleLabels) {
            for (BxZoneLabel lab2 : possibleLabels) {
                if (lab2.ordinal() >= lab1.ordinal()) {
                    continue;
                }
                classifierMatrix.put(new LabelPair(lab1, lab2), new SVMZoneClassifier(featureVectorBuilder));
            }
        }
    }

    @Override
    public void buildClassifier(List<TrainingElement<BxZoneLabel>> trainingElements) {
        possibleLabels = new ArrayList<BxZoneLabel>();
        for (TrainingElement<BxZoneLabel> elem : trainingElements) {
            if (!possibleLabels.contains(elem.getLabel())) {
                possibleLabels.add(elem.getLabel());
            }
        }

        for (final BxZoneLabel lab1 : possibleLabels) {
            for (final BxZoneLabel lab2 : possibleLabels) {
                if (lab2.ordinal() >= lab1.ordinal()) {
                    continue;
                }
                LabelPair coord = new LabelPair(lab1, lab2);
                SVMZoneClassifier clas = classifierMatrix.get(coord);
                List<TrainingElement<BxZoneLabel>> filteredTrainigElements = ClassificationUtils.filterElements(trainingElements, new ArrayList<BxZoneLabel>() {

                    {
                        add(lab1);
                        add(lab2);
                    }
                });
                clas.buildClassifier(filteredTrainigElements);
            }
        }
    }
}
*/