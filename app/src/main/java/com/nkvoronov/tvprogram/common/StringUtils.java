package com.nkvoronov.tvprogram.common;

public class StringUtils {

    public static String parseString(String source, String begin, String end) {
        String res = "";
        try {
            int beginIndex = source.indexOf(begin);
            if (beginIndex == -1) {
                return res;
            }
            int endIndex = source.indexOf(end);
            if (endIndex == -1) {
                return res;
            }
            if (endIndex > beginIndex) {
                return source.substring(beginIndex, endIndex);
            } else {
                return res;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return res;
        }

    }

}
