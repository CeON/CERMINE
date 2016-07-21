/**
 * This file is part of CERMINE project.
 * Copyright (c) 2011-2016 ICM-UW
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

package pl.edu.icm.cermine.evaluation.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;
import pl.edu.icm.cermine.tools.classification.general.TrainingSample;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class DividedEvaluationSet {

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

    public static List<DividedEvaluationSet> build(List<TrainingSample<BxZoneLabel>> samples, int numberOfFolds) {
        List<TrainingSample<BxZoneLabel>> shuffledDocs = new ArrayList<TrainingSample<BxZoneLabel>>(samples.size());
        shuffledDocs.addAll(samples);
        Random random = new Random(2102);
        Collections.shuffle(shuffledDocs, random);
        List<List<TrainingSample<BxZoneLabel>>> dividedSamples = new ArrayList<List<TrainingSample<BxZoneLabel>>>(numberOfFolds);

        for (int fold = 0; fold < numberOfFolds; ++fold) {
            int docsPerSet = shuffledDocs.size() / (numberOfFolds - fold);
            dividedSamples.add(new ArrayList<TrainingSample<BxZoneLabel>>());
            for (int idx = 0; idx < docsPerSet; ++idx) {
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