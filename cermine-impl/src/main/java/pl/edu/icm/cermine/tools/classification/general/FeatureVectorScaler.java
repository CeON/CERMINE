package pl.edu.icm.cermine.tools.classification.general;

import pl.edu.icm.cermine.tools.classification.features.FeatureVector;

import java.io.IOException;
import java.util.List;

/**
 * @author Mateusz Fedoryszak (m.fedoryszak@icm.edu.pl)
 */
public interface FeatureVectorScaler {
    FeatureVector scaleFeatureVector(FeatureVector fv);
    <A extends Enum<A>> void calculateFeatureLimits(List<TrainingSample<A>> trainingElements);
    void saveRangeFile(String path) throws IOException;
}
