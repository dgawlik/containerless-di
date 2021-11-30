package io.dgawlik;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class StringConvertersTest {

    @Test
    @DisplayName("StringConverter should convert String to numeric")
    public void test1() {
        var r1 = StringConverters.convert("1", Integer.class);
        var r2 = StringConverters.convert("1.0", Double.class);
        var r3 = StringConverters.convert("y", Boolean.class);

        Assertions.assertEquals(1, r1);
        Assertions.assertEquals(1.0, r2);
        Assertions.assertEquals(true, r3);
    }

    @Test
    @DisplayName("StringConverter should throw exception on conversion error")
    public void test2() {
        Assertions.assertThrows(RuntimeException.class, () -> {
            StringConverters.convert("a", Double.class);
        });
    }
}
