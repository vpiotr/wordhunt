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

import java.util.Map;
import java.util.HashMap;


/**
 * Container for search terms and words 
 * Terms: search symbols like "-word1 +word2 word3|work4". 
 * Words: just array of words to be found.
 *
 * @author piotr
 */
public final class SearchTerms {

    private final String[] anyWords;
    private final String[] fileWords;
    private final String[] pathWords;
    private final String[] contentWords;

    private SearchTerms(String[] aAnyWords, String[] aFileWords, String[] aPathWords, String[] aContentWords) {
        this.anyWords = acceptWords(aAnyWords);
        this.fileWords = acceptWords(aFileWords);
        this.pathWords = acceptWords(aPathWords);
        this.contentWords = acceptWords(aContentWords);
    }

    public static SearchTermsBuilder builder() {
        return new SearchTermsBuilder();
    }

    public String[] getAnyWords() {
        return returnWords(anyWords);
    }

    public String[] getFileWords() {
        return returnWords(fileWords);
    }

    public String[] getPathWords() {
        return returnWords(pathWords);
    }

    public String[] getContentWords() {
        return returnWords(contentWords);
    }

    private String[] returnWords(String[] words) {
        return (words != null) ? words.clone() : null;
    }

    private String[] acceptWords(String[] words) {
        return (words != null) ? words.clone() : null;
    }

    public static class SearchTermsBuilder {

        private Map<String, String[]> terms = new HashMap<>();

        SearchTermsBuilder terms(Map<String, String[]> aNewTerms) {
            this.terms = new HashMap<>();

            if (aNewTerms != null) {
                this.terms.putAll(aNewTerms);
            }

            return this;
        }

        SearchTerms build() {
            return new SearchTerms(
                    terms.get(SearchConst.CFG_SEARCH_TERMS_ANY),
                    terms.get(SearchConst.CFG_SEARCH_TERMS_FILE),
                    terms.get(SearchConst.CFG_SEARCH_TERMS_PATH),
                    terms.get(SearchConst.CFG_SEARCH_TERMS_CONTENT)
            );
        }

    }

}
