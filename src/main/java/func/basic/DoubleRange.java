package func.basic;

import java.util.*;

public class DoubleRange implements List<Double> {
    private final double start;
    private final double end;
    private final double step;
    private final int size;

    public DoubleRange(double end) {
        this(0.0, end, 1.0);
    }

    public DoubleRange(double start, double end) {
        this(start, end, 1.0);
    }

    public DoubleRange(double start, double end, double step) {
        this.start = start;
        this.end = end;
        this.step = step;
        this.size = (int) Math.ceil((end - start) / step);
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
        if (! (o instanceof Double)) {
            return false;
        }
        double i = (Double) o;
        return i >= start && i < end && (i - start) % step == 0;
    }

    @Override
    public Iterator<Double> iterator() {
        return new Iterator<Double>() {
            private double lastReturned = Double.NaN;

            @Override
            public boolean hasNext() {
                return Double.isNaN(lastReturned) || lastReturned + step < end;
            }

            @Override
            public Double next() {
                if (hasNext()) {
                    lastReturned = Double.isNaN(lastReturned) ? start : lastReturned + step;
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
        Double[] ar = new Double[size];
        int i = 0;
        for (double o : this) {
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
        for (Double o : this) {
            ar[i] = (T) o;
        }
        return ar;
    }

    @Override
    public boolean add(Double val) {
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
    public boolean addAll(Collection<? extends Double> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int index, Collection<? extends Double> c) {
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
    public Double get(int index) {
        if ((index < 0 || index >= size())) {
            throw new IndexOutOfBoundsException();
        }
        return start + (step * index);
    }

    @Override
    public Double set(int index, Double element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(int index, Double element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Double remove(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int indexOf(Object o) {
        if (!(o instanceof Double)) {
            return -1;
        }
        double integer = (Double) o;
        for (int i = 0; i < size; i++) {
            if (get(i) == integer) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        if (!(o instanceof Double)) {
            return -1;
        }
        double integer = (Double) o;
        for (int i = size - 1; i >= 0; i--) {
            if (get(i) == integer) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public ListIterator<Double> listIterator() {
        return new RangeListIterator(0);
    }

    @Override
    public ListIterator<Double> listIterator(int index) {
        return new RangeListIterator(index);
    }

    @Override
    public List<Double> subList(int fromIndex, int toIndex) {
        if (fromIndex < 0 || fromIndex > size || toIndex < 0 || toIndex > size) {
            throw new IndexOutOfBoundsException();
        }
        return new DoubleRange(get(fromIndex), get(toIndex - 1) + step, step);
    }

    private class RangeListIterator implements ListIterator<Double> {
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
        public Double next() {
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
        public Double previous() {
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
        public void set(Double integer) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(Double integer) {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public int hashCode() {
        return 31 * (31 * new Double(start).hashCode() + new Double(end).hashCode()) + new Double(step).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DoubleRange)) {
            return false;
        }
        DoubleRange other = (DoubleRange) obj;
        return
            other.start == start &&
            other.end == end &&
            other.step == step;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        if (size < 12) {
            Iterator<Double> iterator = this.iterator();
            if (iterator.hasNext()) {
                sb.append(iterator.next());
            }
            while (iterator.hasNext()) {
                sb.append(", ");
                double i = iterator.next();
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
