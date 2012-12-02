package pl.edu.icm.cermine.evaluation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import libsvm.svm_parameter;
import org.apache.commons.cli.ParseException;
import pl.edu.icm.cermine.exception.AnalysisException;
import pl.edu.icm.cermine.structure.ZoneClassifier;
import pl.edu.icm.cermine.structure.model.*;
import pl.edu.icm.cermine.structure.tools.BxModelUtils;
import pl.edu.icm.cermine.tools.classification.features.FeatureVectorBuilder;
import pl.edu.icm.cermine.tools.classification.general.BxDocsToTrainingSamplesConverter;
import pl.edu.icm.cermine.tools.classification.general.PipelineClassifier;
import pl.edu.icm.cermine.tools.classification.general.PipelineClassifier.PickyClassifier;
import pl.edu.icm.cermine.tools.classification.general.TrainingSample;
import pl.edu.icm.cermine.tools.classification.sampleselection.OversamplingSelector;
import pl.edu.icm.cermine.tools.classification.sampleselection.SampleSelector;
import pl.edu.icm.cermine.tools.classification.svm.SVMZoneClassifier;

public class SVMZoneClassificationEvaluator extends CrossvalidatingZoneClassificationEvaluator {

    @Override
    protected ClassificationResults compareDocuments(BxDocument expected, BxDocument actual) {
        ClassificationResults ret = newResults();
        for (Integer idx = 0; idx < actual.asZones().size(); ++idx) {
            ClassificationResults itemResults = compareItems(expected.asZones().get(idx), actual.asZones().get(idx));
            ret.add(itemResults);
        }
        return ret;
    }

    @Override
    protected void preprocessDocumentForEvaluation(BxDocument doc) {
        for (BxZone zone : doc.asZones()) {
            if (zone.getLabel().getCategory() == BxZoneLabelCategory.CAT_OTHER) {
                zone.setLabel(BxZoneLabel.GEN_OTHER);
            } else if (zone.getLabel().getCategory() == BxZoneLabelCategory.CAT_BODY) {
                zone.setLabel(BxZoneLabel.GEN_BODY);
            } else if (zone.getLabel().getCategory() == BxZoneLabelCategory.CAT_REFERENCES) {
                zone.setLabel(BxZoneLabel.GEN_REFERENCES);
            }
        }
        //else leave it as it is
    }

