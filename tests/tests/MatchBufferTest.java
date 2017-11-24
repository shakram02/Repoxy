package utils;

import org.junit.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Objects;


class MatchBufferTest {

    @Test
    void testBasicFunctionality() {

        MatchBuffer<Integer> integerMatchBuffer = new MatchBuffer<>(Objects::equals);

        assertFalse(integerMatchBuffer.addIfMatchNotFound(5).isPresent());
        assertTrue(integerMatchBuffer.addIfMatchNotFound(5).isPresent());
    }

}