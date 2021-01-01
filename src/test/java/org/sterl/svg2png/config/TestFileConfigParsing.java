package org.sterl.svg2png.config;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TestFileConfigParsing {

    private ObjectMapper m = new ObjectMapper();

    @Test
    public void testAndroid() throws Exception {
        try (InputStream is = getClass().getResourceAsStream("/android.json")) {
            OutputConfig config = m.readerFor(OutputConfig.class).readValue(is);
            assertEquals(5, config.getFiles().size());
            assertEquals(192, config.getFiles().get(0).getHeight());
            assertEquals(192, config.getFiles().get(0).getWidth());
            assertEquals("drawable-xxxhdpi", config.getFiles().get(0).getDirectory());
        }
    }
    
    @Test
    public void testAndroidSmall() throws Exception {
        try (InputStream is = getClass().getResourceAsStream("/android-small.json")) {
            OutputConfig config = m.readerFor(OutputConfig.class).readValue(is);
            assertEquals(5, config.getFiles().size());
            assertEquals(96, config.getFiles().get(0).getHeight());
            assertEquals(96, config.getFiles().get(0).getWidth());
            assertEquals("drawable-xxxhdpi", config.getFiles().get(0).getDirectory());
        }
    }
    
    @Test
    public void testAndroidIcon() throws Exception {
        ObjectMapper m = new ObjectMapper();
        try (InputStream is = getClass().getResourceAsStream("/android-icon.json")) {
            OutputConfig config = m.readerFor(OutputConfig.class).readValue(is);
            
            assertEquals(5, config.getFiles().size());
            assertEquals(192, config.getFiles().get(0).getHeight());
            assertEquals(192, config.getFiles().get(0).getWidth());
            assertEquals("drawable-xxxhdpi", config.getFiles().get(0).getDirectory());
        }
    }
    
    @Test
    public void testNameGeneration() {
        assertEquals("foo.png", FileOutput.buildName("foo", null, null, null));
        assertEquals("ic_foo.png", FileOutput.buildName("foo", null, "ic_", null));
        assertEquals("foo_24dp.png", FileOutput.buildName("foo", null, null, "_24dp"));
        assertEquals("ic_foo_24dp.png", FileOutput.buildName("foo", null, "ic_", "_24dp"));
        assertEquals("ic_foo_24dp.png", FileOutput.buildName("ic_foo_24dp", null, "ic_", "_24dp"));
        
        assertEquals("ic_foo.png", FileOutput.buildName("ic_foo", null, "ic_", null));
        
        assertEquals("bar.png", FileOutput.buildName("foo", "bar", null, null));
        assertEquals("ic_bar.png", FileOutput.buildName("foo", "bar", "ic_", null));
        assertEquals("ic_bar_24dp.png", FileOutput.buildName("foo", "bar", "ic_", "_24dp"));
        
        assertEquals("ic_bar_24dp.png", FileOutput.buildName("foo", "ic_bar_24dp", "ic_", "_24dp"));
    }
}
