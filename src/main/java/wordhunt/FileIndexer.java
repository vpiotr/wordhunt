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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;


/**
 * Class which builds index file
 *
 * @author piotr
 */
public class FileIndexer {

    private final SearchConfig config;
    private final String dirName;
    private final FileTypeDetector detector;
    private final IndexEntryWriter entryWriter;
    private final IndexStorage indexStorage;
    private final DocumentStorage documentStorage;

    public FileIndexer(SearchConfig config, String dirName, FileTypeDetector detector, IndexEntryWriter entryWriter, IndexStorage indexStorage, DocumentStorage documentStorage) {
        this.config = config;
        this.dirName = dirName;
        this.detector = detector;
        this.entryWriter = entryWriter;
        this.indexStorage = indexStorage;
        this.documentStorage = documentStorage;
    }

    public void rebuildIndex() {
        if (indexExists()) {
            removeIndex();
        }
        buildIndex();
    }

    public void buildIndex() {
        if (!indexStorage.createNewIndex(getIndexAbsolutePath())) {
            throw new SearchException("Cannot create an index file - already exists: [" + getIndexAbsolutePath() + "]");
        }

        BufferedWriter bw = null;
        Writer writer = null;

        try {
            String fileName = getIndexAbsolutePath();

            writer = indexStorage.getWriterForIndexFile(fileName);
            bw = new BufferedWriter(writer);

            buildIndex(bw);

            show("Index created");

        } catch (IOException ioe) {
            throw new SearchException("IO error: " + ioe.getMessage(), ioe);
        } finally {

            try {

                if (bw != null) {
                    bw.close();
                }

                if (writer != null) {
                    writer.close();
                }

            } catch (IOException cex) {
                throw new SearchException("IO error: " + cex.getMessage(), cex);
            }
        }
    }


    private void buildIndex(final BufferedWriter writer) throws IOException {
        FileWalker walker = new FileWalker();

        writeMeta(writer, IndexConst.META_SOURCE_PATH, FilePathUtils.toCanonicalPath(dirName));
        final String indexPathTxt = getIndexAbsolutePath();

        walker.walk(dirName, (absolutePath, isDirectory) -> {

            if (documentStorage.isSameDocumentPath(absolutePath, indexPathTxt)) {
                return;
            }

            FileType fileType = detector.detectFileType(absolutePath);
            if (fileType == null) {
                fileType = FileType.UNKNOWN_FILE_TYPE;
            }

            FoundDocument entry = new FoundDocument(absolutePath, isDirectory, fileType.getMimeType(), fileType.getCharsetName());
            entryWriter.writeEntry(writer, entry);
        });

    }

    private void writeMeta(final BufferedWriter writer, String propertyName, String propertyValue) throws IOException {
        writeComment(writer, propertyName + IndexConst.META_SEPARATOR + propertyValue);
    }

    private void writeComment(final BufferedWriter writer, String line) throws IOException {
        writer.write(IndexConst.COMMENT_PREFIX + line + "\n");
    }

    public void removeIndex() {
        indexStorage.removeIndex(getIndexAbsolutePath());
    }

    public boolean indexExists() {
        return indexStorage.indexExists(getIndexAbsolutePath());
    }

    private String getIndexAbsolutePath() {
        return (String) config.getValue(SearchConst.CFG_INDEX_FILE_PATH);
    }

    private void show(String message) {
        System.out.println(message);
    }
}
