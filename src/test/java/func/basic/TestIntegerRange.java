package func.basic;


import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.testng.Assert.*;

/**
 * This class can be useful with higher order functions like map and filter.
 */
public class TestIntegerRange {
    private static final List<Integer> range11 = new IntegerRange(11);
    private static final List<Integer> range4_11 = new IntegerRange(4, 11);
    private static final List<Integer> range4_11_3 = new IntegerRange(4, 11, 3);

    @Test
    public void test() {
        for (int i = 0; i < 11; i++) {
            assertEquals((Object) range11.get(i), i);
        }
    }

    @Test
    public void testSize() {
        assertEquals(range11.size(), 11);
    }

    @Test
    public void testContains() {
        assertFalse(range11.contains(-1));
        for (int i = 0; i < 11; i++) {
            assertTrue(range11.contains(i));
        }
        assertFalse(range11.contains(11));
    }

    @Test
    public void testIterate() {
        int expected = 0;
        for (int actual : range11) {
            assertEquals(actual, expected);
            expected++;
        }
        assertEquals(expected, 11);
    }

    @Test
    public void testIterator() {
        Iterator iterator = range11.iterator();
        for (int i = 0; i < 11; i++) {
            assertTrue(iterator.hasNext());
            assertEquals((int) (Integer) iterator.next(), i);
        }
        assertFalse(iterator.hasNext());
    }

    @Test
    public void testSubList() {
        assertEquals(range11.subList(0, 11),
                     Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
    }

    @Test(expectedExceptions = IndexOutOfBoundsException.class)
    public void testOutOfRange1() {
        range11.get(-1);
    }

    @Test(expectedExceptions = IndexOutOfBoundsException.class)
    public void testOutOfRange2() {
        range11.get(11);
    }

    @Test
    public void testWithStart() {
        for (int i = 0; i < 7; i++) {
            assertEquals((Object) range4_11.get(i),
                         i + 4);
        }
    }

    @Test
    public void testSizeWithStart() {
        assertEquals(range4_11.size(), 7);
    }

    @Test
    public void testContainsWithStart() {
        assertFalse(range4_11.contains(3));
        for (int i = 4; i < 11; i++) {
            assertTrue(range4_11.contains(i));
        }
        assertFalse(range4_11.contains(11));
    }

    @Test
    public void testIterateWithStart() {
        int expected = 4;
        for (int actual : range4_11) {
            assertEquals(actual, expected);
            expected++;
        }
        assertEquals(expected, 11);
    }

    @Test(expectedExceptions = IndexOutOfBoundsException.class)
    public void testOutOfRangeWithStart1() {
        List<Integer> range = range4_11;
        range.get(-1);
    }

    @Test(expectedExceptions = IndexOutOfBoundsException.class)
    public void testOutOfRangeWithStart2() {
        List<Integer> range = range4_11;
        range.get(8);
    }

    @Test
    public void testWithStep() {
        assertEquals((Object) range4_11_3.get(0), 4);
        assertEquals((Object) range4_11_3.get(1), 7);
        assertEquals((Object) range4_11_3.get(2), 10);
    }

    @Test
    public void testSizeWithStep() {
        assertEquals((Object) range4_11_3.size(), 3);
    }

    @Test
    public void testContainsWithStep() {
        assertFalse(range4_11_3.contains(3));
        assertTrue(range4_11_3.contains(4));
        assertTrue(range4_11_3.contains(7));
        assertTrue(range4_11_3.contains(10));
        assertFalse(range4_11_3.contains(11));
    }

    @Test
    public void testIterateWithStep() {
        int expected = 4;
        for (int actual : range4_11_3) {
            assertEquals(actual, expected);
            expected += 3;
        }
        assertEquals(expected, 13);
    }

    @Test
    public void testIteratorWithStep() {
        Iterator iterator = range4_11_3.iterator();
        for (int i = 4; i < 11; i += 3) {
            assertTrue(iterator.hasNext());
            assertEquals((int) (Integer) iterator.next(), i);
        }
        assertFalse(iterator.hasNext());
    }

    @Test(expectedExceptions = IndexOutOfBoundsException.class)
    public void testOutOfRangeWithStep1() {
        range4_11_3.get(-1);
    }

    @Test(expectedExceptions = IndexOutOfBoundsException.class)
    public void testOutOfRangeWithStep2() {
        range4_11_3.get(8);
    }

    @Test
    public void testToString() {
        assertEquals(range11.toString(),
                     "[0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10]");
        assertEquals(new IntegerRange(15).toString(),
                     "[0, 1, 2, 3, ..., 11, 12, 13, 14]");
        assertEquals(new IntegerRange(0, 50, 6).toString(),
                     "[0, 6, 12, 18, 24, 30, 36, 42, 48]");
    }
}
