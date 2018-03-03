package com.captivatelabs.shortlink

import grails.plugins.Plugin

class ShortLinkGrailsPlugin extends Plugin {
    def grailsVersion = "3.3.2 > *"

    Closure doWithSpring() {
        { ->
            checksumGenerator(NoChecksumGenerator)
            shortCodeGenerator(DefaultShortCodeGenerator)
            clickTracker(NoClickTracker)
        }
    }
}
