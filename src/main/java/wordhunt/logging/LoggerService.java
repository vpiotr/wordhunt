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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service that provides logging functionality using SLF4J and Logback.
 * 
 * @author piotr
 */
public class LoggerService {
    
    private final Logger logger;
    private static final Logger searchResultsLogger = LoggerFactory.getLogger("wordhunt.results");
    
    /**
     * Create a logger for the specified class
     * 
     * @param clazz The class to create logger for
     */
    public LoggerService(Class<?> clazz) {
        this.logger = LoggerFactory.getLogger(clazz);
    }
    
    /**
     * Create a logger with the specified name
     * 
     * @param name Logger name
     */
    public LoggerService(String name) {
        this.logger = LoggerFactory.getLogger(name);
    }
    
    /**
     * Log a message at INFO level
     * 
     * @param message The message to log
     */
    public void info(String message) {
        logger.info(message);
    }
    
    /**
     * Log a parameterized message at INFO level
     * 
     * @param format Message format with {} placeholders
     * @param args Arguments to format into the message
     */
    public void info(String format, Object... args) {
        logger.info(format, args);
    }
    
    /**
     * Log a message at DEBUG level
     * 
     * @param message The message to log
     */
    public void debug(String message) {
        logger.debug(message);
    }
    
    /**
     * Log a parameterized message at DEBUG level
     * 
     * @param format Message format with {} placeholders
     * @param args Arguments to format into the message
     */
    public void debug(String format, Object... args) {
        logger.debug(format, args);
    }
    
    /**
     * Log a message at WARN level
     * 
     * @param message The message to log
     */
    public void warn(String message) {
        logger.warn(message);
    }
    
    /**
     * Log a parameterized message at WARN level
     * 
     * @param format Message format with {} placeholders
     * @param args Arguments to format into the message
     */
    public void warn(String format, Object... args) {
        logger.warn(format, args);
    }
    
    /**
     * Log a message at ERROR level
     * 
     * @param message The message to log
     */
    public void error(String message) {
        logger.error(message);
    }
    
    /**
     * Log a parameterized message at ERROR level
     * 
     * @param format Message format with {} placeholders
     * @param args Arguments to format into the message
     */
    public void error(String format, Object... args) {
        logger.error(format, args);
    }
    
    /**
     * Log an exception with message at ERROR level
     * 
     * @param message The message to log
     * @param throwable The exception to log
     */
    public void error(String message, Throwable throwable) {
        logger.error(message, throwable);
    }
    
    /**
     * Log a search result which will be formatted clearly for end-users
     * 
     * @param message The search result message
     */
    public static void logSearchResult(String message) {
        searchResultsLogger.info(message);
    }
    
    /**
     * Check if DEBUG level is enabled
     * 
     * @return true if DEBUG level is enabled
     */
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }
}