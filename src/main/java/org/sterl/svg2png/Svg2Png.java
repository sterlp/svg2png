package org.sterl.svg2png;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.io.FileUtils;
import org.sterl.svg2png.config.FileOutput;
import org.sterl.svg2png.config.OutputConfig;
import org.sterl.svg2png.util.FileUtil;

public class Svg2Png {

    private final OutputConfig outCfg;
    
    public Svg2Png(OutputConfig outCfg) {
        super();
        this.outCfg = outCfg;
    }

    public List<File> convert() throws IOException, TranscoderException {
        List<File> generated;
        if (outCfg.getInputFile() != null) {
            File input = FileUtil.newFile(outCfg.getInputFile());
            generated = convertFile(input, outCfg);
        } else {
            File dir = FileUtil.newFile(outCfg.getInputDirectory());
            @SuppressWarnings("unchecked")
            Collection<File> listFiles = (Collection<File>)FileUtils.listFiles(dir, new String[]{"svg"}, true);
            generated = new ArrayList<>();
            for (File file : listFiles) {
                generated.addAll(convertFile(file, outCfg));
            }
        }
        return generated;
    }

    private static List<File> convertFile(File input, OutputConfig cfg) throws IOException, TranscoderException, FileNotFoundException {
        TranscoderInput ti = new TranscoderInput(input.toURI().toString());
        PNGTranscoder t = new PNGTranscoder();
        List<File> generated = new ArrayList<>();

        StringBuilder info = new StringBuilder();
        for (FileOutput out : cfg.getFiles()) {
            info.setLength(0);
            info.append(input.getName());

            if (out.getWidth() > 0) {
                t.addTranscodingHint(PNGTranscoder.KEY_WIDTH, new Float(out.getWidth()));
                info.append(" w").append(out.getWidth());
            } else t.removeTranscodingHint(PNGTranscoder.KEY_WIDTH);

            if (out.getHeight() > 0) {
                t.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, new Float(out.getHeight()));
                info.append(" h").append(out.getHeight());
            } else t.removeTranscodingHint(PNGTranscoder.KEY_HEIGHT);
            
            File outputFile = out.toOutputFile(input, cfg.getOutputDirectory(), cfg.getOutputName());
            if (outputFile.exists()) {
                outputFile.delete();
            }
            outputFile.getParentFile().mkdirs();
            outputFile.createNewFile();
            try (FileOutputStream outStram = new FileOutputStream(outputFile)) {
                t.transcode(ti, new TranscoderOutput(outStram));
                generated.add(outputFile);
                info.append(" ").append(outputFile.getAbsolutePath());
            }
            System.out.println(info.toString());
        }
        return generated;
    }
}
