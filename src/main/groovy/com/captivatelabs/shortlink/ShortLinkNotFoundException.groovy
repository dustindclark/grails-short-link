package com.captivatelabs.shortlink

import groovy.transform.CompileStatic

@CompileStatic
class ShortLinkNotFoundException extends Exception {
    public ShortLinkNotFoundException(String message) {
        super(message)
    }
}
