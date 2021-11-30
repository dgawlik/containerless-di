package io.dgawlik.scratchpad.pkg3;

import io.dgawlik.annotation.Inject;
import io.dgawlik.annotation.Value;

public class I {
    public final String a;
    public final Integer b;

    @Inject
    public I(@Value("a.a") String a, @Value("numeric") Integer b) {
        this.a = a;
        this.b = b;
    }
}
