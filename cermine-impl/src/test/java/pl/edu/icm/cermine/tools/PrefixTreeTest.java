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
import com.google.common.collect.Sets;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * @author Dominika Tkaczyk
 */
public class PrefixTreeTest {

    @Test
    public void testPT() {
        PrefixTree pt = new PrefixTree(PrefixTree.START_TERM);
        pt.build(Sets.newHashSet("one", "one two", "one two three", "one three", "four five"));
        
        assertEquals(1, pt.match(Lists.newArrayList("one")));
        assertEquals(2, pt.match(Lists.newArrayList("one", "two")));
        assertEquals(2, pt.match(Lists.newArrayList("one", "two", "five")));
        assertEquals(-1, pt.match(Lists.newArrayList("four")));
        assertEquals(-1, pt.match(Lists.newArrayList("four", "six")));
        assertEquals(3, pt.match(Lists.newArrayList("one", "two", "three", "five")));
        assertEquals(2, pt.match(Lists.newArrayList("four", "five", "six")));   
    }

}
