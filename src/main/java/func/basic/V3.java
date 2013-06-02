package func.basic;

public class V3<T,U,V> implements F3<T,U,V,V3> {
    public final T t;
    public final U u;
    public final V v;

    public V3(T t, U u, V v) {
        this.t = t;
        this.u = u;
        this.v = v;
    }

    public static <T,U,V> V3<T,U,V> of(T t, U u, V v) {
        return new V3<>(t, u, v);
    }

    @Override
    public int hashCode() {
        int hashCode = t == null ? 0 : t.hashCode();
        hashCode = (31 * hashCode) ^ (u == null ? 0 : u.hashCode());
        hashCode = (31 * hashCode) ^ (v == null ? 0 : v.hashCode());
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof V3)) {
            return false;
        }
        V3 tuv = (V3) obj;

        return
            (tuv.t == null ? t == null : tuv.t.equals(t)) &&
            (tuv.u == null ? u == null : tuv.u.equals(u)) &&
            (tuv.v == null ? v == null : tuv.v.equals(v));
    }

    @Override
    public String toString() {
        return "V3[" + t + ", " + u + ", " + v + ']';
    }

    @Override
    public V3 execute(T t, U u, V v) {
        return new V3<>(t, u, v);
    }
}
