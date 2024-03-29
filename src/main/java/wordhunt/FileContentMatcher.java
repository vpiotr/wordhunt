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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Checks if file contains in its body required words.
 * @author piotr
 */
public class FileContentMatcher extends BaseFileMatcher {

    private static final String CTX_CONTENT_WORDS_ANY = "content_words_any";
    private static final String CTX_CONTENT_WORDS_CONTENT = "content_words_content";
    private final FileTypeDetector fileTypeDetector;
    private final DocumentStorage documentStorage;

    public FileContentMatcher(SearchConfig config, FileTypeDetector fileTypeDetector, DocumentStorage documentStorage) {
        this(config, null, fileTypeDetector, documentStorage);
    }

    public FileContentMatcher(SearchConfig config, SearchMatcher nextMatcher, FileTypeDetector fileTypeDetector, DocumentStorage documentStorage) {
        super(config, nextMatcher);
        this.fileTypeDetector = fileTypeDetector;
        this.documentStorage = documentStorage;
    }

    @Override
    public void prepare(SearchTerms terms, SearchContext context) {

        prepareIgnoredFiles(context);

        saveWordsInContext(terms.getAnyWords(), context, CTX_CONTENT_WORDS_ANY);
        saveWordsInContext(terms.getContentWords(), context, CTX_CONTENT_WORDS_CONTENT);

        prepareNextMatcher(terms, context);
    }

    @Override
    public Boolean isMatching(FoundDocument entry, SearchContext context, Boolean acceptedStatus) {

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

        DocumentInfo documentInfo = documentStorage.getDocumentInfo(absolutePath);

        if (documentInfo.isDirectory() || !documentInfo.documentExists() || !documentInfo.isReadable() || wordsLeftForContent.length == 0) {

            if ((documentInfo.isDirectory() && !isIncludeDirsEnabled())
                    || !documentInfo.documentExists()
                    || !documentInfo.isReadable()
                    || (wordsLeftForContent.length > 0)) {
                matchedStatus = Boolean.FALSE;
            }

            if (!Boolean.FALSE.equals(matchedStatus)) {
                matchedStatus = nextMatcherResult(entry, context, Boolean.TRUE);
            }

            return matchedStatus;
        }

        if (!canHandleContent(entry.getMimeType(), entry.getCharsetName())) {
            // there are some words to match, but we cannot check them
            return Boolean.FALSE;
        }

        boolean contentOK = hasAllWordsInFile(wordsLeftForContent, entry.getCharsetName(), absolutePath);

        if (!Boolean.FALSE.equals(matchedStatus)) {
            matchedStatus = contentOK;
        }

        if (!Boolean.FALSE.equals(matchedStatus)) {
            matchedStatus = nextMatcherResult(entry, context, matchedStatus);
        }

        return Boolean.TRUE.equals(matchedStatus);
    }

    @Override
    public Boolean isMatching(String absolutePath, boolean isDirectory, SearchContext context, Boolean acceptedStatus) {
        String relative = FilePathUtils.absoluteToRelativePath(absolutePath, getSearchRootDir());
        FileType fileType = fileTypeDetector.detectFileType(absolutePath);
        if (fileType == null) {
            fileType = FileType.UNKNOWN_FILE_TYPE;
        }
        FoundDocument document = new FoundDocument(relative, isDirectory, fileType.getMimeType(), fileType.getCharsetName());
        return isMatching(document, context, acceptedStatus);
    }


    private boolean canHandleContent(String mimeType, String charsetName) {
        return MimeUtils.isTextType(mimeType) && (!charsetName.isEmpty());
    }

    private String[] stripMatchingWords(String[] words, String filePath) {
        List<String> resultList = MatcherUtils.stripMatchingWords(words, filePath, isCaseSensitiveEnabled(),
                isCaseWordSplitEnabled());

        return resultList.toArray(new String[0]);
    }

    private boolean hasAllWordsInFile(String[] words, String charsetName, String absolutePath) {
        Charset charset = Charset.forName(charsetName);

        boolean fileContainsAllWords;

        try (BufferedReader in = documentStorage.getDocumentReader(absolutePath, charset)) {
            fileContainsAllWords = hasAllWords(in, words);
        } catch (UnsupportedEncodingException uee) {
            throw new SearchException("Wrong encoding for file: " + absolutePath + ", encoding: " + charsetName, uee);
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
