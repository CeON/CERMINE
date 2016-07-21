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

package pl.edu.icm.cermine.tools.statistics;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class Population {
    
    private final Multiset<Double> observations = HashMultiset.create();
    
    private double mean = Double.NaN;
    private double sd = Double.NaN;
    
    public void addObservation(double observation) {
        observations.add(observation);
        mean = Double.NaN;
        sd = Double.NaN;
    }
    
    public void reset() {
        observations.clear();
        mean = Double.NaN;
        sd = Double.NaN;
    }
    
    public double getMean() {
        if (!Double.isNaN(mean)) {
            return mean;
        }
        mean = 0;
        for (double observation : observations) {
            mean += observation;
        }
        mean = mean / observations.size();
        return mean;
    }
    
    public double getSD() {
        if (!Double.isNaN(sd)) {
            return sd;
        }
        double sse = 0;
        for (double observation : observations) {
            sse += Math.pow(observation - getMean(), 2);
        }
        sd = Math.sqrt(sse / observations.size());
        return sd;
    }
    
    public double getZScore(double observation) {
        return (observation - getMean()) / getSD();
    }
    
}
