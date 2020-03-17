package com.captivatelabs.shortlink

import groovy.transform.CompileStatic

import javax.servlet.http.HttpServletRequest

@CompileStatic
class NoClickTracker implements ClickTracker {
    @Override
    void track(long id, String targetUrl, HttpServletRequest request) {
        //No op
    }
}
