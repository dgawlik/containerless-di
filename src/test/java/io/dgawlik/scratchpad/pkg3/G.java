package io.dgawlik.scratchpad.pkg3;

import io.dgawlik.annotation.Inject;
import io.dgawlik.annotation.Qualifier;

public class G {
    public final E e;

    @Inject
    public G(@Qualifier("q1") E e) {
        this.e = e;
    }
}
