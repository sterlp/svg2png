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
}
