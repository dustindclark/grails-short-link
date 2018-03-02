package com.captivatelabs.shortlink

import groovy.transform.CompileStatic

@CompileStatic
class DefaultChecksumGenerator extends ChecksumGenerator {
    @Override
    String generate(Long id) {
        return "" //TODO
    }

    @Override
    short checksumLength() {
        return 0
    }
}
