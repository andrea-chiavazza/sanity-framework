package func.utility;

import org.pcollections.OrderedPSet;
import org.pcollections.POrderedSet;
import org.pcollections.TreePVector;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class General {

    /** Useful to avoid having to catch exceptions and to treat them all in the same way. */
    public static Object invoke(Object obj,
                                Method method,
                                Object... args) {
        try {
            return method.invoke(obj, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static <E> POrderedSet<E> replaceInOrderedSet(POrderedSet<E> set,
                                                         E oldE,
                                                         E newE) {
        if (oldE != null) {
            int index = set.indexOf(oldE);
            if (index > -1) {
                return OrderedPSet.from(TreePVector.from(set).with(index, newE));
            } else {
                return set;
            }
        } else {
            return set;
        }
    }

}
