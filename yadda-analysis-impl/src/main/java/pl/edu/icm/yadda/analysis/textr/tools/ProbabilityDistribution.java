package pl.edu.icm.yadda.analysis.textr.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class ProbabilityDistribution<T> {

    private Map<T, Integer> eventCount = new HashMap<T, Integer>();
    private int totalCount = 0;

    public void addEvent(T event) {
        if (!eventCount.containsKey(event)) {
            eventCount.put(event, 0);
        }
        eventCount.put(event, eventCount.get(event) + 1);
        totalCount++;
    }
    
    public void removeEvent(T event) {
        if (eventCount.containsKey(event) && eventCount.get(event) > 0) {
            eventCount.put(event, eventCount.get(event) - 1);
            totalCount--;
        }
    }
    
    public List<T> getEvents() {
        return new ArrayList<T>(eventCount.keySet());
    }

    public int getEventCount(T event) {
        return eventCount.containsKey(event) ? eventCount.get(event) : 0;
    }

    public double getProbability(T event) {
        return (totalCount == 0 || !eventCount.containsKey(event)) ?
            (double) 0 : (double) eventCount.get(event) / (double) totalCount;
    }

    public double getEntropy() {
        if (totalCount == 0) {
            return 0;
        }

        double entropy = 0;
        for (T event : eventCount.keySet()) {
            double probability = getProbability(event);
            if (probability > 0) {
                entropy -= probability * Math.log(probability) / Math.log(2);
            }
        }

        return entropy;
    }

}
