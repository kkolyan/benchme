package net.kkolyan.utils.benchme.util;

/**
 * @author n.plekhanov
 */
public class StringLengthUtil {
    private int maxLength = 0;

    public StringLengthUtil() {
    }

    public StringLengthUtil(int maxLength) {
        this.maxLength = maxLength;
    }

    public String ensureLength(String s) {
        if (s.length() > maxLength) {
            maxLength = s.length();
            return s;
        }
        if (s.length() == maxLength) {
            return s;
        }
        StringBuilder sb = new StringBuilder(s);
        for (int i = s.length(); i < maxLength; i ++) {
            sb.append(' ');
        }
        return sb.toString();
    }
}
