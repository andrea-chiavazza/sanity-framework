package func.persist;

import org.pcollections.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

import static func.utility.General.invoke;

public class Refl {
    private static final Map<Class,Constructor<?>> classToConstructorCache = new HashMap<>();
    private static final Map<Class,PVector<Method>> classToGettersCache = new HashMap<>();

    private static <T> T makeInstanceWithOneFields(Class<T> cl) {
        Constructor<T> ctor = findConstructor(cl);
        Class<?>[] parameterTypes = ctor.getParameterTypes();
        int parsLength = parameterTypes.length;
        Object[] pars = new Object[parsLength];
        for (int i = 0; i < parsLength; i++) {
            pars[i] = makeOneValue(parameterTypes[i]);
        }
        return instantiate(ctor, pars);
    }

    private static <T> T makeInstanceWithDefaultFields(Class<T> cl) {
        Constructor<T> ctor = findConstructor(cl);
        Class<?>[] parameterTypes = ctor.getParameterTypes();
        int parsLength = parameterTypes.length;
        Object[] pars = new Object[parsLength];
        for (int i = 0; i < parsLength; i++) {
            pars[i] = makeDefaultValue(parameterTypes[i]);
        }
        return instantiate(ctor, pars);
    }

    private static <T> T makeOneValue(Class<T> cl) {
        Class<T> unwrapped = Utility.wrapperToPrimitiveType(cl);
        byte one = 1;
        Object result;
        if (unwrapped == Boolean.TYPE) {
            result = false;
        } else if (unwrapped == Character.TYPE) {
            result = (char) one;
        } else if (unwrapped == Byte.TYPE) {
            result = one;
        } else if (unwrapped == Short.TYPE) {
            result = (short) one;
        } else if (unwrapped == Integer.TYPE) {
            result = (int) one;
        } else if (unwrapped == Long.TYPE) {
            result = (long) one;
        } else if (unwrapped == Float.TYPE) {
            result = (float) one;
        } else if (unwrapped == Double.TYPE) {
            result = (double) one;
        } else if (unwrapped == String.class) {
            result = "1";
        } else if (unwrapped == BigInteger.class) {
            result = new BigInteger("1");
        } else if (unwrapped == BigDecimal.class) {
            result = new BigDecimal("1.0");
        } else if (PVector.class.isAssignableFrom(unwrapped)) {
            result = Empty.<Integer>vector().plus(1);
        } else if (PSet.class.isAssignableFrom(unwrapped)) {
            result = Empty.<Integer>set().plus(1);
        } else if (PMap.class.isAssignableFrom(unwrapped)) {
            result = Empty.<String,Integer>map().plus("a", 1);
        } else {
            result = makeInstanceWithOneFields(unwrapped);
        }
        return (T) result;
    }

    private static <T> T makeRecognizableValue(Class<T> cl) {
        Class<T> unwrapped = Utility.wrapperToPrimitiveType(cl);
        Object result;
        if (unwrapped == Boolean.TYPE) {
            result = true;
        } else if (unwrapped == Character.TYPE) {
            result = '\ud834';
        } else if (unwrapped == Byte.TYPE) {
            result = (byte) 0x27;
        } else if (unwrapped == Short.TYPE) {
            result = (short) 0xa7b3;
        } else if (unwrapped == Integer.TYPE) {
            result = 0x27b32f43;
        } else if (unwrapped == Long.TYPE) {
            result = 0x27b32f4361ab3d54L;
        } else if (unwrapped == Float.TYPE) {
            result = Float.intBitsToFloat(0x27b32f43);
        } else if (unwrapped == Double.TYPE) {
            result = Double.longBitsToDouble(0x27b32f4361ab3d54L);
        } else if (unwrapped == String.class) {
            result = "0xa7b32f4361ab3d54L";
        } else if (unwrapped == BigInteger.class) {
            result = new BigInteger("a7b32f4361ab3d547b32f4361ab3d54", 16);
        } else if (unwrapped == BigDecimal.class) {
            result = new BigDecimal("1.23456789");
        } else if (PVector.class.isAssignableFrom(unwrapped)) {
            result = Empty.<Integer>vector().plus(1324).plus(0x5a2f383).plus(0x7a263442);
        } else if (PSet.class.isAssignableFrom(unwrapped)) {
            result = Empty.<Integer>set().plus(323).plus(-0x1a2f113).plus(0x1a267842);
        } else if (PMap.class.isAssignableFrom(unwrapped)) {
            result = Empty.<String,Integer>map().plus("a", 323).plus("bc", -0x1a2f113).plus("dd", 0x1a267842);
        } else {
            result = makeInstanceWithAllRecognizableFields(findConstructor(unwrapped));
        }
        return (T) result;
    }

    private static <T> T makeInstanceWithRecognizableField(Constructor<T> ctor,
                                                           int ord) {
        Class<?>[] parameterTypes = ctor.getParameterTypes();
        int parsLength = parameterTypes.length;
        Object[] pars = new Object[parsLength];
        for (int i = 0; i < parsLength; i++) {
            Class<?> fieldClass = parameterTypes[i];
            if (i == ord) {
                pars[i] = makeRecognizableValue(fieldClass);
            } else {
                pars[i] = makeOneValue(fieldClass);
            }
        }
        return instantiate(ctor, pars);
    }

