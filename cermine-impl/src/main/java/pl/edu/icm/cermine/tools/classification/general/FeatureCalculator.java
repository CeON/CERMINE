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

/**
 * Feature calculator is able to calculate a single feature's value.
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 *
 * @param <S> Type of objects whose feature value can be calculated.
 * @param <T> Type of an additional context object that can be used
 * for calculation.
 */
public abstract class FeatureCalculator<S, T> {

    private String featureName;
    
    /**
     * Returns the name of the feature that can be calculated by the calculator.
     * Two different feature calculators of the same parameter types should
     * return different feature names.
     *
     * @return Feature name.
     */
    public String getFeatureName() {
        if (featureName == null) {
            String className = this.getClass().getName();
            String[] classNameParts = className.split("\\.");
            className = classNameParts[classNameParts.length-1];
		
            if (className.contains("Feature")) {
                featureName = className.replace("Feature", "");
            } else {
                featureName =  className;
            }
        }
        return featureName;
	}

    /**
     * Calculates the value of a single feature.
     *
     * @param object An object whose feature value will be calculated.
     * @param context An additional context object used for calculation.
     * @return Calculated feature value.
     */
    public abstract double calculateFeatureValue(S object, T context);

}
