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

package pl.edu.icm.cermine.tools.classification.knn;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import pl.edu.icm.cermine.tools.classification.general.FeatureVector;
import pl.edu.icm.cermine.tools.classification.general.TrainingSample;
import pl.edu.icm.cermine.tools.distance.FeatureVectorDistanceMetric;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 * @param <T> class label
 */
public class KnnClassifier<T> {
    
    public T classify(KnnModel<T> model, FeatureVectorDistanceMetric metric, FeatureVector sample, int samplesCount) {
       
        FVEuclideanDistanceComparator comparator = new FVEuclideanDistanceComparator(sample, metric);
        
        TrainingSample[] voters = new TrainingSample[samplesCount];
        Iterator<TrainingSample<T>> trainingIterator = model.getIterator();
        int i = 0;
        TrainingSample<T> largest = null;
        int largestIndex = 0;
        while (trainingIterator.hasNext()) {
            if (i < samplesCount) {
                voters[i] = trainingIterator.next();
            } else {
                if (largest == null) {
                    largest = voters[0];
                    largestIndex = 0;
                    for (int j = 1; j < samplesCount; j++) {
                        if (comparator.compare(largest, voters[j]) < 0) {
                            largest = voters[j];
                            largestIndex = j;
                        }
                    }
                }
                TrainingSample<T> next = trainingIterator.next();
                if (comparator.compare(largest, next) > 0) {
                    voters[largestIndex] = next;
                    largest = null;
                }
            }
            i++;
        }
        
        Map<T,Integer> labelCountMap = new HashMap<T,Integer>();
        for (TrainingSample<T> trainingSample : voters) {
            if (trainingSample != null) {
                T label = trainingSample.getLabel();
                if (labelCountMap.get(label) == null) {
                    labelCountMap.put(label, 1);
                } else {
                    labelCountMap.put(label, labelCountMap.get(label)+1);
                }
            }
        }
        
        T label = null;
        int labelCount = 0;
        for (Entry<T, Integer> entry : labelCountMap.entrySet()) {
            if (labelCountMap.get(entry.getKey()) > labelCount) {
                label = entry.getKey();
                labelCount = labelCountMap.get(entry.getKey());
            }
        }
       
        return label;
    }

    public class FVEuclideanDistanceComparator implements Comparator<TrainingSample<T>> {
        
        private final FeatureVectorDistanceMetric metric;
        private final FeatureVector sample;

        public FVEuclideanDistanceComparator(FeatureVector sample, FeatureVectorDistanceMetric metric) {
            this.sample = sample;
            this.metric = metric;
        }
        
        @Override
        public int compare(TrainingSample<T> ts1, TrainingSample<T> ts2) {
            double dist1 = metric.getDistance(sample, ts1.getFeatureVector());
            double dist2 = metric.getDistance(sample, ts2.getFeatureVector());
            return Double.compare(dist1, dist2);
        }
 
    }
    
}
