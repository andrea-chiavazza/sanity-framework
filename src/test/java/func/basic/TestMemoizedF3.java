package func.basic;


import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class TestMemoizedF3 {
    private final F3<Integer,Integer,Integer,Integer> prodPlus;
    private final F3<Integer,Integer,Integer,Integer> memoizedProdPlus1;

    public TestMemoizedF3() {
        prodPlus = new F3<Integer,Integer,Integer,Integer>() {
            public Integer execute(Integer i1, Integer i2, Integer i3) {
                return (i1 == null ? 0 : i1) * (i2 == null ? 0 : i2) + (i3 == null ? 0 : i3);
            }
        };
        memoizedProdPlus1 = new MemoizedF3<>(prodPlus);
    }

    @Test
    public void test() {
        assertEquals(memoizedProdPlus1.execute(3, 4, -5), prodPlus.execute(3, 4, -5));
        assertEquals(memoizedProdPlus1.execute(-3, 0, 4), prodPlus.execute(-3, 0, 4));
        assertEquals(memoizedProdPlus1.execute(5, 6, 1), prodPlus.execute(5, 6, 1));
        assertEquals(memoizedProdPlus1.execute(null, 6, 1), prodPlus.execute(null, 6, 1));
        assertEquals(memoizedProdPlus1.execute(6, null, 0), prodPlus.execute(6, null, 0));
    }
}
