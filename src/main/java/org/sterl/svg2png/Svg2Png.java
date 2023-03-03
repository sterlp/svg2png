package org.sterl.svg2png;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

import org.apache.batik.transcoder.SVGAbstractTranscoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.sterl.svg2png.config.FileOutput;
import org.sterl.svg2png.config.OutputConfig;
import org.sterl.svg2png.util.FileUtil;

import javax.imageio.ImageIO;

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
            Collection<File> listFiles = FileUtils.listFiles(dir, new String[]{"svg"}, true);
            generated = new ArrayList<>();
            for (File file : listFiles) {
                generated.addAll(convertFile(file, outCfg));
            }
        }

        if (outCfg.isContentsJson()) {
            String contentsJson;

            try (InputStream stream = Svg2Png.class.getResourceAsStream("/contents.json")) {
                assert stream != null;
                Scanner s = new Scanner(stream).useDelimiter("\\A");
                contentsJson = s.hasNext() ? s.next() : "";
            }

            contentsJson = contentsJson.replace("?PREFIX?", outCfg.getOutputName());

            File contents = new File(outCfg.getOutputDirectory() + File.separator + "Contents.json");
            try (PrintWriter out = new PrintWriter(contents)) {
                out.println(contentsJson);
            }

            generated.add(contents);
        }

        return generated;
    }

    private static List<File> convertFile(File input, OutputConfig cfg) throws IOException, TranscoderException, FileNotFoundException {
        final TranscoderInput ti = new TranscoderInput(input.toURI().toString());
        final PNGTranscoder t = new PNGTranscoder();

        // Disable XXE
        t.addTranscodingHint(SVGAbstractTranscoder.KEY_ALLOW_EXTERNAL_RESOURCES, cfg.isAllowExternalResource()); 
        // https://github.com/sterlp/svg2png/issues/11
        t.addTranscodingHint(PNGTranscoder.KEY_FORCE_TRANSPARENT_WHITE, cfg.isForceTransparentWhite());
        

        final List<File> generated = new ArrayList<>();
        final String inputPath = input.getParent();
        final StringBuilder info = new StringBuilder();

        for (FileOutput out : cfg.getFiles()) {
            info.setLength(0);
            info.append(input.getName());

            if (out.getWidth() > 0) {
                t.addTranscodingHint(SVGAbstractTranscoder.KEY_WIDTH, Float.valueOf(out.getWidth()));
                info.append(StringUtils.leftPad(" w"+ out.getWidth(), 5));
            } else t.removeTranscodingHint(SVGAbstractTranscoder.KEY_WIDTH);

            if (out.getHeight() > 0) {
                t.addTranscodingHint(SVGAbstractTranscoder.KEY_HEIGHT, Float.valueOf(out.getHeight()));
                info.append(StringUtils.leftPad(" h"+out.getHeight(), 5));
            } else t.removeTranscodingHint(SVGAbstractTranscoder.KEY_HEIGHT);

            File outputFile = out.toOutputFile(input, cfg.getOutputDirectory(), cfg.getOutputName());
            if (outputFile.exists()) {
                outputFile.delete();
            }
            outputFile.getParentFile().mkdirs();
            outputFile.createNewFile();

            if (cfg.getNoAlpha() == null) {
                try (FileOutputStream outStream = new FileOutputStream(outputFile)) {
                    t.transcode(ti, new TranscoderOutput(outStream));
                    generated.add(outputFile);
                    String outF = outputFile.getCanonicalPath();
                    if (inputPath != null && inputPath.length() > 0) outF.replace(inputPath, "");
                    info.append("\t").append(outF);
                }
            } else {
                try (ByteArrayOutputStream outStream = new ByteArrayOutputStream()) {
                    t.transcode(ti, new TranscoderOutput(outStream));

                    byte[] bytes = outStream.toByteArray();
                    removeAlpha(bytes, outputFile, Color.decode("#" + cfg.getNoAlpha()));

                    generated.add(outputFile);
                    String outF = outputFile.getCanonicalPath();
                    if (inputPath != null && inputPath.length() > 0) outF.replace(inputPath, "");
                    info.append("\t").append(outF);
                }
            }
            System.out.println(info.toString());
        }
        return generated;
    }

    private static void removeAlpha(byte[] inputBytes, File outputFile, Color background) throws java.io.IOException {
        try (ByteArrayInputStream sourceStream = new ByteArrayInputStream(inputBytes)) {
            BufferedImage sourceImage = ImageIO.read(sourceStream);

            // Create new image without alpha channel
            BufferedImage destImage = new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight(), BufferedImage.TYPE_INT_RGB);

            // Draw source onto dest canvas
            Graphics2D g2d = destImage.createGraphics();
            g2d.setColor(background);
            g2d.fillRect(0, 0, destImage.getWidth(), destImage.getHeight());
            g2d.drawImage(sourceImage, 0, 0, null);
            g2d.dispose();

            ImageIO.write(destImage, "png", outputFile);
        }
    }
}
