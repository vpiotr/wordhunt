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
            HelpWriter.writeVersion(getVersionString(), App::writeLineToConsole);
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
        writeHelp(false);
    }

    private static void showHelp() {
        writeHelp(true);
    }

    private static void writeHelp(boolean longDescription) {
        HelpWriter.writeHelp(longDescription, App::writeLineToConsole);
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
        SearchTerms searchTerms = buildTerms(config);
        if (iv.indexExists()) {
            final SearchStrategyUsingPreparedIndex searchStrategyUsingPreparedIndex = new SearchStrategyUsingPreparedIndex(config, App::writeLineToConsole);
            searchStrategyUsingPreparedIndex.invoke(searchTerms);
        } else {
            final SearchStrategyWithoutIndex searchStrategyWithoutIndex = new SearchStrategyWithoutIndex(config, App::writeLineToConsole);
            searchStrategyWithoutIndex.invoke(searchTerms);
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

}
