# Free SVG to PNG converter 

![Java CI with Maven](https://github.com/sterlp/svg2png/workflows/Java%20CI%20with%20Maven/badge.svg?branch=master)
[![huntr](https://cdn.huntr.dev/huntr_security_badge_mono.svg)](https://huntr.dev)
[![CodeQL](https://github.com/sterlp/svg2png/actions/workflows/codeql-analysis.yml/badge.svg)](https://github.com/sterlp/svg2png/actions/workflows/codeql-analysis.yml)
[![Maven](https://img.shields.io/maven-central/v/org.sterl.svg2png/svg2png)](https://central.sonatype.com/artifact/org.sterl.svg2png/svg2png)

## Goal
Manage your Icons in SVG and generate the needed PNG into your projects as needed. No "Web Service" needed, just an executable JAR file. This library is based on [batik](https://xmlgraphics.apache.org/batik/)

## Supported platforms
Only platforms with a full Java JRE are supported like:

- Windows
- Linux
- Mac OS

## Not supported platforms

### Android

Batik SVG Toolkit isn't supported by android because of the missing [Java2D API](https://stackoverflow.com/questions/7418937/how-to-integrate-batik-with-android-to-open-display-svg-files)

## Tested Java distributions

- zulu
- graalvm

> temurin version 11 failed in the ci/cd build. 

- 1.2.x => JDK 8
- 1.3.x => JDK 11

## Download & Requirements

* [Download latest release version of svg2png](https://github.com/puel/svg2png/releases)
  * [Or from maven](https://central.sonatype.com/artifact/org.sterl.svg2png/svg2png)
* You have to have Java 8 installed on your PC
* Sidenote: [Google Android Icons](https://www.google.com/design/icons/)

## CLI Options

  > **file:** Can be set for each file individually, see json config, or for all using a cli option. The name can be used in the files.

| CLI | CLI long | Name      | Level      | Description      |
| --- | -------- | --------- | ---------- | ---------------- |
| w   | width    | width     | **file**   | Image width in pixel |
| h   | height   | height    | **file**   | Image height in pixel |
| n   | name     | name      | **file**   | Output name of the image |
| o   | out-dir  | directory | **file**   | Output directory for this image |
| | background-color   | backgroundColor    | **file**  | Provide a custom background color to use for opaque image formats e.g. 0077FF. |




## CLI Usage

```
================================================================================
                                   SVG to PNG                                   

usage: svg2png
 -a,--no-alpha <arg>           Saves PNG without alpha channel and with
                               specified background hex triplet. (Needed
                               for iOS assets.)
    --android                  Android Icon 48dp mdpi 48x48 -> xxxhdpi
                               192x192.
    --android-24dp             Android 24dp icons, with suffix _24dp --
                               mdpi 24x24 -> xxxhdpi 96x96.
    --android-36dp             Android 36dp icons, with suffix _36dp --
                               mdpi 36x36 -> xxxhdpi 144x144.
    --android-48dp             Android 48dp icons, with suffix _48dp --
                               mdpi 48x48 -> xxxhdpi 192x192.
    --android-icon             Android Icon (Action Bar, Dialog etc.)
                               config mdpi 36x36 -> xxxhdpi 128x128.
    --android-launch           Android Launcher Icon config mdpi 48x48 ->
                               xxxhdpi 192x192.
    --android-small            Android Small default config from mdpi
                               24x24 -> xxxhdpi 96x96.
    --background-color <arg>   Provide a custom background color to use
                               for opaque image formats e.g. 0077FF.
 -c,--config <arg>             JSON Config file for the file output.
 -d,--in-dir <arg>             Source directory with one or more files to
                               convert.
 -e,--allow-external           Allow external entities to be loaded by the
                               SVG.
 -f,--file <arg>               Source file to convert.
 -h,--height <arg>             Height of the output file.
    --ios                      iOS icons and Contents.json.
 -n,--name <arg>               New name to use for all output files.
 -o,--out-dir <arg>            Output directory where to put the file.
    --transparent-white        This is a trick so that viewers which do
                               not support the alpha channel will see a
                               white background (and not a black one).
 -w,--width <arg>              Width of the output file.
```

## CLI Samples

* use either **java -jar svg2png**
* or download the executable jar and use **svg2png**

```Shell
# just convert one file, will create a foo.png as result
java -jar svg2png.jar foo.svg

# generate a PNG with a custom name
java -jar svg2png.jar -f foo.svg -n bar.png

# convert all files in a directory to a output directory
java -jar svg2png.jar -d /Pictures/icons/svg -o /Pictures/icons/png

# generate a 512x512 PNG
java -jar svg2png.jar -w 512 -h 512 -f foo.svg

# convert a directory with a JSON configuration
java -jar svg2png.jar -d . -c my.json

# convert SVG files using the default Android configuration
java -jar svg2png.jar -d . -o /dev/workset/android-project/app/src/main/res --android

# Convert all *.svg files in the current directory to 24dp android png files (generates drawable-* directories)
java -jar svg2png.jar --android-small -d .

# Convert 'my_picture.svg' using the android profile 48dp (generates drawable-* directories)
java -jar svg2png.jar --android -f my_picture.svg

# Converts 'my-logo.svg' as android logo 48dp, using ic_launcher.png as name, generates into mipmap-* directories
java -jar svg2png.jar --android-launch -f my-logo.svg

# convert all files in the directory '/Picures/icons/svg' and use '/Pictures/icons/png' as the output directory
java -jar svg2png.jar -d /Picures/icons/svg -o /Pictures/icons/png

# convert with a JSON configuration
java -jar svg2png.jar -d . -c my.json

# convert SVG to iOS icons with an orange background
java -jar svg2png.jar -f foo.svg -n bar -o icons --ios --no-alpha ffbd33

# select a custom background color
java -jar svg2png.jar --background-color 6610f2 -f sample.svg
```

### JSON Android Config Sample

```JSON
{
    "files": [
        {
            "directory": "drawable-xxxhdpi",
            "nameSuffix": "_24dp",
            "height": 96,
            "width": 96
        },{
            "directory": "drawable-xxhdpi",
            "nameSuffix": "_24dp",
            "height": 72,
            "width": 72
        },{
            "directory": "drawable-xhdpi",
            "nameSuffix": "_24dp",
            "height": 48,
            "width": 48
        },{
            "directory": "drawable-hdpi",
            "nameSuffix": "_24dp",
            "height": 36,
            "width": 36
        },{
            "directory": "drawable-mdpi",
            "nameSuffix": "_24dp",
            "height": 24,
            "width": 24
        }
    ]
}
```

# Links
- https://developer.android.com/training/multiscreen/screendensities
