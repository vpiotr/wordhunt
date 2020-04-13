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
import java.nio.file.Paths;
import java.nio.file.Path;
import java.io.IOException;

import java.util.Arrays;

/**
 * File-related utility functions.
 *
 * @author piotr
 */
public class FileUtils {

    /**
     * Build path from directory and file names
     *
     * @param dirName
     * @param fileName
     * @return
     */
    public static String buildFilePath(String dirName, String fileName) {
        return dirName + File.separator + fileName;
    }

    /**
     * Extract file name from file path
     *
     * @param filePath
     * @return
     */
    public static String extractFileName(String filePath) {
        return new File(filePath).getName();
    }

    /**
     * Extract file directory from file path
     *
     * @param filePath
     * @return directory path
     */
    public static String extractFileDir(String filePath) {
        return new File(filePath).getParent();
    }

    /**
     * Build full path from root directory and maybe relative file path
     *
     * @param rootPath
     * @param fileNameRelative
     * @return
     */
    public static String toAbsolutePath(String rootPath, String fileNameRelative) {
        Path path = Paths.get(fileNameRelative);
        Path effectivePath = path;
        if (!path.isAbsolute()) {
            Path base = Paths.get(rootPath);
            effectivePath = base.resolve(path).toAbsolutePath();
        }
        return effectivePath.normalize().toString();
    }

    /**
     * Silent version of getCanonicalPath with path building.
     *
     * @param rootPath
     * @param fileNameRelative
     * @return Canonical file path.
     */
    public static String toCanonicalPath(String rootPath, String fileNameRelative) {
        return toCanonicalPath(toAbsolutePath(rootPath, fileNameRelative));
    }

    /**
     * Silent version of getCanonicalPath with path building.
     *
     * @param filePath
     * @return Canonical file path.
     */
    public static String toCanonicalPath(String filePath) {
        try {
            return new File((String) filePath).getCanonicalPath();
        } catch (IOException ioe) {
            throw new SearchException("IO error: " + ioe.getMessage(), ioe);
        }
    }

    /**
     * Read part of file as byte array.
     *
     * @param file
     * @param maxSampleLength
     * @return
     */
    public static byte[] readFileSampleIntoByteArray(File file, int maxSampleLength) {
        int targetLen = maxSampleLength;
        if (targetLen == 0 || file.length() < targetLen) {
            targetLen = (int) file.length();
        }

        byte[] bFile = new byte[targetLen];
        int bytesRead = 0;

        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            bytesRead = fileInputStream.read(bFile);
        } catch (IOException ioe) {
            throw new SearchException("IO error: " + ioe.getMessage(), ioe);
        } catch (Exception e) {
            throw new SearchException("Unknown error: " + e.getMessage(), e);
        }

        if (bytesRead == targetLen) {
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
