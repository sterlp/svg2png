package org.sterl.encoding;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.sterl.encoding.config.OutputConfig;

public class TestSvgToPng {

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
        File dir = new File("./tmp1");
        dir.mkdirs();
        dir.deleteOnExit();
        List<File> files = Main.run(new String[] {
                "-f",
                getClass().getResource("/sample.svg").toURI().toString(),
                "-c",
                getClass().getResource("/android.json").toURI().toString(),
                "-o",
                dir.getAbsolutePath()
        });
        assertEndsWith(files.get(0).getAbsolutePath(), "/tmp1/drawable-xxxhdpi/sample.png");
        assertEndsWith(files.get(1).getAbsolutePath(), "/tmp1/drawable-xxhdpi/sample.png");
        assertEndsWith(files.get(2).getAbsolutePath(), "/tmp1/drawable-xhdpi/sample.png");
        assertEndsWith(files.get(3).getAbsolutePath(), "/tmp1/drawable-hdpi/sample.png");
        assertEndsWith(files.get(4).getAbsolutePath(), "/tmp1/drawable-mdpi/sample.png");
        // clean
        FileUtils.deleteDirectory(dir);
    }
    
    @Test
    public void testInternalConfig() throws Exception {
        File dir = new File("./tmp2");
        dir.mkdirs();
        dir.deleteOnExit();
        List<File> files = Main.run(new String[] {
                "-d",
                new File(getClass().getResource("/sample.svg").toURI()).getParent(),
                "-android-small",
                "-o",
                dir.getAbsolutePath()
        });
        assertEquals(10, files.size());
        dir.delete();
    }

    private void assertEndsWith(String absolutePath, String value) {
        assertNotNull("Expected end with: " + value, absolutePath);
        assertTrue(value + " doesn't end with: " + value, absolutePath.endsWith(value));
    }
}
