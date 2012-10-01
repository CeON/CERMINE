package pl.edu.icm.coansys.metaextr.tools.classification.knn;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 *
 * @author Dominika Tkaczyk
 */
public class KnnModel<T> {
    
    private Set<KnnTrainingSample<T>> trainingSamples;

    public KnnModel(Set<KnnTrainingSample<T>> trainingSamples) {
        this.trainingSamples = trainingSamples;
    }

    public KnnModel() {
        trainingSamples = new HashSet<KnnTrainingSample<T>>();
    }
    
    public void addTrainingSample(KnnTrainingSample<T> sample) {
        trainingSamples.add(sample);
    }
    
    public Iterator<KnnTrainingSample<T>> getIterator() {
        return trainingSamples.iterator();
    }

}