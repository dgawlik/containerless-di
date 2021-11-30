package io.dgawlik;

import io.dgawlik.scratchpad.pkg3.*;
import io.dgawlik.scratchpad.pkg4.AA;
import io.dgawlik.scratchpad.pkg4.AB;
import io.dgawlik.scratchpad.pkg4.C;
import io.dgawlik.scratchpad.pkg5.B;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class InjectorTest {

    @Test
    @DisplayName("Injector's root should be validated to have zero-args constructor or @Inject annotation")
    public void test1() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> {
                    Injector.forClass(D.class).assemble();
                });
    }

    @Test
    @DisplayName("In correct setup should assemble object without throwing exception")
    public void test2() {
        var obj = Injector.forClass(E.class).scanning("io.dgawlik.scratchpad")
                .assemble();

        Assertions.assertNotNull(obj.a);
        Assertions.assertNotNull(obj.b);
    }

    @Test
    @DisplayName("Should throw exception for unsatisfied dependency")
    public void test3() {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            Injector.forClass(F.class).scanning("io.dgawlik.scratchpad")
                    .assemble();
        });
    }

    @Test
    @DisplayName("Should inject only if qualifiers match")
    public void test4() {
        G g = Injector.forClass(G.class).scanning("io.dgawlik.scratchpad")
                .assemble();
        Assertions.assertNotNull(g.e);
    }

    @Test
    @DisplayName("If qualifiers don't match should throw exception")
    public void test5() {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            Injector.forClass(H.class).scanning("io.dgawlik.scratchpad")
                    .assemble();
        });
    }

    @Test
    @DisplayName("Should correctly convert and inject @Value params")
    public void test6() {
        var res = Injector.forClass(I.class)
                .scanning("io.dgawlik.scratchpad")
                .environment(new Environment().withClasspathFile("/1.properties"))
                .assemble();

        Assertions.assertEquals("hello", res.a);
        Assertions.assertEquals(12, res.b);
    }

    @Test
    @DisplayName("Should respect priority order while injecting")
    public void test7() {
        var prio = Map.of(AA.class, 2, AB.class, 1);
        var res = Injector.forClass(C.class)
                .scanning("io.dgawlik.scratchpad")
                .priorities(prio)
                .assemble();

        Assertions.assertEquals(AB.class, res.ia.getClass());
    }

    @Test
    @DisplayName("Cache should be able to simulate singleton scoping")
    public void test8() {
        var cache = new Cache();

        var res1 = Injector.forClass(B.class)
                .scanning("io.dgawlik.scratchpad")
                .cache(cache)
                .assemble();

        var res2 = Injector.forClass(B.class)
                .scanning("io.dgawlik.scratchpad")
                .cache(cache)
                .assemble();

        Assertions.assertSame(res1.a, res2.a);
    }


}
