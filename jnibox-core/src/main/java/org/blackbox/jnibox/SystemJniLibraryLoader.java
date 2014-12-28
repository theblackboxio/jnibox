package org.blackbox.jnibox;

/**
 * Created by guillermoblascojimenez on 28/12/14.
 */
class SystemJniLibraryLoader implements JniLibraryLoader {

    @Override
    public void load(String libraryPath) {
        System.load(libraryPath);
    }
}
