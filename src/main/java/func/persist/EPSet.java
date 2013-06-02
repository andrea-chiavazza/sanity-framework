package func.persist;

import org.pcollections.Empty;
import org.pcollections.PSet;
import org.pcollections.PVector;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;

class EPSet<E> extends AbstractSet<E> implements PSet<E>,EPCollection<E> {
    private final PSet<E> original;
    private final PSet<E> instance;
    private final PVector<CollChange> changes;

    public EPSet(PSet<E> instance) {
        this.original = instance;
        this.instance = instance;
        this.changes = Empty.vector();
    }

    private EPSet(PSet<E> instance, PVector<CollChange> changes, PSet<E> original) {
        this.original = original;
        this.instance = instance;
        this.changes = changes;
    }

    private EPSet<E> withChanges(PSet<E> instance, PVector<CollChange> changes) {
        return new EPSet<>(instance, changes, original);
    }

    public EPSet<E> plus(E e) {
        return withChanges(instance.plus(e), changes.plus(new Plus<>(e)));
    }

    public EPSet<E> plusAll(Collection<? extends E> list) {
        return withChanges(instance.plusAll(list), changes.plus(new PlusAll<>(list)));
    }

    public EPSet<E> minus(Object e) {
        return withChanges(instance.minus(e), changes.plus(new Minus<>(e)));
    }

    public EPSet<E> minusAll(Collection<?> list) {
        return withChanges(instance.minusAll(list), changes.plus(new MinusAll<>(list)));
    }

    @Override
    public Iterator<E> iterator() {
        return instance.iterator();
    }

    public int size() {
        return instance.size();
    }

    public EPSet<E> minus(int i) {
        return withChanges(instance.minus(i), changes.plus(new MinusI(i)));
    }

    public PVector<CollChange> getChanges() {
        return changes;
    }

    public PSet<E> getOriginal() {
        return original;
    }

    @Override
    public EPSet<E> withResetChanges() {
        return new EPSet<>(instance);
    }
}

