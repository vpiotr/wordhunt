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
 * Global constants for search
 * 
 * @author piotr
 */
public class SearchConst {

    public final static String OPT_ENABLE_DEBUG = "--debug";
    public final static String DEF_INDEX_FILE_NAME = "index.dat";
    public final static String CFG_INDEX_FILE_PATH = "index_path";
    public final static String CFG_SEARCH_ROOT_DIR = "search_root_dir";

    public final static String CFG_SEARCH_TERMS = "terms";
    public final static String CFG_SEARCH_TERMS_ANY = "terms_any";
    public final static String CFG_SEARCH_TERMS_FILE = "terms_file";
    public final static String CFG_SEARCH_TERMS_PATH = "terms_path";
    public final static String CFG_SEARCH_TERMS_CONTENT = "terms_content";

    public final static String CFG_SEARCH_INCLUDE_DIRS = "include_dirs";
    public final static String CFG_SEARCH_CASE_SENSITIVE = "case_sensitive";
    public final static String CFG_SEARCH_NO_CASE_SPLIT = "no_case_split";
    public final static String CFG_SEARCH_BRIEF = "list";

    private SearchConst() {
    }
}
