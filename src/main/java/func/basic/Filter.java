package func.basic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * "filter" refers to the higher-order-function. See http://en.wikipedia.org/wiki/Filter_(higher-order_function)
 */
public class Filter {
    // 2 different algorithms are used depending on the data-size/number-of-cores ratio threshold
    private static final int SIZE_CORES_RATIO = 50;

    /** Returns a collection of the items in the given collection for which the given predicate returns true. */
    public static <T> List<T> filter(F1<T,Boolean> pred,
                                     Collection<? extends T> coll) {
        List<T> result = new ArrayList<T>(coll.size());
        for (T t : coll) {
            if (pred.execute(t)) {
                result.add(t);
            }
        }
        return result;
    }

    //todo: improves only on few heavy jobs, unless array copying is removed
    /** Returns a collection of the items in the given collection for which the given predicate returns true.<br/>
     * This method is multi-threaded and uses all available processors. */
    public static <T> List<T> pfilter(final F1<T,Boolean> pred,
                                      final List<? extends T> coll) {
        final int size = coll.size();
        //Note: availableProcessors() may change during a particular invocation of the virtual machine
        final int usedCores = Math.min(size, Runtime.getRuntime().availableProcessors());
        if (usedCores == 1) {
            return filter(pred, coll);
        }

        final Thread[] threads = new Thread[usedCores];
        final List<T> result;

        if (size / (double) usedCores < SIZE_CORES_RATIO) { // small number of jobs per thread: jobs are fetched by threads
            final AtomicInteger counter = new AtomicInteger(0);
//            result = Collections.synchronizedList(new ArrayList<T>(size));
            final Map<Integer,T> resultMap = new ConcurrentSkipListMap<Integer,T>();

            for (int p = 0; p < threads.length; p++) {
                Thread thread = new Thread(Integer.toString(p)) {
                    public void run() {
                        int index;
                        while ((index = counter.getAndIncrement()) < size) {
                            final T t = coll.get(index);
                            if (pred.execute(t)) {
//                                result.add(t);
                                resultMap.put(index, t);
                            }
                        }
                    }
                };
                thread.start();
                threads[p] = thread;
            }

            // waits for all threads to terminate
            try {
                for (Thread thread : threads) {
                    thread.join();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            result = new ArrayList(resultMap.values());
        } else { // big number of jobs per thread: we split the jobs between the threads
//            final Map<Integer,List<T>> slices = new ConcurrentHashMap<Integer,List<T>>(usedCores);
            final Map<Integer,List<T>> slices = new ConcurrentSkipListMap<Integer,List<T>>();
            final double sliceSize = size / (double) usedCores;
            for (int p = 0; p < threads.length; p++) {
                final int begin = (int) Math.ceil(p * sliceSize);
                final int end =   (int) Math.ceil((p + 1) * sliceSize);

                final int finalP = p;
                final List<? extends T> subColl = coll.subList(begin, end);
                Thread thread = new Thread() {
                    public void run() {
                        slices.put(
                            finalP,
                            filter(pred, subColl));
                    }
                };
                thread.start();
                threads[p] = thread;
            }
            result = new ArrayList<T>(size);
            try {
                for (int p = 0; p < threads.length; p++) {
                    threads[p].join();
                    result.addAll(slices.get(p));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
