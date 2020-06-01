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
 * Compares given index entry with required search terms.
 *
 * @author piotr
 */
public interface SearchMatcher {

    /**
     * Prepares required configuration (for provided terms) which can be stored
     * in provided context.
     *
     * @param terms
     * @param context
     */
    void prepare(SearchTerms terms, SearchContext context);

    /**
     * Checks if a given entry is matching required terms.
     *
     * @param entry Entry to be compared
     * @param context Search context
     * @param acceptedStatus Status of entry acceptance (input)
     * @return acceptedStatus if matcher could not be applied (no requirements
     * provided) or TRUE/FALSE if match has been performed.
     */
    Boolean isMatching(FoundDocument entry, SearchContext context, Boolean acceptedStatus);

    /**
     * Checks if a given entry is matching required terms.
     *
     * @param absolutePath Full document path
     * @param isDirectory true if document is a directory
     * @param context Search context
     * @param acceptedStatus Status of entry acceptance (input)
     * @return acceptedStatus if matcher could not be applied (no requirements
     * provided) or TRUE/FALSE if match has been performed.
     */
    Boolean isMatching(String absolutePath, boolean isDirectory, SearchContext context, Boolean acceptedStatus);
}
