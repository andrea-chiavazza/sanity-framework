package func.basic;

import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.testng.Assert.*;


/**
 * This class can be useful with higher order functions like map and filter.
 */
public class TestBigDecimalRange {
    private static final List<BigDecimal> range11 =
        new BigDecimalRange(BigDecimal.valueOf(11));
    private static final List<BigDecimal> range4_11 =
    new BigDecimalRange(BigDecimal.valueOf(4),
                        BigDecimal.valueOf(11));
    private static final List<BigDecimal> range4_11_3 =
        new BigDecimalRange(BigDecimal.valueOf(4),
                            BigDecimal.valueOf(11),
                            BigDecimal.valueOf(3));
    private static final List<BigDecimal> range4_11_3steps =
        new BigDecimalRange(BigDecimal.valueOf(4),
                            BigDecimal.valueOf(11),
                            3);

    @Test
    public void test() {
        for (int i = 0; i < 11; i++) {
            assertEquals(range11.get(i), BigDecimal.valueOf(i));
        }
    }

    @Test
    public void testSize() {
        assertEquals(range11.size(), 11);
    }

    @Test
    public void testContains() {
        assertFalse(range11.contains(BigDecimal.valueOf(-1.0)));
        for (double i = 0.0; i < 11.0; i += 1.0) {
            assertTrue(range11.contains(BigDecimal.valueOf(i)));
        }
        assertFalse(range11.contains(BigDecimal.valueOf(11.0)));
    }

    @Test
    public void testIterate() {
        int expected = 0;
        for (BigDecimal actual : range11) {
            assertEquals(actual, BigDecimal.valueOf(expected));
            expected++;
        }
        assertEquals(expected, 11);
    }

    @Test
    public void testIterator() {
        Iterator<BigDecimal> iterator = range11.iterator();
        for (int i = 0; i < 11; i++) {
            assertTrue(iterator.hasNext());
            assertEquals(iterator.next(), BigDecimal.valueOf(i));
        }
        assertFalse(iterator.hasNext());
    }

    @Test
    public void testSubList() {
        assertEquals(
            range11.subList(0, 11),
            Arrays.asList(BigDecimal.valueOf(0), BigDecimal.valueOf(1), BigDecimal.valueOf(2),
                          BigDecimal.valueOf(3), BigDecimal.valueOf(4), BigDecimal.valueOf(5),
                          BigDecimal.valueOf(6), BigDecimal.valueOf(7), BigDecimal.valueOf(8),
                          BigDecimal.valueOf(9), BigDecimal.valueOf(10)));
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
        for (int i = 4; i < 11; i++) {
            assertEquals(range4_11.get(index), BigDecimal.valueOf(i));
            index++;
        }
    }

    @Test
    public void testSizeWithStart() {
        assertEquals(range4_11.size(), 7);
    }

    @Test
    public void testContainsWithStart() {
        assertFalse(range4_11.contains(BigDecimal.valueOf(3)));
        for (double i = 4; i < 11; i++) {
            assertTrue(range4_11.contains(BigDecimal.valueOf(i)));
        }
        assertFalse(range4_11.contains(BigDecimal.valueOf(11)));
    }

    @Test
    public void testIterateWithStart() {
        BigDecimal expected = BigDecimal.valueOf(4);
        for (BigDecimal actual : range4_11) {
            assertEquals(actual, expected);
            expected = expected.add(BigDecimal.ONE);
        }
        assertEquals(expected, BigDecimal.valueOf(11));
    }

    @Test(expectedExceptions = IndexOutOfBoundsException.class)
    public void testOutOfRangeWithStart1() {
        List<BigDecimal> range = range4_11;
        range.get(-1);
    }

    @Test(expectedExceptions = IndexOutOfBoundsException.class)
    public void testOutOfRangeWithStart2() {
        List<BigDecimal> range = range4_11;
        range.get(8);
    }

    @Test
    public void testWithStep() {
        assertEquals(range4_11_3.get(0), BigDecimal.valueOf(4));
        assertEquals(range4_11_3.get(1), BigDecimal.valueOf(7));
        assertEquals(range4_11_3.get(2), BigDecimal.valueOf(10));
    }

    @Test
    public void testSizeWithStep() {
        assertEquals((Object) range4_11_3.size(), 3);
    }

    @Test
    public void testContainsWithStep() {
        assertFalse(range4_11_3.contains(BigDecimal.valueOf(3)));
        assertTrue(range4_11_3.contains(BigDecimal.valueOf(4)));
        assertTrue(range4_11_3.contains(BigDecimal.valueOf(7)));
        assertTrue(range4_11_3.contains(BigDecimal.valueOf(10)));
        assertFalse(range4_11_3.contains(BigDecimal.valueOf(11)));
    }

    @Test
    public void testIterateWithStep() {
        int expected = 4;
        for (BigDecimal actual : range4_11_3) {
            assertEquals(actual, BigDecimal.valueOf(expected));
            expected += 3;
        }
        assertEquals(expected, 13);
    }

    @Test
    public void testIteratorWithStep() {
        Iterator iterator = range4_11_3.iterator();
        for (int i = 4; i < 11; i += 3) {
            assertTrue(iterator.hasNext());
            assertEquals(iterator.next(), BigDecimal.valueOf(i));
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
        assertEquals(new BigDecimalRange(BigDecimal.valueOf(15)).toString(),
                     "[0, 1, 2, 3, ..., 11, 12, 13, 14]");
        assertEquals(new BigDecimalRange(BigDecimal.valueOf(0),
                                         BigDecimal.valueOf(50),
                                         BigDecimal.valueOf(6)).toString(),
                     "[0, 6, 12, 18, 24, 30, 36, 42, 48]");
        assertEquals(new BigDecimalRange(BigDecimal.valueOf(0),
                                         BigDecimal.valueOf(10),
                                         BigDecimal.valueOf(1.5)).toString(),
                     "[0, 1.5, 3.0, 4.5, 6.0, 7.5, 9.0]");
        assertEquals(range4_11_3steps.toString(),
                     "[4, 7.5, 11]");
        assertEquals(new BigDecimalRange(BigDecimal.valueOf(0),
                                         BigDecimal.valueOf(20),
                                         6).toString(),
                     "[0, 4.0, 8.0, 12.0, 16.0, 20]");
        assertEquals(new BigDecimalRange(BigDecimal.valueOf(0),
                                         BigDecimal.valueOf(20),
                                         0).toString(),
                     "[]");
        assertEquals(new BigDecimalRange(BigDecimal.valueOf(0),
                                         BigDecimal.valueOf(20),
                                         1).toString(),
                     "[0]");
        assertEquals(new BigDecimalRange(BigDecimal.valueOf(0),
                                         BigDecimal.valueOf(20),
                                         3).toString(),
                     "[0, 10.0, 20]");
        assertEquals(new BigDecimalRange(BigDecimal.valueOf(0),
                                         BigDecimal.valueOf(20),
                                         18).toString(),
                     "[0, 1.1764705882352942, 2.3529411764705884, 3.5294117647058826, ..., 16.4705882352941188, 17.6470588235294130, 18.8235294117647072, 20]");
        assertEquals(new BigDecimalRange(BigDecimal.valueOf(0),
                                         BigDecimal.valueOf(20),
                                         7).toString(),
                     "[0, 3.3333333333333335, 6.6666666666666670, 10.0000000000000005, 13.3333333333333340, 16.6666666666666675, 20]");
    }

}
