package pl.edu.icm.cermine.tools.classification.knn;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import pl.edu.icm.cermine.tools.classification.general.TrainingSample;


/**
 *
 * @author Dominika Tkaczyk
 */
public class KnnModel<T> {
    
    private Set<TrainingSample<T>> trainingSamples;

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