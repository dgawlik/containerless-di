package io.dgawlik.scratchpad.pkg4;

import io.dgawlik.annotation.Inject;

public class C {
    public final InterfaceA ia;

    @Inject
    public C(InterfaceA ia) {
        this.ia = ia;
    }
}
