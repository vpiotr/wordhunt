package wordhunt;

final class SearchStrategyUsingPreparedIndex {

    private final SearchConfig config;
    private final ProcessLog processLog;

    SearchStrategyUsingPreparedIndex(SearchConfig aConfig, ProcessLog aProcessLog) {
        this.config = aConfig;
        this.processLog = aProcessLog;
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
