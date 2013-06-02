package func.utility;

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
}
