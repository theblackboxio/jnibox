package org.blackbox.jnibox;

/**
 * Exception when a library is loaded but already has been loaded.
 */
public class JniLibraryAlreadyLoadedException extends JniRepositoryException {
    public JniLibraryAlreadyLoadedException(String libraryPackage, String libraryName) {
        super("Library with package " + libraryPackage + " and name " + libraryName + " already loaded");
    }
}
