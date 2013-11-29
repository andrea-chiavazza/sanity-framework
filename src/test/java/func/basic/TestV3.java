package func.basic;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * User: andrea
 * Date: 29/11/13
 * Time: 14:01
 */
public class TestV3 {
    @Test
    public void testOf() throws Exception {
        assertEquals(V3.of("oeu", 98, 'a'), new V3<>("oeu", 98, 'a'));
    }

    @Test
    public void testHashCode() throws Exception {
        assertEquals(V3.of("oeu", 98, 'a').hashCode(), 105665116);
    }

    @Test
    public void testEquals() throws Exception {
        assertTrue(new V3<>("oeu", 98, 'a').equals(new V3<>("oeu", 98, 'a')));
        assertFalse(new V3<>("oeu", 8, 'a').equals(new V3<>("oeu", 98, 'a')));
    }

    @Test
    public void testToString() throws Exception {
        assertEquals(new V3<>("oeu", 98, 'a').toString(),
                     "V3[\"oeu\", 98, 'a']");
    }

    @Test
    public void testExecute() throws Exception {
        assertEquals(new V3<>("oeu", 98, 'a').execute("hh", 33, 'a'),
                     new V3<>("hh", 33, 'a'));
    }
}
