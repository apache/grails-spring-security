#!/usr/bin/env groovy

import static groovy.io.FileType.*

new File(args[0]).eachFileRecurse FILES, { File file ->
    if (file.name.contains(' ')) {
        file.renameTo(file.absolutePath.replaceAll(' ', '_'))
    } 
}
