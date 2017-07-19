package utils;


import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;


public class ConnectionIdTest {
    @Test
    public void testSequencing() {
        assertEquals("AA", getFromNumber(27));

    }

    private String getFromNumber(int value) {

        StringBuilder result = new StringBuilder();
        while (--value >= 0) {
            result.insert(0, (char) ('A' + value % 26));
            value /= 26;
        }
        return result.toString();
    }
}
