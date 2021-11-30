package io.dgawlik.scratchpad.pkg3;

import io.dgawlik.annotation.Inject;
import io.dgawlik.annotation.Qualifier;

public class H {
    public final E e;

    @Inject
    public H(@Qualifier("q2") E e) {
        this.e = e;
    }
}
