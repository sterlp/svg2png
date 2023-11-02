package org.sterl.svg2png.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TestFileConfigParsing {

    private final ObjectMapper m = new ObjectMapper();

    @Test
    public void testAndroid() throws Exception {
        System.err.println(getClass().getResourceAsStream("./android.json"));
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
        try (InputStream is = getClass().getResourceAsStream("/android-icon.json")) {
            OutputConfig config = m.readerFor(OutputConfig.class).readValue(is);

            assertEquals(5, config.getFiles().size());
            assertEquals(192, config.getFiles().get(0).getHeight());
            assertEquals(192, config.getFiles().get(0).getWidth());
            assertEquals("drawable-xxxhdpi", config.getFiles().get(0).getDirectory());
        }
    }

    @Test
    public void testIos() throws Exception {
        try (InputStream is = getClass().getResourceAsStream("/ios.json")) {
            OutputConfig config = m.readerFor(OutputConfig.class).readValue(is);

            assertEquals(15, config.getFiles().size());
            assertEquals(20, config.getFiles().get(0).getHeight());
            assertEquals(20, config.getFiles().get(0).getWidth());
            assertEquals("ffffff", config.getNoAlpha());
            assertTrue(config.isContentsJson());
            assertNull(config.getFiles().get(0).getDirectory());
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
