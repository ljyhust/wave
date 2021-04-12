package com.wave.util;

public class HexConvertUtils {
    
    /**
     * 数字转换为36进制
     * @param num
     * @return
     */
    public static String dicTo36Hex(long num) {
        StringBuffer stff = new StringBuffer();
        long divisor = num;
        while (divisor > 0) {
            long remain = divisor % 36;
            if (remain >= 10) {
                stff.append((char) (remain - 10 + 'a'));
            } else {
                stff.append(remain + "");
            }
            divisor = divisor / 36;
        }
        return stff.reverse().toString();
    }
    
    public static long hex36ToDic(String hex) {
        long num = 0;
        int length = hex.length();
        for (int i = length; i > 0; i--) {
            char c = hex.charAt(i);
            if (c > '9') {
                num += Math.pow(c - 'a', length - i);
            } else {
                num += Math.pow(c - '0', length - i);
            }
        }
        return num;
    }
}
