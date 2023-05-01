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

import java.io.File;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.Map.Entry;


class App {

    public static final int MIN_FIND_ARGUMENT_COUNT = 3;

    private App() {}

    public static void main(String[] args) {
        boolean commandSyntaxOK = false;
        boolean returnFail = false;
        boolean showExceptionStack = Arrays.asList(args).contains(SearchConst.OPT_ENABLE_DEBUG);

        try {
            if (args.length > 0) {
                commandSyntaxOK = processCommand(args);
            }
        } catch (SearchException se) {
            showException("Search error: ", se, showExceptionStack);
            returnFail = true;
            commandSyntaxOK = true;
        } catch (Exception e) {
            showException("Unknown error: ", e, showExceptionStack);
            returnFail = true;
            commandSyntaxOK = true;
        }

        if (!commandSyntaxOK) {
            returnFail = true;
            if (args.length > 0) {
                showHelp();
            } else {
                showShortHelp();
            }
        }

        if (returnFail) {
            System.exit(1);
        }
    }

    private static boolean processCommand(String[] args) {

        String command = args[0];
        boolean simpleMode = !command.equals("--index")
                && !command.equals("--find")
                && !command.equals("--help")
                && !command.equals("--version");

        if (command.equals("--find") && args.length >= MIN_FIND_ARGUMENT_COUNT) {
            processFindCommand(args, false, args[1], 2);
            return true;
        } else if (simpleMode) {
            processFindCommand(args, true,".", 0);
            return true;
        } else if (command.equals("--index") && args.length >= 2) {
            validateDir(args[1]);
            SearchConfig config = parseOptions(args, false, args[1], 2);
            performIndex(config);
            return true;
        } else if (command.equals("--help")) {
            showHelp();
            return true;
        } else if (command.equals("--version")) {
            showVersion();
            return true;
        }

        return false;
    }

    private static void processFindCommand(String[] args, boolean simpleMode, String searchDir, int optionIndex) {
        validateDir(searchDir);
        SearchConfig config = parseOptions(args, simpleMode, searchDir, optionIndex);
        validateTerms(config);
        performFind(config);
    }

    private static SearchConfig parseOptions(String[] args, boolean simpleMode, String dirName, int startIndex) {
        if (simpleMode) {
            return parseSimpleOptions(args, dirName, startIndex);
        } else {
            return parseAdvancedOptions(args, dirName, startIndex);
        }
    }

    private static SearchConfig parseSimpleOptions(String[] args, String dirName, int startIndex) {
        SearchConfig result = new SearchConfig();

        setupSearchRootDir(result, dirName);
        setIndexFile(result, dirName, SearchConst.DEF_INDEX_FILE_NAME);

        String[] anyTerms = Arrays.copyOfRange(args, startIndex, args.length);
        anyTerms = ArrayUtils.merge(anyTerms, getTermsInConfig(result, SearchConst.CFG_SEARCH_TERMS_ANY));
        setTermsInConfig(anyTerms, result, SearchConst.CFG_SEARCH_TERMS_ANY);

        return result;
    }

    private static SearchConfig parseAdvancedOptions(String[] args, String dirName, int startIndex) {
        SearchConfig result = new SearchConfig();

        setupSearchRootDir(result, dirName);
        setIndexFile(result, dirName, SearchConst.DEF_INDEX_FILE_NAME);

        int i = startIndex;
        while (i < args.length) {
            String value = args[i];
            if ("--brief".equals(value)) {
                result.setValue(SearchConst.CFG_SEARCH_BRIEF, Boolean.TRUE);
            } else if ("--include-dirs".equals(value)) {
                result.setValue(SearchConst.CFG_SEARCH_INCLUDE_DIRS, Boolean.TRUE);
            } else if ("--index-path".equals(value)) {
                parseIndexPath(args, i, result);
                i++;
            } else if ("--case-sensitive".equals(value)) {
                result.setValue(SearchConst.CFG_SEARCH_CASE_SENSITIVE, Boolean.TRUE);
            } else if ("--no-case-split".equals(value)) {
                result.setValue(SearchConst.CFG_SEARCH_NO_CASE_SPLIT, Boolean.TRUE);
            } else if ("--anywhere".equals(value)) {
                parseTerms(args, i, result, SearchConst.CFG_SEARCH_TERMS_ANY);
                i++;
            } else if ("--inname".equals(value)) {
                parseTerms(args, i, result, SearchConst.CFG_SEARCH_TERMS_FILE);
                i++;
            } else if ("--inpath".equals(value)) {
                parseTerms(args, i, result, SearchConst.CFG_SEARCH_TERMS_PATH);
                i++;
            } else if ("--incontent".equals(value)) {
                parseTerms(args, i, result, SearchConst.CFG_SEARCH_TERMS_CONTENT);
                i++;
            } else if (!SearchConst.OPT_ENABLE_DEBUG.equals(value)) {
                String[] anyTerms = Arrays.copyOfRange(args, i, args.length);
                anyTerms = ArrayUtils.merge(anyTerms, getTermsInConfig(result, SearchConst.CFG_SEARCH_TERMS_PATH));
                setTermsInConfig(anyTerms, result, SearchConst.CFG_SEARCH_TERMS_PATH);
                i += anyTerms.length;
            }

            i++;
        }

        return result;
    }

