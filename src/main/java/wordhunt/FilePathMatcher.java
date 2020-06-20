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
 * Class which compares a specified file path with word requirements.
 *
 * @author piotr
 */
public class FilePathMatcher extends BaseFileMatcher implements SearchMatcher {

    private final static String CTX_PATH_WORDS_FILE = "path_words_file";
    private final static String CTX_PATH_WORDS_PATH = "path_words_path";
    private final DocumentStorage documentStorage;

    public FilePathMatcher(SearchConfig config, DocumentStorage documentStorage) {
        this(config, null, documentStorage);
    }

    public FilePathMatcher(SearchConfig config, SearchMatcher nextMatcher, DocumentStorage documentStorage) {
        super(config, nextMatcher);
        this.documentStorage = documentStorage;
    }

    @Override
    public void prepare(SearchTerms terms, SearchContext context) {
        prepareIgnoredFiles(context);

        saveWordsInContext(terms.getFileWords(), context, CTX_PATH_WORDS_FILE);
        saveWordsInContext(terms.getPathWords(), context, CTX_PATH_WORDS_PATH);

        prepareNextMatcher(terms, context);
    }

    @Override
    public Boolean isMatching(FoundDocument entry, SearchContext context, Boolean acceptedStatus) {

        if (isIgnoredFile(entry.getFilePath(), context)) {
            return Boolean.FALSE;
        }

        String filePath = entry.getFilePath();

        Boolean matchedStatus = null;

        matchedStatus = fileMatchesWordsFromContext(context, CTX_PATH_WORDS_PATH, filePath, matchedStatus);

        matchedStatus = fileMatchesWordsFromContext(context, CTX_PATH_WORDS_FILE,
                FilePathUtils.extractFileName(filePath), matchedStatus);

        if (Boolean.TRUE.equals(matchedStatus) && !isIncludeDirsEnabled() && documentStorage.isDirectory(filePath)) {
            matchedStatus = Boolean.FALSE;
        }

        if (!Boolean.FALSE.equals(matchedStatus)) {
            matchedStatus = nextMatcherResult(entry, context, matchedStatus);
        }

        return Boolean.TRUE.equals(matchedStatus);
    }

    @Override
    public Boolean isMatching(String absolutePath, boolean isDirectory, SearchContext context, Boolean acceptedStatus) {
        if (isIgnoredFile(absolutePath, context)) {
            return Boolean.FALSE;
        }

        String filePath = FilePathUtils.absoluteToRelativePath(absolutePath, getSearchRootDir());

        Boolean matchedStatus = null;

        matchedStatus = fileMatchesWordsFromContext(context, CTX_PATH_WORDS_PATH, filePath, matchedStatus);

        matchedStatus = fileMatchesWordsFromContext(context, CTX_PATH_WORDS_FILE,
                FilePathUtils.extractFileName(filePath), matchedStatus);

        if (Boolean.TRUE.equals(matchedStatus) && !isIncludeDirsEnabled() && documentStorage.isDirectory(filePath)) {
            matchedStatus = Boolean.FALSE;
        }

        if (!Boolean.FALSE.equals(matchedStatus)) {
            matchedStatus = nextMatcherResult(absolutePath, isDirectory, context, matchedStatus);
        }

        return Boolean.TRUE.equals(matchedStatus);
    }

    private Boolean fileMatchesWordsFromContext(SearchContext context,
            String contextName, String filePath, Boolean acceptedStatus) {
        String[] words = getWordsFromContext(context, contextName);

        Boolean result;
        if (words != null) {
            result = (!Boolean.FALSE.equals(acceptedStatus)) && fileMatchesWords(words, filePath);
        } else {
            result = acceptedStatus;
        }

        return result;
    }

    private boolean fileMatchesWords(String[] words, String filePath) {
        return MatcherUtils.matchesAllWords(filePath, words, isCaseSensitiveEnabled(),
                isCaseWordSplitEnabled());
    }

}
