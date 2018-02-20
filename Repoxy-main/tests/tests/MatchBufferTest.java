package tests;

import org.junit.Test;
import utils.MatchBuffer;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Objects;


public class MatchBufferTest {

    @Test
    public void testBasicFunctionality() {

        MatchBuffer<Integer> integerMatchBuffer = new MatchBuffer<>(Objects::equals);

        assertFalse(integerMatchBuffer.addIfMatchNotFound(5).isPresent());
        assertTrue(integerMatchBuffer.addIfMatchNotFound(5).isPresent());
    }

}