package func.basic;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MemoizedF3<T,U,V,R> implements F3<T,U,V,R> {
    private final Map<V3,R> cache = new ConcurrentHashMap<>();
    private final F3<T,U,V,R> f3;

    public MemoizedF3(F3<T,U,V,R> f3) {
        this.f3 = f3;
    }

    @Override
    public R execute(T t, U u, V v) {
        V3<T,U,V> tuv = new V3<>(t, u, v);
        if (cache.containsKey(tuv)) {
            return cache.get(tuv);
        } else {
            R r = f3.execute(t, u, v);
            cache.put(tuv, r);
            return r;
        }
    }

}
