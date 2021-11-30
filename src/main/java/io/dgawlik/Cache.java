package io.dgawlik;

import java.util.HashMap;
import java.util.Map;

public class Cache {

    Map<Class<?>, Object> map;

    public Cache() {
        map = new HashMap<>();
    }

    public boolean hasKey(Class<?> key) {
        return map.containsKey(key);
    }

    public Cache put(Class<?> key, Object value) {
        map.put(key, value);
        return this;
    }

    public <T> T get(Class<T> key) {
        return (T) map.get(key);
    }

    public void invalidate(Class<?> key) {
        map.entrySet().removeIf(entry -> key.isAssignableFrom(entry.getKey()));
    }

    public void invalidatePackageRegex(String path) {
        map.entrySet().removeIf(entry -> entry.getKey().getCanonicalName().contains(path));
    }
}
