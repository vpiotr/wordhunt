package wordhunt;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Abstraction layer for I/O operations done on documents (files).
 */
public class DocumentStorage {
    public boolean documentExists(String absolutePath) {
        return Paths.get(absolutePath).toFile().exists();
    }

    public DocumentInfo getDocumentInfo(String absolutePath) {
        File entryFile = new File(absolutePath);
        boolean isDirectory = entryFile.isDirectory();
        boolean exists = entryFile.exists();
        boolean canRead = entryFile.canRead();
        return new DocumentInfo(exists, isDirectory, canRead);
    }


    public BufferedReader getDocumentReader(String absolutePath, Charset charset) {
        try {
            return new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(absolutePath), charset));
        } catch (FileNotFoundException e) {
            throw new DocumentAccessException("Document access error, path: " + absolutePath + ", encoding: " + charset.displayName(), e);
        }
    }

    public boolean isSameDocumentPath(String path1, String path2) {
        try {
            return Files.isSameFile(Paths.get(path1), Paths.get(path2));
        } catch (IOException e) {
            throw new DocumentAccessException(e);
        }
    }

    public boolean isDirectory(String filePath) {
        return new File(filePath).isDirectory();
    }
}
