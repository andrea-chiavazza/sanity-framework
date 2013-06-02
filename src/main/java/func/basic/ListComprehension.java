package func.basic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * See http://en.wikipedia.org/wiki/List_comprehension
 */
public class ListComprehension {
    /** Returns a collection of a given function applied to the cartesian product of 2 given collections.<br/>
     * Example:<br/>
     * list1: [a, b, c]<br/>
     * list2: [d, e]<br/>
     * f: *<br/>
     *
     * Result: [a*d, a*e, b*d, b*e, c*d, c*e]
     * */
    public static <T,U,R> List<R> listComprehension(Collection<T> tColl,
                                                    Collection<U> uColl,
                                                    F2<T,U,R> f2) {
        List<R> result = new ArrayList<>();
        for (T t : tColl) {
            for (U u : uColl) {
                result.add(f2.execute(t, u));
            }
        }
        return result;
    }

    /** Returns a collection of a given function applied to the cartesian product of 3 given collections.<br/>
     * Example with 2 collections, with 3 is analogous:<br/>
     * list1: [a, b, c]<br/>
     * list2: [d, e]<br/>
     * f: *<br/>
     *
     * Result: [a*d, a*e, b*d, b*e, c*d, c*e]
     * */
    public static <T,U,V,R> List<R> listComprehension(Collection<T> tColl,
                                                      Collection<U> uColl,
                                                      Collection<V> vColl,
                                                      F3<T,U,V,R> f3) {
        List<R> result = new ArrayList<R>();
        for (T t : tColl) {
            for (U u : uColl) {
                for (V v : vColl) {
                    result.add(f3.execute(t, u, v));
                }
            }
        }
        return result;
    }
}
