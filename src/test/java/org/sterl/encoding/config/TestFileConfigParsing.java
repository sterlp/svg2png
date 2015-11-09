package org.sterl.encoding.config;

import static org.junit.Assert.*;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TestFileConfigParsing {

    @Test
    public void testAndroid() throws Exception {
        ObjectMapper m = new ObjectMapper();
        OutputConfig config = m.readerFor(OutputConfig.class).readValue(getClass().getResourceAsStream("/android.json"));
        
        assertEquals(5, config.getFiles().size());
        assertEquals(192, config.getFiles().get(0).getHeight());
        assertEquals(192, config.getFiles().get(0).getWidth());
        assertEquals("drawable-xxxhdpi", config.getFiles().get(0).getDirectory());
    }
    
    @Test
    public void testAndroidSmall() throws Exception {
        ObjectMapper m = new ObjectMapper();
        OutputConfig config = m.readerFor(OutputConfig.class).readValue(getClass().getResourceAsStream("/android-small.json"));
        
        assertEquals(5, config.getFiles().size());
        assertEquals(96, config.getFiles().get(0).getHeight());
        assertEquals(96, config.getFiles().get(0).getWidth());
        assertEquals("drawable-xxxhdpi", config.getFiles().get(0).getDirectory());
    }
    
    @Test
    public void testAndroidIcon() throws Exception {
        ObjectMapper m = new ObjectMapper();
        OutputConfig config = m.readerFor(OutputConfig.class).readValue(getClass().getResourceAsStream("/android-icon.json"));
        
        assertEquals(5, config.getFiles().size());
        assertEquals(128, config.getFiles().get(0).getHeight());
        assertEquals(128, config.getFiles().get(0).getWidth());
        assertEquals("drawable-xxxhdpi", config.getFiles().get(0).getDirectory());
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
