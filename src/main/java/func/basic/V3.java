package func.basic;

public class V3<T,U,V> extends Ob implements F3<T,U,V,V3> {
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
    public String toString() {
        return "V3[" +
            Ob.formatObject(t) + ", " +
            Ob.formatObject(u) + ", " +
            Ob.formatObject(v) + ']';
    }

    @Override
    public V3 execute(T t, U u, V v) {
        return new V3<>(t, u, v);
    }
}
