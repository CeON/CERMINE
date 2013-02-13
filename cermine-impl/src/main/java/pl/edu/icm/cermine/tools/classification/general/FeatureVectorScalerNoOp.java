package pl.edu.icm.cermine.tools.classification.general;

import pl.edu.icm.cermine.tools.classification.features.FeatureVector;

import java.io.IOException;
import java.util.List;

/**
 * @author Mateusz Fedoryszak (m.fedoryszak@icm.edu.pl)
 */
public class FeatureVectorScalerNoOp implements FeatureVectorScaler {
    @Override
    public FeatureVector scaleFeatureVector(FeatureVector fv) {
        return fv;
    }

    @Override
    public <A extends Enum<A>> void calculateFeatureLimits(List<TrainingSample<A>> trainingElements) {
        // intentionally left blank
    }

    @Override
    public void saveRangeFile(String path) throws IOException {
        // intentionally left blank
    }
}
