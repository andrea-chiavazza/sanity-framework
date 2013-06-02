package func.basic;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * "map" refers to the higher-order-function. See http://en.wikipedia.org/wiki/Map_(higher-order_function)
 */
public class MapFunc {
    // 2 different algorithms are used depending on the data-size/number-of-cores ratio threshold
    private static final int SIZE_CORES_RATIO = 50;

    /** Returns the collection resulting from applying a function to a given collection. */
    public static <T,R> List<R> map(F1<T,? extends R> f1,
                                    Collection<? extends T> coll) {
        List<R> result = new ArrayList<R>(coll.size());
        for (T t : coll) {
            result.add(f1.execute(t));
        }
        return result;
    }

    /** Returns the collection resulting from applying a function to a given collection.<br/>
     * This method is multi-threaded and uses all available processors. */
    public static <T,R> List<R> pmap(final F1<T,? extends R> f1,
                                     final List<? extends T> coll) {
        final int size = coll.size();
        //Note: availableProcessors() may change during a particular invocation of the virtual machine
        final int usedCores = Math.min(size, Runtime.getRuntime().availableProcessors());
        if (usedCores == 1) {
            return map(f1, coll);
        }

        final Thread[] threads = new Thread[usedCores];

        // an unchecked cast, but avoids an array copy
        final Object[] resultAr = new Object[size];
        final List<R> result = Arrays.asList((R[]) resultAr);

        if (size / (double) usedCores < SIZE_CORES_RATIO) { // small number of jobs per thread: jobs are fetched by threads
            final AtomicInteger counter = new AtomicInteger(0);

            for (int p = 0; p < threads.length; p++) {
                Thread thread = new Thread() {
                    public void run() {
                        int index;
                        while ((index = counter.getAndIncrement()) < size) {
                            resultAr[index] = f1.execute(coll.get(index));
                        }
                    }
                };
                thread.start();
                threads[p] = thread;
            }
        } else { // big number of jobs per thread: jobs are evenly split between the threads
            final double sliceSize = size / (double) usedCores;

            for (int p = 0; p < threads.length; p++) {
                final int fromIndex = (int) Math.ceil(sliceSize * p);
                final int toIndex   = (int) Math.ceil(sliceSize * p + sliceSize);

                final List<? extends T> inputSlice = coll.subList(fromIndex, toIndex);
                final List<R> outputSlice = result.subList(fromIndex, toIndex);
                Thread thread = new Thread() {
                    public void run() {
                        for (int i = 0, size1 = outputSlice.size(); i < size1; i++) {
                            outputSlice.set(i, f1.execute(inputSlice.get(i)));
                        }
                    }
                };
                thread.start();
                threads[p] = thread;
            }
        }

        // waits for threads to terminate
        try {
            for (Thread thread : threads) {
                thread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return result;
    }

    /** Returns the collection resulting from applying f2 to the set of first items of each coll,<br />
     * followed by applying f2 to the set of second items in each coll, until any one of the collection is exhausted.<br />
     * Any remaining items in other collections are ignored.<br />
     * Function f2 should accept number-of-collections arguments.<br />
     * */
    public static <T,U,R> List<R> map(F2<T,U,? extends R> f2,
                                      Collection<? extends T> coll1,
                                      Collection<? extends U> coll2) {
        int size = Math.min(coll1.size(), coll2.size());
        Iterator<? extends T> it1 = coll1.iterator();
        Iterator<? extends U> it2 = coll2.iterator();
        List<R> results = new ArrayList<R>(size);
        for (int i = 0; i < size; i++) {
            results.add(f2.execute(it1.next(), it2.next()));
        }
        return results;
    }

    /** Returns the collection resulting from applying f2 to the set of first items of each coll,<br />
     * followed by applying f2 to the set of second items in each coll, until any one of the collection is exhausted.<br />
     * Any remaining items in other collections are ignored.<br />
     * Function f2 should accept number-of-collections arguments.<br />
     * This method is multi-threaded and uses all available processors. */
    public static <T,U,R> List<R> pmap(final F2<T,U,? extends R> f2,
                                       final List<? extends T> coll1,
                                       final List<? extends U> coll2) {
        final int size = Math.min(coll1.size(), coll2.size());
        //Note: availableProcessors() may change during a particular invocation of the virtual machine
        final int usedCores = Math.min(size, Runtime.getRuntime().availableProcessors());
        if (usedCores == 1) {
            return map(f2, coll1, coll2);
        }

        final Thread[] threads = new Thread[usedCores];

        // an unchecked cast, but avoids an array copy
        final Object[] resultAr = new Object[size];
        final List<R> result = Arrays.asList((R[]) resultAr);

        if (size / (double) usedCores < SIZE_CORES_RATIO) { // small number of jobs per thread: jobs are fetched by threads
            final AtomicInteger counter = new AtomicInteger(0);

            for (int p = 0; p < threads.length; p++) {
                Thread thread = new Thread() {
                    public void run() {
                        int index;
                        while ((index = counter.getAndIncrement()) < size) {
                            resultAr[index] = f2.execute(coll1.get(index), coll2.get(index));
                        }
                    }
                };
                thread.start();
                threads[p] = thread;
            }
        } else { // big number of jobs per thread: jobs are evenly split between the threads
            final double sliceSize = size / (double) usedCores;

            for (int p = 0; p < threads.length; p++) {
                final int fromIndex = (int) Math.ceil(sliceSize * p);
                final int toIndex   = (int) Math.ceil(sliceSize * p + sliceSize);

                final List<? extends T> inputSlice1 = coll1.subList(fromIndex, toIndex);
                final List<? extends U> inputSlice2 = coll2.subList(fromIndex, toIndex);
                final List<R> outputSlice = result.subList(fromIndex, toIndex);
                Thread thread = new Thread() {
                    public void run() {
                        for (int i = 0; i < outputSlice.size(); i++) {
                            outputSlice.set(i, f2.execute(inputSlice1.get(i), inputSlice2.get(i)));
                        }
                    }
                };
                thread.start();
                threads[p] = thread;
            }
        }

        // waits for all threads to terminate
        try {
            for (Thread thread : threads) {
                thread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return result;
    }
}
