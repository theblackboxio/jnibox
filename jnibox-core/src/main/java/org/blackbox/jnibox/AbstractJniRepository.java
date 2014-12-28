package org.blackbox.jnibox;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.io.Resources;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;

/**
 * This class encapsulates the logic of the JniRepository. The repository Directory is delegated
 * to the superclass. See README and JniRepository documentation.
 */
abstract class AbstractJniRepository implements JniRepository {

    private final File repositoryDirectory;

    private final Table<String, String, JniLibrary> libraries;

    AbstractJniRepository(File repositoryDirectory) {
        assert repositoryDirectory != null;
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

        checkStore(libraryPackage, libraryName);

        try {
            String localLibraryParentPath = libraryPackage.replace('.', File.separatorChar);
            String localLibraryPath = localLibraryParentPath + File.separator + libraryName;
            safeStore(Resources.getResource(localLibraryPath).openStream(), libraryPackage, libraryName);
            return libraries.get(libraryPackage, libraryName);
        } catch (IOException e) {
            throw new JniRepositoryException(e);
        }
    }

    @Override
    public JniLibrary store(String libraryPath, String libraryPackage, String libraryName) throws JniRepositoryException {

        Preconditions.checkNotNull(libraryPath);
        Preconditions.checkNotNull(libraryName);
        Preconditions.checkNotNull(libraryPackage);

        checkStore(libraryPackage, libraryName);

        try{
            File libraryFile = new File(libraryPath);
            safeStore(new FileInputStream(libraryFile), libraryPackage, libraryName);
            return libraries.get(libraryPackage, libraryName);
        } catch (IOException e) {
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
    public JniLibrary load(String libraryPath, String libraryPackage, String libraryName) throws JniRepositoryException {
        JniLibrary jniLibrary = store(libraryPath, libraryPackage, libraryName);
        safeLoad(jniLibrary);
        return jniLibrary;
    }

    @Override
    public JniLibrary load(String libraryPackage, String libraryName) throws JniRepositoryException {
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
        if (library.getStatus().equals(JniLibrary.Status.LOADED)) {
            throw new JniLibraryAlreadyLoadedException(library.getLibraryPackage(), library.getLibraryName());
        }
        synchronized (library) {
            String path = library.getLibraryPath();
            System.load(path);
            library.setStatus(JniLibrary.Status.LOADED);
        }
    }

    private void checkStore(String libraryPackage, String libraryName) throws JniLibraryAlreadyExistsException {
        assert libraryName != null;
        assert libraryPackage != null;
        synchronized (libraries) {
            if (libraries.contains(libraryPackage, libraryName)) {
                throw new JniLibraryAlreadyExistsException(libraryPackage, libraryName);
            } else {
                libraries.put(libraryPackage, libraryName, new JniLibrary(libraryPackage, libraryName, this));
            }
        }
    }

    private void safeStore(InputStream inputStream, String libraryPackage, String libraryName) throws JniRepositoryException {
        assert inputStream != null;
        assert libraryName != null;
        assert libraryPackage != null;
        try {
            final JniLibrary library = libraries.get(libraryPackage, libraryName);
            assert library != null;
            assert library.getStatus().equals(JniLibrary.Status.DECLARED);
            synchronized (library) {
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
                library.setStatus(JniLibrary.Status.STORED);
            }
        } catch (Exception e) {
            throw new JniRepositoryException(e);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        this.close();
        super.finalize();
    }
}
