package com.captivatelabs.shortlink

import groovy.transform.CompileStatic

@CompileStatic
class NoChecksumGenerator extends ChecksumGenerator {
    @Override
    String generate(Long id) {
        return null
    }

    @Override
    short checksumLength() {
        return 0
    }
}
