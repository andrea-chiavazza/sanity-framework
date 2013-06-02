package func.persist;

import java.util.Collection;

class IChange extends CollChange {
    public final int i;

    public IChange(int i) {
        this.i = i;
    }
}

class With<E> extends IChange {
    public final E e;

    public With(int i, E e) {
        super(i);
        this.e = e;
    }
}

class PlusI<E> extends IChange {
    public final E e;

    public PlusI(int i, E e) {
        super(i);
        this.e = e;
    }
}

class PlusAllI<E> extends IChange {
    public final Collection<E> list;

    public PlusAllI(int i, Collection<E> list) {
        super(i);
        this.list = list;
    }
}

class MinusI extends IChange {
    public MinusI(int i) {
        super(i);
    }
}
