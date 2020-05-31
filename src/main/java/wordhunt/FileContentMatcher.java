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
import java.util.Arrays;
import java.util.List;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.io.File;

import java.nio.charset.Charset;

/**
 * Checks if file contains in it's body required words.
 * @author piotr
 */
public class FileContentMatcher extends BaseFileMatcher implements SearchMatcher {

    private final static String CTX_CONTENT_WORDS_ANY = "content_words_any";
    private final static String CTX_CONTENT_WORDS_CONTENT = "content_words_content";

    public FileContentMatcher(SearchConfig config) {
        this(config, null);
    }

    public FileContentMatcher(SearchConfig config, SearchMatcher nextMatcher) {
        super(config, nextMatcher);
    }

    @Override
    public void prepare(SearchTerms terms, SearchContext context) {

        prepareIgnoredFiles(context);

        saveWordsInContext(terms.getAnyWords(), context, CTX_CONTENT_WORDS_ANY);
        saveWordsInContext(terms.getContentWords(), context, CTX_CONTENT_WORDS_CONTENT);

        prepareNextMatcher(terms, context);
    }

    @Override
    public Boolean isMatching(IndexEntry entry, SearchContext context, Boolean acceptedStatus) {

        String[] wordsForPath = getWordsFromContext(context, CTX_CONTENT_WORDS_ANY);
        String[] wordsForContent = getWordsFromContext(context, CTX_CONTENT_WORDS_CONTENT);

        if (wordsForPath == null && wordsForContent == null) {
            // no conditions for content
            return nextMatcherResult(entry, context, acceptedStatus);
        }

        if (wordsForPath == null) {
            wordsForPath = new String[]{};
        }

        String filePath = entry.getFilePath();
        String[] nonMatchingWordsInPath = stripMatchingWords(wordsForPath, filePath);
        String[] wordsLeftForContent = ArrayUtils.merge(nonMatchingWordsInPath, wordsForContent);

        Boolean matchedStatus = acceptedStatus;

        String absolutePath = buildEntryAbsolutePath(filePath);

        File entryFile = new File(absolutePath);
        boolean isDirectory = entryFile.isDirectory();
        boolean exists = entryFile.exists();
        boolean canRead = entryFile.canRead();

        if (isDirectory || !exists || !canRead || wordsLeftForContent.length == 0) {

            if ((isDirectory && !isIncludeDirsEnabled())
                    || !exists
                    || !canRead
                    || (wordsLeftForContent.length > 0)) {
                matchedStatus = Boolean.FALSE;
            }

            if (!Boolean.FALSE.equals(matchedStatus)) {
                if (wordsLeftForContent.length == 0) {
                    matchedStatus = Boolean.TRUE;
                }

                matchedStatus = nextMatcherResult(entry, context, matchedStatus);
            }

            return matchedStatus;
        }

        if (!canHandleContent(entry, context)) {
            // there are some words to match but we cannot check them
            return Boolean.FALSE;
        }

        boolean contentOK = hasAllWordsInFile(wordsLeftForContent, entry, absolutePath);

        if (!Boolean.FALSE.equals(matchedStatus)) {
            matchedStatus = contentOK;
        }

        if (!Boolean.FALSE.equals(matchedStatus)) {
            matchedStatus = nextMatcherResult(entry, context, matchedStatus);
        }

        return Boolean.TRUE.equals(matchedStatus);
    }

    private boolean canHandleContent(IndexEntry entry, SearchContext context) {
        boolean result = MimeUtils.isTextType(entry.getMimeType()) && (entry.getCharsetName().length() > 0);

        return result;
    }

    private String[] stripMatchingWords(String[] words, String filePath) {
        List<String> resultList = MatcherUtils.stripMatchingWords(words, filePath, isCaseSensitiveEnabled(),
                isCaseWordSplitEnabled());

        return resultList.toArray(new String[resultList.size()]);
    }

    private boolean hasAllWordsInFile(String[] words, IndexEntry entry, String entryAbsolutePath) {
        Charset charset = Charset.forName(entry.getCharsetName());

        boolean fileContainsAllWords = false;

        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(entryAbsolutePath), charset))) {
            fileContainsAllWords = hasAllWords(in, words);
        } catch (UnsupportedEncodingException uee) {
            throw new SearchException("Wrong encoding for file: " + entryAbsolutePath + ", encoding: " + entry.getCharsetName(), uee);
        } catch (IOException ioe) {
            throw new SearchException("IO error: " + ioe.getMessage(), ioe);
        }

        return fileContainsAllWords;
    }

    private String buildEntryAbsolutePath(String filePath) {
        String rootPath = getSearchRootDir();
        return FilePathUtils.toAbsolutePath(rootPath, filePath);
    }

    private boolean hasAllWords(BufferedReader in, String[] words) throws IOException {

        Set<String> wordSet = new HashSet<>(Arrays.asList(words));

        boolean caseSensitive = isCaseSensitiveEnabled();
        boolean caseWordSplit = isCaseWordSplitEnabled();

        String line;

        while ((!wordSet.isEmpty()) && ((line = in.readLine()) != null)) {
            Set<String> lineWords = MatcherUtils.extractWords(line, caseSensitive, caseWordSplit);
            for (String lineWord : lineWords) {
                if (wordSet.contains(lineWord)) {
                    wordSet.remove(lineWord);
                    if (wordSet.isEmpty()) {
                        break;
                    }
                }
            }
        }

        return wordSet.isEmpty();
    }
}
