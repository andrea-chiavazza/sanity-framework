package func.persist;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class Utility {

    public static String camelToUnderscored(String camel) {
        StringBuilder sb = new StringBuilder();
        for (char c : camel.toCharArray()) {
            if (c == '_') {
                sb.append("__");
            } else if (c == '.' || c == '$' || Character.isDigit(c)) {
                sb.append(c);
            } else if (Character.isUpperCase(c)) {
                sb.append('_').append(c);
            } else if (Character.isLowerCase(c)) {
                sb.append(Character.toUpperCase(c));
            } else {
                throw new RuntimeException("Illegal character '" + c + "' in string '" + camel + "'");
            }
        }
        return sb.toString();
    }

    static String commaSeparate(Collection<?> tokens) {
        StringBuilder sb = new StringBuilder();
        Iterator<?> it = tokens.iterator();
        if (it.hasNext()) {
            sb.append(it.next());
        }
        while (it.hasNext()) {
            sb.append(", ").append(it.next());
        }
        return sb.toString();
    }

    static String makeUpdateQuery(Class cl,
                                  Collection<String> tokens,
                                  long idValue) {
        List<String> added = new ArrayList<>();
        for (String token : tokens) {
            added.add(token + "=?");
        }
        return "UPDATE " + DB.classToTableName(cl) + " SET " + commaSeparate(added) + " WHERE " + DB.ID + "=" + idValue;
    }

    public static <T> Class<T> wrapperToPrimitiveType(Class<T> cl) {
        Class result;
        if (cl.isPrimitive()) {
            return cl;
        } else if (cl == Boolean.class) {
            result = Boolean.TYPE;
        } else if (cl == Character.class) {
            result = Character.TYPE;
        } else if (cl == Byte.class) {
            result = Byte.TYPE;
        } else if (cl == Short.class) {
            result = Short.TYPE;
        } else if (cl == Integer.class) {
            result = Integer.TYPE;
        } else if (cl == Long.class) {
            result = Long.TYPE;
        } else if (cl == Float.class) {
            result = Float.TYPE;
        } else if (cl == Double.class) {
            result = Double.TYPE;
        } else {
            return cl;
        }
        return result;
    }

    public static <T> Class<T> primitiveTypeToWrapper(Class<T> cl) {
        Class result;
        if (cl == Boolean.TYPE) {
            result = Boolean.class;
        } else if (cl == Character.TYPE) {
            result = Character.class;
        } else if (cl == Byte.TYPE) {
            result = Byte.class;
        } else if (cl == Short.TYPE) {
            result = Short.class;
        } else if (cl == Integer.TYPE) {
            result = Integer.class;
        } else if (cl == Long.TYPE) {
            result = Long.class;
        } else if (cl == Float.TYPE) {
            result = Float.class;
        } else if (cl == Double.TYPE) {
            result = Double.class;
        } else {
            result = cl;
        }
        return result;
    }

    /** Types that are stored directly in the table, not with a long reference */
    public static boolean isBasicType(Class<?> cl) {
        return (wrapperToPrimitiveType(cl).isPrimitive() ||
            cl == String.class || cl == BigDecimal.class || cl == BigInteger.class);
    }
}
