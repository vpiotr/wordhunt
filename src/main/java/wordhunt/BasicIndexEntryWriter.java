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

import java.io.File;
import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Writes entries to index file.
 * @author piotr
 */
public class BasicIndexEntryWriter implements IndexEntryWriter {

    private final File rootFile;

    public BasicIndexEntryWriter(String rootDir) {
        this.rootFile = new File(rootDir);
    }

    @Override
    public void writeEntry(BufferedWriter target, IndexEntry entry) {
        StringBuilder builder = new StringBuilder();

        if (entry.isDirectory()) {
            builder.append(IndexConst.DIR_PREFIX);
        } else {
            builder.append(IndexConst.FILE_PREFIX);
        }

        builder.append(IndexConst.ENTRY_FIELD_SEPARATOR);

        String filePath = entry.getFilePath();
        String relativePath = rootFile.toURI().relativize(new File(filePath).toURI()).getPath();
        builder.append(relativePath);
        builder.append(IndexConst.ENTRY_FIELD_SEPARATOR);

        builder.append(entry.getMimeType());
        builder.append(IndexConst.ENTRY_FIELD_SEPARATOR);

        builder.append(entry.getCharsetName());
        builder.append(IndexConst.ENTRY_FIELD_SEPARATOR);

        builder.append(IndexConst.ENTRY_SEPARATOR);

        try {
            target.write(builder.toString());
        } catch (IOException ioe) {
            throw new SearchException("IO error: " + ioe.getMessage(), ioe);
        }

    }

}
