package func.basic;

/**
 * User: andrea
 * Date: 14/07/12
 * Time: 19:52
 */
public class Atom<T> {
    private T obj;

    public Atom() {
        this.obj = null;
    }

    public Atom(T value) {
        this.obj = value;
    }

    public synchronized void setValue(T value) {
        this.obj = value;
    }

    public synchronized T getValue() {
        return obj;
    }

    public synchronized void swap(F1<T,T> f) {
        obj = f.execute(obj);
    }

    public synchronized boolean compareAndSet(T oldValue, T newValue) {
        if (obj == null ? oldValue == null : obj.equals(oldValue)) {
            obj = newValue;
            return true;
        } else {
            return false;
        }
    }
}
