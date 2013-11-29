package func.basic;


import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static func.basic.DoListComprehension.doSeq;
import static org.testng.Assert.assertEquals;

public class TestDoListComprehension {
    @Test
    public void testArity1() {
        final List<Character> result = new ArrayList<>();
        doSeq(
            Arrays.asList('a', 'b', 'c'),
            new F1<Character,Character>() {
                @Override
                public Character execute(Character c) {
                    result.add(Character.toUpperCase(c));
                    return null;
                }
            });

        assertEquals(result, Arrays.asList('A', 'B', 'C'));
    }

    @Test
    public void testArity2() {
        final List<V2<Character,Integer>> result = new ArrayList<>();
        doSeq(
            Arrays.asList('a', 'b', 'c'),
            Arrays.asList(1, 2, 3, 4),
            new F2<Character,Integer,V2<Character,Integer>>() {
                @Override
                public V2<Character,Integer> execute(Character c, Integer i) {
                    result.add(V2.of(c, i));
                    return null;
                }
            });

        assertEquals(
            result,
            Arrays.asList(
                V2.of('a', 1), V2.of('a', 2), V2.of('a', 3), V2.of('a', 4),
                V2.of('b', 1), V2.of('b', 2), V2.of('b', 3), V2.of('b', 4),
                V2.of('c', 1), V2.of('c', 2), V2.of('c', 3), V2.of('c', 4)));
    }

    @Test
    public void testArity3() {
        final List<V3<Character,Integer,Double>> result = new ArrayList<>();
        doSeq(
            Arrays.asList('a', 'b'),
            Arrays.asList(2, 3),
            Arrays.asList(1.1, 4.4),
            new F3<Character,Integer,Double,V3<Character,Integer,Double>>() {
                @Override
                public V3<Character,Integer,Double> execute(Character c, Integer i, Double d) {
                    result.add(V3.of(c, i, d));
                    return null;
                }
            });

        assertEquals(
            result,
            Arrays.asList(
                V3.of('a', 2, 1.1), V3.of('a', 2, 4.4), V3.of('a', 3, 1.1), V3.of('a', 3, 4.4),
                V3.of('b', 2, 1.1), V3.of('b', 2, 4.4), V3.of('b', 3, 1.1), V3.of('b', 3, 4.4)));
    }
}
