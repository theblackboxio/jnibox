package org.blackbox.jnibox;

/**
 * A JniLoader is an object that given a package and a library name like 'org.mycompany.myproject'
 * and 'complex_maths' that is located as a packed resource of the application (in some jar or nar)
 * stores it in some safe place and loads it in the system.
 *
 * The implementation provides synchronization in the store phase so you can store
 * multiple libraries asynchronously and the store process will be synchronized.
 */
public interface JniRepository {

    /**
     * Stores and loads a library that is stored in the classpath as resource.
     *
     * @param libraryPackage Package of the library.
     * @param libraryName Name of the library in the package.
     * @return The JNI library object with the attached info of the loading.
     * @throws JniRepositoryException Wraps any exception thrown during the process of store and loading.
     */
    JniLibrary load(String libraryPackage, String libraryName) throws JniRepositoryException;

    /**
     * Stores a library that is stored in the classpath as resource.
     *
     * @param libraryPackage Package of the library.
     * @param libraryName Name of the library in the package.
     * @return The JNI library object with the attached info of the storing.
     * @throws JniRepositoryException Wraps any exception thrown during the process of store.
     */
    JniLibrary store(String libraryPackage, String libraryName) throws JniRepositoryException;

    /**
     * Stores a library that is stored in the given libraryPath parameter.
     *
     * @param libraryPath Path where the library is.
     * @param libraryPackage Package of the library.
     * @param libraryName Name of the library in the package.
     * @return The JNI library object with the attached info of the storing.
     * @throws JniRepositoryException Wraps any exception thrown during the process of store.
     */
    JniLibrary store(String libraryPath, String libraryPackage, String libraryName) throws JniRepositoryException;

    /**
     * Loads a library that is stored in the given libraryPath parameter.
     *
     * @param libraryPath Path where the library is.
     * @param libraryPackage Package of the library.
     * @param libraryName Name of the library in the package.
     * @return The JNI library object with the attached info of the storing.
     * @throws JniRepositoryException Wraps any exception thrown during the process of store and loading.
     */
    JniLibrary load(String libraryPath, String libraryPackage, String libraryName) throws JniRepositoryException;

    /**
     * Loads the given JNI library.
     *
     * @param jniLibrary The JNI library to load.
     * @throws JniRepositoryException Wraps any excpetion thrown during the process of load
     */
    void load(JniLibrary jniLibrary) throws JniRepositoryException;

    /**
     * Closes the loader.
     *
     * @throws JniRepositoryException Wraps any exception thrown during the process of close.
     */
    void close() throws JniRepositoryException;

    /**
     * Returns the repository directory.
     * @return The repository directory.
     */
    String getRepositoryDirectory();

    /**
     * The amount of libraries in the repository.
     *
     * @return Amount of libraries in the repository.
     */
    int size();

}
