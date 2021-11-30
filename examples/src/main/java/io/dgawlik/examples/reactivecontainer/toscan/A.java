package io.dgawlik.examples.reactivecontainer.toscan;

import io.dgawlik.annotation.Inject;
import io.dgawlik.annotation.Value;

public class A {
    public final String prop1;
    public final Integer prop2;

    @Inject
    public A(@Value("prop1") String prop1, @Value("prop2") Integer prop2) {
        this.prop1 = prop1;
        this.prop2 = prop2;
    }

    @Override
    public String toString() {
        return "A{" +
                "prop1='" + prop1 + '\'' +
                ", prop2=" + prop2 +
                '}';
    }
}
