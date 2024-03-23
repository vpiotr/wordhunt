package wordhunt;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class FileUtilsTest {

    @Test
    void testReadFileSampleIntoByteArray_SmallerThanMaxSampleLength() throws IOException {
        Path tempFile = Files.createTempFile("temp", ".txt");
        Files.write(tempFile, "Hello".getBytes());
        byte[] result = FileUtils.readFileSampleIntoByteArray(tempFile.toFile(), 10);
        assertArrayEquals("Hello".getBytes(), result);
    }

    @Test
    void testReadFileSampleIntoByteArray_LargerThanMaxSampleLength() throws IOException {
        Path tempFile = Files.createTempFile("temp", ".txt");
        Files.write(tempFile, "Hello World".getBytes());
        byte[] result = FileUtils.readFileSampleIntoByteArray(tempFile.toFile(), 5);
        assertArrayEquals("Hello".getBytes(), result);
    }

    @Test
    void testReadFileSampleIntoByteArray_FileNotFound() {
        File nonExistentFile = new File("non_existent_file.txt");
        assertThrows(SearchException.class, () -> FileUtils.readFileSampleIntoByteArray(nonExistentFile, 10));
    }

    @Test
    void testReadFileSampleIntoByteArray_ZeroMaxSampleLength() throws IOException {
        Path tempFile = Files.createTempFile("temp", ".txt");
        Files.write(tempFile, "Hello".getBytes());
        byte[] result = FileUtils.readFileSampleIntoByteArray(tempFile.toFile(), 0);
        assertArrayEquals("Hello".getBytes(), result);
    }
}