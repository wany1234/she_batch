package com.batch.utils;

public class Methods {

    /**
     * 좌측문자추가
     *
     * @param s
     * @param len
     * @param append
     * @return
     */
    public static String lpad(String s, int len, String append) {
        String result = s;
        int loop = len - result.length();
        for (int i = 0; i < loop; i++) {
            result = append + result;
        }

        return result;
    }

    /**
     * 우측문자추가
     *
     * @param s
     * @param len
     * @param append
     * @return
     */
    public static String rpad(String s, int len, String append) {
        String result = s;
        int loop = len - result.length();
        for (int i = 0; i < loop; i++) {
            result = result + append;
        }

        return result;
    }

}
