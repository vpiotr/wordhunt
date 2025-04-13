package wordhunt;

class SearchStrategyWithoutIndex {

    private final SearchConfig config;
    private final ProcessLog processLog;

    SearchStrategyWithoutIndex(SearchConfig aConfig, ProcessLog aProcessLog) {
        this.config = aConfig;
        this.processLog = aProcessLog;
    }

    public void invoke(SearchTerms searchTerms) {
        DocumentStorage documentStorage = new DocumentStorageViaFiles();
        OnflySearcher searcher = new OnflySearcher(config);
        SearchConsumer consumer = new BasicSearchConsumer(config, documentStorage, processLog::writeLine);
        SearchMatcher matcher = new FilePathMatcher(config, new FileContentMatcher(config, new TextFileTypeDetector(), documentStorage), documentStorage);
        searcher.search(searchTerms, matcher, consumer);
    }
}
