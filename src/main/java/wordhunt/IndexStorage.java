package wordhunt;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Abstraction layer for I/O operations on index files
 */
public class IndexStorage {
    public BufferedReader getReaderForIndexFile(String indexFilePath) {
        try {
            return Files.newBufferedReader(Paths.get(indexFilePath), StandardCharsets.UTF_8);
        } catch (FileNotFoundException fnfe) {
            throw new IndexAccessException("Index file not found: " + indexFilePath, fnfe);
        } catch (IOException ioe) {
            throw new IndexAccessException("IO error while reading index: " + indexFilePath, ioe);
        }
    }

    public OutputStreamWriter getWriterForIndexFile(String indexFilePath) {
        try {
            return new OutputStreamWriter(new FileOutputStream(indexFilePath), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IndexAccessException(e);
        } catch (FileNotFoundException e) {
            throw new IndexAccessException(e);
        }
    }

    public boolean createNewIndex(String indexAbsolutePath) {
        try {
            return new File(indexAbsolutePath).createNewFile();
        } catch (IOException e) {
            throw new IndexAccessException("Index creation failed, path: " + indexAbsolutePath, e);
        }
    }

    public void removeIndex(String indexAbsolutePath) {
        new File(indexAbsolutePath).delete();
    }

    public boolean indexExists(String indexAbsolutePath) {
        return new File(indexAbsolutePath).exists();
    }
}