    private static void setupSearchRootDir(SearchConfig result, String dirName) {
        result.setValue(SearchConst.CFG_SEARCH_ROOT_DIR, dirName);
    }

    private static void parseIndexPath(String[] args, int index, SearchConfig config) {
        if (args.length > index + 1) {
            setIndexFile(config, args[index + 1]);
        } else {
            String optionName = args[index];
            throw new SearchException("Index path not found for option: [" + optionName + "]");
        }
    }

    private static void setIndexFile(SearchConfig config, String indexDir, String indexFileName) {
        String indexPath = FilePathUtils.toCanonicalPath(indexDir, indexFileName);
        setIndexFile(config, indexPath);
    }

    private static void setIndexFile(SearchConfig config, String indexPath) {
        config.setValue(SearchConst.CFG_INDEX_FILE_PATH, indexPath);
    }

    private static void parseTerms(String[] args, int index, SearchConfig config, String configName) {
        if (args.length > index + 1) {
            setTermsInConfig(new String[]{args[index + 1]}, config, configName);
        } else {
            String optionName = args[index];
            throw new SearchException("Terms not found for option: [" + optionName + "]");
        }
    }

    private static String[] getTermsInConfig(SearchConfig config, String configName) {
        Map<String, String[]> termMap = prepareTermsMap(config);
        return termMap.get(configName);
    }

