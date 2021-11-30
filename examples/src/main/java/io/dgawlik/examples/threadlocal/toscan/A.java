package io.dgawlik.examples.threadlocal.toscan;

public class A {
    private static int instanceCounter = 1;

    private final int instanceNo;

    public A() {
        instanceNo = instanceCounter++;
    }

    @Override
    public String toString() {
        return "A{" +
                "instanceNo=" + instanceNo + "," +
                "thread=" + Thread.currentThread().getName() +
                '}';
    }
}
