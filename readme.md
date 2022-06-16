# Free SVG to PNG converter 

![Java CI with Maven](https://github.com/sterlp/svg2png/workflows/Java%20CI%20with%20Maven/badge.svg?branch=master)
[![huntr](https://cdn.huntr.dev/huntr_security_badge_mono.svg)](https://huntr.dev)
[![CodeQL](https://github.com/sterlp/svg2png/actions/workflows/codeql-analysis.yml/badge.svg)](https://github.com/sterlp/svg2png/actions/workflows/codeql-analysis.yml)

## Goal
Manage your Icons in SVG and generate the needed PNG into your projects as needed. No "Web Service" needed, just an executable JAR file.

## Download & Requirements

* [Download latest release version of svg2png](https://github.com/puel/svg2png/releases)
* You have to have Java 8 installed on your PC
* Sidenote: [Google Android Icons](https://www.google.com/design/icons/)

## CLI Samples

* use either **java -jar svg2png**
* or download the executable jar and use **svg2png**

```Shell
# just convert a file
svg2png foo.svg

# generate a 512x512 PNG
./svg2png -w 512 -h 512 -f foo.svg

# generate a PNG with a name
svg2png -f foo.svg -n bar.png

# Convert all *.svg files in the current directory to 24dp android png files (generates drawable-* directories)
svg2png --android-small -d .

# Convert 'my_picture.svg' using the android profile 48dp (generates drawable-* directories)
svg2png --android -f my_picture.svg

# Converts 'my-logo.svg' as android logo 48dp, using ic_launcher.png as name, generates into mipmap-* directories
svg2png --android-launch -f my-logo.svg

# convert all files in the directory '/Picures/icons/svg' and use '/Pictures/icons/png' as the output directory
svg2png -d /Picures/icons/svg -o /Pictures/icons/png

# convert with a JSON configuration
svg2png -d . -c my.json

# convert SVG files using the default Android configuration
svg2png -d . -o /dev/workset/android-project/app/src/main/res --android

# you can always start it like any other java jar file
java -jar svg2png
```

## CLI Usage

        ================================================================================
                                   SVG to PNG                                   
                                   
        usage: svg2png
            --android             Android Icon 48dp mdpi 48x48 -> xxxhdpi 192x192.
            --android-24dp        Android 24dp icons, with suffix _24dp -- mdpi
                                24x24 -> xxxhdpi 96x96.
            --android-36dp        Android 36dp icons, with suffix _36dp -- mdpi
                                36x36 -> xxxhdpi 144x144.
            --android-48dp        Android 48dp icons, with suffix _48dp -- mdpi
                                48x48 -> xxxhdpi 192x192.
            --android-icon        Android Icon (Action Bar, Dialog etc.)  config
                                mdpi 36x36 -> xxxhdpi 128x128.
            --android-launch      Android Launcher Icon config mdpi 48x48 ->
                                xxxhdpi 192x192.
            --android-small       Android Small default config from mdpi 24x24 ->
                                xxxhdpi 96x96.
        -c <arg>                 JSON Config file for the file output.
        -d <arg>                 Source directory with one or more files to
                                convert.
        -e,--allow-external      Allow external entities to be loaded by the SVG.
        -f <arg>                 Source file to convert.
        -h <arg>                 Height of the output file.
        -n <arg>                 New name to use for all output files.
        -o <arg>                 Output directory where to put the file.
            --transparent-white   This is a trick so that viewers which do not
                                support the alpha channel will see a white
                                background (and not a black one).
        -w <arg>                 Width of the output file.

Examples:
---------
# just convert a file
svg2png foo.svg

# generate a PNG with a name
svg2png -f foo.svg -n bar.png

# convert all file in a directory
svg2png -d /Picures/icons/svg -o /Pictures/icons/png

# convert with a JSON configuration
svg2png -d . -c my.json

# convert SVG files using the default Android configuration
svg2png -d . -o /dev/workset/android-project/app/src/main/res --android



## JSON Android Config Sample

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
