package org.blackbox.jnibox;

/**
 * Exception when a library is stored but already exists.
 */
public class JniLibraryAlreadyExistsException extends JniRepositoryException {
    public JniLibraryAlreadyExistsException(String libraryPackage, String libraryName) {
        super("Library with package " + libraryPackage + " and name " + libraryName + " already exists.");
    }
}
