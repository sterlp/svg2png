package org.sterl.svg2png;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;

public class AbstractFileTest {
    protected static final File tmpDir = new File("./tmp1");

    @BeforeEach
    public void before() throws IOException {
        tmpDir.mkdirs();
        tmpDir.deleteOnExit();
        Collection<File> files = FileUtils.listFiles(tmpDir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
        for (File file : files) {
            file.delete();
        }
    }
    
    @AfterAll
    public static void after() throws Exception {
        clean();
    }
    
    protected static void clean() {
        try {
            FileUtils.deleteDirectory(tmpDir);
        } catch (Exception e) {
            System.err.println("TEST: Failed to clean " + tmpDir + " -> " + e.getCause().getMessage());
            Collection<File> files = FileUtils.listFiles(tmpDir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
            for (File file : files) {
                file.delete();
                file.deleteOnExit();
            }
            tmpDir.deleteOnExit();
        }
    }
}
