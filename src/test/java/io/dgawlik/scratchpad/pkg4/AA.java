package io.dgawlik.scratchpad.pkg4;

import io.dgawlik.annotation.Inject;
import io.dgawlik.scratchpad.pkg1.A;

public class AA implements InterfaceA {
    public final A a;

    @Inject
    public AA(A a) {
        this.a = a;
    }
}
