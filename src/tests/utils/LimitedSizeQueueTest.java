package utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LimitedSizeQueueTest {
    @Test
    void checkOverFlow() {
        int[] array = new int[]{1, 2, 3, 4};
        LimitedSizeQueue<Integer> queue = new LimitedSizeQueue<>(2);

        for (int anArray : array) {
            queue.add(anArray);
        }

        int oldest = queue.getOldest();
        int youngest = queue.getYongest();

        assertTrue(oldest == array[2]);
        assertTrue(youngest == array[3]);
    }

    @Test
    void checkNormal() {
        int[] array = new int[]{1, 2, 3, 4};
        LimitedSizeQueue<Integer> queue = new LimitedSizeQueue<>(4);

        for (int anArray : array) {
            queue.add(anArray);
        }
        int oldest = queue.getOldest();
        int youngest = queue.getYongest();

        assertTrue(oldest == array[0]);
        assertTrue(youngest == array[array.length - 1]);
    }

}