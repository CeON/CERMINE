/**
 * This file is part of CERMINE project.
 * Copyright (c) 2011-2013 ICM-UW
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

package pl.edu.icm.cermine.tools.classification.hmm.model;

import pl.edu.icm.cermine.tools.classification.features.FeatureVector;
import pl.edu.icm.cermine.tools.classification.general.TrainingSample;

/**
 * 
 * @author Dominika Tkaczyk (dtkaczyk@icm.edu.pl)
 */
public class HMMTrainingSample<S> extends TrainingSample<S> {

    private S nextLabel;
    private boolean first;

    public HMMTrainingSample(FeatureVector observation, S label, boolean first) {
        super(observation, label);
        this.first = first;
    }

    public void setNextLabel(S nextLabel) {
        this.nextLabel = nextLabel;
    }

    public FeatureVector getObservation() {
        return getFeatureVector();
    }

    public boolean isFirst() {
        return first;
    }

    public S getNextLabel() {
        return nextLabel;
    }
    
    @Override
    public HMMTrainingSample<S> clone() throws CloneNotSupportedException {
        HMMTrainingSample<S> element = (HMMTrainingSample) super.clone();
        element.first = first;
        element.nextLabel = nextLabel;
        return element;
    }

}
