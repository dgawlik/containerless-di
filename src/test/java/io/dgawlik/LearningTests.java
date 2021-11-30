package io.dgawlik;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class LearningTests {
    private static class Box<T> {
        public T element;

        public Box(T element) {
            this.element = element;
        }
    }

    private static class Box2 {
        public Object element;

        public Box2(Object element) {
            this.element = element;
        }
    }

    @Test
    @DisplayName("Class objects are insensitive to generic typing")
    public void test1(){
        var o1 = new Box<>(1);
        var o2 = new Box<>("hello");
        var o3 = new Box2("hello");

        Assertions.assertEquals(o1.getClass(), o2.getClass());
        Assertions.assertNotEquals(o2.getClass(), o3.getClass());
    }
}
