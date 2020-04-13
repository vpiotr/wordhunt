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
    public IndexEntry next() {
        String line = null;

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
            String metaLine = line.substring(IndexConst.COMMENT_PREFIX.length(), line.length());
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
        String line = null;

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

    private IndexEntry parseEntryLine(String line) {
        String[] parts = line.split("\\" + IndexConst.ENTRY_FIELD_SEPARATOR);

        boolean isDir = (parts.length >= 1) ? parts[0].equals(IndexConst.DIR_PREFIX) : false;
        String relativePath = (parts.length >= 2) ? parts[1] : ".";
        String mimeType = (parts.length >= 3) ? parts[2] : "";
        String charsetName = (parts.length >= 4) ? parts[3] : "";

        IndexEntry entry = new IndexEntry(relativePath, isDir, mimeType, charsetName);
        return entry;
    }
}
