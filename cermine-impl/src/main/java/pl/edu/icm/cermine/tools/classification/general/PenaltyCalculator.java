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
import java.util.Collections;
import java.util.List;
import pl.edu.icm.cermine.structure.model.BxZoneLabel;

/**
 * @author Pawel Szostek
 */
public class PenaltyCalculator {

    private final List<TrainingSample<BxZoneLabel>> samples;
    private List<BxZoneLabel> classes = null;

    public PenaltyCalculator(List<TrainingSample<BxZoneLabel>> samples) {
        this.samples = samples;
    }

    public double getPenaltyWeigth(BxZoneLabel label) {
        int allSamples = samples.size();
        int thisSamples = 0;
        for (TrainingSample<BxZoneLabel> sample : samples) {
            if (sample.getLabel() == label) {
                ++thisSamples;
            }
        }
        return (double) allSamples / thisSamples;
    }

    public List<BxZoneLabel> getClasses() {
        if (classes == null) {
            classes = new ArrayList<BxZoneLabel>();
            for (TrainingSample<BxZoneLabel> sample : samples) {
                if (!classes.contains(sample.getLabel())) {
                    classes.add(sample.getLabel());
                }
            }
            Collections.sort(classes);
        }
        return classes;
    }

}
