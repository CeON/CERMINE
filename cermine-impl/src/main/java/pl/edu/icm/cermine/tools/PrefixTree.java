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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Dominika Tkaczyk (d.tkaczyk@icm.edu.pl)
 */
public class PrefixTree {
    
    public static final String START_TERM = "<START>";
    
    private final String term;
    List<PrefixTree> subTrees = new ArrayList<PrefixTree>();
    
    public PrefixTree(String term) {
        this.term = term;
    }
    
    public void build(Set<String> sentences) {
        for (String sentence : sentences) {
            String[] terms = sentence.split(" ");
            String first = terms[0];
            PrefixTree subTree = null;
            for (PrefixTree pt : subTrees) {
                if (pt.term.equals(first)) {
                    subTree = pt;
                }
            }
            if (subTree == null) {
                subTree = new PrefixTree(first);
                subTrees.add(subTree);
            }
            List<String> addTerms = new ArrayList<String>(Arrays.asList(terms));
            addTerms.remove(0);
            subTree.add(addTerms);
        }
    }
    
    public void add(List<String> terms) {
        if (terms.isEmpty()) {
            for (PrefixTree pt : subTrees) {
                if (pt.term == null) {
                    return;
                }
            }
            subTrees.add(new PrefixTree(null));
            return;
        }
        String first = terms.get(0);
        PrefixTree subTree = null;
        for (PrefixTree pt : subTrees) {
            if (first.equals(pt.term)) {
                subTree = pt;
            }
        }
        if (subTree == null) {
            subTree = new PrefixTree(first);
            subTrees.add(subTree);
        }
        terms.remove(0);
        subTree.add(terms);
    }
    
    public int match(List<String> terms) {
        if (terms.isEmpty()) {
            return term == null ? 0 : -1;
        }
        if (term == null) {
            return 0;
        }
        if (term.equals(START_TERM)) {
            int best = -1;
            for (PrefixTree t : subTrees) {
                int m = t.match(terms);
                if (m > best) {
                    best = m;
                }
            }
            return best;
        }
        if (term.equals(terms.get(0))) {
            int best = -1;
            for (PrefixTree t : subTrees) {
                int m = t.match(Lists.newArrayList(terms.subList(1, terms.size())));
                if (m > -1 && m+1 > best) {
                    best = m+1;
                }
            }
            return best;
        }
        return -1;
    }
    
    public void print() {
        print("");
    }
    
    private void print(String pref) {
        System.out.println(pref + "term: " + term);
        for (PrefixTree t : subTrees) {
            t.print(pref + "  ");
        }
    }
}
