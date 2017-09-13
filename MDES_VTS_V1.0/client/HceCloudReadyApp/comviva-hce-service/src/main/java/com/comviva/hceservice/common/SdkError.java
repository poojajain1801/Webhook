package com.comviva.hceservice.common;

/**
 * <p>Contains all error code that can be thrown from SDK APIs.</p>
 * <p><i>Note-1xx : SDK Error <br>
 *            3xx : Server Error <br>
 *            4xx : Common Error</i>
 * </p>
 */
public interface SdkError {
    /**
     * Returns error message
     * @return Error message
     */
    String getMessage();

    /**
     * Returns Error Code.
     * @return Error Code
     */
    int getErrorCode();
}
