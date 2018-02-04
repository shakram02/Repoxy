package utils;

/**
 * This utility class is created as {@link System#currentTimeMillis()} doesn't always
 * return a consistent time, which leads to errors basically in tests, I don't need
 * that extra fuss, so since {@link System#nanoTime()} is consistent, it'll be used
 * here
 */
public class MonotonicClock {
    public static long getTimeMillis() {
        return System.nanoTime() / 1000000L;
    }

    public static long getTimeNano() {
        return System.nanoTime();
    }
}
