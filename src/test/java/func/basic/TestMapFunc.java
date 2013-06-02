package func.basic;


import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static func.basic.MapFunc.map;
import static func.basic.MapFunc.pmap;
import static org.testng.Assert.assertEquals;

public class TestMapFunc {
    private final F1<Integer,Integer> mul9;
    private final F1<Double,Double> sin;
    private final F1<Double,Double> heavy;
    private final F2<Integer,Integer,Integer> prodPlus1;

    public TestMapFunc() {
        mul9 = new F1<Integer,Integer>() {
            public Integer execute(Integer i) {
                return i * 9;
            }
        };

        prodPlus1 = new F2<Integer,Integer,Integer>() {
            public Integer execute(Integer i1, Integer i2) {
                return i1 * i2 + 1;
            }
        };

        sin = new F1<Double,Double>() {
            public Double execute(Double i) {
                return Math.sin(i);
            }
        };

        heavy = new F1<Double,Double>() {
            public Double execute(Double i) {
                for (int n = 0; n < 8000000; n++) {
                    Math.sin(n * 0.00183721937345);
                }
                return 1.0;
            }
        };
    }

    @Test
    public void testMap1() throws Exception {
        assertEquals(
            Arrays.asList(36),
            map(
                mul9,
                Arrays.asList(4)));
    }

    @Test
    public void testPmap1() throws Exception {
        assertEquals(
            Arrays.asList(36),
            pmap(
                mul9,
                Arrays.asList(4)));
    }

    @Test
    public void testMap2() throws Exception {
        assertEquals(
            Arrays.asList(36, 99),
            map(
                mul9,
                Arrays.asList(4, 11)));
    }

    @Test
    public void testPmap2() throws Exception {
        assertEquals(
            Arrays.asList(36, 99),
            pmap(
                mul9,
                Arrays.asList(4, 11)));
    }

    @Test
    public void testMap4() throws Exception {
        assertEquals(
            Arrays.asList(9, 18, 27, 36),
            map(
                mul9,
                Arrays.asList(1, 2, 3, 4)));
    }

    @Test
    public void testPmap4() throws Exception {
        assertEquals(
            Arrays.asList(9, 18, 27, 36),
            pmap(
                mul9,
                Arrays.asList(1, 2, 3, 4)));
    }

    @Test
    public void testMap7() throws Exception {
        assertEquals(
            Arrays.asList(9, 18, 27, 36, 45, 54, 63),
            map(
                mul9,
                Arrays.asList(1, 2, 3, 4, 5, 6, 7)));
    }

    @Test
    public void testPmap7() throws Exception {
        assertEquals(
            Arrays.asList(9, 18, 27, 36, 45, 54, 63),
            pmap(
                mul9,
                Arrays.asList(1, 2, 3, 4, 5, 6, 7)));
    }

    @Test
    public void testMapLarge() throws Exception {
        final int size = 400000;

        F1<Integer,Integer> fInt = mul9;
        List<Integer> coll = new ArrayList<Integer>(size);
        List<Integer> expected = new ArrayList<Integer>(size);
        for (int i = 0; i < size; i++) {
            coll.add(i);
            expected.add(fInt.execute(coll.get(i)));
        }
        assertEquals(
            expected,
            map(fInt, coll));

        F1<Double,Double> fDouble = sin;
        List<Double> coll2 = new ArrayList<Double>(size);
        List<Double> expected2 = new ArrayList<Double>(size);
        for (int i = 0; i < size; i++) {
            coll2.add((double) i);
            expected2.add(fDouble.execute(coll2.get(i)));
        }
        assertEquals(
            expected2,
            map(fDouble, coll2));
    }

