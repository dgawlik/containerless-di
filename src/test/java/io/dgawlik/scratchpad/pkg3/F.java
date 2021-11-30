package io.dgawlik.scratchpad.pkg3;

import io.dgawlik.annotation.Inject;
import io.dgawlik.scratchpad.pkg1.A;

import java.math.BigDecimal;

public class F {
    public final A a;
    public final BigDecimal b;

    @Inject
    public F(A a, BigDecimal b) {
        this.a = a;
        this.b = b;
    }
}
