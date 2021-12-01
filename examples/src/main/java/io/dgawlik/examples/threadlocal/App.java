package io.dgawlik.examples.threadlocal;

import io.dgawlik.Cache;
import io.dgawlik.Injector;
import io.dgawlik.examples.threadlocal.toscan.Composite;


/**
 * This example shows how to create Thread-local pseudo-container.
 * Just store intermediate cache in ThreadLocal variable.
 *
 */
public class App {

    public static void main(String[] args) {

        var context = ThreadLocal.withInitial(Cache::new);

        Runnable runnable = () -> {
            for (int i = 0; i < 3; i++) {

                Cache temp = context.get();
                Composite comp = Injector.forClass(Composite.class)
                        .scanning("io.dgawlik.examples.threadlocal.toscan")
                        .cache(temp)
                        .assemble();
                context.set(temp);

                System.out.printf("%s %s\n", comp.a, comp.b);

                try {
                    Thread.sleep(1_000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        new Thread(runnable).start();
        new Thread(runnable).start();
    }
}
