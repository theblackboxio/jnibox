package org.blackbox.jnibox;

/**
 * Created by guillermoblascojimenez on 28/12/14.
 */
public class JniLibraryNotFoundException extends JniRepositoryException {
    public JniLibraryNotFoundException(String libraryPackage, String libraryName) {
        super("Library with package " + libraryPackage + " and name " + libraryName + " not found.");
    }
}
