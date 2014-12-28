package org.blackbox.jnibox;

/**
 * Jni loader parent exception.
 */
public class JniRepositoryException extends Exception {

    public JniRepositoryException() {
    }

    public JniRepositoryException(String message) {
        super(message);
    }

    public JniRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public JniRepositoryException(Throwable cause) {
        super(cause);
    }

    public JniRepositoryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
