package io.dgawlik;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class EnvironmentTest {

    @Test
    @DisplayName("Should follow predecence of sources and return null if no match for property")
    public void test1(){
        var env = new Environment()
                .withClasspathFile("/1.properties")
                .withMap(Map.of("a.b", "world"));

        Assertions.assertEquals("hello", env.getProperty("a.a"));
        Assertions.assertEquals("world", env.getProperty("a.b"));
        Assertions.assertNull(env.getProperty("a.c"));
    }
}
