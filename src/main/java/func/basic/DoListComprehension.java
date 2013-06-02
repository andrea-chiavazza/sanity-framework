package func.basic;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Like a list comprehension, but results are not retained.<br/>
 * Can be useful only for the side-effects of non-pure functions, for example a function that draws on the screen.<br/>
 * See http://en.wikipedia.org/wiki/List_comprehension
 */
public class DoListComprehension {
    // 2 different algorithms are used depending on the data-size/number-of-cores ratio threshold
    private static final int SIZE_CORES_RATIO = 50;

    public static <T> void doSeq(Collection<? extends T> tColl,
                                 F1<T,?> f1) {
        for (T t : tColl) {
            f1.execute(t);
        }
    }

    public static <T> void doPSeq(final List<? extends T> tColl,
                                  final F1<T,?> f1) {
        final int size = tColl.size();
        //Note: availableProcessors() may change during a particular invocation of the virtual machine
        final int usedCores = Math.min(size, Runtime.getRuntime().availableProcessors());
        if (usedCores == 1) {
            doSeq(tColl, f1);
        }

        final Thread[] threads = new Thread[usedCores];

        if (size / (double) usedCores < SIZE_CORES_RATIO) { // small number of jobs per thread: jobs are fetched by threads
            final AtomicInteger counter = new AtomicInteger(0);

            for (int p = 0; p < threads.length; p++) {
                Thread thread = new Thread() {
                    public void run() {
                        int index;
                        while ((index = counter.getAndIncrement()) < size) {
                            f1.execute(tColl.get(index));
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
                final int toIndex = (int) Math.ceil(sliceSize * p + sliceSize);

                final List<? extends T> inputSlice = tColl.subList(fromIndex, toIndex);
                Thread thread = new Thread() {
                    public void run() {
                        for (int i = 0, size1 = inputSlice.size(); i < size1; i++) {
                            f1.execute(inputSlice.get(i));
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
    }

    public static <T,U> void doSeq(Collection<? extends T> tColl,
                                   Collection<? extends U> uColl,
                                   F2<T,U,?> f2) {
        for (T t : tColl) {
            for (U u : uColl) {
                f2.execute(t, u);
            }
        }
    }

    public static <T,U> void doPSeq(final List<? extends T> tColl,
                                    final Collection<? extends U> uColl,
                                    final F2<T,U,?> f2) {
        //Note: availableProcessors() may change during a particular invocation of the virtual machine
        final int usedCores = Math.min(tColl.size(), Runtime.getRuntime().availableProcessors());
        if (usedCores == 1) {
            doSeq(tColl, uColl, f2);
        }

        doPSeq(
            tColl,
            new F1<T,Void>() {
                @Override
                public Void execute(final T t) {
                    doSeq(
                        uColl,
                        new F1<U,Void>() {
                            @Override
                            public Void execute(U u) {
                                f2.execute(t, u);
                                return null;
                            }
                        }
                    );
                    return null;
                }
            }
        );
    }

    public static <T,U,V> void doSeq(Collection<? extends T> tColl,
                                     Collection<? extends U> uColl,
                                     Collection<? extends V> vColl,
                                     F3<T,U,V,?> f3) {
        for (T t : tColl) {
            for (U u : uColl) {
                for (V v : vColl) {
                    f3.execute(t, u, v);
                }
            }
        }
    }

    //todo doPSeq() for arity 3
}
