package pl.edu.icm.cermine.structure.tools;

import static org.junit.Assert.*;

import org.junit.Test;
import pl.edu.icm.cermine.structure.model.BxBounds;

/**
 *
 * @author krusek
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
