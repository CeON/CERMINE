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

import java.io.IOException;
import java.util.List;

/**
 * @author Mateusz Fedoryszak
 */
public interface FeatureVectorScaler {
    
    FeatureVector scaleFeatureVector(FeatureVector fv);
    
    <A extends Enum<A>> void calculateFeatureLimits(List<TrainingSample<A>> trainingElements);
    
    void saveRangeFile(String path) throws IOException;
    
}
