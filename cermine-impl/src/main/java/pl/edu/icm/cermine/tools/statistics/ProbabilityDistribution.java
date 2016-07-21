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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** A Generic container for keeping information on events' probabilities.
 * 
 * @param <E> is a type of the event (i.e. observations, states)
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class ProbabilityDistribution<E> {

    private final Map<E, Integer> eventCount = new HashMap<E, Integer>();
    private int totalCount = 0;

    public void addEvent(E event) {
        if (!eventCount.containsKey(event)) {
            eventCount.put(event, 0);
        }
        eventCount.put(event, eventCount.get(event) + 1);
        totalCount++;
    }
    
    public void removeEvent(E event) {
        if (eventCount.containsKey(event) && eventCount.get(event) > 0) {
            eventCount.put(event, eventCount.get(event) - 1);
            totalCount--;
        }
    }
    
    public List<E> getEvents() {
        return new ArrayList<E>(eventCount.keySet());
    }

    public int getEventCount(E event) {
        return eventCount.containsKey(event) ? eventCount.get(event) : 0;
    }

    public double getProbability(E event) {
        return (totalCount == 0 || !eventCount.containsKey(event)) ?
            (double) 0 : (double) eventCount.get(event) / (double) totalCount;
    }

    public double getEntropy() {
        if (totalCount == 0) {
            return 0;
        }

        double entropy = 0;
        for (E event : eventCount.keySet()) {
            double probability = getProbability(event);
            if (probability > 0) {
                entropy -= probability * Math.log(probability) / Math.log(2);
            }
        }

        return entropy;
    }

}
