package pl.edu.icm.yadda.analysis.relations;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


/**
 * Unit tests for {@link PersonDirectoryUtils}.
 * 
 * @author Lukasz Bolikowski (bolo@icm.edu.pl)
 *
 */
public class PersonDirectoryUtilsTest {
    @Test
    public void testSplit() {
        assertEquals("urn:foo.org:bar42", PersonDirectoryUtils.documentFromContrId("urn:foo.org:bar42#69"));
        assertEquals(69, PersonDirectoryUtils.positionFromContrId("urn:foo.org:bar42#69"));
    }
}
