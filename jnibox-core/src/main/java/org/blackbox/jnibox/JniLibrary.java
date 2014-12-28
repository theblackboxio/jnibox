package org.blackbox.jnibox;

/**
 * Model of a load JNI library. It stores the basic information of the library.
 */
public final class JniLibrary {

    public enum Status {
        DECLARED,
        STORED,
        LOADED
    }

    private final String libraryPackage;
    private final String libraryName;
    private String libraryPath;
    private Status status;
    private final JniRepository jniRepository;

    JniLibrary(String libraryPackage, String libraryName, JniRepository jniRepository) {
        this(libraryPackage, libraryName, Status.DECLARED, jniRepository);
    }

    JniLibrary(String libraryPackage, String libraryName, Status status, JniRepository jniRepository) {

        assert libraryName!= null;
        assert libraryPackage != null;
        assert status != null;
        assert jniRepository != null;

        this.libraryPackage = libraryPackage;
        this.libraryName = libraryName;
        this.status = status;
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

    public Status getStatus() {
        return status;
    }

    void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Returns the repository that manages this library.
     *
     * @return The repository that manages this library.
     */
    public JniRepository getJniRepository() {
        return jniRepository;
    }

    public void setLibraryPath(String libraryPath) {
        this.libraryPath = libraryPath;
    }

    /**
     * Same as library.getJniRepository().load(library)
     * @throws JniRepositoryException
     */
    public void load() throws JniRepositoryException {
        this.getJniRepository().load(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JniLibrary that = (JniLibrary) o;

        if (!jniRepository.equals(that.jniRepository)) return false;
        if (!libraryName.equals(that.libraryName)) return false;
        if (!libraryPackage.equals(that.libraryPackage)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = libraryPackage.hashCode();
        result = 31 * result + libraryName.hashCode();
        result = 31 * result + jniRepository.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "JniLibrary(" + libraryName + '.' +  libraryPackage + ')';
    }
}
