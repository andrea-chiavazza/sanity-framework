package func.utility;

import org.pcollections.OrderedPSet;
import org.testng.annotations.Test;

import java.util.Arrays;

import static func.utility.General.replaceInOrderedSet;
import static org.junit.Assert.assertEquals;

public class TestGeneral {
    @Test
    public void testReplace() throws Exception {
        assertEquals(
            OrderedPSet.from(Arrays.asList(2, 9, 8, -4, 0)),
            replaceInOrderedSet(
                OrderedPSet.from(Arrays.asList(2, 5, 8, -4, 0)), 5, 9));
        assertEquals(
            OrderedPSet.from(Arrays.asList(2, 9, 8, -4, 0)),
            replaceInOrderedSet(
                OrderedPSet.from(Arrays.asList(2, 9, 8, -4, 0)), 1, 3));
    }

}
