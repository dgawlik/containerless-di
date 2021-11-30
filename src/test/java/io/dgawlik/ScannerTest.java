package io.dgawlik;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;

public class ScannerTest {

    @Test
    @DisplayName("Scanner should detect all classes in package with empty constructor or annotated with @Inject")
    public void test1(){
        var scanner = new Scanner("io.dgawlik.scratchpad");

        var result = scanner.search();

        var classes = result.stream()
                .map(def -> def.getDeclaringClass().getSimpleName())
                .collect(Collectors.toSet());

        Assertions.assertTrue(classes.contains("A"));
        Assertions.assertTrue(classes.contains("B"));
        Assertions.assertTrue(classes.contains("C"));
    }

}