    @Override
    protected ZoneClassifier getZoneClassifier(List<BxDocument> trainingDocuments) throws AnalysisException {
        FeatureVectorBuilder<BxZone, BxPage> featureVectorBuilder = getFeatureVectorBuilder();

        // Filter the training documents
        // so that in the learning examples all classes are
        // represented equally

        double inequalityFactor = 1.5;
        SampleSelector<BxZoneLabel> selector = new OversamplingSelector<BxZoneLabel>(1.0);
        List<TrainingSample<BxZoneLabel>> trainingSamples;

        SVMZoneClassifier metaBodyRefClassifier = new SVMZoneClassifier(featureVectorBuilder);
        {
            svm_parameter param = SVMZoneClassifier.getDefaultParam();
            param.svm_type = svm_parameter.C_SVC;
            param.gamma = 0.176776695297;
            param.C = 4.0;
            param.degree = 2;
            param.kernel_type = svm_parameter.POLY;
            metaBodyRefClassifier.setParameter(param);

            List<BxDocument> copiedTrainingDocuments = BxModelUtils.deepClone(trainingDocuments);
            for (BxDocument doc : copiedTrainingDocuments) {
                for (BxZone zone : doc.asZones()) {
                    if (zone.getLabel().getCategory() == BxZoneLabelCategory.CAT_OTHER) {
                        zone.setLabel(BxZoneLabel.GEN_BODY);
                    } else {
                        zone.setLabel(zone.getLabel().getGeneralLabel());
                    }
                }
            }
            trainingSamples = BxDocsToTrainingSamplesConverter.getZoneTrainingSamples(copiedTrainingDocuments, 
                    featureVectorBuilder, BxZoneLabel.getLabelToGeneralMap());
            trainingSamples = selector.pickElements(trainingSamples);

            System.out.println("building metaBodyRef");
            metaBodyRefClassifier.buildClassifier(trainingSamples);
        }


        SVMZoneClassifier metaClassifier = new SVMZoneClassifier(featureVectorBuilder);
        {
            svm_parameter param = SVMZoneClassifier.getDefaultParam();
            param.svm_type = svm_parameter.C_SVC;
            param.gamma = 0.176776695297;
            param.C = 4.0;
            param.degree = 2;
            param.kernel_type = svm_parameter.POLY;
            metaClassifier.setParameter(param);

            List<BxDocument> copiedTrainingDocuments = BxModelUtils.deepClone(trainingDocuments);
            System.out.println("trainingDocuments " + trainingDocuments.size());
            for (BxDocument doc : copiedTrainingDocuments) {
                for (BxZone zone : doc.asZones()) {
                    if (zone.getLabel().getCategory() == BxZoneLabelCategory.CAT_OTHER) {
                        zone.setLabel(BxZoneLabel.GEN_BODY);
                    }
                    if (zone.getLabel().getCategory() == BxZoneLabelCategory.CAT_REFERENCES) {
                        zone.setLabel(BxZoneLabel.GEN_REFERENCES);
                    }
                }
            }

            trainingSamples = BxDocsToTrainingSamplesConverter.getZoneTrainingSamples(copiedTrainingDocuments, 
                    featureVectorBuilder, BxZoneLabel.getLabelToGeneralMap());
            System.out.println("TrainingSamples" + trainingSamples.size());
            List<TrainingSample<BxZoneLabel>> toBeRemoved = new ArrayList<TrainingSample<BxZoneLabel>>();
            for (TrainingSample<BxZoneLabel> elem : trainingSamples) {
                if (elem.getLabel().getGeneralLabel() != BxZoneLabel.GEN_METADATA) {
                    toBeRemoved.add(elem);
                }
            }
            trainingSamples.removeAll(toBeRemoved);
            System.out.println("TrainingSamples" + trainingSamples.size());
            trainingSamples = selector.pickElements(trainingSamples);
            System.out.println("TrainingSamples" + trainingSamples.size());
            System.out.println("building meta");
            metaClassifier.buildClassifier(trainingSamples);
        }

        SVMZoneClassifier bodyOtherClassifier = new SVMZoneClassifier(featureVectorBuilder);
        {
            svm_parameter param = SVMZoneClassifier.getDefaultParam();
            param.svm_type = svm_parameter.C_SVC;
            param.gamma = 0.176776695297;
            param.C = 4.0;
            param.degree = 2;
            param.kernel_type = svm_parameter.POLY;
            bodyOtherClassifier.setParameter(param);

            List<BxDocument> copiedTrainingDocuments = BxModelUtils.deepClone(trainingDocuments);
            for (BxDocument doc : copiedTrainingDocuments) {
                for (BxZone zone : doc.asZones()) {
                    zone.setLabel(zone.getLabel().getGeneralLabel());
                }
            }

            trainingSamples = BxDocsToTrainingSamplesConverter.getZoneTrainingSamples(copiedTrainingDocuments, 
                    featureVectorBuilder, BxZoneLabel.getLabelToGeneralMap());
            System.out.println("TrainingSamples" + trainingSamples.size());
            List<TrainingSample<BxZoneLabel>> toBeRemoved = new ArrayList<TrainingSample<BxZoneLabel>>();
            for (TrainingSample<BxZoneLabel> elem : trainingSamples) {
                if (elem.getLabel().getGeneralLabel() != BxZoneLabel.GEN_BODY
                        && elem.getLabel().getGeneralLabel() != BxZoneLabel.GEN_OTHER) {
                    toBeRemoved.add(elem);
                }
            }
            trainingSamples.removeAll(toBeRemoved);
            System.out.println("TrainingSamples" + trainingSamples.size());
            trainingSamples = selector.pickElements(trainingSamples);
            System.out.println("TrainingSamples" + trainingSamples.size());
            System.out.println("building bodyOtherClassifier");
            bodyOtherClassifier.buildClassifier(trainingSamples);
        }
        PipelineClassifier classifier = new PipelineClassifier();
        classifier.addClassifier(new PickyClassifier(metaBodyRefClassifier) {

            @Override
            public Boolean shouldBeClassified(BxZone zone) {
                return true;
            }
        });
        classifier.addClassifier(new PickyClassifier(metaClassifier) {

            @Override
            public Boolean shouldBeClassified(BxZone zone) {
                return zone.getLabel() == BxZoneLabel.GEN_METADATA;
            }
        });
        classifier.addClassifier(new PickyClassifier(bodyOtherClassifier) {

            @Override
            public Boolean shouldBeClassified(BxZone zone) {
            	return zone.getLabel() == BxZoneLabel.GEN_BODY;
            }
        });
        return classifier;
    }

    public static void main(String[] args)
            throws ParseException, RuntimeException, AnalysisException, IOException {
        CrossvalidatingZoneClassificationEvaluator.main(args, new SVMZoneClassificationEvaluator());
    }
}
