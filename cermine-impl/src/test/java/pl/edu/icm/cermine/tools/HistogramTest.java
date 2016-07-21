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

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * @author Krzysztof Rusek
 */
public class HistogramTest {

    private static final double EPSILON = 1e-3;
    
    @Test
    public void testGetFrequency() {
        Histogram h = new Histogram(0, 16, 2);
        h.add(0);
        h.add(1);

        h.add(3);
        h.add(3);
        h.add(3.5);

        assertEquals(2.0, h.getFrequency(0), EPSILON);
        assertEquals(3.0, h.getFrequency(1), EPSILON);
    }

    @Test
    public void testGetPeakValue() {
        Histogram h = new Histogram(0, 16, 2);
        h.add(1); h.add(1); h.add(1);

        h.add(2.5); h.add(3.5); h.add(2.1); h.add(2.1);

        assertEquals(3.0, h.getPeakValue(), EPSILON);
    }

    @Test
    public void testSmooth() {
        Histogram h = new Histogram(20.0, 40.0, 2.0);
        h.add(21.0); h.add(21.0); h.add(21.0);
        h.smooth(6.0);
        assertEquals(1.0, h.getFrequency(0), EPSILON);
        assertEquals(1.0, h.getFrequency(1), EPSILON);
        assertEquals(0.0, h.getFrequency(2), EPSILON);
    }

    @Test
    public void testCircularSmooth() {
        Histogram h = new Histogram(0.0, 10.0, 1.0);
        h.add(0.5);
        h.circularSmooth(5.0);
        assertEquals(0.2, h.getFrequency(0), EPSILON);
        assertEquals(0.2, h.getFrequency(1), EPSILON);
        assertEquals(0.2, h.getFrequency(2), EPSILON);
        assertEquals(0.0, h.getFrequency(3), EPSILON);
        assertEquals(0.0, h.getFrequency(4), EPSILON);
        assertEquals(0.0, h.getFrequency(5), EPSILON);
        assertEquals(0.0, h.getFrequency(6), EPSILON);
        assertEquals(0.0, h.getFrequency(7), EPSILON);
        assertEquals(0.2, h.getFrequency(8), EPSILON);
        assertEquals(0.2, h.getFrequency(9), EPSILON);
    }
}
