package tests;

public class TestPackets {
    public static byte[] GetConfigRequestXid23 = new byte[]{1, 7, 0, 8, 0, 0, 0, 23};
    public static byte[] GetConfigRequestXid22 = new byte[]{1, 7, 0, 8, 0, 0, 0, 22};

    public static byte[] GetConfigReplyXid23 = new byte[]{1, 9, 0, 12, 0, 0, 0, 23, 0, 2, 0, 12};

    public static byte[] BarrierRequestXid54 = new byte[]{1, 18, 0, 8, 0, 0, 0, 54};
    public static byte[] BarrierRequestXid53 = new byte[]{1, 18, 0, 8, 0, 0, 0, 53};

    public static byte[] BarrierReplyXid54 = new byte[]{1, 19, 0, 8, 0, 0, 0, 54};

    public static byte[] FeaturesRequestXid10 = new byte[]{1, 5, 0, 8, 0, 0, 0, 10};
    public static byte[] FeaturesRequestXid11 = new byte[]{1, 5, 0, 8, 0, 0, 0, 11};
    public static byte[] FeaturesReplyXid10 = new byte[]
            {
                    1, 6, 0, -32, 0, 0, 0, 10, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0,
                    -2, 0, 0, 0, 0, 0, 0, -57, 0, 0, 15, -1, 0, 3, -6, -86, 66, -3,
                    -53, -69, 115, 49, 45, 101, 116, 104, 51, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -64, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 1, 46, 21, 96, -68, -73, 80, 115, 49, 45, 101,
                    116, 104, 49, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, -64, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, -94, 69,
                    28, -61, -71, -83, 115, 49, 45, 101, 116, 104, 50, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -64, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, -1, -2, -6, -122, -94, 82, -111, 68, 115, 49,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
            };
}
