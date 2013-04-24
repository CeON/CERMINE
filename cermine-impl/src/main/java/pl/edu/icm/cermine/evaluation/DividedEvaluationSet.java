package pl.edu.icm.cermine.evaluation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pl.edu.icm.cermine.structure.model.BxZoneLabel;
import pl.edu.icm.cermine.tools.classification.general.TrainingSample;

class DividedEvaluationSet {

    public DividedEvaluationSet(List<TrainingSample<BxZoneLabel>> trainingSamples, List<TrainingSample<BxZoneLabel>> testSamples) {
        this.trainingSamples = trainingSamples;
        this.testSamples = testSamples;
    }
    List<TrainingSample<BxZoneLabel>> trainingSamples;
    List<TrainingSample<BxZoneLabel>> testSamples;

    public List<TrainingSample<BxZoneLabel>> getTrainingDocuments() {
        return trainingSamples;
    }

    public List<TrainingSample<BxZoneLabel>> getTestDocuments() {
        return testSamples;
    }

    public static List<DividedEvaluationSet> build(List<TrainingSample<BxZoneLabel>> samples, Integer numberOfFolds) {
        List<TrainingSample<BxZoneLabel>> shuffledDocs = new ArrayList<TrainingSample<BxZoneLabel>>(samples.size());
        shuffledDocs.addAll(samples);
        Collections.shuffle(shuffledDocs);
        List<List<TrainingSample<BxZoneLabel>>> dividedSamples = new ArrayList<List<TrainingSample<BxZoneLabel>>>(numberOfFolds);

        for (Integer fold = 0; fold < numberOfFolds; ++fold) {
            Integer docsPerSet = shuffledDocs.size() / (numberOfFolds - fold);
            dividedSamples.add(new ArrayList<TrainingSample<BxZoneLabel>>());
            for (Integer idx = 0; idx < docsPerSet; ++idx) {
                dividedSamples.get(dividedSamples.size() - 1).add(shuffledDocs.get(0));
                shuffledDocs.remove(0);
            }
        }

        List<DividedEvaluationSet> ret = new ArrayList<DividedEvaluationSet>(numberOfFolds);

        for (int fold = 0; fold < numberOfFolds; ++fold) {
            List<TrainingSample<BxZoneLabel>> trainingSamples = new ArrayList<TrainingSample<BxZoneLabel>>();
            List<TrainingSample<BxZoneLabel>> testSamples = new ArrayList<TrainingSample<BxZoneLabel>>();
            for (int setIdx = 0; setIdx < numberOfFolds; ++setIdx) {
                if (setIdx == fold) {
                    testSamples.addAll(dividedSamples.get(setIdx));
                } else {
                    trainingSamples.addAll(dividedSamples.get(setIdx));
                }
            }
            ret.add(new DividedEvaluationSet(trainingSamples, testSamples));
        }
        return ret;
    }
}