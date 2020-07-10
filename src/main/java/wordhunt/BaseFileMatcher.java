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

import java.util.Set;
import java.util.HashSet;

/**
 * Base class for file matchers.
 *
 * @author piotr
 */
public abstract class BaseFileMatcher implements SearchMatcher {

    private final static String CTX_FILES_TO_IGNORE = "files_to_ignore";

    private SearchMatcher nextMatcher;
    protected SearchConfig config;

    private boolean caseSensitive;
    private boolean includeDirs;
    private boolean caseWordSplit;

    public BaseFileMatcher(SearchConfig config) {
        this(config, null);
    }

    public BaseFileMatcher(SearchConfig config, SearchMatcher nextMatcher) {
        this.config = config;
        this.nextMatcher = nextMatcher;

        this.caseSensitive = Boolean.TRUE.equals(config.getValue(SearchConst.CFG_SEARCH_CASE_SENSITIVE));
        this.includeDirs = Boolean.TRUE.equals(config.getValue(SearchConst.CFG_SEARCH_INCLUDE_DIRS));
        this.caseWordSplit = !Boolean.TRUE.equals(config.getValue(SearchConst.CFG_SEARCH_NO_CASE_SPLIT));
    }

    protected boolean isCaseSensitiveEnabled() {
        return caseSensitive;
    }

    protected boolean isIncludeDirsEnabled() {
        return includeDirs;
    }

    protected boolean isCaseWordSplitEnabled() {
        return caseWordSplit;
    }

    protected void saveWordsInContext(String[] words, SearchContext context, String contextName) {
        context.setValue(contextName, MatcherUtils.prepareWordsFromTerms(words, isCaseSensitiveEnabled()));
    }

    String[] getWordsFromContext(SearchContext context, String contextName) {
        return (String[]) context.getValue(contextName);
    }

    @SuppressWarnings("unchecked")
    protected boolean isIgnoredFile(String filePath, SearchContext context) {
        Set<String> filesToIgnore = (Set<String>) context.getValue(CTX_FILES_TO_IGNORE);
        String absPath = FilePathUtils.toCanonicalPath(getSearchRootDir(), filePath);
        return filesToIgnore.contains(absPath);
    }

    protected void prepareIgnoredFiles(SearchContext context) {
        String indexFilePath = getIndexCanonicalPath();
        Set<String> filesToIgnore = new HashSet<>();
        filesToIgnore.add(indexFilePath);

        context.setValue(CTX_FILES_TO_IGNORE, filesToIgnore);
    }

    protected Boolean nextMatcherResult(FoundDocument entry, SearchContext context, Boolean acceptedStatus) {
        if (nextMatcher != null) {
            return nextMatcher.isMatching(entry, context, acceptedStatus);
        } else {
            return acceptedStatus;
        }
    }

    protected Boolean nextMatcherResult(String absolutePath, boolean isDirectory, SearchContext context, Boolean acceptedStatus) {
        if (nextMatcher != null) {
            return nextMatcher.isMatching(absolutePath, isDirectory, context, acceptedStatus);
        } else {
            return acceptedStatus;
        }
    }


    protected void prepareNextMatcher(SearchTerms terms, SearchContext context) {
        if (nextMatcher != null) {
            nextMatcher.prepare(terms, context);
        }
    }

    protected String getSearchRootDir() {
        return (String) config.getValue(SearchConst.CFG_SEARCH_ROOT_DIR);
    }

    protected String getIndexCanonicalPath() {
        return (String) config.getValue(SearchConst.CFG_INDEX_FILE_PATH);
    }
}
