package io.dgawlik.examples.threadlocal.toscan;

public class B {
    private static int instanceCounter = 1;

    private final int instanceNo;

    public B() {
        instanceNo = instanceCounter++;
    }

    @Override
    public String toString() {
        return "B{" +
                "instanceNo=" + instanceNo + "," +
                "thread=" + Thread.currentThread().getName() +
                '}';
    }
}
