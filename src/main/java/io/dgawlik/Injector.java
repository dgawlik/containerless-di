package io.dgawlik;


import io.dgawlik.annotation.Inject;
import io.dgawlik.annotation.Qualifier;
import io.dgawlik.annotation.Value;
import org.reflections.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@SuppressWarnings({"unchecked", "rawtypes"})
public class Injector {
    public static class Work<T> {
        private final Class<T> root;
        private String[] scanPackages;
        private Environment environment = new Environment();
        private Map<Class, Integer> priorities = new HashMap<>();
        private Cache cache = new Cache();

        /**
         * Fluent API starts with object intiating assembly - "root"
         *
         * @param cls root.getClass()
         */
        public Work(Class<T> cls) {
            root = cls;
        }

        /**
         * Recursively scan each package
         *
         * @param basePackages
         * @return next step
         * @see Scanner
         */
        public Work<T> scanning(String... basePackages) {
            scanPackages = basePackages;
            return this;
        }

        /**
         * Next specify sources from which properties can be injected.
         * Primitive types are converted.
         *
         * @param environment
         * @return
         * @see Value
         */
        public Work<T> environment(Environment environment) {
            this.environment = environment;
            return this;
        }

        /**
         * By supplying dynamic mapping of class-priority you
         * can change which bean gets injected. Default priority
         * if not specified is 0 smaller wins and only first in
         * list gets injected
         *
         * @param priorities
         * @param <U>        dirty hack
         * @return
         */
        public <U> Work<T> priorities(Map<Class<? extends U>, Integer> priorities) {
            this.priorities = (Map<Class, Integer>) (Map) priorities;
            return this;
        }

        /**
         * By storing intermediate steps in cache you can
         * simulate singleton scope. Custom scopes are
         * implemented by directly manipulating cache instance
         * in between the Injector calls.
         *
         * @param cache
         * @return
         */
        public Work<T> cache(Cache cache) {
            this.cache = cache;
            return this;
        }

        /**
         * Final step where all the magic happens.
         *
         * @return assembled bean
         * @throws IllegalArgumentException something wrong with root bean
         * @throws IllegalStateException    something went wrong during injection
         * @throws RuntimeException         everything other
         */
        public T assemble() {
            var emptyCtors = ReflectionUtils
                    .getConstructors(root, ctor -> ctor.getParameterCount() == 0);
            var injectCtors = ReflectionUtils
                    .getConstructors(root, ctor -> ctor.getAnnotation(Inject.class) != null);

            Constructor ctor = null;
            if (emptyCtors.isEmpty() && injectCtors.isEmpty()) {
                throw new IllegalArgumentException("Root has no empty constructor or no @Inject annotation");
            } else if (!emptyCtors.isEmpty()) {
                ctor = new ArrayList<>(emptyCtors).get(0);
            } else {
                ctor = new ArrayList<>(injectCtors).get(0);
            }

            var factories = new Scanner(scanPackages)
                    .search()
                    .stream()
                    .collect(Collectors.groupingBy(
                            Constructor::getDeclaringClass, Collectors.toList()));

            return (T) resolve(ctor, factories);
        }

        private Object resolve(Constructor ctor, Map<Class, List<Constructor>> factories) {
            // for empty constructor just invoke it
            if (ctor.getParameterCount() == 0) {
                return instantiateWrapException(ctor);
            }

            // now for multi-param constructors
            Object[] factoryArgs = new Object[ctor.getParameterCount()];

            // outline: recursively resolve each constructor's parameter
            int ind = 0;
            Annotation[][] parameterAnnotations = ctor.getParameterAnnotations();
            Class[] parameterTypes = ctor.getParameterTypes();
            for (Class paramType : ctor.getParameterTypes()) {
                var annValue = getFirstValueAnnotation(parameterAnnotations[ind]);
                var annQualifier = getFirstQualifierAnnotation(parameterAnnotations[ind]);
                var chosenFactory = getAssignableWithHighestPriority(paramType, factories, annQualifier);

                if (annValue == null) {
                    // it's not primitive type, it's another bean

                    // no suitable constructor found
                    if (chosenFactory == null) {
                        throw new IllegalStateException("Unstatisfied dependency for "
                                + ctor.getDeclaringClass().getSimpleName() + ": "
                                + paramType.getSimpleName() + " missing.");
                    }

                    // reuse if present
                    if (!cache.hasKey(chosenFactory.getDeclaringClass())) {
                        cache.put(chosenFactory.getDeclaringClass(), resolve(chosenFactory, factories));
                    }

                    factoryArgs[ind++] = cache.get(chosenFactory.getDeclaringClass());
                } else {
                    // resolve @Value

                    var prop = environment.getProperty(annValue.value());
                    if (prop == null) {
                        throw new RuntimeException("No defined property " + annValue.value());
                    }
                    factoryArgs[ind] = StringConverters.convert(prop, parameterTypes[ind++]);
                }
            }

            return instantiateWrapException(ctor, factoryArgs);
        }

        /**
         * Reduces noise in code
         *
         * @param ctor
         * @param args
         * @return
         */
        private Object instantiateWrapException(Constructor ctor, Object... args) {
            try {
                return ctor.newInstance(args);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new IllegalStateException("Error invoking constructor", e);
            }
        }

        private Constructor getAssignableWithHighestPriority(
                Class paramType, Map<Class, List<Constructor>> factories,
                Qualifier qualifier) {

            // first: get all constructors with class assignable to param
            // second: if qualifier present narrow them down to matchinq qualifiers
            List<Constructor> lst = new ArrayList<>();
            for (Class key : factories.keySet()) {
                if (paramType.isAssignableFrom(key)) {

                    var stream = factories.get(key)
                            .stream();

                    if (qualifier != null) {
                        stream = stream.filter(ctor ->
                                ctor.getAnnotation(Qualifier.class) != null
                                        & qualifier.value()
                                        .equals(
                                                ((Qualifier) ctor.getAnnotation(Qualifier.class)).value()));
                    }

                    lst.addAll(stream.collect(Collectors.toSet()));
                }
            }

            if (lst.isEmpty()) {
                return null;
            }

            // order natural by priority, get first
            lst.sort(Comparator.comparing(c -> getPriority(c, priorities)));
            return lst.get(0);
        }

        private Integer getPriority(Constructor ctor, Map<Class, Integer> priorities) {
            return priorities.getOrDefault(ctor.getDeclaringClass(), 0);
        }

        private Qualifier getFirstQualifierAnnotation(Annotation[] annotations) {
            return (Qualifier) Stream.of(annotations)
                    .filter(a -> a instanceof Qualifier).findFirst().orElse(null);
        }

        private Value getFirstValueAnnotation(Annotation[] annotations) {
            return (Value) Stream.of(annotations)
                    .filter(a -> a instanceof Value).findFirst().orElse(null);
        }
    }


    public static <T> Work<T> forClass(Class<T> cls) {
        return new Work<>(cls);
    }
}
