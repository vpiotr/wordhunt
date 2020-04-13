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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.File;

/**
 * Basic MIME type detector for a given file.
 *
 * @author piotr
 */
public class TextFileTypeDetector implements FileTypeDetector {

    private final FileTypeDetector nextDetector;
    private final static int DEFAULT_SAMPLE_LEN = 5000;
    private final int maxSampleLength;

    public TextFileTypeDetector(FileTypeDetector nextDetector) {
        this.nextDetector = nextDetector;
        this.maxSampleLength = DEFAULT_SAMPLE_LEN;
    }

    public TextFileTypeDetector() {
        this(null);
    }

    @Override
    public FileType detectFileType(String absolutePath) {
        Path path = Paths.get(absolutePath);
        if (!Files.isReadable(path)) {
            return detectByNext(absolutePath);
        }

        File file = new File(absolutePath);

        if (!file.isDirectory()) {
            byte[] data = FileUtils.readFileSampleIntoByteArray(file, this.maxSampleLength);

            if (isPlainTextData(data)) {
                String mimeType = MimeConst.PLAIN_TEXT;
                String charsetName = "";
                if (CharsetUtils.isValidUtf8(data)) {
                    charsetName = CharsetUtils.CHARSET_UTF8;
                } else {
                    charsetName = CharsetUtils.DEFAULT_CHARSET;
                }
                return new FileType(mimeType, charsetName);
            }
        }

        return detectByNext(absolutePath);
    }

    private boolean isPlainTextData(byte[] data) {
        boolean result = true;

        int b;

        loop:
        for (int i = 0; i < data.length; i++) {
            b = data[i] & 0xFF;
            if (b < 32) {
                switch (b) {
                    case 9:
                    case 10:
                    case 13:
                        continue;
                    default:
                        result = false;
                        break loop;
                } // switch
            } // if
        } // for

        return result;
    }

    private FileType detectByNext(String absolutePath) {
        if (nextDetector != null) {
            return nextDetector.detectFileType(absolutePath);
        } else {
            return null;
        }
    }

}
