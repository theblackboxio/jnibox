package org.blackbox.jnibox;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The repository directory of this repository is built with the following template:
 *
 * ${System.getProperty("java.io.tmpdir")}/${ManagementFactory.getRuntimeMXBean().getName()}-jniRepository-${repositoryCount}
 *
 * Where the repositoryCount is the instance number of this type of repository. Example:
 *
 * /var/folders/49/gsgkx3xx23b0ytmy53_vmqjh0000gn/T/28250@localhost-jniRepository-1
 *
 * This morphology guarantees that repository directories are not shared among multiple
 * repositories of this type when:
 *
 *  - Multiple JVM creates instances of this type because the pid and user is in the dir name, and
 *
 *  - One JVM reates multiple instances of this type because the instance count is in the dir name.
 */
public final class TempDirJniRepository extends AbstractJniRepository {
    
    private final static AtomicInteger COUNT = new AtomicInteger(0);

    public TempDirJniRepository(JniLibraryLoader jniLibraryLoader) {
        super(createRepositoryDir(), jniLibraryLoader);
    }

    public TempDirJniRepository() {
        super(createRepositoryDir());
    }

    private static File createRepositoryDir() {

        File tmpDir = new File(System.getProperty("java.io.tmpdir"));
        assert (tmpDir.exists());
        int repositoryCount = COUNT.incrementAndGet();
        String subdir = ManagementFactory.getRuntimeMXBean().getName() + "-" + "jniRepository-" + String.valueOf(repositoryCount);
        String repositoryPath = tmpDir.getAbsolutePath() + File.separator + subdir;

        File repositoryDirectory = new File(repositoryPath);
        repositoryDirectory.mkdir();

        return repositoryDirectory;
    }

}
