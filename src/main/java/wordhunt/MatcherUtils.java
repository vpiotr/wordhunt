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

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Collection;
import java.util.Locale;
import java.util.Arrays;
import java.util.stream.Collectors;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Functions used for content matching against provided words. Used in file and
 * content matchers.
 *
 * @author piotr
 */
public final class MatcherUtils {

    private static final Pattern WORD_PATTERN = Pattern.compile("[\\w']+");
    private static final String CHAR_SPLIT_WORDS_PATTERN = "[\\-_\\.]";
    private static final String CASE_SPLIT_PATTERN = "(?=\\p{Lu})";

    public static String[] prepareWordsFromTerms(String[] words) {
        return prepareWordsFromTerms(words, false);
    }

    public static String[] prepareWordsFromTerms(String[] words, boolean caseSensitive) {
        if (words == null) {
            return new String[0];
        }

        return Arrays.stream(words)
                .flatMap(word -> {
                    var newWord = caseSensitive ? word : word.toUpperCase(Locale.getDefault());
                    var m = WORD_PATTERN.matcher(newWord);
                    var foundWords = new HashSet<String>();
                    while (m.find()) {
                        foundWords.add(newWord.substring(m.start(), m.end()));
                    }
                    return foundWords.stream();
                })
                .toArray(String[]::new);
    }

    public static boolean matchesAllWords(String text, String[] words,
            boolean caseSensitive, boolean caseWordSplit) {
        var wordsInText = extractWords(text, caseSensitive, caseWordSplit);
        return Arrays.stream(words).allMatch(wordsInText::contains);
    }

    public static List<String> stripMatchingWords(String[] words, String text, boolean caseSensitive, boolean caseWordSplit) {
        var wordsInText = extractWords(text, caseSensitive, caseWordSplit);
        return Arrays.stream(words)
                .filter(word -> !wordsInText.contains(word))
                .collect(Collectors.toList());
    }

    public static Set<String> extractWords(String text, boolean caseSensitive, boolean caseWordSplit) {
        var m = WORD_PATTERN.matcher(text);
        var result = new HashSet<String>();

        while (m.find()) {
            var word = text.substring(m.start(), m.end());
            result.add(caseSensitive ? word : word.toUpperCase(Locale.getDefault()));
            if (caseWordSplit) {
                addCaseSplitWords(word, caseSensitive, result);
            }
            addCharSplitWords(word, CHAR_SPLIT_WORDS_PATTERN, caseSensitive, result);
        }

        return result;
    }

    private static void addCaseSplitWords(String word, boolean caseSensitive, Collection<String> output) {
        Arrays.stream(word.split(CASE_SPLIT_PATTERN))
              .map(cword -> caseSensitive ? cword : cword.toUpperCase(Locale.getDefault()))
              .forEach(output::add);
    }

    private static void addCharSplitWords(String word, String splitChars, boolean caseSensitive, Collection<String> output) {
        Arrays.stream(word.split(splitChars))
              .map(sword -> caseSensitive ? sword : sword.toUpperCase(Locale.getDefault()))
              .forEach(output::add);
    }

    private MatcherUtils() {
    }
}
