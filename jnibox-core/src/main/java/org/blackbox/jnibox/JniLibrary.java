package org.blackbox.jnibox;

/**
 * Model of a load JNI library. It stores the basic information of the library.
 */
public final class JniLibrary {

    private final String libraryPackage;
    private final String libraryName;
    private final String libraryPath;
    private boolean isLoaded;
    private final JniRepository jniRepository;

    /**
     * Plain constructor for all arguments.
     *
     * @param libraryPackage Package of library (e.g. 'org.mycompany.myproject.mycomponent')
     * @param libraryName Name of the library (e.g. 'complex_math')
     * @param libraryPath Path where it has been stored (e.g '/tmp/1234@localhost/org/mycompany/myproject/mycomponent/complex_math')
     * @param isLoaded The loading status of the library
     * @param jniRepository The repository that manages this library.
     */
    JniLibrary(String libraryPackage, String libraryName, String libraryPath, boolean isLoaded, JniRepository jniRepository) {

        assert libraryName!= null;
        assert libraryPackage != null;
        assert libraryPath != null;
        assert jniRepository != null;

        this.libraryPackage = libraryPackage;
        this.libraryName = libraryName;
        this.libraryPath = libraryPath;
        this.isLoaded = isLoaded;
        this.jniRepository = jniRepository;
    }

    /**
     * Returns the library package.
     *
     * @return the library package.
     */
    public String getLibraryPackage() {
        return libraryPackage;
    }

    /**
     * Returns the library name.
     *
     * @return The library name.
     */
    public String getLibraryName() {
        return libraryName;
    }

    /**
     * Returns the library path where it is stored.
     *
     * @return The library path where it is stored.
     */
    public String getLibraryPath() {
        return libraryPath;
    }

    /**
     * Returns if the library is loaded or not.
     *
     * @return True if the library is loaded, false otherwise.
     */
    public boolean isLoaded(){
        return isLoaded;
    }

    /**
     * Returns the repository that manages this library.
     *
     * @return The repository that manages this library.
     */
    public JniRepository getJniRepository() {
        return jniRepository;
    }

    void setLoaded(boolean isLoaded) {
        this.isLoaded = isLoaded;
    }

    /**
     * Same as library.getJniRepository().load(library)
     * @throws JniLoaderException
     */
    public void load() throws JniLoaderException {
        this.getJniRepository().load(this);
    }
}
