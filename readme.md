# Free SVG to PNG converter 

## Goal
Manage your Icons in SVG and generate the needed PNG into your projects as needed. No "Web Service" needed, just an executable JAR file.

## Download

[Download at bintray](https://bintray.com/puel/Releases/SvgToPng#files)

## CLI Samples

        # just convert a file
        svgtopng foo.svg
        
        # generate a PNG with a name
        svgtopng -f foo.svg -n bar.png
        
        # convert all file in a directory
        svgtopng -d /Picures/icons/svg -o /Pictures/icons/png
        
        # convert with a JSON configuration
        svgtopng -d . -c my.json
        
        # convert SVG files using the default Android configuration
        svgtopng -d . -o /dev/workset/android-project/app/src/main/res --android
        
## CLI Usage

        ================================================================================
                                           SVG to PNG                                   
        
        usage: svgtopng
            --android         Android default config from mdpi 48x48 -> xxxhdpi
                              192x192.
            --android-small   Android Small default config from mdpi 24x24 ->
                              xxxhdpi 96x96.
         -c <arg>             JSON Config file for the file output.
         -d <arg>             Source directory with one or more files to convert.
         -f <arg>             Source file to convert.
         -h <arg>             Height of the output file.
         -n <arg>             New name to use for all output files.
         -o <arg>             Output directory where to put the file.
         -w <arg>             Width of the output file.

## JSON Android Config Sample
        
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

