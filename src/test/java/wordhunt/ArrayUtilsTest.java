package wordhunt;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ArrayUtilsTest {

    @Test
    void mergeOfBothNonEmpty() {
        Integer [] arr1 = new Integer[] {1,3,5};
        Integer [] arr2 = new Integer[] {4,5,7};
        Integer [] expected = new Integer[] {1,3,5,4,5,7};
        Integer [] actual = ArrayUtils.merge(arr1, arr2);
        assertArrayEquals(expected, actual);
    }

    @Test
    void mergeOfBothEmpty() {
        Integer [] arr1 = new Integer[0];
        Integer [] arr2 = new Integer[0];
        Integer [] expected = new Integer[0];
        Integer [] actual = ArrayUtils.merge(arr1, arr2);
        assertArrayEquals(expected, actual);
    }

    @Test
    void mergeOfFilledAndEmpty() {
        Integer [] arr1 = new Integer[0];
        Integer [] arr2 = new Integer[] {4,5,7};
        Integer [] expected = new Integer[] {4,5,7};
        Integer [] actual = ArrayUtils.merge(arr1, arr2);
        assertArrayEquals(expected, actual);
    }

    @Test
    void mergeOfFilledAndNull1() {
        Integer [] arr2 = new Integer[] {4,5,7};
        Integer [] expected = new Integer[] {4,5,7};
        Integer [] actual = ArrayUtils.merge(null, arr2);
        assertArrayEquals(expected, actual);
    }

    @Test
    void mergeOfFilledAndNull2() {
        Integer [] arr1 = new Integer[] {4,5,7};
        Integer [] expected = new Integer[] {4,5,7};
        Integer [] actual = ArrayUtils.merge(arr1, null);
        assertArrayEquals(expected, actual);
    }

    @Test
    void mergeOfNullAndEmpty() {
        Integer [] arr2 = new Integer[0];
        Integer [] expected = new Integer[0];
        Integer [] actual = ArrayUtils.merge(null, arr2);
        assertArrayEquals(expected, actual);
    }
    
    @Test
    void mergeOfEmptyAndNull() {
        Integer [] arr1 = new Integer[0];
        Integer [] expected = new Integer[0];
        Integer [] actual = ArrayUtils.merge(arr1, null);
        assertArrayEquals(expected, actual);
    }
    
    @Test
    void mergeOfBothNull() {
        Integer [] expected = null;
        Integer [] actual = ArrayUtils.merge(null, null);
        assertArrayEquals(expected, actual);
    }    
}
