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
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 * @param <S> class label
 */
public class TrainingSample<S> implements Cloneable {
    
    private FeatureVector features;
    private S label;
    
    private String data;
  
    public TrainingSample(FeatureVector features, S label) {
        this.features = features;
        this.label = label;
    }
    
    public FeatureVector getFeatureVector() {
        return features;
    }

    public void setFeatureVectors(FeatureVector features) {
        this.features = features;
    }

    public S getLabel() {
        return label;
    }

    public void setLabel(S label) {
        this.label = label;
    }

    @Override
    public TrainingSample<S> clone() throws CloneNotSupportedException {
        TrainingSample<S> element = (TrainingSample) super.clone();
        element.label = label;
        element.features = features.copy();
        return element;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
    
}
