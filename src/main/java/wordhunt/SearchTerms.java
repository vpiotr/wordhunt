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
public class SearchTerms {

    private String[] anyWords;
    private String[] fileWords;
    private String[] pathWords;
    private String[] contentWords;

    public static class SearchTermsBuilder {

        private Map<String, String[]> terms = new HashMap<String, String[]>();

        SearchTermsBuilder terms(Map<String, String[]> terms) {
            this.terms = new HashMap<String, String[]>();

            if (terms != null) {
                this.terms.putAll(terms);
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

    public static SearchTermsBuilder builder() {
        return new SearchTermsBuilder();
    }

    private SearchTerms() {
    }

    private SearchTerms(String[] anyWords, String[] fileWords, String[] pathWords, String[] contentWords) {
        this.anyWords = acceptWords(anyWords);
        this.fileWords = acceptWords(fileWords);
        this.pathWords = acceptWords(pathWords);
        this.contentWords = acceptWords(contentWords);
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
        return (words != null) ? (String[]) words.clone() : null;
    }

    private String[] acceptWords(String[] words) {
        return (words != null) ? (String[]) words.clone() : null;
    }
}
