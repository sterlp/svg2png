package org.sterl.svg2png;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

import javax.imageio.ImageIO;

import org.apache.batik.transcoder.SVGAbstractTranscoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.TranscodingHints;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
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
            Collection<File> listFiles = FileUtils.listFiles(dir, new String[]{"svg"}, true);
            generated = new ArrayList<>();
            for (File file : listFiles) {
                generated.addAll(convertFile(file, outCfg));
            }
        }

        return generated;
    }

    private static List<File> convertFile(File input, OutputConfig cfg) throws IOException, TranscoderException, FileNotFoundException {
        final TranscoderInput ti = new TranscoderInput(input.toURI().toString());
        final PNGTranscoder t = new PNGTranscoder();

        // Disable XXE
        t.addTranscodingHint(SVGAbstractTranscoder.KEY_ALLOW_EXTERNAL_RESOURCES, cfg.isAllowExternalResource());


        final List<File> generated = new ArrayList<>();
        final String inputPath = input.getParent();
        final StringBuilder info = new StringBuilder();

        for (FileOutput out : cfg.getFiles()) {
            info.setLength(0);
            info.append(StringUtils.rightPad(input.getName(), 12));

            // https://github.com/sterlp/svg2png/issues/11
            t.addTranscodingHint(ImageTranscoder.KEY_FORCE_TRANSPARENT_WHITE, out.isForceTransparentWhite());
            if (out.getWidth() > 0) {
                setSizeHint(SVGAbstractTranscoder.KEY_WIDTH, out.getWidth(), t, info);
                info.append(out.getWidth() +"w ");
            }
            if (out.getHeight() > 0) {
                setSizeHint(SVGAbstractTranscoder.KEY_HEIGHT, out.getHeight(), t, info);
                info.append(out.getHeight() +"h ");
            }
            if (out.hasBackgroundColor()) {
                t.addTranscodingHint(PNGTranscoder.KEY_BACKGROUND_COLOR, Color.decode("#" + out.getBackgroundColor()));
            }

            final File outputFile = out.toOutputFile(input, cfg.getOutputDirectory(), cfg.getOutputName());
            FileUtil.recreateNewFile(outputFile);
            
            try (ByteArrayOutputStream outStream = new ByteArrayOutputStream()) {
                t.transcode(ti, new TranscoderOutput(outStream));

                if (cfg.getNoAlpha() == null) {
                    IOUtils.write(outStream.toByteArray(), new FileOutputStream(outputFile));
                } else {
                    replaceAlphaBackground(outStream.toByteArray(), outputFile, Color.decode("#" + cfg.getNoAlpha()));
                }

                generated.add(outputFile);
                String outF = outputFile.getCanonicalPath();
                if (inputPath != null && inputPath.length() > 0) {
                    outF = outF.replace(inputPath, "");
                }
                info.append("\t").append(outF);
            }

            System.out.println(info.toString());
        }

        if (cfg.isContentsJson()) {
            final File contents = generateIOSContentJson(cfg);
            generated.add(contents);
            System.out.println("Generated iOS " + contents.getCanonicalPath());
        }

        return generated;
    }

    private static void setSizeHint(TranscodingHints.Key hint, int size,  final PNGTranscoder t, final StringBuilder info) {
        if (size > 0) {
            t.addTranscodingHint(hint, Float.valueOf(size));
            info.append(StringUtils.rightPad((SVGAbstractTranscoder.KEY_WIDTH == hint ? " w" : " h") + size, 6));
        } else t.removeTranscodingHint(hint);
    }

    private static File generateIOSContentJson(OutputConfig cfg) throws IOException, FileNotFoundException {
        String contentsJson;

        try (InputStream stream = Svg2Png.class.getResourceAsStream("/contents.json")) {
            assert stream != null;
            try (Scanner s = new Scanner(stream).useDelimiter("\\A")) {
                contentsJson = s.hasNext() ? s.next() : "";
            }
        }

        contentsJson = contentsJson.replace("?PREFIX?", cfg.getOutputName());

        final File contents = new File(cfg.getOutputDirectory() + File.separator + "Contents.json");
        try (PrintWriter out = new PrintWriter(contents)) {
            out.println(contentsJson);
        }
        return contents;
    }

    private static void replaceAlphaBackground(byte[] inputBytes, 
            File outputFile, 
            final Color newBackground) throws IOException {

        try (ByteArrayInputStream sourceStream = new ByteArrayInputStream(inputBytes)) {
            BufferedImage sourceImage = ImageIO.read(sourceStream);

            // Create new image without alpha channel
            BufferedImage destImage = new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight(), BufferedImage.TYPE_INT_RGB);

            // Draw source onto dest canvas
            Graphics2D g2d = destImage.createGraphics();
            g2d.setColor(newBackground);
            g2d.fillRect(0, 0, destImage.getWidth(), destImage.getHeight());
            g2d.drawImage(sourceImage, 0, 0, null);
            g2d.dispose();

            if (!ImageIO.write(destImage, "png", outputFile)) {
                throw new RuntimeException("No image writer found in Graphics2D, cannot replace the background!");
            }
        }
    }
}
