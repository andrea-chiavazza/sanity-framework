package func.basic;


import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;

import static func.basic.ListComprehension.listComprehension;
import static org.testng.Assert.assertEquals;

public class TestListComprehension {
    @Test
    public void testArity2() {
        Assert.assertEquals(
            Arrays.asList(
                V2.of('a', 1), V2.of('a', 2), V2.of('a', 3), V2.of('a', 4),
                V2.of('b', 1), V2.of('b', 2), V2.of('b', 3), V2.of('b', 4),
                V2.of('c', 1), V2.of('c', 2), V2.of('c', 3), V2.of('c', 4)),
            ListComprehension.listComprehension(
                Arrays.asList('a', 'b', 'c'),
                Arrays.asList(1, 2, 3, 4),
                new F2<Character,Integer,V2<Character,Integer>>() {
                    @Override
                    public V2<Character,Integer> execute(Character c,
                                                         Integer i) {
                        return new V2<>(c, i);
                    }
                }));
    }

    @Test
    public void testArity3() {
        assertEquals(
            Arrays.asList(
                V3.of('a', 2, 1.1), V3.of('a', 2, 4.4), V3.of('a', 3, 1.1), V3.of('a', 3, 4.4),
                V3.of('b', 2, 1.1), V3.of('b', 2, 4.4), V3.of('b', 3, 1.1), V3.of('b', 3, 4.4)),
            listComprehension(
                Arrays.asList('a', 'b'),
                Arrays.asList(2, 3),
                Arrays.asList(1.1, 4.4),
                new F3<Character,Integer,Double,V3<Character,Integer,Double>>() {
                    @Override
                    public V3<Character,Integer,Double> execute(Character c, Integer i, Double d) {
                        return new V3<>(c, i, d);
                    }
                }));
    }
}
