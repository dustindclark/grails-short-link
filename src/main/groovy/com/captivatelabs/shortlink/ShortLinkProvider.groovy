package com.captivatelabs.shortlink

import groovy.transform.CompileStatic

@CompileStatic
interface ShortLinkProvider {
    long create(String targetUrl);

    String resolve(long id) throws ShortLinkNotFoundException;
}
