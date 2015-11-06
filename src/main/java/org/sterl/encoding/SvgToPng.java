package org.sterl.encoding;

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
import org.sterl.encoding.config.FileOutput;
import org.sterl.encoding.config.OutputConfig;
import org.sterl.encoding.util.FileUtil;

public class SvgToPng {

    private final OutputConfig outCfg;
    
    public SvgToPng(OutputConfig outCfg) {
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

        for (FileOutput out : cfg.getFiles()) {
            if (out.getHeight() > 0) t.addTranscodingHint(PNGTranscoder.KEY_MAX_HEIGHT, new Float(out.getHeight()));
            else t.removeTranscodingHint(PNGTranscoder.KEY_MAX_HEIGHT);
            if (out.getWidth() > 0) t.addTranscodingHint(PNGTranscoder.KEY_MAX_WIDTH, new Float(out.getWidth()));
            else t.removeTranscodingHint(PNGTranscoder.KEY_MAX_WIDTH);
            
            File outputFile = out.toOutputFile(input, cfg.getOutputDirectory(), cfg.getOutputName());
            if (outputFile.exists()) {
                outputFile.delete();
            }
            outputFile.getParentFile().mkdirs();
            outputFile.createNewFile();
            try (FileOutputStream outStram = new FileOutputStream(outputFile)) {
                t.transcode(ti, new TranscoderOutput(outStram));
                generated.add(outputFile);
                System.out.println(input.getName() + " converted to " + outputFile.getAbsolutePath());
            }
        }
        return generated;
    }
}
