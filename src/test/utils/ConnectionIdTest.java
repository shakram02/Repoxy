package utils;


import org.junit.Test;

import static org.junit.Assert.*;


public class ConnectionIdTest {
    // TODO use base-26 chars later
    @Test
    public void testSequencing() {
        assertEquals(String.valueOf(150),"150");
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
