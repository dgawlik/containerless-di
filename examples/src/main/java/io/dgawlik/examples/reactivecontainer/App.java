package io.dgawlik.examples.reactivecontainer;

import io.dgawlik.Environment;
import io.dgawlik.Injector;
import io.dgawlik.examples.reactivecontainer.toscan.A;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;


/**
 * This example shows how injector can be used as reactive microcontainer.
 * Microcontainer subscribes to stream of updates and reloads it's components.
 * With some coding this can be extended to remote reloading as well...
 * <p>
 * To see how changes in file are reflected in console output modify
 * some.properties in examples root directory.
 * <p>
 * If no file found try to modify relative path.
 */
public class App {
    public static class SingletonContainer {

        private Map<Class<?>, Object> singletons;
        private final Class[] classes;

        public static SingletonContainer forClasses(Class<?>... cls) {
            return new SingletonContainer(cls);
        }

        public <T> T getBean(Class<T> cls) {
            return (T) singletons.get(cls);
        }

        public void subscribeTicks(Flux<Object> flux) {
            flux.subscribeOn(Schedulers.single(), false)
                    .subscribe(i -> {
                        System.out.println("Container received tick " + i + ", refreshing");
                        refresh();
                    });

        }

        private SingletonContainer(Class<?>... forClasses) {
            singletons = new HashMap<>();
            classes = forClasses;
            refresh();
        }

        private void refresh() {
            for (Class<?> cls : classes) {
                var obj = Injector.forClass(cls)
                        .scanning("io.dgawlik.examples.reactivecontainer.toscan")
                        .environment(new Environment().withFile("./examples/some.properties"))
                        .assemble();
                singletons.put(cls, obj);
            }
        }
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        SingletonContainer cont = SingletonContainer.forClasses(A.class);

        Flux<Object> stream = Flux.generate(() -> 0, (state, sink) -> {
            sink.next(state);
            return state + 1;
        }).delayElements(Duration.ofSeconds(1));

        cont.subscribeTicks(stream);

        stream.subscribeOn(Schedulers.single())
                .subscribe(i -> {
                    System.out.println(cont.getBean(A.class));
                });

        System.in.read();
    }
}
