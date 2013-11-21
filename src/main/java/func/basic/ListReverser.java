package func.basic;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ListReverser<T> implements Iterable<T> {
    private final ListIterator<T> listIterator;

    public ListReverser(List<T> wrappedList) {
        this.listIterator = wrappedList.listIterator(wrappedList.size());
    }

    public static <T> ListReverser<T> reverse(List<T> wrappedList) {
        return new ListReverser<>(wrappedList);
    }

    public Iterator<T> iterator() {
        return new Iterator<T>() {
            public boolean hasNext() {
                return listIterator.hasPrevious();
            }

            public T next() {
                return listIterator.previous();
            }

            public void remove() {
                listIterator.remove();
            }
        };
    }

}
