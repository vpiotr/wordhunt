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

/**
 * Performs directory walking
 *
 * @author piotr
 */
public class FileWalker {

    //TODO: (some day) refactor to Files.walkFileTree()
    public void walk(String dirName, FileVisitor visitor) {

        File root = new File(dirName);
        File[] list = root.listFiles();

        if (list == null) {
            return;
        }

        for (File f : list) {
            if (f.isDirectory()) {
                walk(f.getAbsolutePath(), visitor);
                visitor.handleItem(f.getAbsolutePath(), true);
            } else {
                visitor.handleItem(f.getAbsolutePath(), false);
            }
        }

    }
}
