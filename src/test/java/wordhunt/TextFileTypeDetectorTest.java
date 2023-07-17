package wordhunt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TextFileTypeDetectorTest {

    private TextFileTypeDetector uut;

    @BeforeEach
    void setup() {
        uut = new TextFileTypeDetector();
    }

    @Test
    void shouldDetectAsciiBlockAsPlainText() {
        String testValue = "abc";
        boolean actualResult = uut.isPlainTextData(testValue.getBytes());
        assertTrue(actualResult);
    }

    @Test
    void shouldDetectUtf8AsPlainText() {
        String testValue = "ąbćdęfł!@$^&\uD83D\uDE00";
        boolean actualResult = uut.isPlainTextData(testValue.getBytes());
        assertTrue(actualResult);
    }

    @Test
    void shouldDetectInvalidUtf8AsUnknown() {
        String testValue = "abc\u0001\u001f";
        boolean actualResult = uut.isPlainTextData(testValue.getBytes());
        assertFalse(actualResult);
    }

    @Test
    void shouldDetectEmptyBufferAsPlainText() {
        String testValue = "";
        boolean actualResult = uut.isPlainTextData(testValue.getBytes());
        assertTrue(actualResult);
    }

}