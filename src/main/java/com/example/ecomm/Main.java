package com.example.ecomm;


import com.example.ecomm.utils.ArrayUtil;

public class Main {

    public static void main(String[] args) {
        // Arrange
        int[] shelf = {10, 20, 30};


        // Act
        if (ArrayUtil.getElementByIndex(shelf, 1) == 20) {
            System.out.println("PASS: middle slot"); // assert
        } else {
            System.out.println("FAIL: middle slot");
        }

        try {
            ArrayUtil.getElementByIndex(shelf, 3);
            System.out.println("FAIL: slot 3 should be refused");
        } catch (IllegalArgumentException e) {
            System.out.println("PASS: slot 3 refused");
        }
    }
}
