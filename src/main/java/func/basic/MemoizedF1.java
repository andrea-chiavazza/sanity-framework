package func.basic;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MemoizedF1<T,R> implements F1<T,R> {

    private final Map<T,R> cache = Collections.synchronizedMap(new HashMap<T,R>());
    private final F1<T,R> f1;

    public MemoizedF1(F1<T,R> f1) {
        this.f1 = f1;
    }

    @Override
    public R execute(T t) {
        if (cache.containsKey(t)) {
            return cache.get(t);
        } else {
            R r = f1.execute(t);
            cache.put(t, r);
            return r;
        }
    }
}
