package func.basic;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * User: andrea
 * Date: 29/11/13
 * Time: 13:50
 */
public class TestV2 {
    @Test
    public void testOf() throws Exception {
        assertEquals(V2.of("oeu", 98), new V2<>("oeu", 98));
    }

    @Test
    public void testHashCode() throws Exception {
        assertEquals(V2.of("oeu", 98).hashCode(), 3408579);
    }

    @Test
    public void testEquals() throws Exception {
        assertTrue(new V2<>("oeu", 98).equals(new V2<>("oeu", 98)));
        assertFalse(new V2<>("oeu", 8).equals(new V2<>("oeu", 98)));
    }

    @Test
    public void testToString() throws Exception {
        assertEquals(new V2<>("oeu", 98).toString(),
                     "V2[\"oeu\", 98]");
    }

    @Test
    public void testExecute() throws Exception {
        assertEquals(new V2<>("oeu", 98).execute("hh", 33),
                     new V2<>("hh", 33));
    }

}
