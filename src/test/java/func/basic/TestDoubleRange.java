package func.basic;

import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.testng.Assert.*;


/**
 * This class can be useful with higher order functions like map and filter.
 */
public class TestDoubleRange {
    private static final double PRECISION = 0.00001;
    private static final List<Double> range11 = new DoubleRange(11);
    private static final List<Double> range4_11 = new DoubleRange(4, 11);
    private static final List<Double> range4_11_3 = new DoubleRange(4, 11, 3);

    @Test
    public void test() {
        for (int i = 0; i < 11; i++) {
            assertEquals((double) i, (Object) range11.get(i));
        }
    }

    @Test
    public void testSize() {
        assertEquals(11, range11.size());
    }

    @Test
    public void testContains() {
        assertFalse(range11.contains(-1));
        for (double i = 0.0; i < 11.0; i += 1.0) {
            assertTrue(range11.contains(i));
        }
        assertFalse(range11.contains(11));
    }

    @Test
    public void testIterate() {
        int expected = 0;
        for (double actual : range11) {
            assertEquals(expected, actual, PRECISION);
            expected++;
        }
        assertEquals(11, expected);
    }

    @Test
    public void testIterator() {
        Iterator iterator = range11.iterator();
        for (double i = 0.0; i < 11.0; i++) {
            assertTrue(iterator.hasNext());
            assertEquals(i, (Double) iterator.next(), PRECISION);
        }
        assertFalse(iterator.hasNext());
    }

    @Test
    public void testSubList() {
        assertEquals(
            Arrays.asList(0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0),
            range11.subList(0, 11));
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
        int index = 0;
        for (double i = 4; i < 11; i++) {
            assertEquals(i, range4_11.get(index), PRECISION);
            index++;
        }
    }

    @Test
    public void testSizeWithStart() {
        assertEquals(7, range4_11.size());
    }

    @Test
    public void testContainsWithStart() {
        assertFalse(range4_11.contains(3));
        for (double i = 4; i < 11; i++) {
            assertTrue(range4_11.contains(i));
        }
        assertFalse(range4_11.contains(11));
    }

    @Test
    public void testIterateWithStart() {
        int expected = 4;
        for (double actual : range4_11) {
            assertEquals(expected, actual, PRECISION);
            expected++;
        }
        assertEquals(11, expected);
    }

    @Test(expectedExceptions = IndexOutOfBoundsException.class)
    public void testOutOfRangeWithStart1() {
        List<Double> range = range4_11;
        range.get(-1);
    }

    @Test(expectedExceptions = IndexOutOfBoundsException.class)
    public void testOutOfRangeWithStart2() {
        List<Double> range = range4_11;
        range.get(8);
    }

    @Test
    public void testWithStep() {
        assertEquals(4.0, (Object) range4_11_3.get(0));
        assertEquals(7.0, (Object) range4_11_3.get(1));
        assertEquals(10.0, (Object) range4_11_3.get(2));
    }

    @Test
    public void testSizeWithStep() {
        assertEquals(3, (Object) range4_11_3.size());
    }

    @Test
    public void testContainsWithStep() {
        assertFalse(range4_11_3.contains(3.0));
        assertTrue(range4_11_3.contains(4.0));
        assertTrue(range4_11_3.contains(7.0));
        assertTrue(range4_11_3.contains(10.0));
        assertFalse(range4_11_3.contains(11.0));
    }

    @Test
    public void testIterateWithStep() {
        int expected = 4;
        for (double actual : range4_11_3) {
            assertEquals(expected, actual, PRECISION);
            expected += 3;
        }
        assertEquals(13, expected);
    }

    @Test
    public void testIteratorWithStep() {
        Iterator iterator = range4_11_3.iterator();
        for (double i = 4; i < 11; i += 3) {
            assertTrue(iterator.hasNext());
            assertEquals(i, (Double) iterator.next(), PRECISION);
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
        assertEquals(
            "[0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0]",
            range11.toString());
        assertEquals(
            "[0.0, 1.0, 2.0, 3.0, ..., 11.0, 12.0, 13.0, 14.0]",
            new DoubleRange(15).toString());
        assertEquals(
            "[0.0, 6.0, 12.0, 18.0, 24.0, 30.0, 36.0, 42.0, 48.0]",
            new DoubleRange(0, 50, 6).toString());
    }
}
