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

package pl.edu.icm.cermine.tools;

import com.google.common.collect.Lists;
import java.util.*;
import java.util.Map.Entry;

/**
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 * @param <T> element type
 */
public class CountMap<T> {

    private final Map<T, Integer> map = new HashMap<T, Integer>();
    
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

    public T getMaxCountObject() {
        if (size() == 0) {
            return null;
        }
        List<Map.Entry<T, Integer>> list = Lists.newArrayList(map.entrySet());
        list = sortEntries(list);
        return list.get(0).getKey();
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
