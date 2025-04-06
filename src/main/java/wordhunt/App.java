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

import wordhunt.logging.LoggerProcessLog;
import wordhunt.logging.LoggerService;

class App {

    private static final LoggerService logger = new LoggerService(App.class);
    private static final ProcessLog processLog = new LoggerProcessLog(logger);
    // Add a dedicated console process log for help text
    private static final ProcessLog consoleLog = line -> System.out.println(line);

    public static final int MIN_FIND_ARGUMENT_COUNT = 3;

    private App() {}

    public static void main(String[] args) {
        var commandSyntaxOK = false;
        var returnFail = false;
        var showExceptionStack = Arrays.asList(args).contains(SearchConst.OPT_ENABLE_DEBUG);

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
        var command = args[0];
        var simpleMode = !command.equals("--index")
                && !command.equals("--find")
                && !command.equals("--help")
                && !command.equals("--version");

        if (command.equals("--find")) {
            if (args.length >= MIN_FIND_ARGUMENT_COUNT) {
                processFindCommand(args, false, args[1], 2);
                return true;
            }
        } else if (command.equals("--index")) {
            if (args.length >= 2) {
                validateDir(args[1]);
                var config = parseOptions(args, false, args[1], 2);
                performIndex(config);
                return true;
            }
        } else if (command.equals("--help")) {
            showHelp();
            return true;
        } else if (command.equals("--version")) {
            HelpWriter.writeVersion(getVersionString(), consoleLog);
            return true;
        } else if (simpleMode) {
            processFindCommand(args, true, ".", 0);
            return true;
        }
        
        return false;
    }

    private static void processFindCommand(String[] args, boolean simpleMode, String searchDir, int optionIndex) {
        validateDir(searchDir);
        var config = parseOptions(args, simpleMode, searchDir, optionIndex);
        validateTerms(config);
        performFind(config);
    }

    private static SearchConfig parseOptions(String[] args, boolean simpleMode, String dirName, int startIndex) {
        return simpleMode ? 
            parseSimpleOptions(args, dirName, startIndex) : 
            parseAdvancedOptions(args, dirName, startIndex);
    }

    private static SearchConfig parseSimpleOptions(String[] args, String dirName, int startIndex) {
        var result = new SearchConfig();

        setupSearchRootDir(result, dirName);
        setIndexFile(result, dirName, SearchConst.DEF_INDEX_FILE_NAME);

        var anyTerms = Arrays.copyOfRange(args, startIndex, args.length);
        anyTerms = ArrayUtils.merge(anyTerms, getTermsInConfig(result, SearchConst.CFG_SEARCH_TERMS_ANY));
        setTermsInConfig(anyTerms, result, SearchConst.CFG_SEARCH_TERMS_ANY);

        return result;
    }

