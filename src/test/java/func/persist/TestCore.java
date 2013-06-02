package func.persist;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * User: andrea
 * Date: 03/10/12
 * Time: 11:04
 */
public class TestCore {
    @Test
    public void testCalculation1() {
        assertEquals(
            17,
            Core.makeCalculation1(5, 6));
    }
}
