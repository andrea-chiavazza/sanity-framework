package func.basic;

import java.util.*;

public class IntegerRange implements List<Integer> {
    private final int start;
    private final int end;
    private final int step;
    private final int size;

    public IntegerRange(int end) {
        this(0, end, 1);
    }

    public IntegerRange(int start, int end) {
        this(start, end, 1);
    }

    public IntegerRange(int start, int end, int step) {
        this.start = start;
        this.end = end;
        this.step = step;
        if (step == 1) {
            this.size = end - start;
        } else {
            this.size = (int) Math.ceil((end - start) / (double) step);
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(Object o) {
        if (! (o instanceof Integer)) {
            return false;
        }
        int i = (Integer) o;
        return i >= start && i < end && (i - start) % step == 0;
    }

    @Override
    public Iterator<Integer> iterator() {
        return new Iterator<Integer>() {
            private Integer lastReturned = null;

            @Override
            public boolean hasNext() {
                return lastReturned == null || lastReturned + step < end;
            }

            @Override
            public Integer next() {
                if (hasNext()) {
                    lastReturned = lastReturned == null ? start : lastReturned + step;
                    return lastReturned;
                } else {
                    throw new NoSuchElementException();
                }
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public Object[] toArray() {
        Integer[] ar = new Integer[size];
        int i = 0;
        for (int o : this) {
            ar[i] = o;
        }
        return ar;
    }

    @Override
    public <T> T[] toArray(T[] a) {
        int size = size();
        T[] ar = a.length >= size ? a :
            (T[])java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size);
        int i = 0;
        for (Integer o : this) {
            ar[i] = (T) o;
        }
        return ar;
    }

    @Override
    public boolean add(Integer integer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c) {
            if (!contains(o)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends Integer> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int index, Collection<? extends Integer> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Integer get(int index) {
        if ((index < 0 || index >= size())) {
            throw new IndexOutOfBoundsException();
        }
        return start + (step * index);
    }

    @Override
    public Integer set(int index, Integer element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(int index, Integer element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Integer remove(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int indexOf(Object o) {
        if (!(o instanceof Integer)) {
            return -1;
        }
        int integer = (Integer) o;
        for (int i = 0; i < size; i++) {
            if (get(i) == integer) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        if (!(o instanceof Integer)) {
            return -1;
        }
        int integer = (Integer) o;
        for (int i = size - 1; i >= 0; i--) {
            if (get(i) == integer) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public ListIterator<Integer> listIterator() {
        return new RangeListIterator(0);
    }

    @Override
    public ListIterator<Integer> listIterator(int index) {
        return new RangeListIterator(index);
    }

    @Override
    public List<Integer> subList(int fromIndex, int toIndex) {
        if (fromIndex < 0 || fromIndex > size || toIndex < 0 || toIndex > size) {
            throw new IndexOutOfBoundsException();
        }
        return new IntegerRange(get(fromIndex), get(toIndex - 1) + step, step);
    }

    private class RangeListIterator implements ListIterator<Integer> {
        private int previousIndex;
        private int nextIndex;

        private RangeListIterator(int index) {
            this.previousIndex = index - 1;
            this.nextIndex = index;
        }

        @Override
        public boolean hasNext() {
            return nextIndex < size;
        }

        @Override
        public Integer next() {
            if (hasNext()) {
                previousIndex++;
                return get(nextIndex++);
            } else {
                throw new NoSuchElementException();
            }
        }

        @Override
        public boolean hasPrevious() {
            return previousIndex >= 0;
        }

        @Override
        public Integer previous() {
            if (hasPrevious()) {
                nextIndex--;
                return get(previousIndex--);
            } else {
                throw new NoSuchElementException();
            }
        }

        @Override
        public int nextIndex() {
            return nextIndex;
        }

        @Override
        public int previousIndex() {
            return previousIndex;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void set(Integer integer) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(Integer integer) {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public int hashCode() {
        return 31 * (31 * start + end) + step;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof IntegerRange)) {
            return false;
        }
        IntegerRange other = (IntegerRange) obj;
        return
            other.start == start &&
                other.end == end &&
                other.step == step;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        if (size < 12) {
            Iterator<Integer> iterator = this.iterator();
            if (iterator.hasNext()) {
                sb.append(iterator.next());
            }
            while (iterator.hasNext()) {
                sb.append(", ");
                int i = iterator.next();
                sb.append(i);
            }
        } else {
            sb.append(get(0));
            for (int i = 1; i < 4; i++) {
                sb.append(", ").append(get(i));
            }
            sb.append(", ...");
            for (int i = size - 4; i < size; i++) {
                sb.append(", ").append(get(i));
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