    private static void setTermsInConfig(String[] terms, SearchConfig config, String configName) {
        Map<String, String[]> termMap = prepareTermsMap(config);
        termMap.put(configName, terms);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, String[]> prepareTermsMap(SearchConfig config) {
        Map<String, String[]> termMap = (Map<String, String[]>) config.getValue(SearchConst.CFG_SEARCH_TERMS);
        if (termMap == null) {
            termMap = new HashMap<>();
            config.setValue(SearchConst.CFG_SEARCH_TERMS, termMap);
        }
        return termMap;
    }

    private static void validateDir(String dirName) {
        if (!checkDir(dirName)) {
            throw new SearchException("Directory does not exist: [" + dirName + "]");
        }
    }

    private static void validateTerms(SearchConfig config) {
        String[] terms = getAllTerms(config);
        if (terms.length == 0) {
            throw new SearchException("Search terms not provided");
        }
    }

    private static void showShortHelp() {
        showHelp(false);
    }

    private static void showHelp() {
        showHelp(true);
    }

    private static void showHelp(boolean longDescription) {
        writeLineToConsole("Usage: wordhunt COMMAND DIRECTORY [OPTIONS] [WORD-LIST]");
        if (longDescription) {
            String[] syntax = new String[]{
                "Examples:",
                "* Create index file for/in specified directory:",
                "    wordhunt --index /home/user1/Books",
                "* Find documents with 3 words in current dir:",
                "    wordhunt basic search java",
                "* Find documents with 3 words in Downloads dir:",
                "    wordhunt --find \"/home/user1/Downloads\" --inpath \"basic search java\"",
                "* Create brief output, useful for further processing:",
                "    wordhunt --find /home/user1/Books --brief python machine learning",
                "",
                "COMMAND can be:",
                "    --index   creates index required for searching",
                "    --find    performs search in the specified directory",
                "    --help    shows this help information",
                "    --version shows version information",
                "",
                "DIRECTORY specifies where files to be searched are stored,",
                "    it's absolute or relative path, \".\" is acceptable",
                "",
                "OPTIONS can be:",
                "    --brief                       for clean file listing, useful for further processing",
                "    --include-dirs                for including matching directory names",
                "    --index-path \"path\"           for specifying index file path,",
                "                                  by default index is stored in DIRECTORY",
                "    --case-sensitive              for case-sensitive search",
                "    --no-case-split               for disabling word split by character case",
                "    --inpath \"word-list\"          for searching for words in path",
                "    --inname \"word-list\"          for searching for words in file name",
                "    --incontent \"word-list\"       for searching for words inside file's contents",
                "    --anywhere \"word-list\"        for searching for words in path or file's contents",
                "",
                "WORD-LIST is space-separated word list in any order (any number of arguments).",
                "Default search mode is --inpath"
            };

            for (String line : syntax) {
                writeLineToConsole(line);
            }
        } else {
            writeLineToConsole("Try 'wordhunt --help' for more information.");
        }
    }

    private static void showVersion() {

        writeLineToConsole("wordhunt version " + getVersionString());

        String[] licenseInfo = new String[]{
            "Copyright (C) 2020 Piotr Likus",
            "Apache License v2.0: <https://www.apache.org/licenses/LICENSE-2.0>.",
            "There is NO WARRANTY, to the extent permitted by law."
        };

        for (String line : licenseInfo) {
            writeLineToConsole(line);
        }
    }

    private static String getVersionString() {
        return App.class.getPackage().getImplementationVersion();
    }

    private static void showException(String message, Throwable exception, boolean showStack) {
        Throwable exceptionToBeUsed = exception;
        writeErrorToConsole(message + " " + exceptionToBeUsed.getMessage());

        if (!showStack) {
            return;
        }

        Throwable cause;
        exceptionToBeUsed.printStackTrace();

        while ((cause = exceptionToBeUsed.getCause()) != null) {
            writeErrorToConsole("Caused by: " + cause.getMessage());
            cause.printStackTrace();
            exceptionToBeUsed = cause;
        }
    }

    private static boolean checkDir(String dirName) {
        File f = new File(dirName);
        return (f.exists() && f.isDirectory());
    }

    private static void performFind(SearchConfig config) {
        String dirName = (String) config.getValue(SearchConst.CFG_SEARCH_ROOT_DIR);
        boolean list = Boolean.TRUE.equals(config.getValue(SearchConst.CFG_SEARCH_BRIEF));
        String[] allTerms = getAllTerms(config);

        if (!list) {
            writeLineToConsole(String.format("Performing 'find' in dir [%s] for terms [%s]", dirName, Arrays.toString(allTerms)));
        }

        IndexStorage indexStorage = new IndexStorageViaFiles();
        IndexValidator iv = new IndexValidator(config, indexStorage);
        if (iv.indexExists()) {
            new SearchStrategyUsingPreparedIndex(config).invoke();
        } else {
            new SearchStrategyWithoutIndex(config).invoke();
        }
    }

    @SuppressWarnings("unchecked")
    private static SearchTerms buildTerms(SearchConfig config) {
        return SearchTerms.builder().
                terms((Map<String, String[]>) config.getValue(SearchConst.CFG_SEARCH_TERMS)).
                build();
    }

    private static void performIndex(SearchConfig config) {
        String dirName = (String) config.getValue(SearchConst.CFG_SEARCH_ROOT_DIR);
        writeLineToConsole(String.format("Performing 'index' in dir [%s]", dirName));
        DocumentStorage documentStorage = new DocumentStorageViaFiles();
        IndexStorage indexStorage = new IndexStorageViaFiles();
        FileIndexer fi = new FileIndexer(config, dirName, new TextFileTypeDetector(), new BasicIndexEntryWriter(dirName),
                indexStorage, documentStorage, App::writeLineToConsole);
        fi.rebuildIndex();
    }

    private static String[] getAllTerms(SearchConfig config) {
        Map<String, String[]> terms = prepareTermsMap(config);

        String[] result = new String[]{};

        for (Entry<String, String[]> entry : terms.entrySet()) {
            result = ArrayUtils.merge(result, entry.getValue());
        }

        return result;
    }

    @SuppressWarnings("java:S106")
    private static void writeLineToConsole(String line) {
        System.out.println(line);
    }

    @SuppressWarnings("java:S106")
    private static void writeErrorToConsole(String line) {
        System.err.println(line);
    }

    private static final class SearchStrategyUsingPreparedIndex {

        private final SearchConfig config;

        SearchStrategyUsingPreparedIndex(SearchConfig config) {
            this.config = config;
        }

        public void invoke() {
            SearchTerms searchTerms = buildTerms(config);
            DocumentStorage documentStorage = new DocumentStorageViaFiles();
            IndexStorage indexStorage = new IndexStorageViaFiles();
            DocumentSearcher searcher = new IndexedDocumentSearcher(config, new BasicIndexWalkerFactory(indexStorage),
                    documentStorage, App::writeLineToConsole);
            SearchConsumer consumer = new BasicSearchConsumer(config, documentStorage, App::writeLineToConsole);
            SearchMatcher matcher = new FilePathMatcher(config, new FileContentMatcher(config, new TextFileTypeDetector(), documentStorage), documentStorage);
            searcher.search(searchTerms, matcher, consumer);
        }
    }
    private static class SearchStrategyWithoutIndex {

        private final SearchConfig config;

        private SearchStrategyWithoutIndex(SearchConfig config) {
            this.config = config;
        }

        public void invoke() {
            SearchTerms searchTerms = buildTerms(config);
            DocumentStorage documentStorage = new DocumentStorageViaFiles();
            OnflySearcher searcher = new OnflySearcher(config);
            SearchConsumer consumer = new BasicSearchConsumer(config, documentStorage, App::writeLineToConsole);
            SearchMatcher matcher = new FilePathMatcher(config, new FileContentMatcher(config, new TextFileTypeDetector(), documentStorage), documentStorage);
            searcher.search(searchTerms, matcher, consumer);
        }
    }
}
