package com.captivatelabs.shortlink

import groovy.transform.CompileStatic

@CompileStatic
interface ShortLinkProvider {
    long getId(String targetUrl);

    String resolveShortLink(long id) throws ShortLinkNotFoundException;
}
