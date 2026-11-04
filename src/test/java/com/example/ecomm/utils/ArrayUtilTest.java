package com.example.ecomm;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ArrayUtilTest {
    @ParameterizedTest
    @CsvSource({"0, 10", "1, 20", "2,30"})
//    @DisplayName("middle slot: returns the element")
    void getElementByIndex_firstSlot_returnsElement(int index, int expected) {
        int[] shelf = {10, 20, 30};

        int result = ArrayUtil.getElementByIndex(shelf, index);

        assertThat(result).isEqualTo(expected);
    }

    @Test
//    @DisplayName("index equal to length: throws")
    void getElementByIndex_indexEqualToLength_throws() {
        int[] shelf = {10, 20, 30};

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> ArrayUtil.getElementByIndex(shelf, 3));
    }
}