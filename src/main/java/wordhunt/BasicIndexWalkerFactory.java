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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Creates default index walker object.
 *
 * @author piotr
 */
public class BasicIndexWalkerFactory implements IndexWalkerFactory {

    @Override
    public IndexWalker newWalker(String indexFilePath) {
        try {
            return new BasicIndexWalker(
                    Files.newBufferedReader(Paths.get(indexFilePath), StandardCharsets.UTF_8)
            );

        } catch (FileNotFoundException fnfe) {
            throw new SearchException("Index file not found: " + indexFilePath, fnfe);
        } catch (IOException ioe) {
            throw new SearchException("IO error while reading index: " + indexFilePath, ioe);
        }
    }

}
