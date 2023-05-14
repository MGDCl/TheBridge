package io.github.MGDCl.TheBridge.utils;

public class MathUtils {

    public static int toMegaByte(long bytes) {
        return (int) (bytes / 1048576);
    }

    public static boolean isInt(String str) {
        try {
            int d = Integer.parseInt(str);
            d = d + 1;
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

}
