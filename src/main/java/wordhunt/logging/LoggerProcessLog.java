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
package wordhunt.logging;

import wordhunt.ProcessLog;

/**
 * Implementation of ProcessLog that uses LoggerService for output.
 * 
 * @author piotr
 */
public class LoggerProcessLog implements ProcessLog {

    private final LoggerService logger;
    private final boolean useSearchResultsLogger;
    
    /**
     * Create a process log that uses the given logger
     * 
     * @param logger The logger to use
     */
    public LoggerProcessLog(LoggerService logger) {
        this(logger, false);
    }
    
    /**
     * Create a process log that uses the given logger
     * 
     * @param logger The logger to use
     * @param useSearchResultsLogger Whether to use the search results logger
     */
    public LoggerProcessLog(LoggerService logger, boolean useSearchResultsLogger) {
        this.logger = logger;
        this.useSearchResultsLogger = useSearchResultsLogger;
    }
    
    @Override
    public void writeLine(String line) {
        if (useSearchResultsLogger) {
            LoggerService.logSearchResult(line);
        } else {
            logger.info(line);
        }
    }
}