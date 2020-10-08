package wordhunt;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class FilePathUtils {
    private FilePathUtils() {}

    /**
     * Build path from directory and file names
     *
     * @param dirName directory name
     * @param fileName file name
     * @return full path to file inside directory
     */
    public static String buildFilePath(String dirName, String fileName) {
        return dirName + File.separator + fileName;
    }

    /**
     * Extract file name from file path
     *
     * @param filePath path to extract file name from
     * @return last part of path
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
            return new File(filePath).getCanonicalPath();
        } catch (IOException ioe) {
            throw new SearchException("IO error: " + ioe.getMessage(), ioe);
        }
    }

    /**
     * Convert absolute path to relative path
     * @param absolutePath
     * @param rootDir
     * @return
     */
    public static String absoluteToRelativePath(String absolutePath, String rootDir) {
        return new File(rootDir).toURI().relativize(new File(absolutePath).toURI()).getPath();
    }

}
