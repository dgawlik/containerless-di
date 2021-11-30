package io.dgawlik.scratchpad.pkg4;

import io.dgawlik.annotation.Inject;
import io.dgawlik.scratchpad.pkg2.B;

public class AB implements InterfaceA {
    public final B b;

    @Inject
    public AB(B b) {
        this.b = b;
    }
}
