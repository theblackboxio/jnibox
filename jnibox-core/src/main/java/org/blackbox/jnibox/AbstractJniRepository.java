package org.blackbox.jnibox;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.io.Resources;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.URL;

/**
 * This class encapsulates the logic of the JniRepository. The repository Directory is delegated
 * to the superclass. See README and JniRepository documentation.
 */
abstract class AbstractJniRepository implements JniRepository {

    private final File repositoryDirectory;

    private final JniLibraryLoader jniLibraryLoader;

    private final Table<String, String, JniLibrary> libraries;

    protected AbstractJniRepository(File repositoryDirectory) {
        this(repositoryDirectory, JniLibraryLoader.SYSTEM);
    }

    protected AbstractJniRepository(File repositoryDirectory, JniLibraryLoader jniLibraryLoader) {
        assert repositoryDirectory != null;
        assert jniLibraryLoader != null;
        this.jniLibraryLoader = jniLibraryLoader;
        this.repositoryDirectory = repositoryDirectory;
        if (!this.repositoryDirectory.exists()) {
            repositoryDirectory.mkdirs();
        }
        libraries = HashBasedTable.create();
    }

    @Override
    public String getRepositoryDirectory() {
        return repositoryDirectory.getAbsolutePath();
    }

    @Override
    public JniLibrary store(String libraryPackage, String libraryName) throws JniRepositoryException {

        Preconditions.checkNotNull(libraryName);
        Preconditions.checkNotNull(libraryPackage);
        Preconditions.checkArgument(!libraries.contains(libraryPackage, libraryName), "Library with package " + libraryPackage + " and name " + libraryName + " is already stored.");

        checkStore(libraryPackage, libraryName);

        try {
            String localLibraryParentPath = libraryPackage.replace('.', File.separatorChar);
            String localLibraryPath = localLibraryParentPath + File.separator + libraryName;
            URL localLibraryUrl = Resources.getResource(localLibraryPath);
            safeStore(localLibraryUrl.openStream(), libraryPackage, libraryName);
            return libraries.get(libraryPackage, libraryName);
        } catch (Exception e) {
            throw new JniRepositoryException(e);
        }
    }

    @Override
    public JniLibrary store(String libraryPath, String libraryPackage, String libraryName) throws JniRepositoryException {

        Preconditions.checkNotNull(libraryPath);
        Preconditions.checkNotNull(libraryName);
        Preconditions.checkNotNull(libraryPackage);
        Preconditions.checkArgument(!libraries.contains(libraryPackage, libraryName), "Library with package " + libraryPackage + " and name " + libraryName + " is already stored.");

        checkStore(libraryPackage, libraryName);

        try{
            File libraryFile = new File(libraryPath);
            safeStore(new FileInputStream(libraryFile), libraryPackage, libraryName);
            return libraries.get(libraryPackage, libraryName);
        } catch (Exception e) {
            throw new JniRepositoryException(e);
        }
    }

    @Override
    public void load(JniLibrary jniLibrary) throws JniRepositoryException {

        Preconditions.checkNotNull(jniLibrary);
        Preconditions.checkArgument(this.equals(jniLibrary.getJniRepository()));
        Preconditions.checkState(JniLibrary.Status.STORED.equals(jniLibrary.getStatus()));

        safeLoad(jniLibrary);
    }

    @Override
    public void load(String libraryPackage, String libraryName) throws JniRepositoryException {

        JniLibrary jniLibrary = this.libraries.get(libraryPackage, libraryName);
        if (jniLibrary == null) {
            throw new JniLibraryNotFoundException(libraryPackage, libraryName);
        } else {
            load(jniLibrary);
        }
    }

    @Override
    public JniLibrary storeAndLoad(String libraryPath, String libraryPackage, String libraryName) throws JniRepositoryException {
        JniLibrary jniLibrary = store(libraryPath, libraryPackage, libraryName);
        safeLoad(jniLibrary);
        return jniLibrary;
    }

    @Override
    public JniLibrary storeAndLoad(String libraryPackage, String libraryName) throws JniRepositoryException {
        JniLibrary jniLibrary = store(libraryPackage, libraryName);
        safeLoad(jniLibrary);
        return jniLibrary;
    }

    @Override
    public void close() throws JniRepositoryException {
        try {
            FileUtils.deleteDirectory(repositoryDirectory);
            this.libraries.clear();
        } catch (IOException e) {
            throw new JniRepositoryException(e);
        }
    }

    @Override
    public int size() {
        return libraries.size();
    }

    private void safeLoad(final JniLibrary library) throws JniRepositoryException {
        assert library != null;
        assert libraries.containsValue(library);
        assert library.getStatus().equals(JniLibrary.Status.STORED);
        synchronized (library) {
            String path = library.getLibraryPath();
            try {
                jniLibraryLoader.load(path);
                library.setStatus(JniLibrary.Status.LOADED);
            } catch (Exception e) {
                library.setStatus(JniLibrary.Status.STORED);
                throw new JniRepositoryException(e);
            }
        }
    }

    private void checkStore(String libraryPackage, String libraryName) {
        assert libraryName != null;
        assert libraryPackage != null;
        synchronized (libraries) {
            assert !libraries.contains(libraryPackage, libraryName);
            libraries.put(libraryPackage, libraryName, new JniLibrary(libraryPackage, libraryName, this));
        }
    }

    private void safeStore(InputStream inputStream, String libraryPackage, String libraryName) throws JniRepositoryException {
        assert inputStream != null;
        assert libraryName != null;
        assert libraryPackage != null;
        final JniLibrary library = libraries.get(libraryPackage, libraryName);
        assert library != null;
        assert library.getStatus().equals(JniLibrary.Status.DECLARED);
        try {
            synchronized (library) {
                library.setStatus(JniLibrary.Status.STORED);
            }
            // org.company.project.component => org/company/project/component
            String localLibraryParentPath = libraryPackage.replace('.', File.separatorChar);
            String finalLibraryParentPath = repositoryDirectory.getAbsolutePath() + File.separator + localLibraryParentPath;
            File finalLibraryParentDir = new File(finalLibraryParentPath);
            finalLibraryParentDir.mkdirs();

            String localLibraryPath = localLibraryParentPath + File.separator + libraryName;
            String finalLibraryPath = finalLibraryParentPath + File.separator + libraryName;

            File finalLibraryFile = new File(finalLibraryPath);
            OutputStream out = FileUtils.openOutputStream(finalLibraryFile);
            IOUtils.copy(inputStream, out);
            inputStream.close();
            out.close();
            library.setLibraryPath(finalLibraryPath);
        } catch (Exception e) {
            library.setStatus(JniLibrary.Status.DECLARED);
            throw new JniRepositoryException(e);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        this.close();
        super.finalize();
    }
}
