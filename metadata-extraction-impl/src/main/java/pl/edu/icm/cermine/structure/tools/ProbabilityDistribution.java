package pl.edu.icm.cermine.structure.tools;

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

    private Map<E, Integer> eventCount = new HashMap<E, Integer>();
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
