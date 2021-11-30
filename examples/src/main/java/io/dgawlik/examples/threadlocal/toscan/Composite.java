package io.dgawlik.examples.threadlocal.toscan;

import io.dgawlik.annotation.Inject;

public class Composite {
    public final A a;
    public final B b;

    @Inject
    public Composite(A a, B b) {
        this.a = a;
        this.b = b;
    }
}
