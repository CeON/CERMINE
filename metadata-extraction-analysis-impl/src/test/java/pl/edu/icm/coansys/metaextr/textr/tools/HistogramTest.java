package pl.edu.icm.coansys.metaextr.textr.tools;

import pl.edu.icm.coansys.metaextr.textr.tools.Histogram;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author krusek
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
