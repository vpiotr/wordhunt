package wordhunt;

import java.io.BufferedReader;
import java.nio.charset.Charset;

/**
 * Abstract document storage.
 */
public interface DocumentStorage {
    DocumentInfo getDocumentInfo(String absolutePath);
    BufferedReader getDocumentReader(String absolutePath, Charset charset);
    boolean isSameDocumentPath(String path1, String path2);
    boolean isDirectory(String filePath);
    boolean documentExists(String absolutePath);
}
