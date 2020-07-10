package wordhunt;

import java.io.BufferedReader;
import java.io.OutputStreamWriter;

public interface IndexStorage {
    BufferedReader getReaderForIndexFile(String indexFilePath);
    OutputStreamWriter getWriterForIndexFile(String indexFilePath);
    boolean createNewIndex(String indexAbsolutePath);
    void removeIndex(String indexAbsolutePath);
    boolean indexExists(String indexAbsolutePath);
}