    private static SearchConfig parseAdvancedOptions(String[] args, String dirName, int startIndex) {
        var result = new SearchConfig();

        setupSearchRootDir(result, dirName);
        setIndexFile(result, dirName, SearchConst.DEF_INDEX_FILE_NAME);

        int i = startIndex;
        while (i < args.length) {
            var value = args[i];
            switch (value) {
                case "--brief":
                    result.setValue(SearchConst.CFG_SEARCH_BRIEF, Boolean.TRUE);
                    break;
                case "--include-dirs":
                    result.setValue(SearchConst.CFG_SEARCH_INCLUDE_DIRS, Boolean.TRUE);
                    break;
                case "--index-path":
                    parseIndexPath(args, i, result);
                    i++;
                    break;
                case "--case-sensitive":
                    result.setValue(SearchConst.CFG_SEARCH_CASE_SENSITIVE, Boolean.TRUE);
                    break;
                case "--no-case-split":
                    result.setValue(SearchConst.CFG_SEARCH_NO_CASE_SPLIT, Boolean.TRUE);
                    break;
                case "--anywhere":
                    parseTerms(args, i, result, SearchConst.CFG_SEARCH_TERMS_ANY);
                    i++;
                    break;
                case "--inname":
                    parseTerms(args, i, result, SearchConst.CFG_SEARCH_TERMS_FILE);
                    i++;
                    break;
                case "--inpath":
                    parseTerms(args, i, result, SearchConst.CFG_SEARCH_TERMS_PATH);
                    i++;
                    break;
                case "--incontent":
                    parseTerms(args, i, result, SearchConst.CFG_SEARCH_TERMS_CONTENT);
                    i++;
                    break;
                default:
                    if (!SearchConst.OPT_ENABLE_DEBUG.equals(value)) {
                        var anyTerms = Arrays.copyOfRange(args, i, args.length);
                        anyTerms = ArrayUtils.merge(anyTerms, getTermsInConfig(result, SearchConst.CFG_SEARCH_TERMS_PATH));
                        setTermsInConfig(anyTerms, result, SearchConst.CFG_SEARCH_TERMS_PATH);
                        i += anyTerms.length;
                    }
                    break;
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
            var optionName = args[index];
            throw new SearchException("Index path not found for option: [" + optionName + "]");
        }
    }

    private static void setIndexFile(SearchConfig config, String indexDir, String indexFileName) {
        var indexPath = FilePathUtils.toCanonicalPath(indexDir, indexFileName);
        setIndexFile(config, indexPath);
    }

    private static void setIndexFile(SearchConfig config, String indexPath) {
        config.setValue(SearchConst.CFG_INDEX_FILE_PATH, indexPath);
    }

    private static void parseTerms(String[] args, int index, SearchConfig config, String configName) {
        if (args.length > index + 1) {
            setTermsInConfig(new String[]{args[index + 1]}, config, configName);
        } else {
            var optionName = args[index];
            throw new SearchException("Terms not found for option: [" + optionName + "]");
        }
    }

    private static String[] getTermsInConfig(SearchConfig config, String configName) {
        var termMap = prepareTermsMap(config);
        return termMap.get(configName);
    }

    private static void setTermsInConfig(String[] terms, SearchConfig config, String configName) {
        var termMap = prepareTermsMap(config);
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
        var terms = getAllTerms(config);
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
        HelpWriter.writeHelp(longDescription, consoleLog);
    }

    private static String getVersionString() {
        return App.class.getPackage().getImplementationVersion();
    }

    private static void showException(String message, Throwable exception, boolean showStack) {
        logger.error(message + exception.getMessage());

        if (showStack) {
            logger.error("Stack trace:", exception);
            var cause = exception.getCause();
            while (cause != null) {
                logger.error("Caused by: {}", cause.getMessage());
                logger.error("Details:", cause);
                cause = cause.getCause();
            }
        }
    }

    private static boolean checkDir(String dirName) {
        var f = new File(dirName);
        return (f.exists() && f.isDirectory());
    }

    private static void performFind(SearchConfig config) {
        var dirName = (String) config.getValue(SearchConst.CFG_SEARCH_ROOT_DIR);
        var list = Boolean.TRUE.equals(config.getValue(SearchConst.CFG_SEARCH_BRIEF));
        var allTerms = getAllTerms(config);

        if (!list) {
            logger.info(String.format("Performing 'find' in dir [%s] for terms [%s]", dirName, Arrays.toString(allTerms)));
        }

        var indexStorage = new IndexStorageViaFiles();
        var iv = new IndexValidator(config, indexStorage);
        var searchTerms = buildTerms(config);

        if (iv.indexExists()) {
            final var searchStrategyUsingPreparedIndex = new SearchStrategyUsingPreparedIndex(config, (msg) -> logger.info(msg));
            searchStrategyUsingPreparedIndex.invoke(searchTerms);
        } else {
            final var searchStrategyWithoutIndex = new SearchStrategyWithoutIndex(config, (msg) -> logger.info(msg));
            searchStrategyWithoutIndex.invoke(searchTerms);
        }
    }

    @SuppressWarnings("unchecked")
    private static SearchTerms buildTerms(SearchConfig config) {
        return SearchTerms.builder()
                .terms((Map<String, String[]>) config.getValue(SearchConst.CFG_SEARCH_TERMS))
                .build();
    }

    private static void performIndex(SearchConfig config) {
        var dirName =                 (String) config.getValue(SearchConst.CFG_SEARCH_ROOT_DIR);
        logger.info(String.format("Performing 'index' in dir [%s]", dirName));
        
        var documentStorage =                 new DocumentStorageViaFiles();
        var indexStorage = new IndexStorageViaFiles();
        var fi = new FileIndexer(config, dirName, new TextFileTypeDetector(), 
                                new BasicIndexEntryWriter(dirName),
                                indexStorage, documentStorage, (msg) -> logger.info(msg));
        fi.rebuildIndex();
    }

    private static String[] getAllTerms(SearchConfig config) {
        var terms = prepareTermsMap(config);
        return terms.entrySet().stream()
                .map(Entry::getValue)
                .reduce(new String[]{}, ArrayUtils::merge);
    }

}
