package pl.edu.icm.coansys.metaextr.textr.tools;

import pl.edu.icm.coansys.metaextr.textr.tools.BxBoundsBuilder;
import pl.edu.icm.coansys.metaextr.textr.model.BxBounds;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author krusek
 */
public class BxBoundsBuilderTest {

    private static final double EPSILON = 0.001;

    @Test
    public void testExpand_bounds() {
        BxBoundsBuilder builder = new BxBoundsBuilder();
        assertEquals(null, builder.getBounds());
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
