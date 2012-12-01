package pl.edu.icm.cermine.tools.classification.svm;

/*
public class SVMZoneMultiClassifier2 extends SVMZoneClassifier {

    private FeatureVectorBuilder<BxZone, BxPage> featureVectorBuilder;
    private List<BxZoneLabel> possibleLabels;
    private Map<BxZoneLabel, SVMClassifier> classifiers;
    private SVMClassifier ultimateClassifier;

    public SVMZoneMultiClassifier2(FeatureVectorBuilder<BxZone, BxPage> featureVectorBuilder, Class<BxZoneLabel> bxZoneLabelClass, Boolean forceReadingOrder) {
        super(featureVectorBuilder, bxZoneLabelClass);
        this.featureVectorBuilder = featureVectorBuilder;
    }

    @Override
    public BxDocument classifyZones(BxDocument document) throws AnalysisException {
        for (BxZone zone : document.asZones()) {
            Map<BxZoneLabel, Integer> votes = new HashMap<BxZoneLabel, Integer>();
            for (BxZoneLabel lab : possibleLabels) {
                votes.put(lab, 0);
            }
            votes.put(BxZoneLabel.OTH_UNKNOWN, 0);

            for (BxZoneLabel lab : possibleLabels) {
                BxZoneLabel predicted = classifiers.get(lab).predictZoneLabel(zone);
                votes.put(predicted, votes.get(predicted) + 1);
            }
            if (votes.get(BxZoneLabel.OTH_UNKNOWN) == possibleLabels.size() - 1) { //one classifier recognized this class
                for (BxZoneLabel lab : possibleLabels) {
                    if (votes.get(lab) == 1) {
                        zone.setLabel(lab);
                        continue;
                    }
                }
            } else { //at least two classifiers chose this class - leave decistion to the definitive classifier
                zone.setLabel(ultimateClassifier.predictZoneLabel(zone));
            }
        }
        return document;
    }

    public SVMClassifier getClassifier(BxZoneLabel lab) {
        if (lab == null) {
            return ultimateClassifier;
        }
        return classifiers.get(lab);
    }

    public void setPossibleLabels(Collection<BxZoneLabel> labels) {
        possibleLabels.addAll(labels);
        for (BxZoneLabel lab : possibleLabels) {
            classifiers.put(lab, new SVMClassifier(featureVectorBuilder, this.enumClassObj));
        }
    }

    @Override
    public void buildClassifier(final List<TrainingElement<BxZoneLabel>> trainingElements) {
        possibleLabels = new ArrayList<BxZoneLabel>();
        for (TrainingElement<BxZoneLabel> elem : trainingElements) {
            if (!possibleLabels.contains(elem.getLabel())) {
                possibleLabels.add(elem.getLabel());
            }
        }

        for (final BxZoneLabel lab : possibleLabels) {
            SVMClassifier clas = classifiers.get(lab);

            List<TrainingElement<BxZoneLabel>> convertedElements = new ArrayList<TrainingElement<BxZoneLabel>>() {

                {
                    Integer elemIdx = 0;
                    for (TrainingElement<BxZoneLabel> elem : trainingElements) {
                        //convert training elements' labels to one of two: lab (current iteration) or OTH_UNKNOWN
                        try {
                            TrainingElement<BxZoneLabel> toBeAdded = elem.clone();
                            if (toBeAdded.getLabel() != lab) { //if label is wrong then change it
                                toBeAdded.setLabel(BxZoneLabel.OTH_UNKNOWN);
                                if (elemIdx != trainingElements.size() - 1) {//last element
                                    trainingElements.get(elemIdx + 1).getObservation().setFeature("PreviousZoneFeature",
                                        (double) BxZoneLabel.OTH_UNKNOWN.ordinal());
                                }
                            } //else leave it as it is
                            add(toBeAdded);
                            ++elemIdx;
                        } catch (CloneNotSupportedException ex) {}
                    }
                }
            };
            clas.buildClassifier(convertedElements);
        }
        //build a classifier for ultimate recognition
        ultimateClassifier.buildClassifier(trainingElements);
    }
}
*/
