package org.blackbox.jnibox;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

/**
 * Created by guillermoblascojimenez on 28/12/14.
 */
public class TempDirJniRepositoryTest {

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

    @Test
    public void storeAndLoad() throws JniRepositoryException {
        jniLibrary = jniRepository.store(RESOURCE_PACKAGE, RESOURCE_NAME);

        Assert.assertEquals(jniLibrary.getStatus(), JniLibrary.Status.STORED);
        Assert.assertEquals(jniLibrary.getJniRepository(), jniRepository);
        Assert.assertEquals(jniLibrary.getLibraryName(), RESOURCE_NAME);
        Assert.assertEquals(jniLibrary.getLibraryPackage(), RESOURCE_PACKAGE);
        Assert.assertEquals(jniRepository.size(), 1);

        jniLibrary.load();

        Assert.assertEquals(jniLibrary.getStatus(), JniLibrary.Status.LOADED);
    }

    @Test(expected = JniRepositoryException.class)
    public void testNotFoundFile() throws JniRepositoryException {
        jniRepository.store("org.mycompany.myproject", "mylib.so");
    }

    @Test(expected = JniLibraryNotFoundException.class)
    public void testNotFoundInRepo() throws JniRepositoryException {
        jniRepository.load("org.mycompany.myproject", "mylib.so");
    }

    private static class IdleJniLibraryLoader implements JniLibraryLoader {

        @Override
        public void load(String libraryPath) {
            // do nothing, it is mocked
        }
    }

}
