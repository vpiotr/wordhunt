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
import java.io.IOException;

/**
 * Performs search using provided terms and index file.
 *
 * @author piotr
 */
public class IndexedDocumentSearcher implements DocumentSearcher {

    private final SearchConfig config;
    private final IndexWalkerFactory indexWalkerFactory;
    private final DocumentStorage documentStorage;

    public IndexedDocumentSearcher(SearchConfig config, IndexWalkerFactory indexWalkerFactory, DocumentStorage documentStorage) {
        this.config = config;
        this.indexWalkerFactory = indexWalkerFactory;
        this.documentStorage = documentStorage;
    }

    @Override
    public void search(SearchTerms terms, SearchMatcher matcher, SearchConsumer consumer) {
        String indexFile = getIndexAbsolutePath();
        SearchContext context = newSearchContext();

        boolean list = Boolean.TRUE.equals((Boolean) config.getValue(SearchConst.CFG_SEARCH_BRIEF));
        if (!list) {
            System.out.println("Searching in index file: " + indexFile);
        }

        matcher.prepare(terms, context);

        try (IndexWalker walker = indexWalkerFactory.newWalker(indexFile)) {
            FoundDocument entry;

            String[] sourcePath = walker.nextMeta();
            if (sourcePath != null && sourcePath.length > 1) {
                processMeta(sourcePath[0], sourcePath[1]);
            }

            while ((entry = walker.next()) != null) {
                processIndexEntry(entry, matcher, consumer, context);
            }
        } catch (IOException ioe) {
            throw new SearchException("IO error: " + ioe.getMessage(), ioe);
        } catch (SearchException se) {
            throw se;
        } catch (Exception e) {
            throw new SearchException("Unknown error: " + e.getMessage(), e);
        }
    }

    protected SearchContext newSearchContext() {
        return new SearchContext();
    }

    private void processMeta(String metaName, String metaValue) {
        if (metaName.equals(IndexConst.META_SOURCE_PATH)) {
            validateSourcePathFromMeta(metaValue);
        }
    }

    private void validateSourcePathFromMeta(String sourcePathInMeta) {
        String currentDir = getSearchRootDir();
        if (!documentStorage.isSameDocumentPath(sourcePathInMeta, currentDir)) {
            throw new SearchException(String.format("Invalid search directory - not compatible with used index (search directory: [%s], index built for directory: [%s])",
                    currentDir, sourcePathInMeta));
        }
    }

    private void processIndexEntry(FoundDocument entry, SearchMatcher matcher, SearchConsumer consumer, SearchContext context) {

        if (Boolean.TRUE.equals(matcher.isMatching(entry, context, null))) {
            consumer.handle(
                    new File(
                            FilePathUtils.toCanonicalPath(
                                    getSearchRootDir(),
                                    entry.getFilePath()
                            )
                    ).getAbsolutePath()
            );
        }
    }

    private String getIndexAbsolutePath() {
        return (String) config.getValue(SearchConst.CFG_INDEX_FILE_PATH);
    }

    private String getSearchRootDir() {
        return (String) config.getValue(SearchConst.CFG_SEARCH_ROOT_DIR);
    }

}
