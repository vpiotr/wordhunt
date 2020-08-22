package wordhunt;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CharsetUtilsTest {

    @Test
    void isValidUtf8ForNull() {
        assertThrows(NullPointerException.class, () -> isValidUtf8(null));
    }

    private boolean isValidUtf8(byte[] bytes) {
        return CharsetUtils.isUtf8CharsetName(CharsetUtils.detectCharsetName(bytes));
    }

    @Test
    void isValidUtf8ForEmpty() {
        byte[] input = new byte[0];
        assertTrue(isValidUtf8(input));
    }

    @Test
    void isValidUtf8ForBlank() {
        byte[] input = "  ".getBytes();
        assertTrue(isValidUtf8(input));
    }

    @Test
    void isValidUtf8ForValid() {
        byte[] input = new byte[] {65, 49, 50, -60, -123, -61, -77, -59, -126, 104};
        assertTrue(isValidUtf8(input));
    }

    @Test
    void isValidUtf8ForInvalid() {
        byte[] input = new byte[] {65, 49, 50, -123, -123, -123, 104};
        assertFalse(isValidUtf8(input));
    }
}