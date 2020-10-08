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
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Collection;
import java.util.Locale;

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
    
    public static String[] prepareWordsFromTerms(String[] words) {
        return prepareWordsFromTerms(words, false);
    }

    public static String[] prepareWordsFromTerms(String[] words, boolean caseSensitive) {
        if (words == null) {
            return null;
        }

        List<String> newWords = new ArrayList<>();

        for (String word : words) {

            String newWord = caseSensitive ? word : word.toUpperCase(Locale.getDefault());
            Matcher m = WORD_PATTERN.matcher(newWord);

            while (m.find()) {
                String foundWord = newWord.substring(m.start(), m.end());
                newWords.add(foundWord);
            }
        }

        return newWords.toArray(new String[0]);
    }

    public static boolean matchesAllWords(String text, String[] words,
            boolean caseSensitive, boolean caseWordSplit) {
        Set<String> wordsInText
                = extractWords(text, caseSensitive, caseWordSplit);

        for (String word : words) {
            if (!wordsInText.contains(word)) {
                return false;
            }
        }

        return true;
    }

    public static List<String> stripMatchingWords(String[] words, String text, boolean caseSensitive, boolean caseWordSplit) {
        Set<String> wordsInText
                = extractWords(text, caseSensitive,
                        caseWordSplit);

        List<String> resultList = new ArrayList<>();

        for (String word : words) {
            if (!wordsInText.contains(word)) {
                resultList.add(word);
            }
        }

        return resultList;
    }

    public static Set<String> extractWords(String text, boolean caseSensitive, boolean caseWordSplit) {
        Matcher m = WORD_PATTERN.matcher(text);
        Set<String> result = new HashSet<>();

        while (m.find()) {
            String word = text.substring(m.start(), m.end());
            result.add(caseSensitive ? word : word.toUpperCase(Locale.getDefault()));
            if (caseWordSplit) {
                addCaseSplitWords(word, caseSensitive, result);
            }
            addCharSplitWords(word, "[\\-_\\.]", caseSensitive, result);
        }

        return result;
    }

    private static void addCaseSplitWords(String word, boolean caseSensitive, Collection<String> output) {
        String[] caseWords = word.split("(?=\\p{Lu})");
        for (String cword : caseWords) {
            output.add(caseSensitive ? cword : cword.toUpperCase(Locale.getDefault()));
        }
    }

    private static void addCharSplitWords(String word, String splitChars, boolean caseSensitive, Collection<String> output) {
        String[] splitWords = word.split(splitChars);
        for (String sword : splitWords) {
            output.add(caseSensitive ? sword : sword.toUpperCase(Locale.getDefault()));
        }
    }

    private MatcherUtils() {
    }
}
