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

package pl.edu.icm.cermine.structure.tools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import pl.edu.icm.cermine.structure.model.BxBounds;

/**
 * @author Krzysztof Rusek
 */
public class BxBoundsBuilderTest {

    private static final double EPSILON = 0.001;

    @Test
    public void testExpand_bounds() {
        BxBoundsBuilder builder = new BxBoundsBuilder();
        builder.expand(new BxBounds(0, 0, 1, 1));
        assertBounds(0, 0, 1, 1, builder.getBounds());
        builder.expand(new BxBounds(1, 1, 4, 5));
        assertBounds(0, 0, 5, 6, builder.getBounds());
        builder.expand(new BxBounds(3, 3, 2, 2));
        assertBounds(0, 0, 5, 6, builder.getBounds());
    }

    private static void assertBounds(double x, double y, double w, double h, BxBounds bounds) {
        assertTrue(bounds != null);
        assertEquals(x, bounds.getX(), EPSILON);
        assertEquals(y, bounds.getY(), EPSILON);
        assertEquals(w, bounds.getWidth(), EPSILON);
        assertEquals(h, bounds.getHeight(), EPSILON);
    }
}
