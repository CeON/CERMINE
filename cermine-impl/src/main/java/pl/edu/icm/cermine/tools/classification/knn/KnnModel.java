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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import pl.edu.icm.cermine.tools.classification.general.TrainingSample;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 * @param <T> label
 */
public class KnnModel<T> {
    
    private final Set<TrainingSample<T>> trainingSamples;

    public KnnModel(Set<TrainingSample<T>> trainingSamples) {
        this.trainingSamples = trainingSamples;
    }

    public KnnModel() {
        trainingSamples = new HashSet<TrainingSample<T>>();
    }
    
    public void addTrainingSample(TrainingSample<T> sample) {
        trainingSamples.add(sample);
    }
    
    public Iterator<TrainingSample<T>> getIterator() {
        return trainingSamples.iterator();
    }

}