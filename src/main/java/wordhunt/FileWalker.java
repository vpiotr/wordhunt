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

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Performs directory walking using NIO Files API
 *
 * @author piotr
 */
public class FileWalker {

    public void walk(String dirName, FileVisitor visitor) {
        try {
            var path = Paths.get(dirName);
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    visitor.handleItem(file.toAbsolutePath().toString(), false);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
                    visitor.handleItem(dir.toAbsolutePath().toString(), true);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new SearchException("Error walking directory: " + dirName, e);
        }
    }
}
