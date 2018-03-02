package com.captivatelabs.shortlink

class ShortLink {
    String targetUrl

    static constraints = {
        targetUrl url: true, size: 1..2083
    }

    static mapping = {
        version false
    }
}
