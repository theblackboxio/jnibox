package org.blackbox.jnibox;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

/**
 * Created by guillermoblascojimenez on 28/12/14.
 */
public class JniLibraryAlreadyLoadedExceptionRepositoryTest {

    private static final String RESOURCE_PACKAGE = "org.blackbox.jnibox.test";
    private static final String RESOURCE_NAME = "someMockLibrary.so";

    private JniRepository jniRepository;
    private JniLibrary jniLibrary;

    @Before
    public void setup(){
        jniRepository = new TempDirJniRepository(new IdleJniLibraryLoader());
    }

    @After
    public void tearDown() {
        String repositoryPath = jniRepository.getRepositoryDirectory();
        try {
            jniRepository.close();
        } catch (JniRepositoryException e) {
            Assert.fail();
        }
        Assert.assertFalse(new File(repositoryPath).exists());
    }

    @Test(expected = IllegalStateException.class)
    public void storeExeption() throws JniRepositoryException {
        jniLibrary = jniRepository.store(RESOURCE_PACKAGE, RESOURCE_NAME);
        jniLibrary.load();
        jniLibrary.load();

    }

    private static class IdleJniLibraryLoader implements JniLibraryLoader {

        @Override
        public void load(String libraryPath) {
            // do nothing, it is mocked
        }
    }

}