    @Test
    public void testPmapLarge() throws Exception {
        final int size = 400000;

        F1<Integer,Integer> fInt = mul9;
        List<Integer> coll = new ArrayList<Integer>(size);
        List<Integer> expected = new ArrayList<Integer>(size);
        for (int i = 0; i < size; i++) {
            coll.add(i);
            expected.add(fInt.execute(coll.get(i)));
        }
        assertEquals(
            expected,
            pmap(fInt, coll));

        F1<Double,Double> fDouble = sin;
        List<Double> coll2 = new ArrayList<Double>(size);
        List<Double> expected2 = new ArrayList<Double>(size);
        for (int i = 0; i < size; i++) {
            coll2.add((double) i);
            expected2.add(fDouble.execute(coll2.get(i)));
        }
        assertEquals(
            expected2,
            pmap(fDouble, coll2));
    }

    //////// arity 2

    @Test
    public void testMapArity2() {
        assertEquals(
            Arrays.asList(6, 13, 22, 33),
            map(
                prodPlus1,
                Arrays.asList(1, 2, 3, 4, 55),
                Arrays.asList(5, 6, 7, 8)));
    }

    @Test
    public void testPmapArity2() {
        assertEquals(
            Arrays.asList(6, 13, 22, 33),
            pmap(
                prodPlus1,
                Arrays.asList(1, 2, 3, 4, 55),
                Arrays.asList(5, 6, 7, 8)));
    }

    @Test
    public void testMapLargeArity2() throws Exception {
        final int size = 400000;
        F2<Integer,Integer,Integer> f1 = prodPlus1;

        List<Integer> coll1 = new ArrayList<Integer>(size);
        List<Integer> coll2 = new ArrayList<Integer>(size);
        List<Integer> expected = new ArrayList<Integer>(size);

        for (int i = 0; i < size; i++) {
            coll1.add(i);
            coll2.add(i + 1);
            expected.add(f1.execute(coll1.get(i), coll2.get(i)));
        }

        assertEquals(
            expected,
            map(f1, coll1, coll2));
    }

    @Test
    public void testPmapLargeArity2() throws Exception {
        final int size = 400000;
        F2<Integer,Integer,Integer> f1 = prodPlus1;

        List<Integer> coll1 = new ArrayList<Integer>(size);
        List<Integer> coll2 = new ArrayList<Integer>(size);
        List<Integer> expected = new ArrayList<Integer>(size);

        for (int i = 0; i < size; i++) {
            coll1.add(i);
            coll2.add(i + 1);
            expected.add(f1.execute(coll1.get(i), coll2.get(i)));
        }

        assertEquals(
            expected,
            pmap(f1, coll1, coll2));
    }

//    @Test
    public void testPerformance() {
        int size = 4000000;
        long t1, t2, t3;

        List<Integer> iColl = new ArrayList<Integer>(size);

        for (int i = 0; i < size; i++) {
            iColl.add(i);
        }

        t1 = new Date().getTime();
        map(mul9, iColl);
        t2 = new Date().getTime();
        pmap(mul9, iColl);
        t3 = new Date().getTime();

        System.out.println(size + " mul");
        System.out.println("map " + (t2 - t1));
        System.out.println("pmap " + (t3 - t2));

        List<Double> dColl = new ArrayList<Double>(size);

        for (int i = 0; i < size; i++) {
            dColl.add((double) i);
        }

        t1 = new Date().getTime();
        map(sin, dColl);
        t2 = new Date().getTime();
        pmap(sin, dColl);
        t3 = new Date().getTime();

        System.out.println("\n" + size + " sin");
        System.out.println("map " + (t2 - t1));
        System.out.println("pmap " + (t3 - t2));

        size = 4;
        List<Double> hcoll = new ArrayList<Double>(size);

        for (int i = 0; i < size; i++) {
            hcoll.add((double) i);
        }

        t1 = new Date().getTime();
        map(heavy, hcoll);
        t2 = new Date().getTime();
        pmap(heavy, hcoll);
        t3 = new Date().getTime();

        System.out.println("\n" + size + " heavy");
        System.out.println("map " + (t2 - t1));
        System.out.println("pmap " + (t3 - t2));
    }
}
