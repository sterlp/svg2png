package org.sterl.encoding;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sterl.encoding.config.OutputConfig;

public class TestSvgToPng {
    
    File tmpDir;
    
    @Before
    public void before() {
        tmpDir = new File("./tmp1");
        tmpDir.mkdirs();
        tmpDir.deleteOnExit();
    }
    @After
    public void after() throws Exception {
        FileUtils.deleteDirectory(tmpDir);
    }

    @Test
    public void testConversionOfOneFile() throws Exception {
        OutputConfig cfg = OutputConfig.fromPath(getClass().getResource("/sample.svg").toURI().toString());
        
        List<File> convert = new SvgToPng(cfg).convert();
        System.out.println(convert);
        assertEquals(1, convert.size());
        assertTrue(convert.get(0).exists());
        assertTrue(convert.get(0).isFile());
        assertTrue(convert.get(0).getTotalSpace() > 1);
        
        convert.get(0).delete();
    }
    
    @Test
    public void testFileFormCmd() throws Exception {
        List<File> files = Main.run(new String[]{getClass().getResource("/sample.svg").toURI().toString()});
        System.out.println(files);
        assertEquals(1, files.size());
        files.get(0).delete();
    }
    
    @Test
    public void textCliSingleFile() throws Exception {
        List<File> files = Main.run(new String[]{ "-f", getClass().getResource("/sample.svg").toURI().toString()});
        System.out.println(files);
        assertEquals(1, files.size());
        assertEquals("sample.png", files.get(0).getName());
        files.get(0).delete();
    }
    
    @Test
    public void testConversionDirectory() throws Exception {
        OutputConfig cfg = OutputConfig.fromPath(new File(getClass().getResource("/sample.svg").toURI()).getParent());
        
        List<File> convert = new SvgToPng(cfg).convert();
        convert.stream().forEach(f -> {
            f.deleteOnExit();
        });
        assertEquals(2, convert.size());
        assertEquals("sample.png", convert.get(0).getName());
        assertEquals("sample2.png", convert.get(1).getName());
        
        convert.stream().forEach(f -> f.delete());
    }
    
    @Test
    public void testWithConfig() throws Exception {
        List<File> files = Main.run(new String[] {
                "-f",
                getClass().getResource("/sample.svg").toURI().toString(),
                "-c",
                getClass().getResource("/android.json").toURI().toString(),
                "-o",
                tmpDir.getAbsolutePath()
        });
        assertEquals(5, files.size());
        assertEndsWith(files.get(0).getAbsolutePath(), "/tmp1/drawable-xxxhdpi/sample.png");
        assertEndsWith(files.get(1).getAbsolutePath(), "/tmp1/drawable-xxhdpi/sample.png");
        assertEndsWith(files.get(2).getAbsolutePath(), "/tmp1/drawable-xhdpi/sample.png");
        assertEndsWith(files.get(3).getAbsolutePath(), "/tmp1/drawable-hdpi/sample.png");
        assertEndsWith(files.get(4).getAbsolutePath(), "/tmp1/drawable-mdpi/sample.png");
    }
    
    @Test
    public void testInternalConfig() throws Exception {
        List<File> files = Main.run(new String[] {
                "-d",
                new File(getClass().getResource("/sample.svg").toURI()).getParent(),
                "-android-small",
                "-o",
                tmpDir.getAbsolutePath()
        });
        assertEquals(10, files.size());
    }
    
    @Test
    public void testIcon() throws Exception {
        List<File> files = Main.run(new String[] {
                "-d",
                new File(getClass().getResource("/sample.svg").toURI()).getParent(),
                "-android-icon",
                "-o",
                tmpDir.getAbsolutePath()
        });
        assertEquals(10, files.size());
    }

    private void assertEndsWith(String absolutePath, String value) {
        assertNotNull("Expected end with: " + value, absolutePath);
        assertTrue(value + " doesn't end with: " + value, absolutePath.endsWith(value));
    }
}
