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
        var wordsForPath = getWordsFromContext(context, CTX_CONTENT_WORDS_ANY);
        var wordsForContent = getWordsFromContext(context, CTX_CONTENT_WORDS_CONTENT);

        // No content conditions to check
        if (wordsForPath == null && wordsForContent == null) {
            return nextMatcherResult(entry, context, acceptedStatus);
        }

        var filePath = entry.getFilePath();
        var nonMatchingWordsInPath = stripMatchingWords(
            wordsForPath != null ? wordsForPath : new String[]{}, 
            filePath
        );
        
        var wordsLeftForContent = ArrayUtils.merge(nonMatchingWordsInPath, wordsForContent);
        var absolutePath = buildEntryAbsolutePath(filePath);
        var documentInfo = documentStorage.getDocumentInfo(absolutePath);

        // Fast fail conditions
        if (documentInfo.isDirectory() || !documentInfo.documentExists() || 
            !documentInfo.isReadable() || wordsLeftForContent.length == 0) {

            // No words to check or can't check the document
            if ((documentInfo.isDirectory() && !isIncludeDirsEnabled()) ||
                !documentInfo.documentExists() ||
                !documentInfo.isReadable() ||
                (wordsLeftForContent.length > 0)) {
                return Boolean.FALSE;
            }

            return nextMatcherResult(entry, context, Boolean.TRUE);
        }

        // Can't read the content with current charset
        if (!canHandleContent(entry.getMimeType(), entry.getCharsetName())) {
            return Boolean.FALSE;
        }

        // Check if the content contains all required words
        var contentMatches = hasAllWordsInFile(wordsLeftForContent, entry.getCharsetName(), absolutePath);
        
        // If already rejected or content doesn't match, reject
        if (Boolean.FALSE.equals(acceptedStatus) || !contentMatches) {
            return Boolean.FALSE;
        }

        // Content matches, check next matcher
        return nextMatcherResult(entry, context, Boolean.TRUE);
    }

    @Override
    public Boolean isMatching(String absolutePath, boolean isDirectory, SearchContext context, Boolean acceptedStatus) {
        var relative = FilePathUtils.absoluteToRelativePath(absolutePath, getSearchRootDir());
        var fileType = fileTypeDetector.detectFileType(absolutePath);
        if (fileType == null) {
            fileType = FileType.UNKNOWN_FILE_TYPE;
        }
        
        var document = new FoundDocument(relative, isDirectory, fileType.getMimeType(), fileType.getCharsetName());
        return isMatching(document, context, acceptedStatus);
    }

    private boolean canHandleContent(String mimeType, String charsetName) {
        return MimeUtils.isTextType(mimeType) && !charsetName.isEmpty();
    }

    private String[] stripMatchingWords(String[] words, String filePath) {
        var resultList = MatcherUtils.stripMatchingWords(
            words, filePath, isCaseSensitiveEnabled(), isCaseWordSplitEnabled()
        );
        return resultList.toArray(new String[0]);
    }

    private boolean hasAllWordsInFile(String[] words, String charsetName, String absolutePath) {
        var charset = Charset.forName(charsetName);

        try (var in = documentStorage.getDocumentReader(absolutePath, charset)) {
            return hasAllWords(in, words);
        } catch (UnsupportedEncodingException uee) {
            throw new SearchException(
                "Wrong encoding for file: " + absolutePath + ", encoding: " + charsetName, uee
            );
        } catch (IOException ioe) {
            throw new SearchException("IO error: " + ioe.getMessage(), ioe);
        }
    }

    private String buildEntryAbsolutePath(String filePath) {
        var rootPath = getSearchRootDir();
        return FilePathUtils.toAbsolutePath(rootPath, filePath);
    }

    private boolean hasAllWords(BufferedReader in, String[] words) throws IOException {
        var wordSet = new HashSet<>(Arrays.asList(words));
        var caseSensitive = isCaseSensitiveEnabled();
        var caseWordSplit = isCaseWordSplitEnabled();
        String line;

        while (!wordSet.isEmpty() && (line = in.readLine()) != null) {
            var lineWords = MatcherUtils.extractWords(line, caseSensitive, caseWordSplit);
            lineWords.stream()
                .filter(wordSet::contains)
                .forEach(wordSet::remove);
                
            if (wordSet.isEmpty()) {
                break;
            }
        }

        return wordSet.isEmpty();
    }
}
