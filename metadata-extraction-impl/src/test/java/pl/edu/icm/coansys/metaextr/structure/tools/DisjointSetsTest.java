package pl.edu.icm.coansys.metaextr.structure.tools;

import pl.edu.icm.coansys.metaextr.structure.tools.DisjointSets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author krusek
 */
public class DisjointSetsTest {

    private List<Integer> newRange(int start, int stop) {
        List<Integer> list = new ArrayList<Integer>();
        for (int i = start; i < stop; i++) {
            list.add(i);
        }
        return list;
    }

    private Set<Integer> newSet(Integer... elements) {
        return new HashSet<Integer>(Arrays.asList(elements));
    }

    @Test
    public void testConstruct() {
        DisjointSets<Integer> sets = new DisjointSets<Integer>(newRange(0, 10));
        assertFalse(sets.areTogether(0, 1));
        assertTrue(sets.areTogether(2, 2));
    }

    @Test
    public void testUnion() {
        DisjointSets<Integer> sets = new DisjointSets<Integer>(newRange(0, 10));

        sets.union(0, 8);
        assertTrue(sets.areTogether(0, 8));
        sets.union(0, 4);
        assertTrue(sets.areTogether(0, 4));
        assertTrue(sets.areTogether(4, 8));

        sets.union(1, 3);
        sets.union(5, 7);
        sets.union(3, 5);
        assertTrue(sets.areTogether(1, 7));

        sets.union(5, 4);
        assertTrue(sets.areTogether(0, 7));
        assertTrue(sets.areTogether(4, 5));
    }

    @Test
    public void testIterator() {
        DisjointSets<Integer> sets = new DisjointSets<Integer>(newRange(0, 10));
        for (Set<Integer> subset : sets) {
            assertEquals(1, subset.size());
        }

        Set<Set<Integer>> expected = new HashSet<Set<Integer>>();
        sets.union(0, 4); expected.add(newSet(0, 4));
        sets.union(1, 8); expected.add(newSet(1, 8));
        sets.union(2, 9); expected.add(newSet(2, 9));
        sets.union(3, 5); expected.add(newSet(3, 5));
        sets.union(6, 7); expected.add(newSet(6, 7));
        Set<Set<Integer>> actual = new HashSet<Set<Integer>>();
        for (Set<Integer> subset : sets) {
            assertEquals(2, subset.size());
            actual.add(subset);
        }
        assertEquals(expected, actual);
    }
}
