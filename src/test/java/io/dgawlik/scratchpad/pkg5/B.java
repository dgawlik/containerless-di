package io.dgawlik.scratchpad.pkg5;

import io.dgawlik.annotation.Inject;

public class B {
    public final A a;

    @Inject
    public B(A a) {
        this.a = a;
    }
}
