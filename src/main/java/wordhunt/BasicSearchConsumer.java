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

/**
 * Displays found files on screen.
 * @author piotr
 */
public class BasicSearchConsumer implements SearchConsumer {

    private final boolean formatAsListing;
    private final DocumentStorage documentStorage;

    public BasicSearchConsumer(SearchConfig config, DocumentStorage documentStorage) {
        this.formatAsListing = Boolean.TRUE.equals(config.getValue(SearchConst.CFG_SEARCH_BRIEF));
        this.documentStorage = documentStorage;
    }

    @Override
    public void handle(String absolutePath) {
        if (documentStorage.documentExists(absolutePath)) {
            if (formatAsListing) {
                System.out.println(absolutePath);
            } else {
                System.out.println("Found: " + absolutePath);
            }
        }
    }
}
