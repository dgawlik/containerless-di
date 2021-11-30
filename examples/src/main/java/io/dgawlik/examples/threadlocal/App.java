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
    private static class CustomThread extends Thread {

        ThreadLocal<Cache> cache;


        @Override
        public void run() {
            cache = new ThreadLocal<>();
            cache.set(new Cache());

            for (int i = 0; i < 3; i++) {

                Cache temp = cache.get();
                Composite comp = Injector.forClass(Composite.class)
                        .scanning("io.dgawlik.examples.threadlocal.toscan")
                        .cache(temp)
                        .assemble();
                cache.set(temp);

                System.out.printf("%s %s\n", comp.a, comp.b);

                try {
                    Thread.sleep(1_000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        new CustomThread().start();
        new CustomThread().start();
    }
}
