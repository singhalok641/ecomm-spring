package com.example.ecomm;

public class ArrayUtil {

    public static int getElementByIndex(int[] a, int index) {
        if (a == null || index < 0 || index > a.length) {
            throw new IllegalArgumentException("Bad index: " + index);
        }
        return a[index];
    }
}