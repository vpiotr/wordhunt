package wordhunt;

final class SearchStrategyUsingPreparedIndex {

    private final SearchConfig config;
    private final ProcessLog processLog;

    SearchStrategyUsingPreparedIndex(SearchConfig config, ProcessLog processLog) {
        this.config = config;
        this.processLog = processLog;
    }

    public void invoke(SearchTerms searchTerms) {
        DocumentStorage documentStorage = new DocumentStorageViaFiles();
        IndexStorage indexStorage = new IndexStorageViaFiles();
        DocumentSearcher searcher = new IndexedDocumentSearcher(config, new BasicIndexWalkerFactory(indexStorage),
                documentStorage, processLog::writeLine);
        SearchConsumer consumer = new BasicSearchConsumer(config, documentStorage, processLog::writeLine);
        SearchMatcher matcher = new FilePathMatcher(config, new FileContentMatcher(config, new TextFileTypeDetector(), documentStorage), documentStorage);
        searcher.search(searchTerms, matcher, consumer);
    }
}
