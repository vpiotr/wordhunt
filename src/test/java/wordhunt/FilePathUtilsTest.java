package wordhunt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

public class FilePathUtilsTest {
    @Test
    void buildFilePath() {
        String dirName = "/home/john";
        String fileName = "test.txt";
        String expected = "/home/john" + File.separator + "test.txt";
        String actual = FilePathUtils.buildFilePath(dirName, fileName);
        assertEquals(expected, actual);
    }

    @Test
    void extractFileName() {
        String filePath = "/home/john/test.txt";
        String expected = "test.txt";
        String actual = FilePathUtils.extractFileName(filePath);
        assertEquals(expected, actual);
    }

    @Test
    void toAbsolutePath() {
        String rootPath = "/home/john";
        String fileNameRelative = "test.txt";
        String expected = Paths.get(rootPath, fileNameRelative).toAbsolutePath().normalize().toString();
        String actual = FilePathUtils.toAbsolutePath(rootPath, fileNameRelative);
        assertEquals(expected, actual);
    }

    @Test
    void toCanonicalPath() {
        String rootPath = "/home/john";
        String fileNameRelative = "test.txt";
        String expected = new File(rootPath, fileNameRelative).getAbsolutePath();
        try {
            expected = new File(expected).getCanonicalPath();
        } catch (IOException e) {
            fail("Unexpected IOException");
        }
        String actual = FilePathUtils.toCanonicalPath(rootPath, fileNameRelative);
        assertEquals(expected, actual);
    }

    @Test
    void absoluteToRelativePath() {
        String absolutePath = "/home/john/test.txt";
        String rootDir = "/home/john";
        String expected = "test.txt";
        String actual = FilePathUtils.absoluteToRelativePath(absolutePath, rootDir);
        assertEquals(expected, actual);
    }    
}
