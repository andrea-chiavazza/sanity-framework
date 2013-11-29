package func.basic;


import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class TestMemoizedF1 {
    private final F1<Integer,Integer> mul9;
    private final F1<Integer,Integer> memoizedMul9;

    public TestMemoizedF1() {
        mul9 = new F1<Integer,Integer>() {
            public Integer execute(Integer i) {
                return i == null ? 0 : i * 9;
            }
        };
        memoizedMul9 = new MemoizedF1<>(mul9);
    }

    @Test
    public void test() {
        assertEquals(memoizedMul9.execute(-1), mul9.execute(-1));
        assertEquals(memoizedMul9.execute(0), mul9.execute(0));
        assertEquals(memoizedMul9.execute(4), mul9.execute(4));
        assertEquals(memoizedMul9.execute(9), mul9.execute(9));
        assertEquals(memoizedMul9.execute(null), mul9.execute(null));
    }
}
