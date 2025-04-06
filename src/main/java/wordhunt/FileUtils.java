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
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * File-related utility functions.
 *
 * @author piotr
 */
public final class FileUtils {

    /**
     * Read part of file as byte array.
     *
     * @param file
     * @param maxSampleLength
     * @return
     */
    public static byte[] readFileSampleIntoByteArray(File file, int maxSampleLength) {
        var targetLen = maxSampleLength == 0 || file.length() < maxSampleLength
            ? (int) file.length() 
            : maxSampleLength;

        var bFile = new byte[targetLen];
        int bytesRead;

        try (var fileInputStream = new FileInputStream(file)) {
            bytesRead = fileInputStream.read(bFile);
        } catch (IOException ioe) {
            throw new SearchException("IO error: " + ioe.getMessage(), ioe);
        } catch (Exception e) {
            throw new SearchException("Unknown error: " + e.getMessage(), e);
        }

        if (bytesRead == -1) {
            return new byte[0];
        } else if (bytesRead == targetLen) {
            return bFile;
        } else if (bytesRead >= 0) {
            return Arrays.copyOf(bFile, bytesRead);
        } else {
            return new byte[0];
        }
    }

    private FileUtils() {
    }
}
