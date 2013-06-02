package func.basic;

public class V2<T,U> implements F2<T,U,V2> {
    public final T t;
    public final U u;

    public V2(T t, U u) {
        this.t = t;
        this.u = u;
    }

    public static <T,U> V2<T,U> of(T t, U u) {
        return new V2<>(t, u);
    }

    @Override
    public int hashCode() {
        int hashCode = t == null ? 0 : t.hashCode();
        hashCode = (31 * hashCode) ^ (u == null ? 0 : u.hashCode());
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof V2)) {
            return false;
        }
        V2 tu = (V2) obj;

        return
            (tu.t == null ? t == null : tu.t.equals(t)) &&
            (tu.u == null ? u == null : tu.u.equals(u));
    }

    @Override
    public String toString() {
        return "V2[" + t + ", " + u + ']';
    }

    @Override
    public V2 execute(T t, U u) {
        return new V2<>(t, u);
    }
}
