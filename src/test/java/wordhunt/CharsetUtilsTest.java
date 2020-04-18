package wordhunt;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CharsetUtilsTest {

    @Test
    void isValidUtf8ForNull() {
        assertThrows(NullPointerException.class, () -> CharsetUtils.isValidUtf8(null));
    }

    @Test
    void isValidUtf8ForEmpty() {
        byte[] input = new byte[0];
        assertTrue(CharsetUtils.isValidUtf8(input));
    }

    @Test
    void isValidUtf8ForBlank() {
        byte[] input = "  ".getBytes();
        assertTrue(CharsetUtils.isValidUtf8(input));
    }

    @Test
    void isValidUtf8ForValid() {
        byte[] input = new byte[] {65, 49, 50, -60, -123, -61, -77, -59, -126, 104};
        assertTrue(CharsetUtils.isValidUtf8(input));
    }

    @Test
    void isValidUtf8ForInvalid() {
        byte[] input = new byte[] {65, 49, 50, -123, -123, -123, 104};
        assertFalse(CharsetUtils.isValidUtf8(input));
    }
}