package org.blackbox.jnibox;

/**
 * Created by guillermoblascojimenez on 28/12/14.
 */
public interface JniLibraryLoader {

    public static final JniLibraryLoader SYSTEM = new SystemJniLibraryLoader();

    void load(String libraryPath);

}
