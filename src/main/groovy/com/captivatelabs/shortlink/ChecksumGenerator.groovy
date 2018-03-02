package com.captivatelabs.shortlink

import grails.config.Config
import grails.core.support.GrailsConfigurationAware
import groovy.transform.CompileStatic

@CompileStatic
abstract class ChecksumGenerator implements GrailsConfigurationAware {
    public static final String DEFAULT_CHECKSUM_DICTIONARY = '0987654321ZYXWVUTSRQPONMLKJIHGFEDCBAzyxwvutsrqponmlkjihgfedcba'

    abstract String generate(Long id)
    abstract short checksumLength()

    private char[] checksumDictionary


    @Override
    void setConfiguration(Config co) {
        checksumDictionary = co.getOrDefault('com.captivatelabs.shortlink.checksumDictionary', DEFAULT_CHECKSUM_DICTIONARY).toString().toCharArray()
    }
}
