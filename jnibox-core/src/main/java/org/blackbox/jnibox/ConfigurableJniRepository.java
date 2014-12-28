package org.blackbox.jnibox;

import java.io.File;

/**
 * User free configurable Jni repository.
 */
public class ConfigurableJniRepository extends AbstractJniRepository {

    ConfigurableJniRepository(File repositoryDirectory, JniLibraryLoader jniLibraryLoader) {
        super(repositoryDirectory, jniLibraryLoader);
    }

    public ConfigurableJniRepository(File repositoryDirectory) {
        super(repositoryDirectory);
    }

}
