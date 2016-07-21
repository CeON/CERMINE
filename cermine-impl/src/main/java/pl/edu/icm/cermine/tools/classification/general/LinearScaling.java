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

import pl.edu.icm.cermine.tools.timeout.TimeoutRegister;

/**
 * @author Pawel Szostek
 */
public class LinearScaling implements ScalingStrategy {

    @Override
    public FeatureVector scaleFeatureVector(double scaledLowerBound,
            double scaledUpperBound, FeatureLimits[] limits, FeatureVector fv) {
        final double EPS = 0.00001;
        FeatureVector newVector = new FeatureVector();

        int featureIdx = 0;
        for (String name : fv.getFeatureNames()) {
            //scaling function: y = a*x+b
            // featureLower = a*v_min + b
            // featureUpper = a*v_max + b
            if (Math.abs(limits[featureIdx].getMax() - limits[featureIdx].getMin()) < EPS) {
                newVector.addFeature(name, 1.0);
            } else {
                Double featureValue = fv.getValue(name);
                double a = (scaledUpperBound - scaledLowerBound) / (limits[featureIdx].getMax() - limits[featureIdx].getMin());
                double b = scaledLowerBound - a * limits[featureIdx].getMin();

                featureValue = a * featureValue + b;

				if (featureValue.isNaN()) {
					throw new RuntimeException("Feature value is set to NaN: "+name);
				}
				newVector.addFeature(name, featureValue);
			}
			++featureIdx;
			TimeoutRegister.get().check();
		}
		return newVector;
	}
}
