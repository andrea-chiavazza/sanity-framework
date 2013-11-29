package func.basic;

public class V2<T,U> extends Ob implements F2<T,U,V2> {
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
    public String toString() {
        return "V2[" +
            Ob.formatObject(t) + ", " +
            Ob.formatObject(u) + ']';
    }

    @Override
    public V2 execute(T t, U u) {
        return new V2<>(t, u);
    }
}
