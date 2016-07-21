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
package pl.edu.icm.cermine.tools.classification.general;

import java.util.ArrayList;
import java.util.List;

/**
 * Feature vector builder (GoF factory pattern). The builder calculates feature
 * vectors for objects using a list of single feature calculators.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 *
 * @param <S> Type of objects for whom features' values can be calculated.
 * @param <T> Type of additional context objects that can be used for
 * calculation.
 */
public class FeatureVectorBuilder<S, T> {

    private List<FeatureCalculator<S, T>> featureCalculators = new ArrayList<FeatureCalculator<S, T>>();

    public FeatureVector getFeatureVector(S object, T context) {
        FeatureVector featureVector = new FeatureVector();
        for (FeatureCalculator<S, T> fc : featureCalculators) {
            featureVector.addFeature(fc.getFeatureName(),
                    fc.calculateFeatureValue(object, context));
        }
        return featureVector;
    }

    public List<String> getFeatureNames() {
        List<String> ret = new ArrayList<String>();
        for (FeatureCalculator<S, T> fc : featureCalculators) {
            ret.add(fc.getFeatureName());
        }
        return ret;
    }

    public int size() {
        return featureCalculators.size();
    }

    public List<FeatureCalculator<S, T>> getFeatureCalculators() {
        return featureCalculators;
    }

    public void setFeatureCalculators(
            List<FeatureCalculator<S, T>> featureCalculators) {
        this.featureCalculators = featureCalculators;
    }

}
