/*
Copyright 2017 Piotr Likus

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package wordhunt;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Allows scanning of index file in iterator-like way. 
 * @author piotr
 */
public class BasicIndexWalker implements IndexWalker, AutoCloseable {

    private static final int FIELD_INDEX_DIR_PREFIX = 0;
    private static final int FIELD_INDEX_RELATIVE_PATH = 1;
    private static final int FIELD_INDEX_MIME_TYPE = 2;
    private static final int FIELD_INDEX_CHARSET_NAME = 3;
    private final BufferedReader reader;

    private String bufferedLine;

    public BasicIndexWalker(BufferedReader reader) {
        this.reader = reader;
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }

    @Override
    public FoundDocument next() {
        String line;

        do {
            line = nextLine();
        } while ((line != null) && line.startsWith(IndexConst.COMMENT_PREFIX));

        if (line != null) {
            return parseEntryLine(line);
        } else {
            return null;
        }

    }

    @Override
    public String[] nextMeta() {
        String line = nextLine();
        String[] result = null;

        if ((line != null) && line.startsWith(IndexConst.COMMENT_PREFIX)) {
            String metaLine = line.substring(IndexConst.COMMENT_PREFIX.length());
            if (metaLine.contains(IndexConst.META_SEPARATOR)) {
                result = metaLine.split(IndexConst.META_SEPARATOR);
            }
        }

        if (result == null) {
            bufferedLine = line;
        }

        return result;
    }

    private String nextLine() {
        String line;

        if (bufferedLine != null) {
            line = bufferedLine;
            bufferedLine = null;
            return line;
        }

        try {
            line = reader.readLine();
        } catch (IOException ioe) {
            throw new SearchException("IO error: " + ioe.getMessage(), ioe);
        }

        return line;
    }

    private FoundDocument parseEntryLine(String line) {
        String[] parts = line.split("\\" + IndexConst.ENTRY_FIELD_SEPARATOR);

        boolean isDir = (parts.length > FIELD_INDEX_DIR_PREFIX) && parts[FIELD_INDEX_DIR_PREFIX].equals(IndexConst.DIR_PREFIX);
        String relativePath = (parts.length > FIELD_INDEX_RELATIVE_PATH) ? parts[FIELD_INDEX_RELATIVE_PATH] : ".";
        String mimeType = (parts.length > FIELD_INDEX_MIME_TYPE) ? parts[FIELD_INDEX_MIME_TYPE] : "";
        String charsetName = (parts.length > FIELD_INDEX_CHARSET_NAME) ? parts[FIELD_INDEX_CHARSET_NAME] : "";

        return new FoundDocument(relativePath, isDir, mimeType, charsetName);
    }
}
