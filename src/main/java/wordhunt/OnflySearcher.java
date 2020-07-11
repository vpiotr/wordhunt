package wordhunt;

public class OnflySearcher implements DocumentSearcher {

    private final SearchConfig config;

    public OnflySearcher(SearchConfig config) {
        this.config = config;
    }

    @Override
    public void search(SearchTerms terms, SearchMatcher matcher, SearchConsumer consumer) {
        SearchContext context = newSearchContext();
        matcher.prepare(terms, context);
        String dirName = (String) config.getValue(SearchConst.CFG_SEARCH_ROOT_DIR);
        new FileWalker().walk(dirName, (String absolutePath, boolean isDirectory) -> { processEntry(absolutePath, isDirectory, context, matcher, consumer);});
    }

    private void processEntry(String absolutePath, boolean isDirectory, SearchContext context, SearchMatcher matcher, SearchConsumer consumer) {
        if (Boolean.TRUE.equals(matcher.isMatching(absolutePath, isDirectory, context, null))) {
            consumer.handle(absolutePath);
        }
    }

    protected SearchContext newSearchContext() {
        return new SearchContext();
    }
}
