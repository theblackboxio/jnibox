package org.blackbox.jnibox;

/**
 * Jni loader parent exception.
 */
public class JniLoaderException extends Exception {

    public JniLoaderException() {
    }

    public JniLoaderException(String message) {
        super(message);
    }

    public JniLoaderException(String message, Throwable cause) {
        super(message, cause);
    }

    public JniLoaderException(Throwable cause) {
        super(cause);
    }

    public JniLoaderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
