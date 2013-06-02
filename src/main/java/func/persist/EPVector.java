package func.persist;

import org.pcollections.Empty;
import org.pcollections.PVector;

import java.util.AbstractList;
import java.util.Collection;

class EPVector<E> extends AbstractList<E> implements PVector<E>,EPCollection<E> {
    private final PVector<E> original;
    private final PVector<E> instance;
    private final PVector<CollChange> changes;

    public EPVector(PVector<E> instance) {
        this.original = instance;
        this.instance = instance;
        this.changes = Empty.vector();
    }

    private EPVector(PVector<E> instance, PVector<CollChange> changes, PVector<E> original) {
        this.instance = instance;
        this.original = original;
        this.changes = changes;
    }

    private EPVector<E> withChanges(PVector<E> instance, PVector<CollChange> changes) {
        return new EPVector<>(instance, changes, original);
    }

    public PVector<E> plus(E e) {
        return withChanges(instance.plus(e), changes.plus(new Plus<>(e)));
    }

    public PVector<E> plusAll(Collection<? extends E> list) {
        return withChanges(instance.plusAll(list), changes.plus(new PlusAll<>(list)));
    }

    public PVector<E> with(int i, E e) {
        return withChanges(instance.with(i, e), changes.plus(new With<>(i, e)));
    }

    public PVector<E> plus(int i, E e) {
        return withChanges(instance.plus(i, e), changes.plus(new PlusI<>(i, e)));
    }

    public PVector<E> plusAll(int i, Collection<? extends E> list) {
        return withChanges(instance.plusAll(i, list), changes.plus(new PlusAllI<>(i, list)));
    }

    public PVector<E> minus(Object e) {
        return withChanges(instance.minus(e), changes.plus(new Minus<>(e)));
    }

    public PVector<E> minusAll(Collection<?> list) {
        return withChanges(instance.minusAll(list), changes.plus(new MinusAll<>(list)));
    }

    public int size() {
        return instance.size();
    }

    public PVector<E> minus(int i) {
        return withChanges(instance.minus(i), changes.plus(new MinusI(i)));
    }

    public PVector<E> subList(int start, int end) {
        return instance.subList(start, end);
    }

    public E get(int index) {
        return instance.get(index);
    }

    public PVector<CollChange> getChanges() {
        return changes;
    }

    public PVector<E> getOriginal() {
        return original;
    }

    @Override
    public EPVector<E> withResetChanges() {
        return new EPVector<>(instance);
    }
}

