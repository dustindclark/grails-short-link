package com.captivatelabs.shortlink

import groovy.transform.CompileStatic

@CompileStatic
class NoClickTracker implements ClickTracker{
    @Override
    void track(ShortLink shortLink) {
        //No op
    }
}
