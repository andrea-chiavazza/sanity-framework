package func.persist;

import java.util.Collection;

class CollChange {
}

class SChange<E> extends CollChange {
    public final E e;

    public SChange(E e) {
        this.e = e;
    }
}

class Plus<E> extends SChange<E> {
    public Plus(E e) {
        super(e);
    }
}

class Minus<E> extends SChange<E> {
    public Minus(E e) {
        super(e);
    }
}

class MChange<E> extends CollChange {
    public final Collection<E> list;

    public MChange(Collection<E> list) {
        this.list = list;
    }
}

class PlusAll<E> extends MChange<E> {
    public PlusAll(Collection<E> list) {
        super(list);
    }
}

class MinusAll<E> extends MChange<E> {
    public MinusAll(Collection<E> list) {
        super(list);
    }
}

