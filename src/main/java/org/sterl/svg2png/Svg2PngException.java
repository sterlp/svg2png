package org.sterl.svg2png;

import org.sterl.svg2png.config.OutputConfig;

import lombok.Getter;

public class Svg2PngException extends Exception {
    @Getter
    private final OutputConfig cfg;

    public Svg2PngException(Throwable e, OutputConfig config) {
        super(e);
        cfg = config;
    }
    
    @Override
    public String getMessage() {
        return super.getMessage() + " Config: " + cfg.toString();
    }
}
