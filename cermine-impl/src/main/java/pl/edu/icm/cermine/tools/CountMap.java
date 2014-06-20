package pl.edu.icm.cermine.tools;

import com.google.common.collect.Lists;
import java.util.*;
import java.util.Map.Entry;

/**
 *
 * @author Dominika Tkaczyk
 */
public class CountMap<T> {

    private Map<T, Integer> map = new HashMap<T, Integer>();
    
    public void add(T object) {
        if (map.get(object) == null) {
            map.put(object, 0);
        }
        map.put(object, map.get(object) + 1);
    }
    
    public void clear() {
        map.clear();
    }
    
    public int getCount(T object) {
        return (map.get(object) == null) ? 0 : map.get(object);
    }
    
    public int size() {
        return map.size();
    }
    
    public List<Map.Entry<T, Integer>> getSortedEntries() {
        List<Map.Entry<T, Integer>> list = Lists.newArrayList(map.entrySet());
        return sortEntries(list);
    }
    
    public List<Map.Entry<T, Integer>> getSortedEntries(int minCount) {
        List<Map.Entry<T, Integer>> list = new ArrayList<Map.Entry<T, Integer>>();
        for (Map.Entry<T, Integer> entry : map.entrySet()) {
            if (entry.getValue() >= minCount) {
                list.add(entry);
            }
        }
        return sortEntries(list);
    }
    
    private List<Map.Entry<T, Integer>> sortEntries(List<Map.Entry<T, Integer>> list) {
        Collections.sort(list, new Comparator<Map.Entry<T, Integer>>() {

            @Override
            public int compare(Entry<T, Integer> t, Entry<T, Integer> t1) {
                return t1.getValue().compareTo(t.getValue());
            }
        });
        
        return list;
    }
    
}
