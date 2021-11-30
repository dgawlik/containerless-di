package io.dgawlik.scratchpad.pkg3;

import io.dgawlik.annotation.Inject;
import io.dgawlik.annotation.Qualifier;
import io.dgawlik.scratchpad.pkg1.A;
import io.dgawlik.scratchpad.pkg2.B;

public class E {
    public final A a;
    public final B b;

    @Inject
    @Qualifier("q1")
    public E(A a, B b) {
        this.a = a;
        this.b = b;
    }
}
