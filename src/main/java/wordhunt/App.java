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
import java.util.Optional;

import wordhunt.logging.LoggerService;
import wordhunt.utils.ArrayUtils;

class App {

    public static final int MIN_FIND_ARGUMENT_COUNT = 3;
    public static final int MIN_INDEX_ARGUMENT_COUNT = 2;

    private static final LoggerService logger = new LoggerService(App.class);

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
        var commandMetaOpt = detectMainCommand(command, args);

        if (!commandMetaOpt.isPresent()) {
            processFindCommand(args, true, ".", 0);
            return true;
        }

        if (commandMetaOpt.isPresent() && commandMetaOpt.get().minArgCount > args.length) {
            return false;
        }

        var commandMeta = commandMetaOpt.get();
        var commandType = commandMeta.commandType;
        var commandArgs = commandMeta.args;

        switch (commandType) {
            case FIND: {
                var argsRest = Arrays.copyOfRange(commandArgs, 1, commandArgs.length);
                var searchDir = commandArgs[0];
                processFindCommand(argsRest, false, searchDir, 0);
                return true;
            }
            case INDEX:
                var argsRest = Arrays.copyOfRange(commandArgs, 1, commandArgs.length);
                var searchDir = commandArgs[0];
                validateDir(searchDir);
                var config = parseOptions(argsRest, false, searchDir, 0);
                performIndex(config);
                return true;
            case HELP:
                showHelp();
                return true;
            case VERSION:
                HelpWriter.writeVersion(getVersionString(), App::writeToOutput);
                return true;
            default:
                return false;
        }
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
            i = processAdvancedOption(args, i, result, value);
        }

        return result;
    }

    private static int processAdvancedOption(String[] args, int i, SearchConfig result, String value) {
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
                i = processArgValue(args, i, result, value);
                break;
        }
        return i + 1;
    }

    private static int processArgValue(String[] args, int i, SearchConfig result, String value) {
        if (!SearchConst.OPT_ENABLE_DEBUG.equals(value)) {
            var anyTerms = Arrays.copyOfRange(args, i, args.length);
            anyTerms = ArrayUtils.merge(anyTerms, getTermsInConfig(result, SearchConst.CFG_SEARCH_TERMS_PATH));
            setTermsInConfig(anyTerms, result, SearchConst.CFG_SEARCH_TERMS_PATH);
            i += anyTerms.length;
        }
        return i;
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
        HelpWriter.writeHelp(longDescription, App::writeToOutput);
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
        return f.exists() && f.isDirectory();
    }

    private static void performFind(SearchConfig config) {
        var dirName = (String) config.getValue(SearchConst.CFG_SEARCH_ROOT_DIR);
        var list = Boolean.TRUE.equals(config.getValue(SearchConst.CFG_SEARCH_BRIEF));
        var allTerms = getAllTerms(config);

        if (!list) {
            writeToOutput(String.format("Performing 'find' in dir [%s] for terms [%s]", dirName, Arrays.toString(allTerms)));
        }

        var indexStorage = new IndexStorageViaFiles();
        var iv = new IndexValidator(config, indexStorage);
        var searchTerms = buildTerms(config);

        if (iv.indexExists()) {
            final var searchStrategyUsingPreparedIndex = new SearchStrategyUsingPreparedIndex(config, (msg) -> writeToOutput(msg));
            searchStrategyUsingPreparedIndex.invoke(searchTerms);
        } else {
            final var searchStrategyWithoutIndex = new SearchStrategyWithoutIndex(config, (msg) -> writeToOutput(msg));
            searchStrategyWithoutIndex.invoke(searchTerms);
        }
    }

    private static void writeToOutput(String message) {
        System.out.println(message);
    }

    @SuppressWarnings("unchecked")
    private static SearchTerms buildTerms(SearchConfig config) {
        return SearchTerms.builder()
                .terms((Map<String, String[]>) config.getValue(SearchConst.CFG_SEARCH_TERMS))
                .build();
    }

    private static void performIndex(SearchConfig config) {
        var dirName =                 (String) config.getValue(SearchConst.CFG_SEARCH_ROOT_DIR);
        writeToOutput(String.format("Performing 'index' in dir [%s]", dirName));
        
        var documentStorage =                 new DocumentStorageViaFiles();
        var indexStorage = new IndexStorageViaFiles();
        var fi = new FileIndexer(config, dirName, new TextFileTypeDetector(), 
                                new BasicIndexEntryWriter(dirName),
                                indexStorage, documentStorage, (msg) -> writeToOutput(msg));
        fi.rebuildIndex();
    }

    private static String[] getAllTerms(SearchConfig config) {
        var terms = prepareTermsMap(config);
        return terms.entrySet().stream()
                .map(Entry::getValue)
                .reduce(new String[]{}, ArrayUtils::merge);
    }

    private static Optional<MainCommandMeta> detectMainCommand(String command, String[] args) {
        if (!command.startsWith("--")) {
            return Optional.empty();
        }

        String commandWithoutPrefix = command.substring(2);
        if (commandWithoutPrefix.isEmpty()) {
            return Optional.empty();
        }

        if (commandWithoutPrefix.equals("find")) {
            return Optional.of(new MainCommandMeta(MainCommandType.FIND, MIN_FIND_ARGUMENT_COUNT, Arrays.copyOfRange(args, 1, args.length)));
        } else if (commandWithoutPrefix.equals("index")) {
            return Optional.of(new MainCommandMeta(MainCommandType.INDEX, MIN_INDEX_ARGUMENT_COUNT, Arrays.copyOfRange(args, 1, args.length)));
        } else if (commandWithoutPrefix.equals("help")) {
            return Optional.of(new MainCommandMeta(MainCommandType.HELP, 0, args));
        } else if (commandWithoutPrefix.equals("version")) {
            return Optional.of(new MainCommandMeta(MainCommandType.VERSION, 0, args));
        } else {
            return Optional.empty();
        }
    }

    private record MainCommandMeta(MainCommandType commandType, int minArgCount, String[] args) {
    }

    private enum MainCommandType {
        FIND,
        INDEX,
        HELP,
        VERSION
    }

}