    private static <T> T makeInstanceWithAllRecognizableFields(Constructor<T> ctor) {
        Class<?>[] parameterTypes = ctor.getParameterTypes();
        int parsLength = parameterTypes.length;
        Object[] pars = new Object[parsLength];
        for (int i = 0; i < parsLength; i++) {
            pars[i] = makeRecognizableValue(parameterTypes[i]);
        }
        return instantiate(ctor, pars);
    }

    private static <T> Method findGetter(Class<T> cl, Constructor<T> ctor, int ord) {
        Object instance = makeInstanceWithRecognizableField(ctor, ord);
        Method getter = null;
        for (Method method : cl.getMethods()) {
            String methodName = method.getName();
            if (method.getParameterTypes().length == 0 &&
                !"hashCode".equals(methodName) &&
                !"getClass".equals(methodName) &&
                !"toString".equals(methodName)) {
                Class<?> retType = method.getReturnType();
                if (retType != Void.TYPE) {
                    Object retObj = invoke(instance, method);
                    if (retObj != null && retObj.equals(makeRecognizableValue(retType))) {
                        if (getter != null) {
                            throw new RuntimeException(method + " and " + getter + " return the same value in class " + cl);
                        } else {
                            getter = method;
                        }
                    }
                }
            }
        }
        return getter;
    }

    public static synchronized  <T> T makeDefaultValue(Class<T> cl) {
        Class<T> unwrapped = Utility.wrapperToPrimitiveType(cl);
        byte zero = 0;
        Object result;
        if (unwrapped == Boolean.TYPE) {
            result = false;
        } else if (unwrapped == Character.TYPE) {
            result = (char) zero;
        } else if (unwrapped == Byte.TYPE) {
            result = zero;
        } else if (unwrapped == Short.TYPE) {
            result = (short) zero;
        } else if (unwrapped == Integer.TYPE) {
            result = (int) zero;
        } else if (unwrapped == Long.TYPE) {
            result = (long) zero;
        } else if (unwrapped == Float.TYPE) {
            result = (float) zero;
        } else if (unwrapped == Double.TYPE) {
            result = (double) zero;
        } else if (unwrapped == String.class) {
            result = "";
        } else if (unwrapped == BigInteger.class) {
            result = new BigInteger("0");
        } else if (unwrapped == BigDecimal.class) {
            result = new BigDecimal("0.0");
        } else if (PVector.class.isAssignableFrom(unwrapped)) {
            result = Empty.<Integer>vector();
        } else if (PSet.class.isAssignableFrom(unwrapped)) {
            result = Empty.<Integer>set();
        } else if (PMap.class.isAssignableFrom(unwrapped)) {
            result = Empty.<String,Integer>map();
        } else {
            result = makeInstanceWithDefaultFields(unwrapped);
        }
        return (T) result;
    }

    /** Finds the public constructor with the biggest number of arguments */
    public static synchronized <T> Constructor<T> findConstructor(Class<T> cl) {
        if (classToConstructorCache.containsKey(cl)) {
            return (Constructor<T>) classToConstructorCache.get(cl);
        } else {
            Constructor<T> bestCtor = null;
            for (Constructor<T> ctor : (Constructor<T> []) cl.getConstructors()) { // correct cast according to the API
                if (bestCtor == null || ctor.getParameterTypes().length > bestCtor.getParameterTypes().length) {
                    bestCtor = ctor;
                }
            }
            if (bestCtor != null) {
                classToConstructorCache.put(cl, bestCtor);
                return bestCtor;
            } else {
                throw new RuntimeException(cl + " has no public constructors");
            }
        }
    }

    static boolean hasReferences(Class<?> cl) {
        if (PCollection.class.isAssignableFrom(cl) || PMap.class.isAssignableFrom(cl)) {
            return true;
        }
        boolean hasReferences = false;
        if (!Utility.isBasicType(cl)) {
            for (Method method : findGetters(cl)) {
                if (!Utility.isBasicType(method.getReturnType())) {
                    hasReferences = true;
                    break;
                }
            }
        }
        return hasReferences;
    }

    /* Returns the getters to be called for the constructor in the right order */
    public static synchronized <T> PVector<Method> findGetters(Class<T> cl) {
        if (classToGettersCache.containsKey(cl)) {
            return classToGettersCache.get(cl);
        } else {
            Constructor<T> ctor = findConstructor(cl);
            PVector<Method> result = Empty.vector();
            // for each constructor parameter find the related getter
            for (int i = 0; i < ctor.getParameterTypes().length; i++) {
                Method getter = findGetter(cl, ctor, i);
                if (getter != null) {
                    result = result.plus(getter);
                } else {
                    throw new RuntimeException(cl + " has no getter for argument " + (i + 1) +
                        " of constructor " + ctor);
                }
            }
            classToGettersCache.put(cl, result);
            return result;
        }
    }

    /** The arguments that would be needed to instantiate the object */
    static List<Object> getFieldsValues(Object obj) {
        if (obj instanceof String) {
            return Collections.singletonList(obj);
        }
        List<Method> methods = findGetters(obj.getClass());
        List<Object> values = new ArrayList<>(methods.size());
        for (Method method : methods) {
            values.add(invoke(obj, method));
        }
        return values;
    }

    public static <T> T instantiate(Constructor<T> ctor,
                                    Object[] args) {
        try {
            return ctor.newInstance(args);
        } catch (Exception e) {
            throw new RuntimeException(e + " Constructor: " + ctor + " Arguments: " + Arrays.toString(args));
        }
    }

    public static Class classFromName(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Class '" + name + "' can't be found");
        }
    }
}

