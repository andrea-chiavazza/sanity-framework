package func.basic;

import java.math.BigDecimal;
import java.util.*;

public class BigDecimalRange implements List<BigDecimal> {
    private final BigDecimal start;
    private final BigDecimal end;
    private final BigDecimal step;
    private final int size;

    public BigDecimalRange(BigDecimal end) {
        this(BigDecimal.ZERO, end, BigDecimal.ONE);
    }

    public BigDecimalRange(BigDecimal start,
                           BigDecimal end) {
        this(start, end, BigDecimal.ONE);
    }

    public BigDecimalRange(BigDecimal start,
                           BigDecimal end,
                           BigDecimal step) {
        this.start = start;
        this.end = end;
        this.step = step;
        this.size = (int) Math.ceil(end.subtract(start).divide(step,
                                                               BigDecimal.ROUND_HALF_EVEN).doubleValue());
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
        if (! (o instanceof BigDecimal)) {
            return false;
        }
        BigDecimal i = (BigDecimal) o;
        return i.compareTo(start) >= 0 && i.compareTo(end) < 0 && (i.subtract(start).remainder(step).signum() == 0);
    }

    @Override
    public Iterator<BigDecimal> iterator() {
        return new Iterator<BigDecimal>() {
            private BigDecimal lastReturned = null;

            @Override
            public boolean hasNext() {
                return lastReturned == null || lastReturned.add(step).compareTo(end) < 0;
            }

            @Override
            public BigDecimal next() {
                if (hasNext()) {
                    lastReturned = lastReturned == null ? start : lastReturned.add(step);
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
        BigDecimal[] ar = new BigDecimal[size];
        int i = 0;
        for (BigDecimal o : this) {
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
        for (BigDecimal o : this) {
            ar[i] = (T) o;
        }
        return ar;
    }

    @Override
    public boolean add(BigDecimal val) {
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
    public boolean addAll(Collection<? extends BigDecimal> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int index, Collection<? extends BigDecimal> c) {
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
    public BigDecimal get(int index) {
        if ((index < 0 || index >= size())) {
            throw new IndexOutOfBoundsException();
        }
        return start.add(step.multiply(BigDecimal.valueOf(index)));
    }

    @Override
    public BigDecimal set(int index, BigDecimal element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(int index, BigDecimal element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public BigDecimal remove(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int indexOf(Object o) {
        if (!(o instanceof BigDecimal)) {
            return -1;
        }
        BigDecimal integer = (BigDecimal) o;
        for (int i = 0; i < size; i++) {
            if (get(i) == integer) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        if (!(o instanceof BigDecimal)) {
            return -1;
        }
        BigDecimal integer = (BigDecimal) o;
        for (int i = size - 1; i >= 0; i--) {
            if (get(i) == integer) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public ListIterator<BigDecimal> listIterator() {
        return new RangeListIterator(0);
    }

    @Override
    public ListIterator<BigDecimal> listIterator(int index) {
        return new RangeListIterator(index);
    }

    @Override
    public List<BigDecimal> subList(int fromIndex, int toIndex) {
        if (fromIndex < 0 || fromIndex > size || toIndex < 0 || toIndex > size) {
            throw new IndexOutOfBoundsException();
        }
        return new BigDecimalRange(get(fromIndex), get(toIndex - 1).add(step), step);
    }

    private class RangeListIterator implements ListIterator<BigDecimal> {
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
        public BigDecimal next() {
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
        public BigDecimal previous() {
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
        public void set(BigDecimal integer) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(BigDecimal integer) {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public int hashCode() {
        return 31 * (31 * start.hashCode() + end.hashCode()) + step.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BigDecimalRange)) {
            return false;
        }
        BigDecimalRange other = (BigDecimalRange) obj;
        return
            other.start == start &&
            other.end == end &&
            other.step == step;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        if (size < 12) {
            Iterator<BigDecimal> iterator = this.iterator();
            if (iterator.hasNext()) {
                sb.append(iterator.next());
            }
            while (iterator.hasNext()) {
                sb.append(", ");
                BigDecimal i = iterator.next();
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