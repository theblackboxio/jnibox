package org.blackbox.jnibox;

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

    public AbstractJniRepository(File repositoryDirectory) {
        this.repositoryDirectory = repositoryDirectory;
        if (!this.repositoryDirectory.exists()) {
            repositoryDirectory.mkdirs();
        }
    }

    @Override
    public String getRepositoryDirectory() {
        return repositoryDirectory.getAbsolutePath();
    }

    @Override
    public JniLibrary store(String libraryPackage, String libraryName) throws JniLoaderException {
        try {
            String localLibraryParentPath = libraryPackage.replace('.', File.separatorChar);
            String localLibraryPath = localLibraryParentPath + File.separator + libraryName;
            return store(Resources.getResource(localLibraryPath).openStream(), libraryPackage, libraryName);
        } catch (IOException e) {
            throw new JniLoaderException(e);
        }
    }

    @Override
    public JniLibrary store(String libraryPath, String libraryPackage, String libraryName) throws JniLoaderException {
        try{
            File libraryFile = new File(libraryPath);
            return store(new FileInputStream(libraryFile), libraryPackage, libraryName);
        } catch (IOException e) {
            throw new JniLoaderException(e);
        }
    }

    private JniLibrary store(InputStream inputStream, String libraryPackage, String libraryName) throws JniLoaderException {
        try {
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
            return new JniLibrary(libraryPackage, libraryName, finalLibraryPath, false, this);
        } catch (Exception e) {
            throw new JniLoaderException(e);
        }
    }

    @Override
    public void load(JniLibrary jniLibrary) throws JniLoaderException {
        if (!jniLibrary.isLoaded() && this.equals(jniLibrary.getJniRepository())) {
            try {
                System.load(jniLibrary.getLibraryPath());
                jniLibrary.setLoaded(true);
            } catch (Exception e) {
                throw new JniLoaderException(e);
            }
        }
    }

    @Override
    public JniLibrary load(String libraryPath, String libraryPackage, String libraryName) throws JniLoaderException {
        JniLibrary jniLibrary = store(libraryPath, libraryPackage, libraryName);
        load(jniLibrary);
        return jniLibrary;
    }

    @Override
    public JniLibrary load(String libraryPackage, String libraryName) throws JniLoaderException {
        JniLibrary jniLibrary = store(libraryPackage, libraryName);
        load(jniLibrary);
        return jniLibrary;
    }

    public void close() throws JniLoaderException {
        try {
            FileUtils.deleteDirectory(repositoryDirectory);
        } catch (IOException e) {
            throw new JniLoaderException(e);
        }
    }
}
