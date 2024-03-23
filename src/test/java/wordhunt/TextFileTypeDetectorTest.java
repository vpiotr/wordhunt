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

    @Test
    void shouldDetectPlainTextFileType() {
        String testValue = "This is a plain text file.";
        byte[] bytes = testValue.getBytes();
        boolean actualResult = uut.isPlainTextData(bytes);
        assertTrue(actualResult);
    }

    @Test
    void shouldDetectNonPlainTextFileType() {
        byte[] bytes = new byte[] {0, 5, -45, -14, 20, -91, 57, 126, 127, -51};       
        boolean actualResult = uut.isPlainTextData(bytes);
        assertFalse(actualResult);
    }

    @Test
    void shouldDetectTabAsPlainTextFileType() {
        String testValue = "\t";
        boolean actualResult = uut.isPlainTextData(testValue.getBytes());
        assertTrue(actualResult);
    }

    @Test
    void shouldDetectNewLineAsPlainTextFileType() {
        String testValue = "\n";
        boolean actualResult = uut.isPlainTextData(testValue.getBytes());
        assertTrue(actualResult);
    }

    @Test
    void shouldDetectCarriageReturnAsPlainTextFileType() {
        String testValue = "\r";
        boolean actualResult = uut.isPlainTextData(testValue.getBytes());
        assertTrue(actualResult);
    }

    @Test
    void shouldDetectMixedPlainTextFileType() {
        String testValue = "This is a plain text file.\t\n\r";
        boolean actualResult = uut.isPlainTextData(testValue.getBytes());
        assertTrue(actualResult);
    }
}
