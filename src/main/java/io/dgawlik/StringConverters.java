package io.dgawlik;

import java.util.HashMap;
import java.util.Map;

public class StringConverters {

    interface Converter<T> {
        T convert(String value);
    }

    private static Map<Class<?>, Converter<?>> registered;

    static {
        registered = new HashMap<>();
        registered.put(Byte.class, Byte::valueOf);
        registered.put(Short.class, Short::valueOf);
        registered.put(Integer.class, Integer::valueOf);
        registered.put(Long.class, Long::valueOf);
        registered.put(Float.class, Float::valueOf);
        registered.put(Double.class, Double::valueOf);
        registered.put(String.class, s -> s);
        registered.put(Boolean.class, s -> s.equals("true") || s.equals("y"));
    }

    public static <T> void registerConverter(Class<T> cls, Converter<T> conv) {
        registered.put(cls, conv);
    }

    public static <T> T convert(String value, Class<T> cls) {
        if (registered.containsKey(cls)) {
            try {
                return (T) registered.get(cls).convert(value);
            } catch (Exception ex) {
                throw new RuntimeException("Unable to convert "
                        + value + " to " + cls.getSimpleName(), ex);
            }
        }

        throw new RuntimeException("No registered converter for " + cls.getSimpleName());
    }
}
