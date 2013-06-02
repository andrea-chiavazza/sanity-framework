package func.basic;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.*;

import static func.basic.Filter.filter;
import static func.basic.Filter.pfilter;
import static org.testng.Assert.assertEquals;

public class TestFilter {
    private final F1<Integer,Boolean> predOdd;

    public TestFilter() {
        predOdd = new F1<Integer,Boolean>() {
            public Boolean execute(Integer i) {
                return (i % 2) == 1;
            }
        };
    }

    @Test
    public void testFilter1() {
        Assert.assertEquals(
            Collections.<Integer>emptyList(),
            Filter.filter(
                predOdd,
                Arrays.asList(22)));
        assertEquals(
            Arrays.asList(5),
            Filter.filter(
                predOdd,
                Arrays.asList(5)));
    }

    @Test
    public void testPFilter1() {
        assertEquals(
            Collections.<Integer>emptyList(),
            Filter.pfilter(
                predOdd,
                Arrays.asList(22)));
        assertEquals(
            Arrays.asList(5),
            Filter.pfilter(
                predOdd,
                Arrays.asList(5)));
    }

    @Test
    public void testFilter2() {
        assertEquals(
            Arrays.asList(13),
            Filter.filter(
                predOdd,
                Arrays.asList(13, 22)));
    }

    @Test
    public void testPFilter2() {
        assertEquals(
            Arrays.asList(13),
            Filter.pfilter(
                predOdd,
                Arrays.asList(13, 22)));
    }

    @Test
    public void testFilter4() {
        assertEquals(
            Arrays.asList(13, 33),
            filter(
                predOdd,
                Arrays.asList(6, 13, 22, 33)));
    }

    @Test
    public void testPFilter4() {
        assertEquals(
            Arrays.asList(13, 33),
            pfilter(
                predOdd,
                Arrays.asList(6, 13, 22, 33)));
    }

    @Test
    public void testFilter7() {
        assertEquals(
            Arrays.asList(9, 27, 45, 63),
            filter(
                predOdd,
                Arrays.asList(9, 18, 27, 36, 45, 54, 63)));
    }

    @Test
    public void testPFilter7() {
        assertEquals(
            Arrays.asList(9, 27, 45, 63),
            pfilter(
                predOdd,
                Arrays.asList(9, 18, 27, 36, 45, 54, 63)));
    }

    @Test
    public void testFilterLarge() {
        final int size = 400000;

        List<Integer> coll = new ArrayList<>(size);
        List<Integer> expected = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            coll.add(i);
            final Integer t = coll.get(i);
            if (predOdd.execute(t)) {
                expected.add(t);
            }
        }

        assertEquals(
            expected,
            filter(predOdd, coll));
    }

    @Test
    public void testPFilterLarge() {
        final int size = 400000;

        List<Integer> coll = new ArrayList<>(size);
        List<Integer> expected = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            coll.add(i);
            final Integer t = coll.get(i);
            if (predOdd.execute(t)) {
                expected.add(t);
            }
        }

        assertEquals(
            expected,
            pfilter(predOdd, coll));
    }

    public void testPerformance() {
        int size = 10000000;
        long t1, t2, t3;

        List<Integer> iColl = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            iColl.add(i);
        }

        t1 = new Date().getTime();
        filter(predOdd, iColl);
        t2 = new Date().getTime();
        pfilter(predOdd, iColl);
        t3 = new Date().getTime();

        System.out.println("filter " + (t2 - t1));
        System.out.println("pfilter " + (t3 - t2));
    }
}
