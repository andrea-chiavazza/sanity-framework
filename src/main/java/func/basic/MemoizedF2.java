package func.basic;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MemoizedF2<T,U,R> implements F2<T,U,R> {
    private final Map<V2,R> cache = new ConcurrentHashMap<>();
    private final F2<T,U,R> f2;

    public MemoizedF2(F2<T,U,R> f2) {
        this.f2 = f2;
    }

    @Override
    public R execute(T t, U u) {
        V2<T,U> tu = new V2<>(t, u);
        if (cache.containsKey(tu)) {
            return cache.get(tu);
        } else {
            R r = f2.execute(t, u);
            cache.put(tu, r);
            return r;
        }
    }

}
